import assert from "node:assert/strict";
import crypto from "node:crypto";
import fs from "node:fs";
import os from "node:os";
import path from "node:path";
import { spawnSync } from "node:child_process";
import { fileURLToPath } from "node:url";
import test from "node:test";

const testDir = path.dirname(fileURLToPath(import.meta.url));
const validator = path.resolve(testDir, "../scripts/validate-texture-sources.mjs");

function sha256(data) {
    return crypto.createHash("sha256").update(data).digest("hex");
}

function fixture() {
    const root = fs.mkdtempSync(path.join(os.tmpdir(), "tst-texture-sources-"));
    const controller = Buffer.from("controller-source");
    const casing = Buffer.from("casing-source");
    const controllerPath = path.join(root, "textures/01_TestMachine/controller.png");
    const casingPath = path.join(root, "texture_block_casing/01_TestMachine/A_test_casing.png");
    fs.mkdirSync(path.dirname(controllerPath), { recursive: true });
    fs.mkdirSync(path.dirname(casingPath), { recursive: true });
    fs.writeFileSync(controllerPath, controller);
    fs.writeFileSync(casingPath, casing);
    const manifest = {
        schemaVersion: 1,
        policy: "LOCKED_PORT_SOURCE_LIBRARY",
        files: [
            { path: "textures/01_TestMachine/controller.png", role: "CONTROLLER", sha256: sha256(controller), size: controller.length },
            { path: "texture_block_casing/01_TestMachine/A_test_casing.png", role: "CASING", sha256: sha256(casing), size: casing.length },
        ],
    };
    const manifestPath = path.join(root, "texture-source-baseline.json");
    fs.writeFileSync(manifestPath, JSON.stringify(manifest));
    return { root, manifestPath, controllerPath, casingPath };
}

function run(fixture) {
    return spawnSync(process.execPath, [validator, fixture.manifestPath, "--project-root", fixture.root], { encoding: "utf8" });
}

test("accepts the locked controller and casing source libraries", () => {
    const f = fixture();
    const result = run(f);
    assert.equal(result.status, 0, result.stderr || result.stdout);
});

test("rejects a modified controller source", () => {
    const f = fixture();
    fs.writeFileSync(f.controllerPath, "fabricated-controller");
    const result = run(f);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /hash mismatch/i);
});

test("rejects an unlisted casing source", () => {
    const f = fixture();
    fs.writeFileSync(path.join(path.dirname(f.casingPath), "invented.png"), "invented");
    const result = run(f);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /not present in the source baseline/i);
});

test("rejects a source role that does not match its root", () => {
    const f = fixture();
    const manifest = JSON.parse(fs.readFileSync(f.manifestPath, "utf8"));
    manifest.files[0].role = "CASING";
    fs.writeFileSync(f.manifestPath, JSON.stringify(manifest));
    const result = run(f);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /role must be CONTROLLER/i);
});
