import fs from "node:fs";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const skillRoot = path.resolve(scriptDir, "..");
const machine = process.argv[2];

if (!machine) {
    console.error("Usage: node create-machine-contract.mjs <MachineName>");
    process.exit(2);
}

const slug = machine
    .replace(/([a-z0-9])([A-Z])/g, "$1-$2")
    .replace(/[^a-zA-Z0-9]+/g, "-")
    .replace(/^-|-$/g, "")
    .toLowerCase();
const output = path.join(skillRoot, "contracts", `${slug}.json`);

if (fs.existsSync(output)) {
    console.error(`Contract already exists: ${output}`);
    process.exit(1);
}

const contract = {
    schemaVersion: 1,
    machine,
    auditStatus: "IN_PROGRESS",
    branch: "dev",
    sourceEvidence: {
        controller: { path: "", symbol: "", hash: "" },
        inheritance: [],
        recipePool: { path: "", symbol: "", hash: "" },
    },
    structure: {
        dimensions: [0, 0, 0],
        sourceAxisOrder: ["", "", ""],
        targetAxisMap: { RIGHT: "", DOWN: "", BACK: "" },
        controller: { symbol: "", count: 0, outwardFacing: "" },
        symbols: [],
    },
    recipes: [],
    textures: [],
    approvedDeviations: [],
    unresolved: [
        "Source controller, inheritance, structure, recipes, casings, and textures require audit evidence.",
    ],
};

fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${JSON.stringify(contract, null, 2)}\n`);
console.log(`Created blocking audit contract: ${output}`);
console.log("Complete the evidence, set AUDIT_COMPLETE, clear unresolved, validate, and obtain user approval before implementation.");
