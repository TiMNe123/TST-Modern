import fs from "node:fs";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const skillRoot = path.resolve(scriptDir, "..");
const projectRoot = path.resolve(skillRoot, "../../..");
const auditRoot = path.join(projectRoot, ".codex-spreadsheet-casing-audit");

const rows = JSON.parse(fs.readFileSync(path.join(auditRoot, "resolved_rows_gtceu_reuse.json"), "utf8"));
const nativeMappings = JSON.parse(fs.readFileSync(path.join(auditRoot, "gtceu_reuse_catalog.json"), "utf8"));

function normalizedSource(block) {
    return block.replace(/^GregTechAPI\./, "");
}

function mappingFor(row) {
    const source = normalizedSource(row.block);
    return nativeMappings.find((mapping) => {
        const sameSource = mapping.sourceBlock === source;
        const sameMeta = mapping.meta === "*" || mapping.meta === String(row.meta || "");
        return sameSource && sameMeta;
    });
}

function relativeTextureFolder(copiedPath) {
    const normalized = copiedPath.replaceAll("\\", "/");
    const marker = "/texture_block_casing/";
    const index = normalized.indexOf(marker);
    const relative = index < 0 ? normalized : `texture_block_casing/${normalized.slice(index + marker.length)}`;
    return path.posix.dirname(relative);
}

function resolution(row, mapping) {
    if (mapping?.level === "DÙNG TRỰC TIẾP") return "direct_native";
    if (mapping?.level === "GTCEu TƯƠNG ĐƯƠNG") return "equivalent_review";
    if (row.status.includes("THIẾT KẾ")) return "dedicated_designed";
    if (row.status.includes("TÁI SỬ DỤNG")) return "dedicated_reused";
    return "dedicated_original";
}

const machines = {};
for (const row of rows) {
    const mapping = mappingFor(row);
    const entry = {
        symbol: row.symbol,
        option: row.optionIndex,
        occurrences: row.occurrenceCount,
        sourceLine: row.sourceLine,
        source: {
            block: row.block,
            meta: row.meta,
            type: row.type,
            expression: row.expression,
        },
        resolution: resolution(row, mapping),
        target: mapping?.targetBlock ?? "Register a dedicated tstmodern block",
        sourceTextureNames: row.textureSources.map((source) => path.win32.basename(source)),
        projectTextureFolder: row.copiedFiles.length > 0 ? relativeTextureFolder(row.copiedFiles[0]) : null,
        projectTextureCount: row.copiedFiles.length,
        note: row.note,
    };
    (machines[row.machine] ??= []).push(entry);
}

const machineFiles = Object.fromEntries(
    Object.keys(machines).sort().map((machine) => [machine, `casing-catalog/${machine}.json`]),
);
const catalog = {
    schemaVersion: 1,
    generatedFrom: [
        ".codex-spreadsheet-casing-audit/resolved_rows_gtceu_reuse.json",
        ".codex-spreadsheet-casing-audit/gtceu_reuse_catalog.json",
    ],
    reuseApproval: {
        equivalentMappingsApproved: true,
        equivalentMappingCount: 10,
        approvedOn: "2026-08-21",
        scope: "All GTCEu TƯƠNG ĐƯƠNG rows in TST_Multiblocks_Casing_Texture_Audit_GTCEu_Reuse.xlsx",
    },
    machineCount: Object.keys(machines).length,
    entryCount: rows.length,
    resolutionPolicy: {
        direct_native: "Use the native GTCEu/vanilla block directly.",
        equivalent_review: "Do not substitute automatically; obtain approval or keep a dedicated TSTModern block.",
        dedicated_original: "Register one shared TSTModern block for the source block/meta and use the original texture.",
        dedicated_designed: "Use the approved designed texture and retain a dedicated TSTModern registry identity.",
        dedicated_reused: "Use the approved reused texture and retain a dedicated TSTModern registry identity when structural identity matters.",
    },
    nativeMappings,
    machineFiles,
};

const generatedRoot = path.join(projectRoot, "build", "generated", "port-tst-multiblock-gtceu");
const output = path.join(generatedRoot, "casing-catalog.json");
const machineArg = process.argv.indexOf("--machine");
if (machineArg >= 0) {
    const machine = process.argv[machineArg + 1];
    if (!machines[machine]) throw new Error(`Unknown machine: ${machine}`);
    process.stdout.write(`${JSON.stringify({ machine, entries: machines[machine] }, null, 2)}\n`);
} else if (process.argv.includes("--index")) {
    process.stdout.write(`${JSON.stringify(catalog, null, 2)}\n`);
} else {
    const machineRoot = path.join(generatedRoot, "casing-catalog");
    fs.mkdirSync(machineRoot, { recursive: true });
    fs.writeFileSync(output, `${JSON.stringify(catalog, null, 2)}\n`);
    for (const [machine, entries] of Object.entries(machines)) {
        fs.writeFileSync(
            path.join(machineRoot, `${machine}.json`),
            `${JSON.stringify({ machine, entries }, null, 2)}\n`,
        );
    }
    console.log(`Staged ${catalog.entryCount} entries for ${catalog.machineCount} machines in ${generatedRoot}`);
}
