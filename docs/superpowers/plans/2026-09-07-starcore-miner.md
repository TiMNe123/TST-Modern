# StarcoreMiner Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port the audited TST StarcoreMiner to GTCEu Modern with exact geometry, generated dimension ore output, standard/wireless-hatch energy, recipes, assets, and tests.

**Architecture:** Use one compressed exact source-pattern holder, one machine with GTCEu custom runtime recipes, and one pure ore-pool helper. Validate the variable mining pipe after the static body forms. GTMThings compatibility is native because its wireless hatch registers `PartAbility.INPUT_ENERGY`; add no dependency or adapter.

**Tech Stack:** Java 17, Forge 47.3.0, Minecraft 1.20.1, GTCEu 7.4.0, LDLib 1.0.40.b, JUnit 5.

**Spec:** `.agents/skills/port-tst-multiblock-gtceu/contracts/starcore-miner.json`

## Global Constraints

- Work directly on `dev`; do not commit, branch, stage `AGENTS.md`, or stage `.agents/`.
- Source authority is local `D:/tmp`; preserve validated contract values.
- Reuse GTCEu abilities and GTMThings `INPUT_ENERGY`; add no new dependency.
- Keep production changes minimal and leave one runnable test per non-trivial seam.

---

### Task 1: Exact structure and ore-selection seams

**Files:**
- Create: `src/main/java/com/tstmodern/registry/machine/StarcoreMinerStructure.java`
- Create: `src/main/java/com/tstmodern/machine/logic/StarcoreMinerLogic.java`
- Test: `src/test/java/com/tstmodern/registry/machine/StarcoreMinerStructureTest.java`
- Test: `src/test/java/com/tstmodern/machine/logic/StarcoreMinerLogicTest.java`

**Interfaces:**
- Produces: `StarcoreMinerStructure.MAIN_AISLES`, dimensions/origin constants, middle-pipe cells.
- Produces: `StarcoreMinerLogic.boostedStackSize(int)`, weighted-entry normalization and selection.

- [ ] Write tests asserting 21×26×31, exact symbol counts, pipe counts, boost formula, weight aggregation, and boundary selection.
- [ ] Run focused tests and confirm they fail because the production classes do not exist.
- [ ] Generate the compressed source shape directly from audited `TST_StarcoreMiner.java`; implement only the tested pure logic.
- [ ] Run focused tests and confirm they pass.

### Task 2: Runtime machine, custom recipes, and registration

**Files:**
- Create: `src/main/java/com/tstmodern/machine/StarcoreMinerMachine.java`
- Create: `src/main/java/com/tstmodern/recipe/starcore/StarcoreMinerRecipeLogic.java`
- Create: `src/main/java/com/tstmodern/registry/machine/StarcoreMinerDefinition.java`
- Modify: `src/main/java/com/tstmodern/registry/TSTRecipeTypes.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/TSTMachineRegistry.java`
- Test: `src/test/java/com/tstmodern/machine/StarcoreMinerMachineTest.java`

**Interfaces:**
- Consumes: Task 1 structure and logic APIs.
- Produces: `TSTRecipeTypes.STARCORE_MINING`, `StarcoreMinerDefinition.MACHINE`, one custom 128-tick/MAX-EU recipe per cycle.

- [ ] Write tests for power abilities, output requirement, no overclock, future booster ID, and pipe validation transforms.
- [ ] Run focused tests and confirm expected failure.
- [ ] Implement the minimum machine, custom recipe source, exact body pattern, post-formation pipe validation, controller slot, and registry hooks.
- [ ] Run focused tests and confirm pass.

### Task 3: Casing, construction recipes, assets, and localization

**Files:**
- Modify: `src/main/java/com/tstmodern/registry/TSTBlocks.java`
- Create: `src/main/java/com/tstmodern/data/recipe/StarcoreMinerRecipes.java`
- Modify: `src/main/java/com/tstmodern/TSTModernGTAddon.java`
- Modify: `src/main/resources/assets/tstmodern/lang/en_us.json`
- Modify: `src/main/resources/assets/tstmodern/lang/zh_cn.json`
- Create: StarcoreMiner controller textures/models and Dimensional Bridge Casing blockstate/models/loot/texture under `src/main/resources`.
- Test: `src/test/java/com/tstmodern/registry/machine/StarcoreMinerContractTest.java`

**Interfaces:**
- Produces: `TSTBlocks.DIMENSIONAL_BRIDGE_CASING`, approved Assembly Line recipes, complete gameplay resources.

- [ ] Write the contract/resource test for registry IDs, recipes, model chain, loot, localization colors, and locked hashes.
- [ ] Run focused test and confirm expected failure.
- [ ] Add the casing, approved recipes, registration call, exact copied assets, model chain, loot, and bilingual text.
- [ ] Run focused test and confirm pass.

### Task 4: Verification and review

**Files:**
- Modify only files required by failures.

- [ ] Run machine contract, casing, texture source, and texture asset validators.
- [ ] Run `gradlew.bat test` and fix root causes.
- [ ] Run `gradlew.bat build` and fix root causes.
- [ ] Inspect `git diff`, confirm no unrelated changes and no staged local-agent files.
- [ ] Run a dedicated review pass against the approved contract; apply only verified fixes, then rerun all gates.
