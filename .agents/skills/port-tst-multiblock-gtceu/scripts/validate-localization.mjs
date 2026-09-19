import fs from "node:fs";
import path from "node:path";
import process from "node:process";

const args = process.argv.slice(2);
const rootIndex = args.indexOf("--project-root");
const projectRoot = path.resolve(rootIndex >= 0 ? args[rootIndex + 1] : ".");
const sources = args
    .filter((arg, index) => !arg.startsWith("--") && args[index - 1] !== "--project-root")
    .map((file) => path.resolve(file));

if (sources.length === 0) {
    console.error("Usage: node validate-localization.mjs <Definition.java> [Recipes.java ...] [--project-root <path>]");
    process.exit(2);
}

const read = (file) => fs.readFileSync(file, "utf8");
const combined = sources.map(read).join("\n");
const javaRoot = path.join(projectRoot, "src/main/java/com/tstmodern/registry");
const registries = {
    TSTItems: read(path.join(javaRoot, "TSTItems.java")),
    TSTBlocks: read(path.join(javaRoot, "TSTBlocks.java")),
    TSTRecipeTypes: read(path.join(javaRoot, "TSTRecipeTypes.java")),
};
const required = new Set();

for (const match of combined.matchAll(/Component\.translatable\("([^"]+)"/g)) {
    required.add(match[1]);
}
for (const match of combined.matchAll(/\.multiblock\("([^"]+)"/g)) {
    required.add(`block.tstmodern.${match[1]}`);
}

function addRegistryKeys(registry, keyTemplates) {
    for (const match of combined.matchAll(new RegExp(`${registry}\\.([A-Z0-9_]+)`, "g"))) {
        const constant = match[1];
        const declaration = registries[registry].match(
            new RegExp(`\\b${constant}\\b\\s*=\\s*[a-zA-Z0-9_]+\\([^\"\\r\\n]*"([^"]+)"`));
        if (declaration) keyTemplates.forEach((template) => required.add(template(declaration[1])));
    }
}

addRegistryKeys("TSTItems", [(id) => `item.tstmodern.${id}`]);
addRegistryKeys("TSTBlocks", [(id) => `block.tstmodern.${id}`]);
addRegistryKeys("TSTRecipeTypes", [
    (id) => `tstmodern.${id}`,
    (id) => `recipetype.tstmodern.${id}`,
    (id) => `gtceu.recipe_type.tstmodern.${id}`,
]);

const errors = [];
for (const locale of ["en_us", "vi_vn"]) {
    const langPath = path.join(projectRoot, `src/main/resources/assets/tstmodern/lang/${locale}.json`);
    const lang = JSON.parse(read(langPath));
    for (const key of required) {
        if (typeof lang[key] !== "string" || !lang[key].trim() || lang[key] === key) {
            errors.push(`${locale} missing or raw localization: ${key}`);
        }
    }
}

if (errors.length) {
    console.error(`Localization invalid (${errors.length} errors):`);
    errors.forEach((error) => console.error(`- ${error}`));
    process.exit(1);
}
console.log(`Localization valid: ${required.size} keys in en_us and vi_vn.`);
