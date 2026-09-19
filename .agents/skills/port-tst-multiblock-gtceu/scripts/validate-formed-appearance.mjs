import fs from "node:fs";
import path from "node:path";
import process from "node:process";

const args = process.argv.slice(2);
const modelArg = args.find((arg) => !arg.startsWith("--"));
const uniformIndex = args.indexOf("--uniform-texture");
const mixedIndex = args.indexOf("--mixed-casing-renderer");

if (!modelArg || (uniformIndex < 0) === (mixedIndex < 0)) {
    console.error(
        "Usage: node validate-formed-appearance.mjs <machine-model.json> " +
        "(--uniform-texture <resource> | --mixed-casing-renderer <type>)",
    );
    process.exit(2);
}

const modeIndex = uniformIndex >= 0 ? uniformIndex : mixedIndex;
const expected = args[modeIndex + 1];
if (!expected || expected.startsWith("--")) {
    console.error("The selected formed-appearance mode requires a resource/type value");
    process.exit(2);
}

const modelPath = path.resolve(modelArg);
let model;
try {
    model = JSON.parse(fs.readFileSync(modelPath, "utf8"));
} catch (error) {
    console.error(`Cannot read machine model ${modelPath}: ${error.message}`);
    process.exit(1);
}

const errors = [];
if (uniformIndex >= 0) {
    if (model.texture_overrides?.all !== expected) {
        errors.push(`texture_overrides.all must be ${expected}`);
    }
    const formedVariants = Object.entries(model.variants ?? {})
        .filter(([state]) => state.includes("is_formed=true"));
    if (formedVariants.length === 0) {
        errors.push("model must declare at least one is_formed=true variant");
    }
    for (const [state, variant] of formedVariants) {
        if (variant?.model?.textures?.all !== expected) {
            errors.push(`${state} textures.all must be ${expected}`);
        }
    }
} else {
    const dynamicRenders = Array.isArray(model.dynamic_renders) ? model.dynamic_renders : [];
    if (!dynamicRenders.some((render) => render?.type === expected)) {
        errors.push(
            `mixed casing roles require dynamic renderer ${expected}; one global texture_overrides.all cannot represent them`,
        );
    }
}

if (errors.length > 0) {
    console.error(`Formed appearance invalid (${errors.length} error${errors.length === 1 ? "" : "s"}):`);
    for (const error of errors) console.error(`- ${error}`);
    process.exit(1);
}

console.log(`Formed appearance valid: ${modelPath}`);
