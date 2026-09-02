# BigBroArray Fidelity Repair Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILLS: use `port-tst-multiblock-gtceu`, then `superpowers:executing-plans` to implement this plan task by task. Steps use checkbox syntax for progress tracking.

**Goal:** Complete BigBroArray with the approved TST behavior on GTCEu Modern 1.20.1 and eliminate the remaining parallel, overclock, transfer, persistence, catalog, recipe, and UI defects.

**Architecture:** `BigBroArrayMachine` coordinates lifecycle, abilities, state, and UI. `BigBroArrayLogic` owns pure arithmetic; the catalog is the only whitelist authority; `BigBroArrayEmbeddedState` is the only state authority; transfers use a two-phase transaction; processor and generator modifiers remain separate.

**Tech Stack:** Java 17, Forge 1.20.1, GTCEu 7.4.0, LDLib, JUnit 5, Gradle.

**Spec:** `.agents/skills/port-tst-multiblock-gtceu/references/port-implementation-standard.md`, original TST/GT5 source, and exact GTCEu 7.4 source/API.

## Global Constraints

- Workspace: `C:\Users\mtien\TST-Modern` (direct checkout; no worktree)
- Branch: `dev`
- Repair baseline HEAD: `e1fbdb0ed3e420c3fac02a87894cf211967088fd`
- Do not change the controller research recipe; it is an approved Modern deviation.
- Do not invent machine IDs, recipe types, material forms, or registry mappings that do not exist in GTCEu 7.4.
- Approved exception: add `GENERATE_FRAME` to GTCEu 7.4 Trinium during `MaterialEvent`; this makes BigBroArray's fourth mapped frame level a registered UV-unlock frame instead of an invalid synthetic lookup.
- Preserve embedded state when the structure becomes invalid, the frame tier is too low, the energy ability is wrong, or the catalog ID is stale.
- Generator mode must not receive processor overclocking, speed bonuses, or coil EU discounts.
- Every calculation that can exceed `int` or `long` must saturate instead of wrapping.
- Attempt the same test/environment failure at most three times. If the third attempt fails identically, record the evidence and continue with the next independent task.
- A successful build may only be reported as `build validated; gameplay validation pending` until in-game and JEI validation are complete.
- `.agents` and `docs` remain local-only under the current repository policy; do not commit them unless that policy changes.

## Review Checkpoint — 2026-08-30

### Current Validation

- [x] Focused BigBroArray suite: 13 suites, 83 tests, 0 failures, errors, or skips (2026-08-30).
- [x] Full suite through `gradlew.bat build`: 28 suites, 148 tests, 0 failures, errors, or skips (2026-08-30; includes the Trinium frame compatibility contract).
- [x] Full `gradlew.bat build`: passed (2026-08-30).
- [x] The green tests are verified and trustworthy: recipe AST contract test validates definitions without swallowing errors, runtime-gate test shares pure production evaluator, and catalog tests validate registered mappings.
- [x] `git diff --check HEAD` is clean; trailing whitespace removed.
- [ ] Gameplay, real save migration, and JEI have not been validated.

### P1 Blockers Before Commit

- [x] Migrate the real legacy keys: `embeddedMachineStack`, `embeddedCount`, `embeddedTier`, and `embeddedMode`.
- [x] Synchronize embedded state to clients; a stale ID must not unload through `ItemStack.EMPTY` and then clear state.
- [x] Complete load/unload as two-phase transactions with simulation/commit mismatch tests.
- [x] Build the catalog from concrete `MachineDefinition` arrays and validate every entry against `GTRegistries.MACHINES`.
- [ ] Replace the remaining AST recipe contract with captured registrations that assert exact map, EU, duration, research, items, and fluids.
- [x] Add the required circuits and fusion/endgame/MAX-stage components to MK4 and MK5.

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
- [x] Add exact expected MK5 values instead of only asserting that results exceed one billion.
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

**Exact-version API decision:** GTCEu 7.4 item recipe handlers operate on `Ingredient`, not
`IRecipeHandler<ItemStack>`. Machine identity and NBT therefore use ordered
`IItemHandlerModifiable` slot transactions with the Forge simulation flag; every simulated and
committed stack is checked by exact item, count, and NBT.

- [x] Build the load plan from the catalog entry, copied NBT, total count, ordered slot consumptions, and snapshots.
- [x] Build the unload plan from ordered slot productions and snapshots.
- [x] Phase A simulates every ordered slot extraction/insertion and accepts only exact compatible results.
- [x] Phase B repeats with `simulate=false` and verifies exact item, count, and NBT—not slot count alone.
- [x] Roll back every touched slot when commit differs from simulation; publish or clear state only after full success.
- [x] Reject an empty machine template; inserting an empty stack never counts as accepted output.
- [x] Use dedicated overflow, empty-template, and transaction-mismatch failure keys.
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

- [x] Route every mutation through `setEmbeddedState(BigBroArrayEmbeddedState)` so cache, synced mirrors, and recipe logic update together.
- [x] Persist one `BigBroArrayEmbeddedState` compound through `saveCustomPersistedData` and `loadCustomPersistedData`.
- [x] Read the real legacy serialized `embeddedMachineStack` and `embeddedCount`; catalog resolution replaces untrusted legacy tier/mode.
- [x] Extract the definition ID from the serialized legacy `MetaMachineItem`; the invented `embeddedMachineId` key is rejected.
- [ ] Build the migration fixture from the exact old `@Persisted` field serialization used at the reviewed HEAD.
- [x] On catalog hit, derive mode and tier from the catalog instead of trusting saved values.
- [x] On catalog miss with a surviving `GTRegistries.MACHINES` definition, keep the invalid operational status but reconstruct the stack so the player can unload it.
- [x] On complete registry miss, preserve state, report stale ID, and do not invoke transfer with an empty template.
- [x] Restore client/description synchronization through derived `@DescSynced` mirrors; only the versioned compound is persisted.
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
- [x] Extract a pure production evaluator that accepts state, frame tier, and ability flags; the machine calls this evaluator.
- [x] Make tests call the production evaluator and remove the copied evaluator from the test class.
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

- [x] Build entries only from concrete definitions supplied by `GTRegistries.MACHINES`.
- [x] Whitelist the exact TST processor families while skipping every absent definition.
- [x] Register only concrete LV–HV combustion, steam-turbine, and gas-turbine definitions.
- [x] Register only concrete TSTModern UHV–MAX Mass Fabricator definitions.
- [x] Read IDs from `definition.getId()`; no catalog entry is synthesized.
- [x] For every runtime entry, require `GTRegistries.MACHINES.get(id)` to be the identical definition.
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

- [x] Use GTCEu's IV superconductor `SamariumIronArsenicOxide` in the source `wireGtSingle` form.
- [x] Add the required ZPM, UHV, UIV, UXV, and MAX circuit stages.
- [x] Add MK4 native fusion/endgame components and MK5 native MAX-stage components.
- [x] Document Modern substitutions beside the recipe and in the machine contract.
- [x] Registration has no `catch (Throwable)`; exceptions propagate with their original cause.
- [ ] Capture registrations and assert exact ID, map, EU/t, duration, output, predecessor, research, items, and fluids.
- [ ] Assert that every ingredient and fluid form is non-empty.

```powershell
.\gradlew.bat test --tests "com.tstmodern.data.recipe.BigBroArrayRecipesContractTest" --rerun-tasks
.\gradlew.bat runData
```

`runData` reached TST Modern recipe registration but then stopped in the unrelated
Mega Stone Breaker blockstate generator because
`gtceu:block/machine/template/cube_all/sided` was unavailable. Do not count this as
a BigBroArray recipe failure, and do not repeat the same environment failure more
than the three-attempt policy permits. Runtime recipe capture, JEI, and gameplay
validation therefore remain open.

**Acceptance:** all six recipes match the table, and tests cannot pass when registration throws or an ingredient is empty.

---

## Task 9: Make UI and Localization Truthful

**Files:**

- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineTransfer.java`
- Modify: `src/main/resources/assets/tstmodern/lang/en_us.json`
- Modify: `src/main/resources/assets/tstmodern/lang/vi_vn.json`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayLocalizationContractTest.java`

- [x] Compute structural maximum with `BigBroArrayLogic.calculateMaxParallelism(addonCount, parallelCasingTier)`.
- [x] Never pass a fake addon count of `64` to `calculateParallelism`.
- [x] Display all four addon rotations/placement bits.
- [x] Do not calculate a fabricated invalid count from `addonCount` and `validMask`.
- [x] Display effective frame, glass, parallel, and coil tiers.
- [x] Display embedded name, mode, count, tier, actual/max parallel, and operational failure reason.
- [x] Display processor duration/speed and coil discount; state explicitly that generator mode receives neither bonus.
- [x] Pass translated mode and machine-name components without flattening them server-side.
- [x] Supply both `%d` count and `%s` localized machine name to `status.loaded` and `status.unloaded`.
- [x] Use dedicated keys for overflow, empty-template, and transaction mismatch.
- [ ] Parse both JSON files in tests, validate every emitted key, and verify format-argument counts for each call.

```powershell
.\gradlew.bat test --tests "com.tstmodern.machine.logic.BigBroArrayLocalizationContractTest" --rerun-tasks
```

**Acceptance:** no raw keys or missing format arguments remain, and every displayed value reflects real runtime state.

---

## Task 10: Synchronize the Local Audit and Catalog Record

- [x] Keep BigBroArray as a manual port record because it is absent from the 25-machine spreadsheet audit; never edit aggregate counts manually.
- [x] Add and validate `.agents/skills/port-tst-multiblock-gtceu/contracts/big-bro-array.json`.
- [x] Preserve source-authority addon counts: `G=134`, `H=44`, `I=42`, `J=64`, `K=530`, `L=86`.
- [x] Preserve roles `K=glass` and `L=clean casing`; do not follow the old swapped design counts.
- [x] Record the Fluid Extractor/Recycler compatibility decision.
- [x] Record exact persistence, catalog, recipe, generator, and pollution deviations.
- [x] Run the global catalog/texture scripts from the main workspace: machine contract valid; casing catalog 25 machines/408 entries; source baseline 155 controller/687 casing assets; production baseline 107 PNGs (2026-08-30).

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\build-casing-catalog.mjs
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-casing-catalog.mjs
```

**Acceptance:** the validated manual machine contract covers BigBroArray without modifying the spreadsheet-derived aggregate counts.

---

## Task 11: Final Validation

**Current checkpoint:** final automated validation was rerun after the P1 fixes on 2026-08-30. Gameplay, JEI, and the unrelated blocked datagen path remain manual gates.

- [x] Run focused BigBroArray tests: 13 suites/83 tests, all passing.
- [x] Run the full test suite: 27 suites/147 tests, all passing through the full build.
- [x] Run the full build.
- [x] Run `git diff --check HEAD`.
- [x] Review changed-file warnings and remove temporary diagnostics; only repository LF-to-CRLF notices remain.

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
