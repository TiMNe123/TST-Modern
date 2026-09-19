# Incompact Cyclotron Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a source-faithful, usable Incompact Cyclotron port with exact structure, visuals, processing rules, and minimal closed particle progression.

**Architecture:** Keep geometry in a frozen structure class, arithmetic in a pure logic class, registration in the definition/registry classes, and recipes in one machine-owned class. Reuse the existing BigBroArray dynamic-render pattern only for mixed E/F formed-part appearances.

**Tech Stack:** Java 17, Forge 47.3.0, GTCEu 7.4.0, LDLib 1.0.40.b, JEI 15.20.0.115, JUnit 5, Gradle.

**Spec:** `docs/superpowers/specs/2026-09-01-incompact-cyclotron-design.md`

## Global Constraints

- Work directly on branch `dev`; do not create a worktree, merge, or push.
- Preserve TST geometry and locked texture bytes exactly.
- Implement only the approved GT++ dependency closure.
- Production behavior requires a witnessed red-green test cycle.
- Stop after three substantially identical verification failures.

---

### Task 1: Freeze structure and modifier contracts

**Files:** Create `IncompactCyclotronLogicTest.java`, `IncompactCyclotronStructureTest.java`, `IncompactCyclotronLogic.java`, and `IncompactCyclotronStructure.java` in their matching test/main packages.

**Interfaces:** Produce `parallelLimit(int)`, `durationMultiplier()`, `euMultiplier()`, `PATTERN_AISLES`, and `symbolAt(int,int,int)`.

- [ ] Write literal tests for `256 + hatch`, saturation, `0.5`, `1.6`, dimensions, symbol totals, and controller coordinate.
- [ ] Run them and verify failure because production classes are absent.
- [ ] Add minimal pure logic and the exact transposed source structure.
- [ ] Re-run and require zero failures.

### Task 2: Register content, map, and recipes

**Files:** Create `TSTItems.java`, `IncompactCyclotronRecipes.java`, and its contract test; modify `TSTBlocks.java`, `TSTRecipeTypes.java`, `TSTModern.java`, and `TSTModernGTAddon.java`.

**Interfaces:** Produce five particle items, three casing blocks, `CYCLOTRON_RECIPES`, and `IncompactCyclotronRecipes.register(Consumer<FinishedRecipe>)`.

- [ ] Write a failing test for four operational IDs, four construction IDs, IO, chances, durations, EU/t, and one registration call.
- [ ] Run it and verify the expected missing-registration failure.
- [ ] Add only approved registrations and recipes.
- [ ] Re-run focused and existing recipe tests.

### Task 3: Register machine and exact pattern

**Files:** Create `IncompactCyclotronMachine.java`, `IncompactCyclotronDefinition.java`, and its contract test; modify `TSTMachineRegistry.java`.

**Interfaces:** Consume Tasks 1-2; produce `IncompactCyclotronDefinition.MACHINE` and the runtime recipe modifier.

- [ ] Write a failing test for axes, predicates, absent maintenance/muffler, modifiers, and single initialization.
- [ ] Run it and verify missing production definitions.
- [ ] Implement the minimal workable machine and exact pattern.
- [ ] Re-run focused machine tests.

### Task 4: Add locked resources and mixed renderer

**Files:** Create `IncompactCyclotronPartRender.java` and resource contract test; modify `TSTClient.java`; add this machine's blockstates, models, textures, languages, and loot tables.

**Interfaces:** Consume `IncompactCyclotronStructure.symbolAt`; produce dynamic render type `tstmodern:incompact_cyclotron_parts` and complete resources.

- [ ] Write a failing resource/hash/renderer-registration test.
- [ ] Run it and verify missing resources.
- [ ] Copy locked PNGs and add minimal JSON/localization/loot plus renderer registration.
- [ ] Run resource and formed-appearance validators.

### Task 5: Verify and record evidence

**Files:** Modify `docs/port-records/incompact-cyclotron.md`.

- [ ] Run machine-contract, casing-catalog, and locked-texture validators.
- [ ] Run focused tests and full Gradle test/build.
- [ ] Start the client and confirm world entry or record the exact blocker within the three-failure cap.
- [ ] Inspect the diff for unrelated changes and record evidence without merging or pushing.
