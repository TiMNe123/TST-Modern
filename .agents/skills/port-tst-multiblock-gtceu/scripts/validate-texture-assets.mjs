import crypto from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const defaultProjectRoot = path.resolve(scriptDir, "../../../..");
const args = process.argv.slice(2);
const manifestArg = args.find((arg) => !arg.startsWith("--")) ??
    path.resolve(scriptDir, "../references/texture-baseline.json");
const rootIndex = args.indexOf("--project-root");
const projectRoot = path.resolve(rootIndex >= 0 ? args[rootIndex + 1] : defaultProjectRoot);
const manifestPath = path.resolve(manifestArg);
const textureRoot = path.join(projectRoot, "src/main/resources/assets/tstmodern/textures");
const modelRoot = path.join(projectRoot, "src/main/resources/assets/tstmodern/models");
const errors = [];

function normalize(file) {
    return file.replaceAll("\\", "/");
}

function walk(root, predicate) {
    if (!fs.existsSync(root)) return [];
    const found = [];
    for (const entry of fs.readdirSync(root, { withFileTypes: true })) {
        const file = path.join(root, entry.name);
        if (entry.isDirectory()) found.push(...walk(file, predicate));
        else if (predicate(file)) found.push(file);
    }
    return found;
}

function sha256(file) {
    return crypto.createHash("sha256").update(fs.readFileSync(file)).digest("hex");
}

function strings(value, output = []) {
    if (typeof value === "string") output.push(value);
    else if (Array.isArray(value)) value.forEach((item) => strings(item, output));
    else if (value && typeof value === "object") Object.values(value).forEach((item) => strings(item, output));
    return output;
}

function textureStrings(value, output = []) {
    if (Array.isArray(value)) {
        value.forEach((item) => textureStrings(item, output));
    } else if (value && typeof value === "object") {
        for (const [key, item] of Object.entries(value)) {
            if (key === "textures") strings(item, output);
            else textureStrings(item, output);
        }
    }
    return output;
}

let manifest;
try {
    manifest = JSON.parse(fs.readFileSync(manifestPath, "utf8"));
} catch (error) {
    console.error(`Cannot read texture baseline ${manifestPath}: ${error.message}`);
    process.exit(1);
}

if (manifest.schemaVersion !== 1) errors.push("schemaVersion must be 1");
if (manifest.policy !== "LOCKED_EXISTING_BASELINE") errors.push("policy must be LOCKED_EXISTING_BASELINE");
if (!Array.isArray(manifest.files)) errors.push("files must be an array");

const entries = new Map();
for (const [index, entry] of (manifest.files ?? []).entries()) {
    const field = `files[${index}]`;
    const relative = normalize(entry.path ?? "");
    if (!relative.startsWith("src/main/resources/assets/tstmodern/textures/") || !relative.endsWith(".png")) {
        errors.push(`${field}.path must be a TSTModern production PNG`);
        continue;
    }
    if (entries.has(relative)) errors.push(`${field}.path duplicates ${relative}`);
    entries.set(relative, entry);
    if (entry.status !== "LOCKED_EXISTING_BASELINE") errors.push(`${field}.status must be LOCKED_EXISTING_BASELINE`);
    const file = path.resolve(projectRoot, relative);
    if (!fs.existsSync(file)) {
        errors.push(`${relative} is missing`);
        continue;
    }
    const actualSize = fs.statSync(file).size;
    if (actualSize !== entry.size) errors.push(`${relative} size mismatch`);
    const actualHash = sha256(file);
    if (actualHash !== entry.sha256) errors.push(`${relative} hash mismatch`);
}

for (const file of walk(textureRoot, (candidate) => candidate.toLowerCase().endsWith(".png"))) {
    const relative = normalize(path.relative(projectRoot, file));
    if (!entries.has(relative)) errors.push(`${relative} is not present in the texture baseline`);
}

for (const modelFile of walk(modelRoot, (candidate) => candidate.toLowerCase().endsWith(".json"))) {
    let model;
    try {
        model = JSON.parse(fs.readFileSync(modelFile, "utf8").replace(/^\uFEFF/, ""));
    } catch (error) {
        errors.push(`${normalize(path.relative(projectRoot, modelFile))} is invalid JSON: ${error.message}`);
        continue;
    }
    for (const value of textureStrings(model)) {
        const match = /^tstmodern:(block|item)\/(.+)$/.exec(value);
        if (!match) continue;
        const texture = path.join(textureRoot, match[1], `${match[2]}.png`);
        if (!fs.existsSync(texture)) {
            errors.push(`${normalize(path.relative(projectRoot, modelFile))} references missing local texture ${value}`);
        }
    }
}

if (errors.length > 0) {
    console.error(`Texture baseline invalid (${errors.length} error${errors.length === 1 ? "" : "s"}):`);
    for (const error of errors) console.error(`- ${error}`);
    process.exit(1);
}

console.log(`Texture baseline valid: ${entries.size} locked production PNGs.`);
