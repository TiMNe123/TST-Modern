# BigBroArray Fidelity Repair Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILLS: use `port-tst-multiblock-gtceu`, then `superpowers:executing-plans` to implement this plan task by task. Steps use checkbox syntax for progress tracking.

**Goal:** Complete BigBroArray with the approved TST behavior on GTCEu Modern 1.20.1 and eliminate the remaining parallel, overclock, transfer, persistence, catalog, recipe, and UI defects.

**Architecture:** `BigBroArrayMachine` coordinates lifecycle, abilities, state, and UI. `BigBroArrayLogic` owns pure arithmetic; the catalog is the only whitelist authority; `BigBroArrayEmbeddedState` is the only state authority; transfers use a two-phase transaction; processor and generator modifiers remain separate.

**Tech Stack:** Java 17, Forge 1.20.1, GTCEu 7.4.0, LDLib, JUnit 5, Gradle.

**Spec:** `.agents/skills/port-tst-multiblock-gtceu/references/port-implementation-standard.md`, original TST/GT5 source, and exact GTCEu 7.4 source/API.

## Global Constraints

- Worktree: `C:\Users\mtien\IdeaProjects\TST-Modern\.worktrees\big-bro-array-fidelity-fix`
- Branch: `codex/big-bro-array-fidelity-fix`
- Reviewed HEAD: `531a1ed24b55b390c75f10dfef3ae6e38d672142`
- Do not change the controller research recipe; it is an approved Modern deviation.
- Do not invent machine IDs, recipe types, material forms, or registry mappings that do not exist in GTCEu 7.4.
- Preserve embedded state when the structure becomes invalid, the frame tier is too low, the energy ability is wrong, or the catalog ID is stale.
- Generator mode must not receive processor overclocking, speed bonuses, or coil EU discounts.
- Every calculation that can exceed `int` or `long` must saturate instead of wrapping.
- Attempt the same test/environment failure at most three times. If the third attempt fails identically, record the evidence and continue with the next independent task.
- A successful build may only be reported as `build validated; gameplay validation pending` until in-game and JEI validation are complete.
- `.agents` and `docs` remain local-only under the current repository policy; do not commit them unless that policy changes.

## Review Checkpoint — 2026-08-27

### Current Validation

- [x] Focused BigBroArray suite: 14 suites, 83 tests, 0 failures, errors, or skips.
- [x] Full `gradlew.bat build`: passed.
- [ ] The current green tests are not yet trustworthy: the recipe test catches `Throwable` and passes, the runtime-gate test duplicates production logic, and the catalog test never queries the registry.
- [ ] `git diff --check HEAD` is not clean; `BigBroArrayMachine.java` still contains trailing whitespace.
- [ ] Gameplay, real save migration, and JEI have not been validated.

### P1 Blockers Before Commit

- [ ] Migrate the real legacy keys: `embeddedMachineStack`, `embeddedCount`, `embeddedTier`, and `embeddedMode`.
- [ ] Synchronize embedded state to clients; a stale ID must not unload through `ItemStack.EMPTY` and then clear state.
- [ ] Complete load/unload as two-phase transactions with simulation/commit mismatch tests.
- [ ] Build the catalog from concrete `MachineDefinition` arrays and validate every entry against `GTRegistries.MACHINES`.
- [ ] Make controller and MK1–MK5 recipe contract tests assert exact map, EU, duration, research, items, and fluids; registration exceptions must fail the test.
- [ ] Add the required circuits and fusion/endgame/MAX-stage components to MK4 and MK5.

### Production Work Already Correct

- [x] MK5 parallel arithmetic uses `long` and saturating helpers.
- [x] The definition no longer applies global `GTRecipeModifiers.OC_NON_PERFECT`.
- [x] Processor mode uses embedded-tier overclocking.
- [x] Generator mode does not use overclocking, processor speed, or coil discounts.
- [x] Runtime frame/energy mode gates exist in production code; integration coverage is still missing.

---

## Task 1: Clean the Scope and Lock the Baseline

**Files:**

- Modify: `.gitignore`
- Modify: `src/main/java/com/tstmodern/TSTModern.java`
- Review/revert: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeAdapter.java`
- Review/revert: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeIndex.java`
- Review/revert: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerSpecialRecipes.java`
- Review/revert: `src/main/java/com/tstmodern/registry/TSTRecipeTypes.java`

- [ ] Capture `git status --short`, `git diff --stat HEAD`, and hashes/snippets for the approved WIP baseline.
- [ ] Remove the empty `TSTModern.commonSetup()` method and its listener.
- [ ] Remove or isolate Disassembler deprecation suppressions that are unrelated to BigBroArray.
- [ ] Decide the `/src/test/` policy explicitly: restore the previous ignore rule, or intentionally track the tests and state that policy change in the commit message.
- [ ] Run `git diff --check HEAD` and remove every trailing-whitespace finding.
- [ ] Run the focused baseline tests.

```powershell
.\gradlew.bat test --tests "com.tstmodern.*BigBroArray*" --rerun-tasks
```

**Acceptance:** the BigBroArray commit contains no empty lifecycle hook, unrelated suppressions, or accidental test-policy change.

---

## Task 2: Lock Parallel Arithmetic

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayLogic.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayLogicTest.java`

- [x] Use `shiftLeftSaturating((long) Integer.MAX_VALUE, multiplier)` for MK5.
- [x] Use `saturatingMultiply(infinity / 5L, 1L + addonCount)`.
- [x] Cover tiers `0..5`, addon counts `0..4`, the no-addon cap of 64, monotonicity, and non-negative results.
- [ ] Add exact expected MK5 values instead of only asserting that results exceed one billion.
- [ ] Run the focused test class.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayLogicTest" --rerun-tasks
```

**Acceptance:** MK5 never becomes negative or wraps, and exact TST formula boundaries are frozen by tests.

---

## Task 3: Complete Processor and Generator Modifier Tests

**Files:**

- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java`
- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiers.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiersTest.java`
- Modify: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`

- [x] Register only `BigBroArrayMachine::recipeModifier` in the machine definition.
- [x] Use `OverclockingLogic.NON_PERFECT_OVERCLOCK.getModifier(machine, recipe, GTValues.V[embeddedTier])` for processor mode.
- [x] Compose embedded overclocking before array parallel, discount, and speed effects.
- [x] Keep generator duration multiplier at `1.0` and apply parallel only.
- [ ] Create behavioral recipe fixtures and apply the real modifier; null-input tests are insufficient.
- [ ] Assert that processor mode rejects base EU/t above the embedded tier.
- [ ] Assert processor input, output, EU/t, and duration after overclocking and parallelization.
- [ ] Assert generator duration is unchanged when coil tier or parallel-casing tier changes.
- [ ] Assert the definition source contains no second/global overclock modifier.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayRecipeModifiersTest" --rerun-tasks
.\gradlew.bat test --tests "com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest" --rerun-tasks
```

**Acceptance:** tests execute production modifiers and prove that every factor is applied exactly once.

---

## Task 4: Implement Two-Phase Transfer Transactions

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineTransfer.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayMachineTransferTest.java`

**Interfaces:**

- The transfer core consumes ordered `IRecipeHandler<ItemStack>` handlers.
- The machine adapter obtains item handlers from import/export `RecipeHandlerList` instances.
- Slot snapshots may use `IItemHandlerModifiable`, but snapshot/restore does not replace the simulation phase.

- [ ] Create `LoadPlan` with the catalog entry, copied NBT, total count, ordered consumptions, and snapshots.
- [ ] Create `UnloadPlan` with ordered productions and snapshots.
- [ ] Load phase A calls `handleRecipe(IO.IN, null, stacks, true)` and continues only when every remainder is empty.
- [ ] Unload phase A calls `handleRecipe(IO.OUT, null, stacks, true)` and continues only when every remainder is empty.
- [ ] Phase B repeats the operation with `simulate=false` and verifies exact item, count, and NBT—not slot count alone.
- [ ] Roll back every touched slot when commit differs from simulation; publish or clear state only after full success.
- [ ] Reject an empty machine template; inserting an empty stack must never count as accepted output.
- [ ] Use a dedicated overflow failure key instead of `empty_bus`.
- [ ] Add fake-handler tests for simulation success/commit failure, same-count wrong item, wrong NBT, full output, split stacks, stable ordering, and repeated clicks.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayMachineTransferTest" --rerun-tasks
```

**Acceptance:** every failure path is all-or-nothing; no machine can be duplicated, lost, or cleared incorrectly.

---

## Task 5: Versioned Persistence, Real Migration, and Client Sync

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedState.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedStateTest.java`
- Modify: `src/test/java/com/tstmodern/machine/BigBroArrayMachinePersistenceTest.java`

**State contract:** `version`, `definitionId`, catalog-resolved `mode/tier`, `count`, and copied `itemTag`. State is authoritative; client mirrors are derived data only.

- [ ] Route every mutation through `setEmbeddedState(BigBroArrayEmbeddedState)` so cache, synced mirrors, and recipe logic update together.
- [ ] Persist one `BigBroArrayEmbeddedState` compound through `saveCustomPersistedData` and `loadCustomPersistedData`.
- [ ] Read the real legacy keys: `embeddedMachineStack`, `embeddedCount`, `embeddedTier`, and `embeddedMode`.
- [ ] Extract the definition ID from the serialized legacy `MetaMachineItem`; do not use the invented `embeddedMachineId` key.
- [ ] Build the migration fixture from the exact old `@Persisted` field serialization used at the reviewed HEAD.
- [ ] On catalog hit, derive mode and tier from the catalog instead of trusting saved values.
- [ ] On catalog miss with a surviving `GTRegistries.MACHINES` definition, keep the invalid operational status but reconstruct the stack so the player can unload it.
- [ ] On complete registry miss, preserve state, report stale ID, and do not invoke transfer with an empty template.
- [ ] Restore client/description synchronization through derived `@DescSynced` mirrors or a custom managed-field serializer.
- [ ] Cover load, unload, reload, client sync, corrupt tags, stale/future IDs, and structure invalidation.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayEmbeddedStateTest" --rerun-tasks
.\gradlew.bat test --tests "com.tstmodern.machine.BigBroArrayMachinePersistenceTest" --rerun-tasks
```

**Acceptance:** old saves migrate without losing machines, stale state cannot be cleared accidentally, and clients receive the correct machine name, count, tier, and mode.

---

## Task 6: Integrate Runtime Frame and Energy Ability Validation

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/main/java/com/tstmodern/machine/logic/OperationalStatus.java`
- Modify: `src/test/java/com/tstmodern/machine/BigBroArrayMachineRuntimeGateTest.java`

- [x] Processor mode requires input energy, substation input, or input laser.
- [x] Generator mode requires output energy, substation output, or output laser.
- [x] Embedded tier is compared with the current effective frame tier at runtime.
- [ ] Extract a pure production evaluator that accepts state, frame tier, and ability flags; the machine must call this evaluator.
- [ ] Make tests call the production evaluator and remove the copied evaluator from the test class.
- [ ] Cover rebuilding with a lower frame, wrong-direction energy hatches, reduced addon minima, and successful unload while blocked.
- [ ] Reset recipe logic whenever structure, addon, or part changes alter operational status.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.BigBroArrayMachineRuntimeGateTest" --rerun-tasks
```

**Acceptance:** rebuilding cannot bypass frame or ability gates, and tests do not duplicate production logic.

---

## Task 7: Build the Catalog from Concrete MachineDefinitions

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineCatalog.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayMachineCatalogTest.java`

- [ ] Add `registerDefinitions(map, recipeType, mode, MachineDefinition[] definitions)`.
- [ ] Register GTCEu processors from exact `GTMachines.*` arrays and skip null entries.
- [ ] Register generators only from `GTMachines.COMBUSTION`, `GTMachines.STEAM_TURBINE`, and `GTMachines.GAS_TURBINE` definitions that actually exist.
- [ ] Register TSTModern Mass Fabricators from the project's concrete definitions/array.
- [ ] Read IDs from `definition.getId()` and delete the namespace/tier/name ID-building helper.
- [ ] For every entry, assert that `GTRegistries.MACHINES.get(id)` exists and is the same registered definition.
- [ ] Cover duplicate IDs, recipe type, tier, mode, unsupported processors, and unsupported generators.

**Modern compatibility decision:** TST Fluid Extractor is represented by GTCEu 7.4 Extractor semantics because no separate Fluid Extractor machine/type exists. Recycler remains unsupported until the project registers a real machine and recipe type.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayMachineCatalogTest" --rerun-tasks
```

**Acceptance:** the catalog contains no synthesized IDs or dead registry entries.

---

## Task 8: Restore Exact Construction Recipe Progression

**Files:**

- Modify: `src/main/java/com/tstmodern/data/recipe/BigBroArrayRecipes.java`
- Rewrite: `src/test/java/com/tstmodern/data/recipe/BigBroArrayRecipesContractTest.java`

| Output | Map/tier | Duration | Predecessor/research | Required contract |
|---|---:|---:|---|---|
| Controller | Assembler/IV | 24,000 t | none | 16 Data Orbs; IV arms, emitters, and field generators; verified IV superconducting wire; 24,576 mB Titanium |
| MK1 | Assembler/6,400 EU/t | 3,000 t | none | Robust Tungstensteel base; IV components, wires, and circuits; 9,216 mB Soldering Alloy |
| MK2 | Assembly Line/ZPM | 12,000 t | MK1 + 288,000 t scanner | ZPM components/circuits; Stable Titanium-compatible base; Soldering Alloy + Naquadah Alloy |
| MK3 | Assembly Line/UHV | 24,000 t | MK2 + 576,000 t scanner | UHV components/circuits; Clean Stainless-compatible base; Soldering Alloy + Naquadah Alloy + Europium |
| MK4 | Researchable AL/UIV | 24,000 t | 16 MK3 + station | UIV components/circuits; fusion/endgame native components; Neutronium-family fluids |
| MK5 | Researchable AL/UXV | 24,000 t | 16 MK4 + station | UXV components/circuits; MAX-stage components; Tritanium-family fluids |

- [ ] Verify the exact registered material/form for “IV superconducting wire”; the current `wireGtHex + Samarium` choice lacks sufficient provenance.
- [ ] Add the required ZPM, UHV, UIV, and UXV circuits.
- [ ] Add MK4 fusion/endgame components and MK5 MAX-stage components.
- [ ] Document every Modern substitute beside its recipe and freeze it in an exact contract test.
- [ ] Remove `catch (Throwable)`; registration exceptions must fail with their original cause.
- [ ] Capture registrations and assert exact ID, map, EU/t, duration, output, predecessor, research, items, and fluids.
- [ ] Assert that every ingredient and fluid form is non-empty.

```powershell
.\gradlew.bat test --tests "com.tstmodern.data.recipe.BigBroArrayRecipesContractTest" --rerun-tasks
.\gradlew.bat runData
```

**Acceptance:** all six recipes match the table, and tests cannot pass when registration throws or an ingredient is empty.

---

## Task 9: Make UI and Localization Truthful

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineTransfer.java`
- Modify: `src/main/resources/assets/tstmodern/lang/en_us.json`
- Modify: `src/main/resources/assets/tstmodern/lang/vi_vn.json`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayLocalizationContractTest.java`

- [ ] Compute structural maximum with `BigBroArrayLogic.calculateMaxParallelism(addonCount, parallelCasingTier)`.
- [ ] Never pass a fake addon count of `64` to `calculateParallelism`.
- [ ] Display all four addon directions/placement bits.
- [ ] Do not calculate invalid count as `addonCount - bitCount(validMask)` because `addonCount` already counts only valid addons.
- [ ] Display effective frame, glass, parallel, and coil tiers.
- [ ] Display embedded name, mode, count, tier, actual/max parallel, and operational failure reason.
- [ ] Display processor duration/speed and coil discount; state explicitly that generator mode receives neither bonus.
- [ ] Pass `Component.translatable(modeKey)` as a component instead of calling `.getString()` early.
- [ ] Supply both `%d` count and `%s` machine name to `status.loaded` and `status.unloaded`, or change the format consistently in Java, `en_us`, and `vi_vn`.
- [ ] Add dedicated keys for overflow and transaction mismatch instead of misusing `empty_bus` or `export_bus_full`.
- [ ] Parse both JSON files in tests, validate every emitted key, and verify format-argument counts for each call.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayLocalizationContractTest" --rerun-tasks
```

**Acceptance:** no raw keys or missing format arguments remain, and every displayed value reflects real runtime state.

---

## Task 10: Synchronize the Local Audit and Catalog Record

- [ ] Generate/review a BigBroArray casing catalog entry through the skill scripts; never edit aggregate counts manually.
- [ ] Preserve source-authority addon counts: `G=134`, `H=44`, `I=42`, `J=64`, `K=530`, `L=86`.
- [ ] Preserve roles `K=glass` and `L=clean casing`; do not follow the old swapped design counts.
- [ ] Record the Fluid Extractor/Recycler compatibility decision.
- [ ] Record exact persistence, catalog, recipe, and generator deviations.
- [ ] Run the catalog scripts from the main workspace where local audit assets exist.

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\build-casing-catalog.mjs
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-casing-catalog.mjs
```

**Acceptance:** the validator includes BigBroArray, and the port record never claims an aggregate count that disagrees with the validator.

---

## Task 11: Final Validation

**Current checkpoint:** focused tests and the full build passed on 2026-08-27, but all commands must be rerun after fixing the P1 blockers. Do not reuse the current green result.

- [ ] Run focused BigBroArray tests.
- [ ] Run the full test suite.
- [ ] Run the full build.
- [ ] Run `git diff --check HEAD`.
- [ ] Review changed-file warnings and remove temporary diagnostics.

```powershell
.\gradlew.bat test --tests "com.tstmodern.*BigBroArray*" --rerun-tasks
.\gradlew.bat test --rerun-tasks
.\gradlew.bat build
git diff --check HEAD
```

### Manual Gameplay Matrix

- [ ] Form the core without addons and confirm the cap of 64.
- [ ] Attach one through four addons in all directions and confirm mask, directions, and effective minima.
- [ ] Test MK1 through MK5, especially that MK5 never becomes negative or zero.
- [ ] Test processor recipes below, at, and above the embedded machine tier.
- [ ] Rebuild with a lower frame after loading: processing stops, state remains, and unload still works.
- [ ] Test processor input-energy success/failure and generator output-energy success/failure.
- [ ] Confirm generator duration is unaffected by coil or parallel-casing speed tier.
- [ ] Test multiple buses, nearly full buses, split stacks, NBT, and repeated load/unload clicks.
- [ ] Save/reload versioned state and a verified legacy fixture.
- [ ] Invalidate and reform the structure without losing embedded state.
- [ ] Verify controller and MK1–MK5 JEI recipes, ingredients, fluids, and research.
- [ ] Verify that neither `en_us` nor `vi_vn` displays raw keys or unfilled `%s` tokens.

## Definition of Done

- MK5 arithmetic is correct and exact boundary tests pass.
- Transfer is all-or-nothing, including simulation/commit mismatch.
- Legacy saves migrate correctly, and stale state cannot be cleared through empty-template unload.
- One state authority exists with derived client synchronization.
- Frame and ability gates are covered through production-path tests.
- Processor overclocks to embedded tier; generator receives no overclock, processor speed, or coil discount.
- The catalog contains only concrete registered definitions.
- Controller and MK1–MK5 have exact recipe contract tests and non-empty forms.
- UI/localization correctly reports mode, directions, minima, parallel, and failure reasons.
- No `catch (Throwable)`-and-pass test, copied test evaluator, or catalog test that only queries its own map remains.
- `git diff --check HEAD` is clean, and the commit contains no unrelated lifecycle hook or suppressions.
- Automated validation passes; if gameplay has not been tested, report exactly `build validated; gameplay validation pending`.
