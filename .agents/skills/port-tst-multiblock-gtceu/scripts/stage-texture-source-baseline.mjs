import crypto from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(scriptDir, "../../../..");
const output = path.join(projectRoot, "build/generated/port-tst-multiblock-gtceu/texture-source-baseline.json");
const roots = [
    { path: path.join(projectRoot, "textures"), role: "CONTROLLER" },
    { path: path.join(projectRoot, "texture_block_casing"), role: "CASING" },
];

function normalize(file) {
    return file.replaceAll("\\", "/");
}

function isTextureAsset(file) {
    const lower = file.toLowerCase();
    return lower.endsWith(".png") || lower.endsWith(".png.mcmeta");
}

function walk(root) {
    const files = [];
    for (const entry of fs.readdirSync(root, { withFileTypes: true })) {
        const file = path.join(root, entry.name);
        if (entry.isDirectory()) files.push(...walk(file));
        else if (isTextureAsset(file)) files.push(file);
    }
    return files;
}

const files = roots.flatMap((root) => walk(root.path).map((file) => ({
    path: normalize(path.relative(projectRoot, file)),
    role: root.role,
    sha256: crypto.createHash("sha256").update(fs.readFileSync(file)).digest("hex"),
    size: fs.statSync(file).size,
}))).sort((a, b) => a.path.localeCompare(b.path));

const manifest = { schemaVersion: 1, policy: "LOCKED_PORT_SOURCE_LIBRARY", files };
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${JSON.stringify(manifest, null, 2)}\n`);
console.log(`Staged ${files.length} source texture assets at ${output}`);
console.log("Promote only after the user approves changes to the source libraries.");
