import crypto from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(scriptDir, "../../../..");
const textureRoot = path.join(projectRoot, "src/main/resources/assets/tstmodern/textures");
const output = path.join(projectRoot, "build/generated/port-tst-multiblock-gtceu/texture-baseline.json");

function normalize(file) {
    return file.replaceAll("\\", "/");
}

function walk(root) {
    const found = [];
    for (const entry of fs.readdirSync(root, { withFileTypes: true })) {
        const file = path.join(root, entry.name);
        if (entry.isDirectory()) found.push(...walk(file));
        else if (file.toLowerCase().endsWith(".png")) found.push(file);
    }
    return found;
}

const files = walk(textureRoot).sort().map((file) => ({
    path: normalize(path.relative(projectRoot, file)),
    sha256: crypto.createHash("sha256").update(fs.readFileSync(file)).digest("hex"),
    size: fs.statSync(file).size,
    status: "LOCKED_EXISTING_BASELINE",
}));

const manifest = {
    schemaVersion: 1,
    policy: "LOCKED_EXISTING_BASELINE",
    generatedFrom: "src/main/resources/assets/tstmodern/textures/**/*.png",
    files,
};

fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${JSON.stringify(manifest, null, 2)}\n`);
console.log(`Staged ${files.length} production textures at ${output}`);
console.log("Do not promote this file without explicit user approval of the texture changes.");
