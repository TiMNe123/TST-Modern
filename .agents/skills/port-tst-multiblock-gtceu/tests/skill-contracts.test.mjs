import assert from "node:assert/strict";
import crypto from "node:crypto";
import fs from "node:fs";
import os from "node:os";
import path from "node:path";
import { spawnSync } from "node:child_process";
import { fileURLToPath } from "node:url";
import test from "node:test";

const testDir = path.dirname(fileURLToPath(import.meta.url));
const skillRoot = path.resolve(testDir, "..");
const machineValidator = path.join(skillRoot, "scripts", "validate-machine-contract.mjs");
const textureValidator = path.join(skillRoot, "scripts", "validate-texture-assets.mjs");
const appearanceValidator = path.join(skillRoot, "scripts", "validate-formed-appearance.mjs");
const localizationValidator = path.join(skillRoot, "scripts", "validate-localization.mjs");
const implementationStandard = path.join(skillRoot, "references", "port-implementation-standard.md");

function sha256(data) {
    return crypto.createHash("sha256").update(data).digest("hex");
}

function run(script, args) {
    return spawnSync(process.execPath, [script, ...args], { encoding: "utf8" });
}

function fixture() {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "tst-port-contract-"));
    const texture = Buffer.from("approved-test-texture");
    const texturePath = path.join(
        root,
        "src/main/resources/assets/tstmodern/textures/block/casings/test_casing.png",
    );
    fs.mkdirSync(path.dirname(texturePath), { recursive: true });
    fs.writeFileSync(texturePath, texture);

    const modelPath = path.join(root, "src/main/resources/assets/tstmodern/models/block/test_casing.json");
    fs.mkdirSync(path.dirname(modelPath), { recursive: true });
    fs.writeFileSync(modelPath, JSON.stringify({
        parent: "minecraft:block/cube_all",
        textures: { all: "tstmodern:block/casings/test_casing" },
    }));

    const contract = {
        schemaVersion: 1,
        machine: "TestMachine",
        auditStatus: "AUDIT_COMPLETE",
        branch: "dev",
        sourceEvidence: {
            controller: { path: "TST/TestMachine.java", symbol: "TestMachine", hash: "a".repeat(64) },
            inheritance: [],
            recipePool: { path: "TST/TestRecipes.java", symbol: "TestRecipes", hash: "b".repeat(64) },
        },
        structure: {
            dimensions: [3, 3, 3],
            sourceAxisOrder: ["width", "height", "depth"],
            targetAxisMap: { RIGHT: "width", DOWN: "height", BACK: "depth" },
            controller: { symbol: "~", count: 1, outwardFacing: "front" },
            symbols: [{
                symbol: "A",
                occurrences: 26,
                source: { registry: "gregtech:test", meta: "0", evidence: "TST/TestMachine.java#shape" },
                target: { registryOrPredicate: "tstmodern:test_casing", decision: "APPROVED_DESIGN" },
            }],
        },
        recipes: [{
            id: "test_machine",
            authority: "VERIFIED_SOURCE",
            source: "TST/TestRecipes.java#controller",
            recipeType: "assembler",
            duration: 100,
            eut: 128,
        }],
        textures: [{
            role: "casing:A",
            status: "VERIFIED_LOCAL",
            authority: "TST",
            resource: "tstmodern:block/casings/test_casing",
            localPath: "src/main/resources/assets/tstmodern/textures/block/casings/test_casing.png",
            sha256: sha256(texture),
            sourceEvidence: "TST/assets/test_casing.png",
        }],
        approvedDeviations: [],
        unresolved: [],
    };
    const contractPath = path.join(root, "test-machine.json");
    fs.writeFileSync(contractPath, JSON.stringify(contract, null, 2));

    const baseline = {
        schemaVersion: 1,
        policy: "LOCKED_EXISTING_BASELINE",
        files: [{
            path: "src/main/resources/assets/tstmodern/textures/block/casings/test_casing.png",
            sha256: sha256(texture),
            size: texture.length,
            status: "LOCKED_EXISTING_BASELINE",
        }],
    };
    const baselinePath = path.join(root, "texture-baseline.json");
    fs.writeFileSync(baselinePath, JSON.stringify(baseline, null, 2));
    return { root, contract, contractPath, baseline, baselinePath, texturePath };
}

test("a complete verified machine contract is accepted", () => {
    const f = fixture();
    const result = run(machineValidator, [f.contractPath, "--project-root", f.root]);
    assert.equal(result.status, 0, result.stderr || result.stdout);
});

test("an unresolved texture blocks the machine contract", () => {
    const f = fixture();
    f.contract.textures[0].status = "UNRESOLVED";
    fs.writeFileSync(f.contractPath, JSON.stringify(f.contract, null, 2));
    const result = run(machineValidator, [f.contractPath, "--project-root", f.root]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /UNRESOLVED/);
});

test("a fabricated local texture hash is rejected", () => {
    const f = fixture();
    f.contract.textures[0].sha256 = "0".repeat(64);
    fs.writeFileSync(f.contractPath, JSON.stringify(f.contract, null, 2));
    const result = run(machineValidator, [f.contractPath, "--project-root", f.root]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /hash mismatch/i);
});

test("the locked baseline accepts only declared production PNGs", () => {
    const f = fixture();
    let result = run(textureValidator, [f.baselinePath, "--project-root", f.root]);
    assert.equal(result.status, 0, result.stderr || result.stdout);

    fs.writeFileSync(path.join(path.dirname(f.texturePath), "invented.png"), "invented");
    result = run(textureValidator, [f.baselinePath, "--project-root", f.root]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /not present in the texture baseline/i);
});

test("a model cannot reference a missing local TSTModern texture", () => {
    const f = fixture();
    const modelPath = path.join(f.root, "src/main/resources/assets/tstmodern/models/block/test_casing.json");
    fs.writeFileSync(modelPath, JSON.stringify({
        parent: "minecraft:block/cube_all",
        textures: { all: "tstmodern:block/casings/does_not_exist" },
    }));
    const result = run(textureValidator, [f.baselinePath, "--project-root", f.root]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /missing local texture/i);
});

test("a mixed-casing formed model requires its declared part renderer", () => {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "tst-formed-appearance-"));
    const modelPath = path.join(root, "mixed.json");
    fs.writeFileSync(modelPath, JSON.stringify({
        texture_overrides: { all: "gtceu:block/casings/robust" },
        variants: {},
    }));

    let result = run(appearanceValidator, [
        modelPath,
        "--mixed-casing-renderer",
        "tstmodern:test_parts",
    ]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /dynamic renderer/i);

    fs.writeFileSync(modelPath, JSON.stringify({
        dynamic_renders: [{ type: "tstmodern:test_parts" }],
        texture_overrides: { all: "gtceu:block/casings/robust" },
        variants: {},
    }));
    result = run(appearanceValidator, [
        modelPath,
        "--mixed-casing-renderer",
        "tstmodern:test_parts",
    ]);
    assert.equal(result.status, 0, result.stderr || result.stdout);
});

test("a uniform formed model keeps controller and part casing textures aligned", () => {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "tst-formed-appearance-"));
    const modelPath = path.join(root, "uniform.json");
    const texture = "gtceu:block/casings/clean";
    fs.writeFileSync(modelPath, JSON.stringify({
        texture_overrides: { all: texture },
        variants: {
            "is_formed=true,recipe_logic_status=idle": {
                model: { textures: { all: texture } },
            },
            "is_formed=false,recipe_logic_status=idle": {
                model: { textures: { all: "gtceu:block/casings/unformed" } },
            },
        },
    }));

    const result = run(appearanceValidator, [modelPath, "--uniform-texture", texture]);
    assert.equal(result.status, 0, result.stderr || result.stdout);
});

test("localization validation rejects raw or missing names in either locale", () => {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "tst-localization-"));
    const javaRoot = path.join(root, "src/main/java/com/tstmodern");
    const langRoot = path.join(root, "src/main/resources/assets/tstmodern/lang");
    fs.mkdirSync(path.join(javaRoot, "registry/machine"), { recursive: true });
    fs.mkdirSync(path.join(javaRoot, "registry"), { recursive: true });
    fs.mkdirSync(langRoot, { recursive: true });
    const definition = path.join(javaRoot, "registry/machine/TestDefinition.java");
    const recipes = path.join(javaRoot, "TestRecipes.java");
    fs.writeFileSync(definition, '.multiblock("test_machine", Test::new)\nComponent.translatable("test.tooltip")');
    fs.writeFileSync(recipes, "TSTItems.TEST_PART TSTBlocks.TEST_CASING TSTRecipeTypes.TEST_RECIPES");
    fs.writeFileSync(path.join(javaRoot, "registry/TSTItems.java"),
        'TEST_PART = item("test_part");');
    fs.writeFileSync(path.join(javaRoot, "registry/TSTBlocks.java"),
        'TEST_CASING = casing("test_casing");');
    fs.writeFileSync(path.join(javaRoot, "registry/TSTRecipeTypes.java"),
        'TEST_RECIPES = register("test_recipes", MULTIBLOCK);');
    const english = {
        "block.tstmodern.test_machine": "Test Machine",
        "block.tstmodern.test_casing": "Test Casing",
        "item.tstmodern.test_part": "Test Part",
        "tstmodern.test_recipes": "Test Recipes",
        "recipetype.tstmodern.test_recipes": "Test Recipes",
        "gtceu.recipe_type.tstmodern.test_recipes": "Test Recipes",
        "test.tooltip": "Test tooltip",
    };
    fs.writeFileSync(path.join(langRoot, "en_us.json"), JSON.stringify(english));
    fs.writeFileSync(path.join(langRoot, "vi_vn.json"), JSON.stringify({ ...english,
        "item.tstmodern.test_part": "item.tstmodern.test_part",
    }));

    let result = run(localizationValidator, [definition, recipes, "--project-root", root]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /vi_vn.*test_part/i);

    fs.writeFileSync(path.join(langRoot, "vi_vn.json"), JSON.stringify(english));
    result = run(localizationValidator, [definition, recipes, "--project-root", root]);
    assert.equal(result.status, 0, result.stderr || result.stdout);
});

test("localization validation does not treat a later exception string as a registry id", () => {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "tst-localization-registry-"));
    const javaRoot = path.join(root, "src/main/java/com/tstmodern");
    const langRoot = path.join(root, "src/main/resources/assets/tstmodern/lang");
    fs.mkdirSync(path.join(javaRoot, "registry/machine"), { recursive: true });
    fs.mkdirSync(path.join(javaRoot, "registry"), { recursive: true });
    fs.mkdirSync(langRoot, { recursive: true });
    const definition = path.join(javaRoot, "registry/machine/TestDefinition.java");
    fs.writeFileSync(definition, '.multiblock("test_machine", Test::new)\nTSTBlocks.TIERED_CASING');
    fs.writeFileSync(path.join(javaRoot, "registry/TSTItems.java"), "");
    fs.writeFileSync(path.join(javaRoot, "registry/TSTRecipeTypes.java"), "");
    fs.writeFileSync(path.join(javaRoot, "registry/TSTBlocks.java"), `
        TIERED_CASING = componentAssemblyLineCasing(1);
        throw new IllegalArgumentException("not a registry id");
    `);
    const lang = { "block.tstmodern.test_machine": "Test Machine" };
    fs.writeFileSync(path.join(langRoot, "en_us.json"), JSON.stringify(lang));
    fs.writeFileSync(path.join(langRoot, "vi_vn.json"), JSON.stringify(lang));

    const result = run(localizationValidator, [definition, "--project-root", root]);
    assert.equal(result.status, 0, result.stderr || result.stdout);
});

test("the port standard requires Research Station for ZPM+ controllers and casings", () => {
    const standard = fs.readFileSync(implementationStandard, "utf8");
    assert.match(standard, /ZPM and above/i);
    assert.match(standard, /controller.*dedicated casing/i);
    assert.match(standard, /Research Station/i);
    assert.match(standard, /Data Orb.*casing/i);
    assert.match(standard, /Data Module.*controller/i);
    assert.match(standard, /unique.*researchId/i);
});

test("the port standard requires source isotope chains before invented recipes", () => {
    const standard = fs.readFileSync(implementationStandard, "utf8");
    assert.match(standard, /isotope/i);
    assert.match(standard, /TST\/GT5|TST\/GTNH/i);
    assert.match(standard, /before.*invent/i);
});
