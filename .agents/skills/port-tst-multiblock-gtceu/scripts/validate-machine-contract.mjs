import crypto from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const defaultProjectRoot = path.resolve(scriptDir, "../../../..");
const args = process.argv.slice(2);
const contractArg = args.find((arg) => !arg.startsWith("--"));
const rootIndex = args.indexOf("--project-root");
const projectRoot = path.resolve(rootIndex >= 0 ? args[rootIndex + 1] : defaultProjectRoot);

if (!contractArg) {
    console.error("Usage: node validate-machine-contract.mjs <contract.json> [--project-root <path>]");
    process.exit(2);
}

const contractPath = path.resolve(contractArg);
const errors = [];
const allowedTextureStatuses = new Set([
    "VERIFIED_LOCAL",
    "VERIFIED_NATIVE",
    "APPROVED_REUSE",
    "APPROVED_MODIFIED",
    "APPROVED_DESIGN",
    "UNRESOLVED",
]);
const localTextureStatuses = new Set([
    "VERIFIED_LOCAL",
    "APPROVED_REUSE",
    "APPROVED_MODIFIED",
    "APPROVED_DESIGN",
]);
const allowedRecipeAuthorities = new Set(["VERIFIED_SOURCE", "APPROVED_DEVIATION"]);

function object(value) {
    return value !== null && typeof value === "object" && !Array.isArray(value);
}

function nonEmpty(value) {
    return typeof value === "string" && value.trim().length > 0;
}

function hashFile(file) {
    return crypto.createHash("sha256").update(fs.readFileSync(file)).digest("hex");
}

function resolveInsideProject(relativePath, field) {
    if (!nonEmpty(relativePath)) {
        errors.push(`${field} is required`);
        return null;
    }
    const resolved = path.resolve(projectRoot, relativePath);
    const relative = path.relative(projectRoot, resolved);
    if (relative.startsWith("..") || path.isAbsolute(relative)) {
        errors.push(`${field} escapes the project root: ${relativePath}`);
        return null;
    }
    return resolved;
}

function requireEvidence(evidence, field) {
    if (!object(evidence)) {
        errors.push(`${field} must be an object`);
        return;
    }
    if (!nonEmpty(evidence.path)) errors.push(`${field}.path is required`);
    if (!nonEmpty(evidence.symbol)) errors.push(`${field}.symbol is required`);
    if (!/^[0-9a-f]{64}$/i.test(evidence.hash ?? "")) errors.push(`${field}.hash must be SHA-256`);
}

let contract;
try {
    contract = JSON.parse(fs.readFileSync(contractPath, "utf8"));
} catch (error) {
    console.error(`Cannot read machine contract ${contractPath}: ${error.message}`);
    process.exit(1);
}

if (contract.schemaVersion !== 1) errors.push("schemaVersion must be 1");
if (!nonEmpty(contract.machine)) errors.push("machine is required");
if (contract.auditStatus !== "AUDIT_COMPLETE") errors.push("auditStatus must be AUDIT_COMPLETE before implementation");
if (contract.branch !== "dev") errors.push("branch must be dev for a new machine port");

if (!object(contract.sourceEvidence)) {
    errors.push("sourceEvidence is required");
} else {
    requireEvidence(contract.sourceEvidence.controller, "sourceEvidence.controller");
    requireEvidence(contract.sourceEvidence.recipePool, "sourceEvidence.recipePool");
    if (!Array.isArray(contract.sourceEvidence.inheritance)) {
        errors.push("sourceEvidence.inheritance must be an array, including an empty verified array");
    } else {
        contract.sourceEvidence.inheritance.forEach((entry, index) =>
            requireEvidence(entry, `sourceEvidence.inheritance[${index}]`));
    }
}

if (!object(contract.structure)) {
    errors.push("structure is required");
} else {
    if (!Array.isArray(contract.structure.dimensions) || contract.structure.dimensions.length !== 3 ||
        contract.structure.dimensions.some((n) => !Number.isInteger(n) || n <= 0)) {
        errors.push("structure.dimensions must contain three positive integers");
    }
    if (!Array.isArray(contract.structure.sourceAxisOrder) || contract.structure.sourceAxisOrder.length !== 3 ||
        contract.structure.sourceAxisOrder.some((axis) => !nonEmpty(axis))) {
        errors.push("structure.sourceAxisOrder must explicitly name all three source axes");
    }
    const axisMap = contract.structure.targetAxisMap;
    if (!object(axisMap) || !nonEmpty(axisMap.RIGHT) || !nonEmpty(axisMap.DOWN) || !nonEmpty(axisMap.BACK)) {
        errors.push("structure.targetAxisMap must explicitly map RIGHT, DOWN, and BACK");
    }
    const controller = contract.structure.controller;
    if (!object(controller) || !nonEmpty(controller.symbol) || controller.count !== 1 || !nonEmpty(controller.outwardFacing)) {
        errors.push("structure.controller must define symbol, count=1, and outwardFacing");
    }
    if (!Array.isArray(contract.structure.symbols) || contract.structure.symbols.length === 0) {
        errors.push("structure.symbols must contain every non-space pattern symbol");
    } else {
        const seen = new Set();
        contract.structure.symbols.forEach((entry, index) => {
            const field = `structure.symbols[${index}]`;
            if (!nonEmpty(entry.symbol)) errors.push(`${field}.symbol is required`);
            if (seen.has(entry.symbol)) errors.push(`${field}.symbol duplicates ${entry.symbol}`);
            seen.add(entry.symbol);
            if (!Number.isInteger(entry.occurrences) || entry.occurrences <= 0) errors.push(`${field}.occurrences must be positive`);
            if (!object(entry.source) || !nonEmpty(entry.source.registry) || !nonEmpty(String(entry.source.meta ?? "")) ||
                !nonEmpty(entry.source.evidence)) {
                errors.push(`${field}.source must include registry, meta, and evidence`);
            }
            if (!object(entry.target) || !nonEmpty(entry.target.registryOrPredicate) || !nonEmpty(entry.target.decision)) {
                errors.push(`${field}.target must include registryOrPredicate and decision`);
            }
        });
    }
}

if (!Array.isArray(contract.recipes)) {
    errors.push("recipes must be an array");
} else {
    contract.recipes.forEach((recipe, index) => {
        const field = `recipes[${index}]`;
        if (!nonEmpty(recipe.id)) errors.push(`${field}.id is required`);
        if (!allowedRecipeAuthorities.has(recipe.authority)) errors.push(`${field}.authority must be VERIFIED_SOURCE or APPROVED_DEVIATION`);
        if (!nonEmpty(recipe.source)) errors.push(`${field}.source is required`);
        if (!nonEmpty(recipe.recipeType)) errors.push(`${field}.recipeType is required`);
        if (!Number.isInteger(recipe.duration) || recipe.duration <= 0) errors.push(`${field}.duration must be positive`);
        if (!Number.isInteger(recipe.eut) || recipe.eut <= 0) errors.push(`${field}.eut must be positive`);
        if (recipe.authority === "APPROVED_DEVIATION" && !nonEmpty(recipe.approvalRef)) {
            errors.push(`${field}.approvalRef is required for an approved deviation`);
        }
    });
}

if (!Array.isArray(contract.textures) || contract.textures.length === 0) {
    errors.push("textures must cover every controller and casing visual role");
} else {
    contract.textures.forEach((texture, index) => {
        const field = `textures[${index}]`;
        if (!nonEmpty(texture.role)) errors.push(`${field}.role is required`);
        if (!allowedTextureStatuses.has(texture.status)) errors.push(`${field}.status is invalid`);
        if (texture.status === "UNRESOLVED") errors.push(`${field} is UNRESOLVED and blocks implementation`);
        if (!nonEmpty(texture.authority)) errors.push(`${field}.authority is required`);
        if (!nonEmpty(texture.resource)) errors.push(`${field}.resource is required`);

        if (localTextureStatuses.has(texture.status)) {
            const localFile = resolveInsideProject(texture.localPath, `${field}.localPath`);
            if (localFile && !fs.existsSync(localFile)) {
                errors.push(`${field}.localPath does not exist: ${texture.localPath}`);
            } else if (localFile) {
                if (!/^[0-9a-f]{64}$/i.test(texture.sha256 ?? "")) {
                    errors.push(`${field}.sha256 must be SHA-256`);
                } else {
                    const actual = hashFile(localFile);
                    if (actual !== texture.sha256.toLowerCase()) errors.push(`${field} hash mismatch for ${texture.localPath}`);
                }
            }
            if (!texture.resource?.startsWith("tstmodern:")) errors.push(`${field}.resource must use tstmodern namespace for a local texture`);
            if (texture.status === "APPROVED_DESIGN" && !nonEmpty(texture.approvalRef)) {
                errors.push(`${field}.approvalRef is required for APPROVED_DESIGN`);
            }
            if (!nonEmpty(texture.sourceEvidence) && texture.status !== "APPROVED_DESIGN") {
                errors.push(`${field}.sourceEvidence is required`);
            }
        }

        if (texture.status === "VERIFIED_NATIVE" && !/^(gtceu|minecraft):/.test(texture.resource ?? "")) {
            errors.push(`${field}.resource must use gtceu or minecraft namespace for VERIFIED_NATIVE`);
        }
    });
}

if (!Array.isArray(contract.approvedDeviations)) errors.push("approvedDeviations must be an array");
if (!Array.isArray(contract.unresolved)) {
    errors.push("unresolved must be an array");
} else if (contract.unresolved.length > 0) {
    errors.push(`unresolved contains ${contract.unresolved.length} blocking decision(s)`);
}

if (errors.length > 0) {
    console.error(`Machine contract invalid (${errors.length} error${errors.length === 1 ? "" : "s"}):`);
    for (const error of errors) console.error(`- ${error}`);
    process.exit(1);
}

console.log(`Machine contract valid: ${contract.machine}`);
