# BigBroArray Fidelity Fix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restore TST BigBroArray as a core-only-capable processor/generator array with zero to four optional rotated addons, source parallel progression, safe bus-based embedding, and explicit GTCEu Modern compatibility.

**Architecture:** Keep GTCEu's `FactoryBlockPattern` responsible only for the `11 × 8 × 11` core. Represent the source addon as immutable relative cells, transform it into four fixed placements, and scan one placement every five server ticks because the exact GTCEu 7.4.0 JAR has no named-substructure API. Put geometry, matching, aggregation, catalog, transfers, and modifier math in focused helpers; leave `BigBroArrayMachine` as lifecycle orchestration.

**Tech Stack:** Java 17, Minecraft Forge 1.20.1, GTCEu 7.4.0, LDLib 1.0.40.b, JEI 15.20.0.115, Gradle 8.8, JUnit Jupiter 5.10.2.

**Spec:** `docs/superpowers/specs/2026-08-26-big-bro-array-fidelity-fix-design.md`

## Global Constraints

- Core forms and runs alone; valid `addonCount` is `0..4`.
- Missing, incomplete, mixed-tier, or unloaded addons count as absent and never invalidate core.
- Effective parallel/frame/glass/coil tiers use minima; minimum coil intentionally fixes TST's last-addon overwrite bug.
- Preserve the five Parallelism Casing registry IDs and textures.
- Accept only the explicit catalog, including TSTModern Mass Fabricator.
- Load/unload through item buses transactionally; never use player offhand/inventory/drop.
- Processor and generator modifiers remain separate.
- Structure state is synchronized but not persisted; embedded state is versioned and persisted.
- Use saturating arithmetic. Apply every multiplier once.
- Each task gets at most three test/fix loops for the same failure.
- Automated success yields only `build validated; gameplay validation pending`.
- Preserve unrelated changes; stage task paths only and never use `git add .`.

---

## Task 1: Correct audit evidence and casing catalog

**Files:**
- Modify: `.agents/skills/port-tst-multiblock-gtceu/references/port-records/07_BigBroArray.md`
- Modify: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog.json`
- Create: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/BigBroArray.json`
- Create: `src/test/java/com/tstmodern/registry/machine/BigBroArrayPortRecordContractTest.java`

**Interfaces:** Produces the authoritative source/target/deviation record consumed by later validation.

- [ ] **Step 1: Write the failing record contract**

```java
assertAll(
    () -> assertTrue(record.contains("addonCount = 0..4")),
    () -> assertTrue(record.contains("45 × 26 × 45")),
    () -> assertTrue(record.contains("minimum coil tier")),
    () -> assertTrue(record.contains("Mass Fabricator")),
    () -> assertTrue(record.contains("audit complete; implementation pending")),
    () -> assertFalse(record.contains("Structure/mapping checks: Passed")));
```

- [ ] **Step 2: Verify red**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.machine.BigBroArrayPortRecordContractTest
```

- [ ] **Step 3: Rewrite the record**

Record core/addon dimensions and counts, four offsets/rotations, 0–4 behavior, minimum-tier aggregation, generator scope, catalog scope, recipe substitutions, exact GTCEu 7.4 limits, and honest status. Register `BigBroArray.json` in `machineFiles`; recompute catalog totals with the skill validator.

- [ ] **Step 4: Verify green and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.machine.BigBroArrayPortRecordContractTest
git add -- .agents/skills/port-tst-multiblock-gtceu/references/port-records/07_BigBroArray.md .agents/skills/port-tst-multiblock-gtceu/references/casing-catalog.json .agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/BigBroArray.json src/test/java/com/tstmodern/registry/machine/BigBroArrayPortRecordContractTest.java
git commit -m "docs: correct BigBroArray port contract"
```

---

## Task 2: Freeze source core, addon, rotations, and offsets

**Files:**
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayStructure.java`
- Modify: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`

**Interfaces:**
- Produces `CORE_AISLES`, `ADDON_SOURCE`, `ADDON_PLACEMENTS`.
- Produces `record RelativeCell(int right, int down, int back, char symbol)`.
- Produces `record AddonPlacement(int index, int quarterTurns, int offsetX, int offsetY, int offsetZ, List<RelativeCell> cells)`.

- [ ] **Step 1: Write exact failing geometry tests**

```java
assertPattern(CORE_AISLES, 11, 8, 11,
    "9ea00dcdd9c1e86f0031c7ef3a94072fe4b99ad59d813f1b6a44cc06083a2041");
assertPattern(ADDON_SOURCE, 17, 26, 15,
    "77d3efd59df29b979854c3b1f4c266d8b43a45daeaf82993e0d112481430f250");
```

Digest rows with `\n`, layers with `\f`, UTF-8. Assert core counts `~=1 A=97 B=40 C=97 D=25 E=57 F=4`; addon counts `G=134 H=44 I=42 J=64 K=86 L=530`; four placements ×900 cells; no overlap; full bounds `x=-22..22,y=-23..2,z=-21..23`; occupied total `3,921`.

- [ ] **Step 2: Verify red**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest
```

- [ ] **Step 3: Restore source matrices**

Copy `PATTERN_CORE` and `PATTERN_ADDON` from audited TST source, transpose once to target aisle order, rename addon symbols `A..F` to `G..L`, and remove prototype coil/parallel blocks from core.

- [ ] **Step 4: Generate four immutable placements**

```java
List.of(
    new PlacementSeed(0, 0, -6, 23,  6),
    new PlacementSeed(1, 1,  7, 23, -7),
    new PlacementSeed(2, 2, 22, 23,  6),
    new PlacementSeed(3, 3,  7, 23, 21));
```

Rotate occupied cells horizontally around the controller and freeze with `List.copyOf`.

- [ ] **Step 5: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/registry/machine/BigBroArrayStructure.java src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java
git commit -m "fix: restore BigBroArray source geometry"
```

---

## Task 3: Restore core predicates and uniform tier channels

**Files:**
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayTierRules.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayTierRulesTest.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`

**Interfaces:**
```java
int frameTier(Block block);
int glassTier(Block block);
int machineCasingTier(Block block);
int parallelCasingTier(Block block);
int coilTier(BlockState state);
int maxEmbeddedTier(int frameTier);
record CoreTiers(int frameTier, int glassTier, int machineCasingTier) {}
```

- [ ] **Step 1: Write failing tier tables**

Freeze frames `Titanium=1, TungstenSteel=2, NaquadahAlloy=3, Trinium=4, Neutronium=5, Tritanium=6`; unlocks `IV,LuV,ZPM,UV,UHV,MAX`. Add the approved `GENERATE_FRAME` compatibility flag to GTCEu 7.4 Trinium before material blocks are generated. Tempered Glass=`IV`, Fusion Glass=`MAX`; native machine casings LV→MAX; Parallelism Casings MK1→MK5. Unknown blocks return `-1`.

- [ ] **Step 2: Verify red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayTierRulesTest
```

- [ ] **Step 3: Implement core pattern**

```text
A uniform Tempered/Fusion Glass
B uniform six-level frame
C uniform native machine casing LV..MAX
D Robust Tungstensteel or Maintenance/Muffler/item/fluid IO
E Clean Stainless
F Clean Stainless or explicit input/output energy ability
```

Require exactly one Muffler and at least one item import/export. Store the first A/B/C tier in `PatternMatchContext`; reject later mismatches. Do not use broad `autoAbilities`.

- [ ] **Step 4: Store core state atomically**

`onStructureFormed()` assigns one validated `CoreTiers`. Invalidation clears structure-derived state but preserves embedded machines.

- [ ] **Step 5: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayTierRulesTest --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayTierRules.java src/test/java/com/tstmodern/machine/logic/BigBroArrayTierRulesTest.java src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java src/main/java/com/tstmodern/machine/BigBroArrayMachine.java src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java
git commit -m "fix: restore BigBroArray core tiers"
```

---

## Task 4: Match and aggregate zero to four addons

**Files:**
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonMatcher.java`
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonState.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonMatcherTest.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonStateTest.java`

**Interfaces:**
```java
interface BlockLookup { BlockState getLoadedState(BlockPos pos); }
enum Failure { NONE, UNLOADED, MISSING_BLOCK, WRONG_BLOCK, MIXED_TIER }
record AddonMatch(int index, boolean valid, int frameTier, int glassTier,
                  int parallelTier, int coilTier, Failure failure) {}
record BigBroArrayAddonState(int addonCount, int frameTier, int glassTier,
                             int parallelTier, int coilTier, int validMask) {
    static BigBroArrayAddonState aggregate(CoreTiers core, List<AddonMatch> matches);
}
AddonMatch match(AddonPlacement placement, BlockLookup lookup,
                 BlockPos controllerPos, Direction front,
                 Direction up, boolean flipped);
```

- [ ] **Step 1: Write failing matcher tests**

Test all four rotations. A wrong J/L block, unloaded/missing cell, or mixed G/H/I/K tier invalidates only that placement. Air outside the 900 occupied cells is ignored.

- [ ] **Step 2: Write failing aggregation tests**

```java
assertEquals(0, aggregate(core, List.of()).addonCount());
assertEquals(4, aggregate(core, fourValid).addonCount());
assertEquals(1, aggregate(core, fourValid).parallelTier()); // 5,3,4,1
assertEquals(2, aggregate(core, fourValid).coilTier());     // 4,2,5,3
assertEquals(0b1111, aggregate(core, fourValid).validMask());
```

- [ ] **Step 3: Implement exact-version coordinate lookup**

```java
BlockPos world = RelativeDirection.offsetPos(
    controllerPos, front, up, flipped,
    -cell.down(), -cell.right(), -cell.back());
```

Return invalid/unloaded before reading an absent chunk. Match G/H/I/K with tier rules; J/L with exact GT blocks. Fold valid matches with `Math.min`; no addon yields parallel/coil tier zero.

- [ ] **Step 4: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayAddonMatcherTest --tests com.tstmodern.machine.logic.BigBroArrayAddonStateTest
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonMatcher.java src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonState.java src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonMatcherTest.java src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonStateTest.java
git commit -m "feat: match optional BigBroArray addons"
```

---

## Task 5: Wire addon scanning and previews into GTCEu 7.4

**Files:**
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonScanner.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonScannerTest.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java`
- Modify: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`

**Interfaces:**
```java
BigBroArrayAddonScanner(List<AddonPlacement> placements,
    BigBroArrayAddonMatcher matcher,
    Supplier<CoreTiers> coreTiers,
    Consumer<BigBroArrayAddonState> onChanged);
void scanAllNow();
void scanNext();
void clear();
BigBroArrayAddonState state();
```

- [ ] **Step 1: Write failing cadence tests**

Assert `scanAllNow` checks 0–3; four `scanNext` calls check each once; unchanged scans do not notify; build/remove/upgrade notifies once; invalid core clears all addons.

- [ ] **Step 2: Implement staggered cache**

Cache four `AddonMatch` values and a cursor. While formed, call `scanNext` when `getOffsetTimer() % 5 == 0`: one 900-cell scan per five ticks, full refresh per twenty ticks.

- [ ] **Step 3: Subscribe exactly once**

```java
public void onLoad() {
    super.onLoad();
    addonScanSubscription = subscribeServerTick(this::scanAddonTick);
}
public void onUnload() {
    if (addonScanSubscription != null) unsubscribe(addonScanSubscription);
    addonScanSubscription = null;
    super.onUnload();
}
```

`onStructureFormed` scans all immediately. Aggregate changes reset recipe logic and sync fields, never embedded state.

- [ ] **Step 4: Add five `.shapeInfos` previews**

Expose one core-only preview plus four core-with-one-addon previews, so every preview retains the real controller position. The real `.pattern` remains core-only. Contract-test preview count `5` and absence of G–L in the core-only preview.

- [ ] **Step 5: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayAddonScannerTest --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonScanner.java src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonScannerTest.java src/main/java/com/tstmodern/machine/BigBroArrayMachine.java src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java
git commit -m "feat: track four BigBroArray addons"
```

---

## Task 6: Add the explicit embedded-machine catalog

**Files:**
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMode.java`
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineCatalog.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayMachineCatalogTest.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`

**Interfaces:**
```java
enum BigBroArrayMode { PROCESSOR, GENERATOR }
record Entry(ResourceLocation definitionId, MachineDefinition definition,
             GTRecipeType recipeType, int tier, BigBroArrayMode mode) {}
Optional<Entry> find(ItemStack stack);
Optional<Entry> find(ResourceLocation id);
```

- [ ] **Step 1: Write failing catalog tests**

Accept every spec-listed processor family and every `MassFabricatorDefinition.MACHINES` entry. Accept Combustion, Steam Turbine, and Gas Turbine as generators. Reject BigBroArray, another multiblock, and an unrelated recipe-bearing machine.

- [ ] **Step 2: Implement catalog**

Register concrete definition arrays and exact recipe types, key by registry ID, and throw on duplicates. Never whitelist by localized name or “has a recipe type”.

- [ ] **Step 3: Route dynamic recipe type**

`getRecipeType()` returns the resolved entry type or `DUMMY_RECIPES`; state changes call `recipeLogic.resetRecipeLogic()`.

- [ ] **Step 4: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayMachineCatalogTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayMode.java src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineCatalog.java src/test/java/com/tstmodern/machine/logic/BigBroArrayMachineCatalogTest.java src/main/java/com/tstmodern/machine/BigBroArrayMachine.java
git commit -m "fix: whitelist BigBroArray machines"
```

---

## Task 7: Make bus loading and unloading transactional

**Files:**
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedState.java`
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineTransfer.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedStateTest.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayMachineTransferTest.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`

**Interfaces:**
```java
record BigBroArrayEmbeddedState(int version, ResourceLocation definitionId,
    BigBroArrayMode mode, int tier, int count, CompoundTag itemTag) {}
record TransferStep(IRecipeHandler<ItemStack> handler, ItemStack stack) {}
record LoadPlan(BigBroArrayMachineCatalog.Entry entry, int count,
    ItemStack representative, List<TransferStep> extractions) {}
record UnloadPlan(List<TransferStep> insertions) {}
Optional<LoadPlan> planLoad(List<IRecipeHandler<ItemStack>> imports, int maxTier);
Optional<UnloadPlan> planUnload(BigBroArrayEmbeddedState state,
    List<IRecipeHandler<ItemStack>> exports);
```

- [ ] **Step 1: Write failing state tests**

Round-trip all fields; reject invalid counts/IDs/modes/tags; migrate current `embeddedMachineStack/embeddedCount/embeddedTier` saves.

- [ ] **Step 2: Write failing fake-handler tests**

Cover stable slot order, compatible aggregation, unrelated item preservation, extraction simulation failure, output-full rollback, split stacks, preserved NBT, and repeated clicks.

- [ ] **Step 3: Implement simulate-then-commit**

Use `IRecipeHandler.handleRecipe(..., true)` for the full plan before real operations. Never mutate `getContents()`. Clear state only after complete unload insertion.

- [ ] **Step 4: Replace offhand screwdriver flow**

Load from import buses; unload to output buses. Frame gates tier before extraction. Processor requires input energy; generator requires output energy. Failure changes nothing and sends a localized reason.

- [ ] **Step 5: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayEmbeddedStateTest --tests com.tstmodern.machine.logic.BigBroArrayMachineTransferTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedState.java src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineTransfer.java src/test/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedStateTest.java src/test/java/com/tstmodern/machine/logic/BigBroArrayMachineTransferTest.java src/main/java/com/tstmodern/machine/BigBroArrayMachine.java
git commit -m "fix: load BigBroArray machines through buses"
```

---

## Task 8: Correct processor parallel and overclock behavior

**Files:**
- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayLogic.java`
- Create: `src/main/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiers.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayLogicTest.java`
- Create: `src/test/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiersTest.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java`

**Interfaces:**
```java
record BigBroArrayRuntimeState(CoreTiers core,
    BigBroArrayAddonState addons,
    BigBroArrayEmbeddedState embedded,
    boolean inputEnergyAvailable,
    boolean outputEnergyAvailable) {}
long calculateMaxParallelism(int parallelTier, int addonCount);
long calculateParallelism(int machineCount, int parallelTier, int addonCount);
ModifierFunction processor(MetaMachine machine, GTRecipe recipe,
    BigBroArrayRuntimeState state);
```

- [ ] **Step 1: Expand formula tests**

```text
core only: max=64
casingMultiplier = tier>3 ? tier+6 : tier
tier<5 max = (64 << (tier*2 + (tier>3 ? 6 : 0))) * (1+addons)
tier>=5 max = ((2147483647 << casingMultiplier)/5) * (1+addons)
actual = min(machineCount << casingMultiplier, max)
```

Test addon counts 0–4, tiers 0–5, count boundaries, and saturation.

- [ ] **Step 2: Write failing modifier tests**

Prove embedded-tier rejection, input/output-limited parallel, one-time content scaling, duration `0.66^parallelTier`, EU `parallel × 0.9^coilTier`, no overflow, and overclock cap at embedded voltage.

- [ ] **Step 3: Implement modifier**

Apply structural cap, then `ParallelLogic.getParallelAmount`. Scale input/output once, set `.parallels(actual)`, and replace unrestricted `OC_NON_PERFECT` with embedded-tier-capped OC.

- [ ] **Step 4: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayLogicTest --tests com.tstmodern.machine.logic.BigBroArrayRecipeModifiersTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayLogic.java src/main/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiers.java src/test/java/com/tstmodern/machine/logic/BigBroArrayLogicTest.java src/test/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiersTest.java src/main/java/com/tstmodern/machine/BigBroArrayMachine.java src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java
git commit -m "fix: correct BigBroArray processor scaling"
```

---

## Task 9: Restore supported generator mode

**Files:**
- Modify: `src/main/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiers.java`
- Modify: `src/test/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiersTest.java`
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`

**Interfaces:** Produces:
```java
ModifierFunction generator(MetaMachine machine, GTRecipe recipe,
    BigBroArrayRuntimeState state);
```

- [ ] **Step 1: Write failing generator tests**

For Combustion/Steam/Gas, prove fuel and EU output scale by feasible parallel, output capacity limits admission, base duration remains, coil discount is absent, and input energy is unnecessary.

- [ ] **Step 2: Route by catalog mode**

```java
return switch (state.embedded().mode()) {
    case PROCESSOR -> processor(machine, recipe, state);
    case GENERATOR -> generator(machine, recipe, state);
};
```

Generator scales fuel/content and EU output once. Incompatible reformation stops logic but preserves embedded state for unloading.

- [ ] **Step 3: Verify and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.BigBroArrayRecipeModifiersTest
.\gradlew.bat compileJava
git add -- src/main/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiers.java src/test/java/com/tstmodern/machine/logic/BigBroArrayRecipeModifiersTest.java src/main/java/com/tstmodern/machine/BigBroArrayMachine.java
git commit -m "feat: restore BigBroArray generator mode"
```

---

## Task 10: Restore construction recipe progression

**Files:**
- Modify: `src/main/java/com/tstmodern/data/recipe/BigBroArrayRecipes.java`
- Create: `src/test/java/com/tstmodern/data/recipe/BigBroArrayRecipesContractTest.java`
- Modify: `.agents/skills/port-tst-multiblock-gtceu/references/port-records/07_BigBroArray.md`

**Interfaces:** Produces exactly six owned IDs: controller and MK1–MK5.

- [ ] **Step 1: Write failing progression/obtainability tests**

```java
assertRecipe(CONTROLLER, ASSEMBLER_RECIPES, GTValues.IV, 24_000);
assertRecipe(MK1, ASSEMBLER_RECIPES, 6_400, 3_000);
assertRecipe(MK2, ASSEMBLY_LINE_RECIPES, GTValues.ZPM, 12_000);
assertRecipe(MK3, ASSEMBLY_LINE_RECIPES, GTValues.UHV, 24_000);
assertRecipe(MK4, ASSEMBLY_LINE_RECIPES, GTValues.UIV, 24_000);
assertRecipe(MK5, ASSEMBLY_LINE_RECIPES, GTValues.UXV, 24_000);
```

Assert predecessor chain, MK2/MK3 scanner research, MK4/MK5 station research, and non-empty item/tag/fluid forms.

- [ ] **Step 2: Verify red**

```powershell
.\gradlew.bat test --tests com.tstmodern.data.recipe.BigBroArrayRecipesContractTest
```

- [ ] **Step 3: Rewrite from approved table**

Use spec maps/tiers/durations/component classes/counts/research/native fluids. Preserve stable IDs and record every legacy→Modern substitution. Empty tags fail.

- [ ] **Step 4: Verify, generate, and commit**

```powershell
.\gradlew.bat test --tests com.tstmodern.data.recipe.BigBroArrayRecipesContractTest
.\gradlew.bat runData
git add -- src/main/java/com/tstmodern/data/recipe/BigBroArrayRecipes.java src/test/java/com/tstmodern/data/recipe/BigBroArrayRecipesContractTest.java .agents/skills/port-tst-multiblock-gtceu/references/port-records/07_BigBroArray.md
git commit -m "fix: restore BigBroArray progression"
```

Stage generated files only if they are BigBroArray-owned.

---

## Task 11: Make UI truthful and close validation

**Files:**
- Modify: `src/main/java/com/tstmodern/machine/BigBroArrayMachine.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java`
- Modify: `src/main/resources/assets/tstmodern/lang/en_us.json`
- Modify: `src/main/resources/assets/tstmodern/lang/vi_vn.json`
- Create: `src/test/java/com/tstmodern/registry/machine/BigBroArrayResourceContractTest.java`
- Create: `docs/validation/big-bro-array-manual-checklist.md`
- Modify: `.agents/skills/port-tst-multiblock-gtceu/references/port-records/07_BigBroArray.md`

**Interfaces:** Consumes synchronized core/addon/embedded/runtime state and produces localized display plus evidence.

- [ ] **Step 1: Write failing resource contracts**

Require messages for addon count/mask, effective tiers, embedded name/mode/count/tier, actual/max parallel, processor speed/discount, missing ability, unsupported machine, full output, and load/unload. Reject pollution and unsupported-generator tooltip claims.

- [ ] **Step 2: Render live state**

Show `addonCount/4`, valid directions, minima, mode, machine count, actual/max parallel, and mode-specific multipliers. Preserve controller formed appearance and native hatch overlays.

- [ ] **Step 3: Run automated gates**

```powershell
.\gradlew.bat test --tests "com.tstmodern.*BigBroArray*"
.\gradlew.bat test
.\gradlew.bat build
```

- [ ] **Step 4: Run manual matrix**

Record: core-only form/run at 64; build/remove every rotation while formed; counts 0–4; incomplete/mixed/unloaded addon ignored; minimum tier aggregation; processor and Mass Fabricator runs; Combustion/Steam/Gas; output rollback; save/reload; break/reform; five previews; JEI; controller/hatch textures.

- [ ] **Step 5: Update status and commit**

Use `build validated; gameplay validation pending` until all manual rows pass. Keep Semi-Fluid, Naquadah, ASP/EMT Solar, TST coil overwrite, and pollution as deviations.

```powershell
git add -- src/main/java/com/tstmodern/machine/BigBroArrayMachine.java src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java src/main/resources/assets/tstmodern/lang/en_us.json src/main/resources/assets/tstmodern/lang/vi_vn.json src/test/java/com/tstmodern/registry/machine/BigBroArrayResourceContractTest.java .agents/skills/port-tst-multiblock-gtceu/references/port-records/07_BigBroArray.md
git commit -m "test: validate BigBroArray port"
```

The ignored manual checklist remains local unless the user changes the `docs/` Git policy.
