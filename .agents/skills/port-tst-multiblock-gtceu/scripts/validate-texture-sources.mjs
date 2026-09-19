import crypto from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const defaultProjectRoot = path.resolve(scriptDir, "../../../..");
const args = process.argv.slice(2);
const rootIndex = args.indexOf("--project-root");
const projectRoot = path.resolve(rootIndex >= 0 ? args[rootIndex + 1] : defaultProjectRoot);
const positional = args.filter((arg, index) => !arg.startsWith("--") && args[index - 1] !== "--project-root");
const manifestPath = path.resolve(positional[0] ?? path.resolve(scriptDir, "../references/texture-source-baseline.json"));
const roots = [
    { prefix: "textures/", role: "CONTROLLER", path: path.join(projectRoot, "textures") },
    { prefix: "texture_block_casing/", role: "CASING", path: path.join(projectRoot, "texture_block_casing") },
];
const errors = [];

function normalize(file) {
    return file.replaceAll("\\", "/");
}

function isTextureAsset(file) {
    const lower = file.toLowerCase();
    return lower.endsWith(".png") || lower.endsWith(".png.mcmeta");
}

function walk(root) {
    if (!fs.existsSync(root)) return [];
    const files = [];
    for (const entry of fs.readdirSync(root, { withFileTypes: true })) {
        const file = path.join(root, entry.name);
        if (entry.isDirectory()) files.push(...walk(file));
        else if (isTextureAsset(file)) files.push(file);
    }
    return files;
}

function sha256(file) {
    return crypto.createHash("sha256").update(fs.readFileSync(file)).digest("hex");
}

let manifest;
try {
    manifest = JSON.parse(fs.readFileSync(manifestPath, "utf8").replace(/^\uFEFF/, ""));
} catch (error) {
    console.error(`Cannot read texture source baseline ${manifestPath}: ${error.message}`);
    process.exit(1);
}

if (manifest.schemaVersion !== 1) errors.push("schemaVersion must be 1");
if (manifest.policy !== "LOCKED_PORT_SOURCE_LIBRARY") errors.push("policy must be LOCKED_PORT_SOURCE_LIBRARY");
if (!Array.isArray(manifest.files)) errors.push("files must be an array");

const entries = new Map();
for (const [index, entry] of (manifest.files ?? []).entries()) {
    const field = `files[${index}]`;
    const relative = normalize(entry.path ?? "");
    const root = roots.find((candidate) => relative.startsWith(candidate.prefix));
    if (!root || !isTextureAsset(relative)) {
        errors.push(`${field}.path must be under textures/ or texture_block_casing/ and end in .png or .png.mcmeta`);
        continue;
    }
    if (entry.role !== root.role) errors.push(`${field}.role must be ${root.role} for ${relative}`);
    if (entries.has(relative)) errors.push(`${field}.path duplicates ${relative}`);
    entries.set(relative, entry);
    const file = path.resolve(projectRoot, relative);
    if (!fs.existsSync(file)) {
        errors.push(`${relative} is missing`);
        continue;
    }
    if (fs.statSync(file).size !== entry.size) errors.push(`${relative} size mismatch`);
    if (sha256(file) !== entry.sha256) errors.push(`${relative} hash mismatch`);
}

for (const root of roots) {
    if (!fs.existsSync(root.path)) errors.push(`${root.prefix.slice(0, -1)} source root is missing`);
    for (const file of walk(root.path)) {
        const relative = normalize(path.relative(projectRoot, file));
        if (!entries.has(relative)) errors.push(`${relative} is not present in the source baseline`);
    }
}

if (errors.length > 0) {
    console.error(`Texture source baseline invalid (${errors.length} error${errors.length === 1 ? "" : "s"}):`);
    for (const error of errors) console.error(`- ${error}`);
    process.exit(1);
}

const controllerCount = [...entries.values()].filter((entry) => entry.role === "CONTROLLER").length;
const casingCount = [...entries.values()].filter((entry) => entry.role === "CASING").length;
console.log(`Texture source baseline valid: ${controllerCount} controller and ${casingCount} casing assets locked.`);
