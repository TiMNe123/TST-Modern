import assert from "node:assert/strict";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const skillRoot = path.resolve(scriptDir, "..");
const projectRoot = path.resolve(skillRoot, "../../..");
const catalogPath = path.join(skillRoot, "references", "casing-catalog.json");
const catalog = JSON.parse(fs.readFileSync(catalogPath, "utf8"));
const reuseManifestPath = path.join(projectRoot, "texture_block_casing", "gtceu_reuse_manifest.json");
const reuseManifest = fs.existsSync(reuseManifestPath) ?
    JSON.parse(fs.readFileSync(reuseManifestPath, "utf8")) : null;
const reuseKey = (machine, entry) => `${machine}|${entry.symbol}|${entry.option}|${entry.sourceLine}`;
const reusedEntries = new Map((reuseManifest?.entries ?? []).map((entry) => [
    reuseKey(entry.machine, entry),
    entry,
]));
let validatedReusedEntries = 0;
let expectedReusedEntries = 0;

assert.equal(catalog.schemaVersion, 1);
assert.equal(catalog.machineCount, 25);
assert.equal(Object.keys(catalog.machineFiles).length, 25);
assert.equal(catalog.entryCount, 408);
assert.equal(catalog.reuseApproval?.equivalentMappingsApproved, true);
assert.equal(catalog.reuseApproval?.equivalentMappingCount, 10);
assert.equal(catalog.nativeMappings.filter((mapping) => mapping.level === "GTCEu TƯƠNG ĐƯƠNG").length, 10);

const allowed = new Set([
    "direct_native",
    "equivalent_review",
    "dedicated_original",
    "dedicated_designed",
    "dedicated_reused",
]);

let counted = 0;
for (const [machine, relativeFile] of Object.entries(catalog.machineFiles)) {
    const machineCatalog = JSON.parse(fs.readFileSync(path.join(skillRoot, "references", relativeFile), "utf8"));
    assert.equal(machineCatalog.machine, machine);
    const entries = machineCatalog.entries;
    assert.ok(entries.length > 0, `${machine} has no casing entries`);
    for (const entry of entries) {
        counted++;
        assert.ok(entry.symbol, `${machine} has an entry without a symbol`);
        assert.ok(entry.source?.block, `${machine}/${entry.symbol} has no source block`);
        assert.ok(allowed.has(entry.resolution), `${machine}/${entry.symbol} has invalid resolution`);
        assert.ok(entry.target, `${machine}/${entry.symbol} has no target decision`);
        if (entry.projectTextureFolder) {
            const textureFolder = path.join(projectRoot, entry.projectTextureFolder);
            assert.ok(fs.existsSync(textureFolder), `${machine}/${entry.symbol} is missing its texture folder`);
            if (entry.resolution === "equivalent_review" && catalog.reuseApproval.equivalentMappingsApproved) {
                expectedReusedEntries++;
                const reused = reusedEntries.get(reuseKey(machine, entry));
                assert.ok(reused, `${machine}/${entry.symbol} is missing from the GTCEu reuse manifest`);
                assert.equal(reused.target, entry.target, `${machine}/${entry.symbol} reuse target differs from catalog`);
                assert.ok(reused.files.length > 0, `${machine}/${entry.symbol} has no reused GTCEu textures`);
                for (const file of reused.files) {
                    assert.ok(fs.existsSync(path.join(textureFolder, file)), `${machine}/${entry.symbol} is missing ${file}`);
                }
                validatedReusedEntries++;
            } else {
                const available = fs.readdirSync(textureFolder).filter((name) => name.endsWith(".png")).length;
                assert.ok(
                    available >= entry.projectTextureCount,
                    `${machine}/${entry.symbol} expects ${entry.projectTextureCount} textures but folder has ${available}`,
                );
            }
        }
    }
}

assert.equal(counted, catalog.entryCount);
assert.equal(reuseManifest?.mappingCount, 10);
assert.equal(reuseManifest?.entryCount, 36);
assert.equal(validatedReusedEntries, expectedReusedEntries);
console.log(`Casing catalog valid: ${catalog.machineCount} machines, ${counted} entries.`);
