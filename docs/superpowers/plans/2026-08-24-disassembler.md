# Disassembler Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port TST's Large Disassembler as an exact `27 × 23 × 28`, no-energy GTCEu Modern multiblock that dynamically reverses eligible Assembly Line and Assembler recipes, returns consumed item/fluid ingredients, and uses fourteen uniform tier casings from LV through MAX.

**Architecture:** Keep GTCEu-facing registration and machine code thin. Put immutable reverse-recipe descriptions, eligibility/tier rules, deterministic source ordering, reload-aware indexing, saturating aggregation, and custom recipe synthesis under `recipe/disassembler`. Keep the literal structure in a dedicated class, validate the B-casing tier at formation time, use only the three approved abilities, and own all construction recipes/assets in the Disassembler module.

**Tech Stack:** Java 17, Minecraft Forge 1.20.1, GTCEu 7.4.0, LDLib 1.0.40.b, JEI 15.20.0.115, Gradle 8.8, JUnit Jupiter 5.10.2.

**Spec:** `docs/superpowers/specs/2026-08-24-disassembler-design.md`

## Global Constraints

- The plan/spec/port-record files under `docs/` are local execution records and remain ignored by Git.
- Preserve every pre-existing Mega Tree Farm multi-face texture/model change. Never use `git add .`; every commit stages only the Disassembler paths listed in its task.
- `TSTModernGTAddon.requiresHighTier()` must return `true`. This is the approved supported GTCEu addon hook; do not call `GTCEuAPI.initializeHighTier()` and do not rewrite the player's `gtceu.yaml`.
- Preserve TST's no-energy identity: extend `WorkableMultiblockMachine`, do not register EU input for the recipe type, add no energy hatch, and add no electric overclocking recipe modifier.
- The only structure abilities are item import, item export, and fluid export at the eight I positions, with at least one of each. Do not add fluid import, Energy, Maintenance, Muffler, Parallel Control, or generic auto abilities.
- Assembly Line source priority is `0`; Assembler source priority is `1`; ties within one source are resolved by lexical source recipe ID.
- Recipe output matching ignores NBT. Ingredient alternatives always choose the first representative deterministically. `Content.chance() == 0` is non-consumable and is not returned.
- When an approved legacy ingredient names a dense plate that GTCEu 7.4.0 does not generate, use exactly nine obtainable regular plates of the same material per dense plate and record the semantic conversion. Never register an empty `plateDense` tag ingredient.
- Overflow is deliberately voided. Arithmetic must saturate before conversion to GTCEu's integer content amounts.
- The normalized structure must retain SHA-256 `f133bc1aac5518bba6b594eca4ef9b76a84c27cdb0ae5a8b884f29888642201a` and counts `~=1 A=36 B=66 C=4 D=60 E=56 F=92 G=318 H=4 I=8 J=84`.
- Each behavioral task follows red → minimal implementation → green → focused commit.
- A successful headless build permits only the status `build validated; gameplay validation pending`. Claim gameplay validation only after forming and running the machine in a real client/world.

---

## Task 1: Enable GTCEu High-Tier through the addon contract

**Files:**

- Modify: `src/main/java/com/tstmodern/TSTModernGTAddon.java`
- Create: `src/test/java/com/tstmodern/TSTModernGTAddonContractTest.java`

- [ ] **Step 1: Write the failing source contract test**

Create a test that reads `src/main/java/com/tstmodern/TSTModernGTAddon.java` and asserts all three conditions:

```java
assertTrue(source.contains("boolean requiresHighTier()"));
assertTrue(source.contains("return true;"));
assertFalse(source.contains("initializeHighTier("));
```

The same test must assert the addon source contains neither `ConfigHolder` nor `gtceu.yaml`, so the supported lifecycle hook is the only activation path. Do not assert the user's current configuration value; it may legitimately be either value outside this port.

- [ ] **Step 2: Run the focused test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.TSTModernGTAddonContractTest
```

Expected: failure because `requiresHighTier()` is absent.

- [ ] **Step 3: Implement the official addon hook**

Add exactly this override to `TSTModernGTAddon`:

```java
@Override
public boolean requiresHighTier() {
    return true;
}
```

Do not add direct GTCEu initialization calls.

- [ ] **Step 4: Run the focused test and compile**

```powershell
.\gradlew.bat test --tests com.tstmodern.TSTModernGTAddonContractTest
.\gradlew.bat compileJava
```

Expected: both commands finish with `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit only the addon contract**

```powershell
git add -- src/main/java/com/tstmodern/TSTModernGTAddon.java src/test/java/com/tstmodern/TSTModernGTAddonContractTest.java
git commit -m "feat: enable GTCEu high tier for disassembler"
```

---

## Task 2: Register the sixteen dedicated casing blocks and complete their assets

**Files:**

- Modify: `src/main/java/com/tstmodern/registry/TSTBlocks.java`
- Create: `src/test/java/com/tstmodern/registry/DisassemblerBlockRegistryContractTest.java`
- Create: `src/main/resources/assets/tstmodern/blockstates/component_assembly_line_casing_{lv,mv,hv,ev,iv,luv,zpm,uv,uhv,uev,uiv,umv,uxv,max}.json`
- Create: `src/main/resources/assets/tstmodern/models/block/casings/component_assembly_line_casing_{lv,mv,hv,ev,iv,luv,zpm,uv,uhv,uev,uiv,umv,uxv,max}.json`
- Create: `src/main/resources/assets/tstmodern/models/item/component_assembly_line_casing_{lv,mv,hv,ev,iv,luv,zpm,uv,uhv,uev,uiv,umv,uxv,max}.json`
- Create: `src/main/resources/assets/tstmodern/textures/block/casings/component_assembly_line_casing_{lv,mv,hv,ev,iv,luv,zpm,uv,uhv,uev,uiv,umv,uxv,max}.png`
- Create: corresponding blockstate/model/item-model/texture files for `molecular_casing` and `hollow_casing`
- Create: `src/main/resources/data/tstmodern/loot_tables/blocks/component_assembly_line_casing_{lv,mv,hv,ev,iv,luv,zpm,uv,uhv,uev,uiv,umv,uxv,max}.json`
- Create: `src/main/resources/data/tstmodern/loot_tables/blocks/molecular_casing.json`
- Create: `src/main/resources/data/tstmodern/loot_tables/blocks/hollow_casing.json`

- [ ] **Step 1: Write a failing registry/resource completeness test**

The test must assert:

- `COMPONENT_ASSEMBLY_LINE_CASINGS` contains exactly fourteen registry objects in LV→MAX order;
- `componentAssemblyLineCasing(1)` and `(14)` return the first and last objects;
- tiers `0` and `15` throw `IllegalArgumentException`;
- the method's ordered lookup contract maps list entry `i` to tier `i + 1`; because plain JUnit does not bootstrap Forge registries, execute the live `1..14` mapping assertions in the later client smoke test and assert `componentAssemblyLineTier(Blocks.STONE) == 0` here;
- every one of the sixteen IDs has blockstate, block model, item model, PNG, and self-drop loot JSON.

- [ ] **Step 2: Run the focused test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.DisassemblerBlockRegistryContractTest
```

Expected: compilation failure because the registry API does not exist.

- [ ] **Step 3: Add the exact registry API**

Add the fourteen `RegistryObject<Block>` declarations, the ordered immutable list, and the two lookup methods:

```java
public static RegistryObject<Block> componentAssemblyLineCasing(int tier)
public static int componentAssemblyLineTier(Block block)
```

The integer contract is `1=LV` through `14=MAX`; validate the tier before indexing. Add `MOLECULAR_CASING` and `HOLLOW_CASING` using the existing `casing(String)` helper.

- [ ] **Step 4: Copy and rename the audited textures without resampling**

Map the fourteen B textures in meta order `m0..m13` from `texture_block_casing/05_Disassembler/` to the fourteen LV→MAX target names. Map:

- `G_L149_O1_sBlockCasingsTT_m4_887cb85_EM_CASING.png` → `molecular_casing.png`
- `H_L150_O1_sBlockCasingsTT_m8_afb75b8_EM_HOLLOW.png` → `hollow_casing.png`

Use byte-for-byte copies. Do not recolor or scale these approved audit assets.

- [ ] **Step 5: Add deterministic JSON resources**

Each blockstate points to `tstmodern:block/casings/<id>`. Each block model uses `minecraft:block/cube_all`. Each item model parents the block model. Each loot table drops exactly its own block under `minecraft:survives_explosion`.

- [ ] **Step 6: Run the focused test and resource validation**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.DisassemblerBlockRegistryContractTest
.\gradlew.bat processResources
```

Expected: both commands succeed and the test confirms all sixteen complete resource sets.

- [ ] **Step 7: Commit only dedicated casing registration/assets**

```powershell
git add -- src/main/java/com/tstmodern/registry/TSTBlocks.java src/test/java/com/tstmodern/registry/DisassemblerBlockRegistryContractTest.java src/main/resources/assets/tstmodern/blockstates/component_assembly_line_casing_*.json src/main/resources/assets/tstmodern/blockstates/molecular_casing.json src/main/resources/assets/tstmodern/blockstates/hollow_casing.json src/main/resources/assets/tstmodern/models/block/casings/component_assembly_line_casing_*.json src/main/resources/assets/tstmodern/models/block/casings/molecular_casing.json src/main/resources/assets/tstmodern/models/block/casings/hollow_casing.json src/main/resources/assets/tstmodern/models/item/component_assembly_line_casing_*.json src/main/resources/assets/tstmodern/models/item/molecular_casing.json src/main/resources/assets/tstmodern/models/item/hollow_casing.json src/main/resources/assets/tstmodern/textures/block/casings/component_assembly_line_casing_*.png src/main/resources/assets/tstmodern/textures/block/casings/molecular_casing.png src/main/resources/assets/tstmodern/textures/block/casings/hollow_casing.png src/main/resources/data/tstmodern/loot_tables/blocks/component_assembly_line_casing_*.json src/main/resources/data/tstmodern/loot_tables/blocks/molecular_casing.json src/main/resources/data/tstmodern/loot_tables/blocks/hollow_casing.json
git commit -m "feat: add disassembler tier casings"
```

---

## Task 3: Implement the immutable reverse-recipe domain and arithmetic policy

**Files:**

- Create: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeDescriptor.java`
- Create: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipePolicy.java`
- Create: `src/test/java/com/tstmodern/recipe/disassembler/DisassemblerRecipePolicyTest.java`

**Interfaces:**

```java
public record DisassemblerRecipeDescriptor(
        ResourceLocation sourceId,
        int sourcePriority,
        int recipeTier,
        Item outputItem,
        int outputAmount,
        List<ReturnedItem> returnedItems,
        List<ReturnedFluid> returnedFluids) {
    public record ReturnedItem(Item item, int amount) {}
    public record ReturnedFluid(Fluid fluid, int amount) {}
}
```

```java
public final class DisassemblerRecipePolicy {
    public record AggregatePlan(
            Map<Item, Integer> consumedItems,
            Map<Item, Integer> returnedItems,
            Map<Fluid, Integer> returnedFluids,
            long processed,
            int durationTicks) {}

    public static boolean tierAllows(int casingTier, int recipeTier)
    public static Optional<AggregatePlan> aggregate(
            Map<Item, Long> available,
            int casingTier,
            Function<Item, Optional<DisassemblerRecipeDescriptor>> lookup)
    public static long saturatingAdd(long left, long right)
    public static long saturatingMultiply(long left, long right)
    public static int durationTicks(long processed, int casingTier)
}
```

- [ ] **Step 1: Write failing domain tests**

Cover all approved rules:

- descriptor rejects nulls and non-positive output/return amounts and copies every list;
- casing tiers `1..13` accept recipe tiers through `casingTier + 1`, while tier `14` accepts every non-negative recipe tier;
- batch is `available / sourceOutputAmount` with integer floor;
- insufficient remainder is untouched and not included in `consumedItems`;
- multiple eligible item types aggregate in one run;
- disallowed/unknown items remain untouched;
- item/fluid returns multiply by batch and merge by identity;
- duration is `max(1, processed / (4 * casingTier)) * 100` ticks using integer floor;
- duration clamps to `Integer.MAX_VALUE` after saturating the multiplication by 100;
- addition and multiplication saturate at `Long.MAX_VALUE`, then returned/consumed map amounts clamp to `Integer.MAX_VALUE`;
- empty or wholly invalid input returns `Optional.empty()`.

- [ ] **Step 2: Run the policy test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.recipe.disassembler.DisassemblerRecipePolicyTest
```

Expected: compilation failure because the domain classes are absent.

- [ ] **Step 3: Implement minimal dependency-light policy code**

Use `IdentityHashMap` while aggregating Minecraft registry objects, then expose unmodifiable copied maps. Validate `casingTier` is `1..14`. Calculate the divisor as `4L * casingTier`; do not use floating-point arithmetic.

- [ ] **Step 4: Run the policy suite**

```powershell
.\gradlew.bat test --tests com.tstmodern.recipe.disassembler.DisassemblerRecipePolicyTest
```

Expected: all arithmetic, batching, tier, immutability, and saturation cases pass.

- [ ] **Step 5: Commit the reverse domain**

```powershell
git add -- src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeDescriptor.java src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipePolicy.java src/test/java/com/tstmodern/recipe/disassembler/DisassemblerRecipePolicyTest.java
git commit -m "feat: add disassembler reverse recipe policy"
```

---

## Task 4: Adapt eligible GTCEu recipes and enforce the blacklist

**Files:**

- Create: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeAdapter.java`
- Create: `src/main/resources/data/tstmodern/tags/items/disassembler_blacklist.json`
- Create: `src/test/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeAdapterTest.java`

**Interface:**

```java
public final class DisassemblerRecipeAdapter {
    public Optional<DisassemblerRecipeDescriptor> adapt(GTRecipe recipe, int sourcePriority)
    public boolean isBlacklisted(Item item)
}
```

- [ ] **Step 1: Write failing adapter tests with raw GT recipes**

Build raw recipes and assert:

- exactly one item-output content is required;
- any fluid output rejects the source recipe;
- multiple item outputs reject the source recipe;
- output count and `RecipeHelper.getRecipeEUtTier(recipe)` reach the descriptor;
- consumed item/fluid inputs become returns;
- input `Content` with `chance == 0` is omitted;
- an `Ingredient` or `FluidIngredient` with alternatives selects index `0` only;
- empty representative arrays reject the recipe instead of throwing;
- NBT on the recipe output does not enter the descriptor key;
- `IGTTool` items, Carbon Nanites, single superconductor wire items, and items in `tstmodern:disassembler_blacklist` reject;
- a Modern Fusion Coil output remains eligible when it otherwise satisfies the rules.

- [ ] **Step 2: Run the adapter test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.recipe.disassembler.DisassemblerRecipeAdapterTest
```

Expected: compilation failure because the adapter is absent.

- [ ] **Step 3: Implement exact capability decoding**

Use:

- `recipe.getInputContents(ItemRecipeCapability.CAP)` and `FluidRecipeCapability.CAP`;
- `ItemRecipeCapability.CAP.of(content.content())` and `FluidRecipeCapability.CAP.of(content.content())`;
- `SizedIngredient.getAmount()` when the item ingredient is sized, otherwise amount `1`;
- `ingredient.getItems()[0]` and `fluidIngredient.getStacks()[0]` as deterministic representatives;
- `TagPrefixItem` checks for Carbon `nanite` and superconducting `wireGtSingle`;
- the item tag `tstmodern:disassembler_blacklist` as an explicit addon/datapack extension seam.

Do not carry NBT from the selected output stack or ingredient representatives.

- [ ] **Step 4: Add the explicit tag resource**

Create a non-replacing empty tag:

```json
{
  "replace": false,
  "values": []
}
```

- [ ] **Step 5: Run focused tests**

```powershell
.\gradlew.bat test --tests com.tstmodern.recipe.disassembler.DisassemblerRecipeAdapterTest
```

Expected: every eligibility, representative, blacklist, and Fusion Coil case passes.

- [ ] **Step 6: Commit the adapter**

```powershell
git add -- src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeAdapter.java src/main/resources/data/tstmodern/tags/items/disassembler_blacklist.json src/test/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeAdapterTest.java
git commit -m "feat: adapt GTCEu recipes for disassembly"
```

---

## Task 5: Build the reload-aware source index, custom recipe synthesis, and JEI representatives

**Files:**

- Create: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeSource.java`
- Create: `src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeIndex.java`
- Modify: `src/main/java/com/tstmodern/TSTModern.java`
- Create: `src/test/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeIndexTest.java`
- Create: `src/test/java/com/tstmodern/recipe/disassembler/DisassemblerReloadContractTest.java`

**Interfaces:**

```java
public record DisassemblerRecipeSource(
        String id,
        int priority,
        Supplier<GTRecipeType> recipeType) {}
```

```java
public final class DisassemblerRecipeIndex implements GTRecipeType.ICustomRecipeLogic {
    public static final DisassemblerRecipeIndex INSTANCE;

    public Optional<DisassemblerRecipeDescriptor> find(Item item)
    public void invalidate()
    @Override public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder)
    @Override public void buildRepresentativeRecipes()
}
```

Provide one package-private constructor that accepts ordered sources and an adapter so unit tests do not mutate global GTCEu recipe maps.

- [ ] **Step 1: Write failing deterministic-index tests**

Assert:

- Assembly Line priority `0` wins over Assembler priority `1` for the same output;
- lexical recipe ID wins within an equal priority;
- `find` caches an immutable descriptor by output item identity;
- `invalidate()` forces enumeration again and replaces stale results;
- source enumeration visits every category from `type.getCategories()` and every recipe from `type.getRecipesInCategory(category)`;
- future sources can be supplied without modifying lookup logic.

- [ ] **Step 2: Write failing custom synthesis tests**

Provide holder item handlers with multiple stacks and assert the raw runtime recipe:

- uses ID `tstmodern:runtime/disassembler`;
- consumes only whole eligible batches, matching items without NBT;
- contains the aggregated returned item and fluid outputs;
- has no EU capability content;
- uses `DisassemblerRecipePolicy.durationTicks`;
- returns `null` when no eligible batch exists;
- splits every item output into stack entries of at most that item's maximum stack size and every integer content amount remains positive;
- clamps totals safely and relies on machine voiding for excess capacity.

- [ ] **Step 3: Run index tests and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.recipe.disassembler.DisassemblerRecipeIndexTest
```

Expected: compilation failure because source/index classes are absent.

- [ ] **Step 4: Implement source enumeration and caching**

Create the production source list in this exact order:

```java
new DisassemblerRecipeSource("assembly_line", 0, () -> GTRecipeTypes.ASSEMBLY_LINE_RECIPES)
new DisassemblerRecipeSource("assembler", 1, () -> GTRecipeTypes.ASSEMBLER_RECIPES)
```

Sort eligible candidates by source priority and then `recipe.id.toString()`. Cache positive and negative lookups for the current reload generation. Guard cache rebuild/invalidation so client JEI and server lookup cannot expose a partially rebuilt map.

- [ ] **Step 5: Implement aggregate custom recipe synthesis**

Read every `IO.IN` handler from `holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)`, aggregate stack counts by `Item`, call the policy with the machine casing tier obtained from the holder's `DisassemblerMachine`, and build with `GTRecipeBuilder.buildRawRecipe()`. Encode only crafted-item consumption as input and original consumed source ingredients as outputs.

- [ ] **Step 6: Implement JEI representatives**

`buildRepresentativeRecipes()` must first invalidate the runtime cache. For every eligible source descriptor, add one representative disassembly recipe to the Disassembler type's main category using `type.addToMainCategory(recipe)`. Each representative consumes `outputAmount` of the crafted result and displays one batch of returned items/fluids. Deduplicate by output item with the same priority/ID comparator as runtime lookup.

- [ ] **Step 7: Wire server resource reload invalidation**

In `TSTModern`, register a Forge common-bus listener:

```java
MinecraftForge.EVENT_BUS.addListener(TSTModern::addReloadListeners);
```

Add a private static `AddReloadListenerEvent` handler whose listener completes immediately after calling `DisassemblerRecipeIndex.INSTANCE.invalidate()`. Do not mutate GTCEu's staging/frozen registries.

- [ ] **Step 8: Run focused tests**

```powershell
.\gradlew.bat test --tests com.tstmodern.recipe.disassembler.DisassemblerRecipeIndexTest --tests com.tstmodern.recipe.disassembler.DisassemblerReloadContractTest
```

Expected: deterministic lookup, reload invalidation, aggregate synthesis, and JEI representative tests all pass.

- [ ] **Step 9: Commit index and reload integration**

```powershell
git add -- src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeSource.java src/main/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeIndex.java src/main/java/com/tstmodern/TSTModern.java src/test/java/com/tstmodern/recipe/disassembler/DisassemblerRecipeIndexTest.java src/test/java/com/tstmodern/recipe/disassembler/DisassemblerReloadContractTest.java
git commit -m "feat: index recipes for dynamic disassembly"
```

---

## Task 6: Register the no-energy Disassembler recipe type

**Files:**

- Modify: `src/main/java/com/tstmodern/registry/TSTRecipeTypes.java`
- Create: `src/test/java/com/tstmodern/registry/DisassemblerRecipeTypeContractTest.java`

- [ ] **Step 1: Write the failing recipe-type contract test**

Assert the source registers exactly one `DISASSEMBLER`, configures `setMaxIOSize(16, 16, 0, 4)`, and calls `addCustomRecipeLogic(DisassemblerRecipeIndex.INSTANCE)`. Assert the Disassembler registration chain contains no `setEUIO` call.

- [ ] **Step 2: Run the contract test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.DisassemblerRecipeTypeContractTest
```

Expected: failure because `DISASSEMBLER` is absent.

- [ ] **Step 3: Register the type**

Add:

```java
public static GTRecipeType DISASSEMBLER;
```

and initialize it in `registerRecipeTypes`:

```java
DISASSEMBLER = register(event, "disassembler")
        .setMaxIOSize(16, 16, 0, 4)
        .addCustomRecipeLogic(DisassemblerRecipeIndex.INSTANCE);
```

- [ ] **Step 4: Run the focused test and compile**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.DisassemblerRecipeTypeContractTest
.\gradlew.bat compileJava
```

Expected: both succeed; no EU input appears on this recipe type.

- [ ] **Step 5: Commit the type registration**

```powershell
git add -- src/main/java/com/tstmodern/registry/TSTRecipeTypes.java src/test/java/com/tstmodern/registry/DisassemblerRecipeTypeContractTest.java
git commit -m "feat: register no-energy disassembler recipes"
```

---

## Task 7: Port the exact structure and formation-time tier validation

**Files:**

- Create: `src/main/java/com/tstmodern/machine/DisassemblerStructure.java`
- Create: `src/main/java/com/tstmodern/machine/DisassemblerMachine.java`
- Create: `src/test/java/com/tstmodern/machine/DisassemblerStructureTest.java`
- Create: `src/test/java/com/tstmodern/machine/DisassemblerMachineContractTest.java`

- [ ] **Step 1: Mechanically extract the literal target structure**

Read source rows only from `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/system/Disassembler/TST_Disassembler.java`, the `transpose(new String[][] {` body at source lines 106–130. The source has 23 rows, each containing 28 strings of width 27. Produce target aisle `z`, row `y` as `sourceRows[y][z]`, with no manual symbol edits.

Store all 28 aisles ×23 rows in `DisassemblerStructure.AISLES`. Expose constants `WIDTH=27`, `HEIGHT=23`, `DEPTH=28` and a package-private normalized string helper used only by tests.

- [ ] **Step 2: Write structure tests before adding the literal**

Assert:

- 28 aisles, 23 rows per aisle, 27 characters per row;
- controller `~` occurs once at target `(13,21,0)`;
- the exact symbol counts from Global Constraints;
- SHA-256 of all 644 rows joined by `\n` with no final newline equals the approved hash.

- [ ] **Step 3: Run the structure test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.DisassemblerStructureTest
```

Expected: compilation failure because `DisassemblerStructure` is absent.

- [ ] **Step 4: Add the exact literal and turn the structure test green**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.DisassemblerStructureTest
```

Expected: dimensions, controller coordinate, counts, and hash all pass.

- [ ] **Step 5: Write the failing machine formation/voiding contract**

The test must establish:

- the class extends `WorkableMultiblockMachine`, not the electric subclass;
- `casingTier` starts/reset to `0` while unformed;
- formation scans every matched B position and maps its block through `TSTBlocks.componentAssemblyLineTier`;
- all 66 equal blocks store tier `1..14`;
- a mixed tier calls `setPatternError(new BlockMatchingError(position, List.of(expectedBlock)))` and invalidates formation;
- `getVoidingMode()` returns `VoidingMode.VOID_ITEMS_FLUIDS`;
- no parallel/batch/locking/separation/electric modifier is attached.

- [ ] **Step 6: Implement the machine class**

Constructor:

```java
public DisassemblerMachine(IMachineBlockEntity holder, Object... args) {
    super(holder, args);
}
```

Override `formStructure(String substructureName)`, call `super` first, read `patternStates.get(substructureName).getCache()`, identify all tier-casing block states, and enforce one expected tier. Override invalidation to reset `casingTier=0`. Expose a read-only getter for the custom recipe index.

- [ ] **Step 7: Run focused machine tests**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.DisassemblerStructureTest --tests com.tstmodern.machine.DisassemblerMachineContractTest
```

Expected: exact structure and machine contract pass.

- [ ] **Step 8: Commit structure and machine behavior**

```powershell
git add -- src/main/java/com/tstmodern/machine/DisassemblerStructure.java src/main/java/com/tstmodern/machine/DisassemblerMachine.java src/test/java/com/tstmodern/machine/DisassemblerStructureTest.java src/test/java/com/tstmodern/machine/DisassemblerMachineContractTest.java
git commit -m "feat: port disassembler structure and tier logic"
```

---

## Task 8: Define the multiblock and reproduce the GT5 controller rendering

**Files:**

- Create: `src/main/java/com/tstmodern/registry/machine/DisassemblerDefinition.java`
- Create: `src/test/java/com/tstmodern/registry/machine/DisassemblerDefinitionContractTest.java`
- Create: `src/main/resources/assets/tstmodern/blockstates/disassembler.json`
- Create: `src/main/resources/assets/tstmodern/models/block/machine/disassembler.json`
- Create: `src/main/resources/assets/tstmodern/models/item/disassembler.json`
- Create: `src/main/resources/assets/tstmodern/textures/block/casings/dimensional_transcendent_casing.png`
- Create: `src/main/resources/assets/tstmodern/textures/block/multiblock/disassembler/overlay_front.png`
- Create: `src/main/resources/assets/tstmodern/textures/block/multiblock/disassembler/overlay_front_active.png`
- Create: `src/main/resources/assets/tstmodern/textures/block/multiblock/disassembler/overlay_front_active.png.mcmeta`
- Create: `src/main/resources/assets/tstmodern/textures/block/multiblock/disassembler/overlay_front_active_emissive.png`
- Create: `src/main/resources/assets/tstmodern/textures/block/multiblock/disassembler/overlay_front_active_emissive.png.mcmeta`

- [ ] **Step 1: Write a failing definition contract test**

Assert the definition:

- registers ID `disassembler` with `DisassemblerMachine::new`;
- owns only `TSTRecipeTypes.DISASSEMBLER` and has no recipe modifiers;
- uses `.appearanceBlock(TSTBlocks.MOLECULAR_CASING)`;
- uses axes `RelativeDirection.RIGHT`, `DOWN`, `BACK`;
- adds all 28 exact `DisassemblerStructure.AISLES`;
- maps A/C/D/E/F/G/H/J to the approved native/dedicated predicates;
- B accepts exactly the fourteen registered tier blocks;
- I is `MOLECULAR_CASING OR IMPORT_ITEMS(min 1) OR EXPORT_ITEMS(min 1) OR EXPORT_FLUIDS(min 1)` and contains no other ability;
- the overall pattern requires at least one item import, one item export, and one fluid export;
- frames J use `GTMaterials.Neutronium`.

- [ ] **Step 2: Run the definition test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.machine.DisassemblerDefinitionContractTest
```

Expected: compilation failure because the definition is absent.

- [ ] **Step 3: Implement the definition**

Use the repository's existing multiblock builder style. Feed aisles with an ordinary loop during pattern construction so the literal remains single-source. Use `Predicates.blocks` for all fourteen B blocks and the exact GTCEu/GCYM mappings from the spec. Add five `Component.translatable` tooltips with keys `tstmodern.machine.disassembler.tooltip.0` through `.4` covering no-energy reversal, current Assembly Line/Assembler scope and priority, the casing tier gate, whole-batch all-bus scanning, and voided overflow.

- [ ] **Step 4: Copy the approved GT5 controller textures byte-for-byte**

Source mappings:

- `D:/tmp/GT5/src/main/resources/assets/gregtech/textures/blocks/iconsets/MACHINE_DIM_TRANS_CASING.png` → `dimensional_transcendent_casing.png`
- `OVERLAY_DTPF_OFF.png` → `overlay_front.png`
- `OVERLAY_DTPF_ON.png` and its `.mcmeta` → `overlay_front_active.png` and `.mcmeta`
- `OVERLAY_FUSION1_GLOW.png` and its `.mcmeta` → `overlay_front_active_emissive.png` and `.mcmeta`

- [ ] **Step 5: Add explicit controller model states**

The machine model JSON must contain top-level:

```json
"texture_overrides": {
  "all": "tstmodern:block/casings/molecular_casing"
}
```

Every controller variant uses `tstmodern:block/casings/dimensional_transcendent_casing` for `textures.all`. Idle and suspended states use `overlay_front`; waiting and working states use `overlay_front_active` plus `overlay_front_active_emissive`. Include all eight standard formed/unformed variants used by the existing machines. The blockstate and item model follow current dedicated-machine resources.

- [ ] **Step 6: Run definition and resource validation**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.machine.DisassemblerDefinitionContractTest
.\gradlew.bat processResources
```

Expected: the ability/structure/render contract passes and all assets copy successfully.

- [ ] **Step 7: Commit definition and rendering**

```powershell
git add -- src/main/java/com/tstmodern/registry/machine/DisassemblerDefinition.java src/test/java/com/tstmodern/registry/machine/DisassemblerDefinitionContractTest.java src/main/resources/assets/tstmodern/blockstates/disassembler.json src/main/resources/assets/tstmodern/models/block/machine/disassembler.json src/main/resources/assets/tstmodern/models/item/disassembler.json src/main/resources/assets/tstmodern/textures/block/casings/dimensional_transcendent_casing.png src/main/resources/assets/tstmodern/textures/block/multiblock/disassembler
git commit -m "feat: define and render the disassembler"
```

---

## Task 9: Add exact casing, controller, Molecular, and Hollow recipes

**Files:**

- Create: `src/main/java/com/tstmodern/data/recipe/DisassemblerRecipes.java`
- Create: `src/test/java/com/tstmodern/data/recipe/DisassemblerRecipesContractTest.java`

- [ ] **Step 1: Write a failing recipe ownership/coverage test**

Assert `DisassemblerRecipes.register(provider)` owns exactly:

- fourteen tier-casing recipes with an uninterrupted predecessor chain;
- one Molecular Casing recipe;
- one Hollow Casing recipe;
- one Disassembler controller recipe;
- no processing/reverse recipes, because runtime custom logic owns those.

Assert every output ID is unique and every count/fluid/duration/EUt/research value below is present.

- [ ] **Step 2: Run the recipe contract test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.data.recipe.DisassemblerRecipesContractTest
```

Expected: compilation failure because the recipe module is absent.

- [ ] **Step 3: Add LV–IV Assembler casing recipes**

All five output one casing, last 320 ticks, and use `VA[tier]`:

| Tier | Frame + obtainable plate form + gears | Cable | Components | Circuits | Soldering Alloy |
|---|---|---|---|---|---:|
| LV | Steel frame, 1 Steel dense plate, 4 Steel gears, 16 Steel small gears | 8 Tin single cable | 4 LV robot arms, 8 LV pistons, 10 LV motors | 16 LV | 288 mB |
| MV | Aluminium frame, 9 Aluminium plates, 4 Aluminium gears, 16 Aluminium small gears | 8 Annealed Copper single cable | 4 MV robot arms, 8 MV pistons, 10 MV motors | 8 MV + 16 LV | 432 mB |
| HV | Stainless Steel frame, 9 Stainless Steel plates, 4 Stainless Steel gears, 16 Stainless Steel small gears | 8 Gold single cable | 4 HV robot arms, 8 HV pistons, 10 HV motors | 8 HV + 16 MV | 576 mB |
| EV | Titanium frame, 9 Titanium plates, 4 Titanium gears, 16 Titanium small gears | 8 Aluminium single cable | 4 EV robot arms, 8 EV pistons, 10 EV motors | 8 EV + 16 HV | 720 mB |
| IV | Tungsten Steel frame, 1 Tungsten Steel dense plate, 4 Tungsten Steel gears, 16 Tungsten Steel small gears | 8 Tungsten single cable | 4 IV robot arms, 8 IV pistons, 10 IV motors | 8 IV + 16 EV | 864 mB |

Use `frameGt`, `plateDense`, `gear`, `gearSmall`, `cableGtSingle`, the corresponding `GTItems`, and `CustomTags` circuit tags. If a builder input exceeds one stack, pass repeated legal stacks rather than an invalid count.

- [ ] **Step 4: Add LuV–UHV Assembly Line casing recipes**

Each consumes one predecessor casing, 8 current-tier robot arms, 10 pistons, 16 motors, 4 gears, 16 small gears, 8 single cables, 8 current circuits, and 16 previous circuits. Each lasts 600 ticks, uses `VA[previous tier]`, and scanner-researches the predecessor for 1800 ticks at the previous tier.

| Output | Structural material | Cable | Solder | Material fluid | Lubricant |
|---|---|---|---:|---:|---:|
| LuV | Europium frame; Rhodium-Plated Palladium dense/small gears; Ruridit gears | Vanadium Gallium | 3456 mB | 1728 mB Rhodium-Plated Palladium | 4000 mB |
| ZPM | Iridium frame/gears; Rhodium-Plated Palladium dense/small gears | Naquadah | 4032 mB | 2016 mB Iridium | 5000 mB |
| UV | Tritanium frame/gears/small gears; Rhodium-Plated Palladium dense plate | Naquadah Alloy | 4608 mB | 2304 mB Osmium | 6000 mB |
| UHV | Neutronium frame/gears; Darmstadtium dense/small gears | Tritanium | 5184 mB | 2592 mB Neutronium | 7000 mB |

Use the exact GTCEu symbols `RhodiumPlatedPalladium` and `Ruridit`. The split structural materials are deliberate and must not be simplified: Ruridit generates frame/gear but not dense/small gear; Rhodium-Plated Palladium generates dense/small gear but not frame/gear; Osmium supplies the UV recipe fluid but does not generate the required frame/gear forms.

- [ ] **Step 5: Add UEV–MAX predecessor-chain Assembly Line recipes**

All five use UHV-class component items/circuit tags because GTCEu 7.4.0 exposes component classes only through UHV. High-Tier activation from Task 1 guarantees these inputs exist. All use station research on the direct predecessor.

| Output | Predecessor | Frames | Dense plates | Gears | Small gears | Tritanium cables | UHV robot/piston/motor | UHV circuits | Solder / endgame fluid / lubricant | Duration / EUt |
|---|---|---:|---:|---:|---:|---:|---|---:|---|---|
| UEV | UHV | 1 Neutronium | 2 Darmstadtium | 4 Naquadria | 16 Darmstadtium | 8 | 8 / 10 / 16 | 8 | 6912 / 3456 Darmstadtium / 8000 | 900 / `VA[UHV]` |
| UIV | UEV | 2 Tritanium | 4 Darmstadtium | 8 Tritanium | 24 Tritanium | 16 | 12 / 16 / 24 | 12 | 10368 / 5184 Tritanium / 12000 | 1200 / `VA[UEV]` |
| UMV | UIV | 4 Neutronium | 6 Darmstadtium | 12 Neutronium | 32 Tritanium | 24 | 16 / 24 / 32 | 16 | 13824 / 6912 Neutronium / 16000 | 1500 / `VA[UIV]` |
| UXV | UMV | 8 Neutronium | 8 Darmstadtium | 16 Neutronium | 48 Tritanium | 32 | 24 / 32 / 48 | 24 | 18432 / 9216 Neutronium / 24000 | 1800 / `VA[UMV]` |
| MAX | UXV | 16 Neutronium | 16 Darmstadtium | 32 Neutronium | 64 Tritanium | 64 | 32 / 48 / 64 | 32 | 27648 / 13824 Neutronium / 32000 | 2400 / `VA[UXV]` |

Research duration is `3600, 4800, 6000, 7200, 9600` ticks respectively, at the recipe's listed EU tier. These forms are deliberately limited to GTCEu-generated forms: Darmstadtium supplies dense/small gear, Tritanium supplies frame/gear/small gear/cable, Naquadria supplies gear, and Neutronium supplies frame/gear/fluid.

- [ ] **Step 6: Add Molecular and Hollow casing recipes**

Molecular Casing, Assembler, output 1, 800 ticks, `VA[UV]`:

- 1 UHV Energy Input Hatch
- 54 Osmiridium Plates, the obtainable material-equivalent replacement for 6 unavailable Dense Osmiridium Plates
- 12 Osmium Foils
- 24 Tungsten Steel Screws
- 24 Tungsten Steel Rings
- 1 IV Field Generator
- 1296 mB molten Osmium

Hollow Casing, Assembly Line, output 2, 200 ticks, `200000 EU/t`, scanner research Molecular Casing for 1000 ticks at LuV:

- 1 Molecular Casing
- 18 Europium Plates, the obtainable material-equivalent replacement for 2 unavailable Dense Europium Plates
- 16 Plutonium Plates
- 16 Lead Plates
- 16 Uranium Plates
- 16 Europium Screws
- 1296 mB molten Osmium
- 2000 mB Super Coolant
- 1000 mB Argon

- [ ] **Step 7: Add the approved controller Assembly Line recipe**

Output one controller at `VA[UEV]`, 72000 ticks:

- 64 UHV Assemblers
- 16 UHV Field Generators
- 64 UHV Pumps
- 64 UHV Conveyors
- 256 UHV Robot Arms as four legal stacks of 64
- 64 UHV Energy Input Hatches as the approved eM Power replacement
- 16 Neutronium Frames
- 144 Osmiridium Plates, the obtainable material-equivalent replacement for 16 unavailable Dense Osmiridium Plates
- 147456 mB Soldering Alloy
- 128000 mB UU Matter
- 768000 mB Super Coolant
- scanner research one LV Assembler for exactly `576000` ticks (eight real-time Minecraft hours) at `VA[LV]`

- [ ] **Step 8: Run the recipe contract and data generation compile path**

```powershell
.\gradlew.bat test --tests com.tstmodern.data.recipe.DisassemblerRecipesContractTest
.\gradlew.bat compileJava
```

Expected: exact recipe coverage passes and every GTCEu symbol resolves.

- [ ] **Step 9: Commit construction recipes**

```powershell
git add -- src/main/java/com/tstmodern/data/recipe/DisassemblerRecipes.java src/test/java/com/tstmodern/data/recipe/DisassemblerRecipesContractTest.java
git commit -m "feat: add disassembler progression recipes"
```

---

## Task 10: Wire registration, localization, casing catalogs, and module contracts

**Files:**

- Modify: `src/main/java/com/tstmodern/TSTModernGTAddon.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/TSTMachineRegistry.java`
- Modify: `src/main/resources/assets/tstmodern/lang/en_us.json`
- Modify: `src/main/resources/assets/tstmodern/lang/vi_vn.json`
- Modify: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/Disassembler.json`
- Modify: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/tst-gt5-casing-catalog.json`
- Create: `src/test/java/com/tstmodern/registry/DisassemblerRegistrationContractTest.java`
- Create: `src/test/java/com/tstmodern/registry/DisassemblerLocalizationContractTest.java`

- [ ] **Step 1: Write failing registration tests**

Assert:

- `TSTMachineRegistry.registerMachines` references `DisassemblerDefinition.MACHINE` exactly once;
- `TSTModernGTAddon.addRecipes` calls `DisassemblerRecipes.register(provider)` exactly once;
- no other recipe module owns a `disassembler` output or processing recipe;
- every dedicated block and the controller has both English and Vietnamese names.

- [ ] **Step 2: Run registration/localization tests and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.DisassemblerRegistrationContractTest --tests com.tstmodern.registry.DisassemblerLocalizationContractTest
```

Expected: failures for missing registration and language keys.

- [ ] **Step 3: Wire machine and recipe registration once**

Add the definition to the stable registry list after the existing five machines. Import and call `DisassemblerRecipes.register(provider)` after `MegaTreeFarmRecipes.register(provider)`. Preserve the `requiresHighTier()` override from Task 1.

- [ ] **Step 4: Add localization**

English controller: `TST Large Disassembler`. Vietnamese controller: `Máy Tháo Rã Cỡ Lớn TST`. Name the fourteen casings `Component Assembly Line Casing <tier>` / `Vỏ Dây Chuyền Lắp Ráp Linh Kiện <tier>`, plus `Molecular Casing` / `Vỏ Phân Tử` and `Hollow Casing` / `Vỏ Rỗng`.

Add exact tooltip translations for keys `.0` through `.4`:

| Key | English | Vietnamese |
|---|---|---|
| 0 | `Consumes no energy; reverses eligible crafted items into their consumed ingredients` | `Không tiêu thụ năng lượng; tháo vật phẩm hợp lệ thành các nguyên liệu đã tiêu thụ` |
| 1 | `Sources: Assembly Line first, then Assembler; unavailable legacy integrations are omitted` | `Nguồn: Dây chuyền lắp ráp trước, Máy lắp ráp sau; các tích hợp cũ không còn tồn tại được bỏ qua` |
| 2 | `Casing tier allows recipe tier up to one tier higher; MAX has no tier ceiling` | `Cấp vỏ cho phép recipe cao hơn tối đa một cấp; MAX không giới hạn cấp recipe` |
| 3 | `Scans every input bus and consumes only complete source-output batches` | `Quét mọi bus đầu vào và chỉ tiêu thụ các lô đầu ra nguồn hoàn chỉnh` |
| 4 | `Insufficient output capacity voids only the overflow` | `Khi đầu ra không đủ chỗ, chỉ phần dư bị hủy` |

- [ ] **Step 5: Correct both casing catalogs**

For the Disassembler record, replace zero occurrence counts with the exact structure counts. Record native mappings A/C/D/E/F/J, dedicated mappings B/G/H/I, the fourteen B target IDs, their exact audit filenames, and dedicated Molecular/Hollow target resource names. Preserve all unrelated machine records.

- [ ] **Step 6: Validate the catalog and module contract**

Run the validator documented by the port skill against both catalog files, then run:

```powershell
.\gradlew.bat test --tests com.tstmodern.registry.DisassemblerRegistrationContractTest --tests com.tstmodern.registry.DisassemblerLocalizationContractTest
```

Expected: catalog validation succeeds and all ownership/translation assertions pass.

- [ ] **Step 7: Commit registration, localization, and catalogs only**

```powershell
git add -- src/main/java/com/tstmodern/TSTModernGTAddon.java src/main/java/com/tstmodern/registry/machine/TSTMachineRegistry.java src/main/resources/assets/tstmodern/lang/en_us.json src/main/resources/assets/tstmodern/lang/vi_vn.json .agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/Disassembler.json .agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/tst-gt5-casing-catalog.json src/test/java/com/tstmodern/registry/DisassemblerRegistrationContractTest.java src/test/java/com/tstmodern/registry/DisassemblerLocalizationContractTest.java
git commit -m "feat: register and localize the disassembler"
```

---

## Task 11: Add end-to-end static contracts and run full verification

**Files:**

- Create: `src/test/java/com/tstmodern/DisassemblerPortContractTest.java`
- Modify after evidence exists: `docs/port-records/disassembler.md` (local ignored record only)

- [ ] **Step 1: Write a cross-module port contract test**

The test must verify from current sources/resources:

- one behavior class, one definition, one construction-recipe module, one recipe type;
- no electric superclass, EU input, energy ability, parallel modifier, or fluid import;
- exactly sixteen dedicated casing block registrations and complete assets;
- exact structure dimensions/counts/hash/controller coordinate;
- exact allowed abilities and uniform B-tier validation;
- dynamic source order Assembly Line then Assembler;
- reload invalidation listener and JEI representative hook exist;
- `requiresHighTier()` returns true;
- controller uses Dimensional Transcendent textures and formed parts use Molecular appearance;
- all recipe/register/localization owners occur exactly once.

- [ ] **Step 2: Run the new contract test first**

```powershell
.\gradlew.bat test --tests com.tstmodern.DisassemblerPortContractTest
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Run all focused Disassembler tests together**

```powershell
.\gradlew.bat test --tests "com.tstmodern.*Disassembler*"
```

Expected: all Disassembler unit and contract tests pass with no skipped failures.

- [ ] **Step 4: Run full project verification**

```powershell
.\gradlew.bat test
.\gradlew.bat compileJava
.\gradlew.bat build
git status --short
git diff --check
```

Expected:

- all Gradle commands finish with `BUILD SUCCESSFUL`;
- `git diff --check` produces no output;
- `git status --short` contains no unintended changes and still preserves any unrelated Mega Tree Farm work that existed before this port.

- [ ] **Step 5: Inspect warnings and generated resources**

Check compiler output for unused imports/locals and inspect the built JAR for the Disassembler machine JSON, sixteen casing resource sets, five controller textures, language entries, blacklist tag, and loot tables. Fix and re-run the full commands if anything is absent.

- [ ] **Step 6: Update the local port record truthfully**

Record source references, approved deviations, exact casing table, structure hash/count evidence, recipe/index behavior, all commands and results, and final status `build validated; gameplay validation pending`. Do not mark in-world formation, JEI, active animation, reload behavior, or processing as gameplay-validated until those are actually exercised.

- [ ] **Step 7: Commit the final cross-module test**

```powershell
git add -- src/test/java/com/tstmodern/DisassemblerPortContractTest.java
git commit -m "test: verify disassembler port contract"
```

- [ ] **Step 8: Review the final commit range without touching unrelated work**

```powershell
git log --oneline c65c6bc..HEAD
git diff --stat c65c6bc..HEAD
git status --short
```

Expected: the range contains only the focused Disassembler commits from this plan; pre-existing Mega Tree Farm edits remain outside those commits.

---

## Task 12: Perform the client/world gameplay and JEI gates

**Files:**

- Modify only when evidence changes: `docs/port-records/disassembler.md` (local ignored record)
- Modify production/test files only if a gate exposes a defect; each defect requires a focused regression test and commit

- [ ] **Step 1: Launch a development client**

```powershell
.\gradlew.bat runClient
```

Expected: the title screen loads without registry, model, texture, recipe, or language errors. Capture relevant log lines and screenshots under the existing ignored `run/` area.

- [ ] **Step 2: Verify JEI and construction progression**

Check all sixteen casing blocks and the controller are searchable and named in the active language. Confirm every casing recipe has obtainable inputs, the UHV-and-above inputs exist with `highTierContent: false`, the predecessor chain is continuous through MAX, and each eligible winning Assembly Line/Assembler output shows exactly one reverse representative with consumed catalysts omitted.

- [ ] **Step 3: Verify structure formation boundaries**

Build the exact structure with all B blocks at one tier and one each of Input Bus, Output Bus, and Output Hatch. Confirm it forms, records the expected tier, and rejects each of these mutations separately: one mixed B casing, missing Input Bus, missing Output Bus, missing Output Hatch, an Energy Hatch in I, and a fluid import hatch in I. Confirm tiers LV, UHV, and MAX map to internal values 1, 9, and 14.

- [ ] **Step 4: Verify rendering**

While unformed, formed-idle, and formed-active, inspect every controller face and ability part. The controller must use Dimensional Transcendent base pixels, OFF when idle, ON plus Fusion glow when active. Formed Input/Output/Fluid hatches must retain their native overlays on Molecular Casing rather than inheriting controller pixels or sampling neighboring casing colors.

- [ ] **Step 5: Verify no-energy processing and tier gating**

Run without any energy input. At a non-MAX casing tier, test one recipe at the allowed `tier + 1` boundary and one immediately above it; only the allowed item is consumed. At MAX, confirm the formerly rejected recipe runs. Confirm the machine has no parallel, batch, locking, or separation controls.

- [ ] **Step 6: Verify aggregation, remainder, fluids, and overflow**

Place at least two different reversible crafted items across separate input buses, including a source output whose recipe creates more than one item. Confirm all valid complete batches process atomically, unmatched remainder stays, returned item/fluid amounts equal the selected source inputs, and duration matches the integer-floor formula. Fill the Output Bus and Output Hatch, repeat the run, and confirm only excess return output is voided with no duplication or rollback.

- [ ] **Step 7: Verify reload removal and addition**

Use a small test datapack to add or replace a source recipe, reload resources, and confirm the new reverse result appears and executes. Remove that source recipe, reload again, and confirm neither runtime lookup nor JEI retains the stale reverse recipe.

- [ ] **Step 8: Fix any exposed defect test-first**

For each failed gate, add the smallest deterministic regression test, confirm it fails, implement the fix, run the focused and full verification commands from Task 11, and commit only that defect's files with a descriptive `fix: disassembler ...` message.

- [ ] **Step 9: Record the final evidence**

If every gate passes, update the local port record to `gameplay and JEI validated` with the world/config, test items, screenshots/logs, and reload generations used. If any gate is not executed or remains failing, retain `build validated; gameplay validation pending` and name the exact pending gate.
