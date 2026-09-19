# TST Machine Module Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox ('- [ ]') syntax for tracking.

**Goal:** Split the five ported multiblocks out of 'TSTMachines' and 'TSTRecipes', keep behaviour classes in the existing flat 'machine' package, and consolidate Mega Tree Farm processing math into 'MegaTreeFarmMachine'.

**Architecture:** Each multiblock gets one definition module under 'registry.machine' and one recipe module under 'data.recipe'. A small 'TSTMachineRegistry' lifecycle adapter initializes the five definitions, while 'TSTModernGTAddon' invokes the five recipe modules directly. This is a structural refactor with no gameplay or registry changes.

**Tech Stack:** Java 17, Forge 47.3.0, Minecraft 1.20.1, GTCEu 7.4.0, LDLib, Gradle 8.8, PowerShell verification.

**Spec:** 'docs/superpowers/specs/2026-08-22-tst-machine-module-refactor-design.md'

## Global Constraints

- Keep 'com.tstmodern.machine' flat.
- Preserve registry names, resource locations, recipes, structures, assets, casing mappings, tooltips, and modifiers.
- Preserve the current Mega Tree Farm TST multiplier × GTCEu Parallel Hatch behaviour.
- Do not alter 'TSTBlocks', 'TSTRecipeTypes', materials, assets, or mixins except necessary imports.
- Use 'apply_patch' for source edits.
- Do not commit during this refactor: the two monoliths contain overlapping pre-existing user work.
- The unrelated 'runData' model failure is not a completion gate.

## Target files and interfaces

Create five definition modules under 'src/main/java/com/tstmodern/registry/machine/', each exposing:

~~~java
public static final MultiblockMachineDefinition MACHINE
~~~

Create five recipe modules under 'src/main/java/com/tstmodern/data/recipe/', each exposing:

~~~java
public static void register(Consumer<FinishedRecipe> provider)
~~~

Create 'TSTMachineRegistry.registerMachines(...)' as the single lifecycle adapter.

After consolidation, 'MegaTreeFarmMachine' exposes only this recipe-facing interface:

~~~java
public enum OutputMode { LOG, SAPLING, LEAVES, FRUIT }
public static int baseOutputAmount(OutputMode mode)
~~~

All profile and arithmetic helpers remain private.

---

### Task 1: Establish the architecture regression gate

**Files:**
- Read: 'src/main/java/com/tstmodern/registry/TSTMachines.java'
- Read: 'src/main/java/com/tstmodern/data/TSTRecipes.java'
- Read: 'src/main/java/com/tstmodern/machine/MegaTreeFarmMath.java'

**Interfaces:**
- Consumes: the approved target layout.
- Produces: a repeatable architecture check used again in Task 8.

- [ ] **Step 1: Run the target-layout assertion before implementation**

~~~powershell
$required = @(
  'src/main/java/com/tstmodern/registry/machine/MegaStoneBreakerDefinition.java',
  'src/main/java/com/tstmodern/registry/machine/GiantVacuumDryingFurnaceDefinition.java',
  'src/main/java/com/tstmodern/registry/machine/NetherInterfaceDefinition.java',
  'src/main/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinition.java',
  'src/main/java/com/tstmodern/registry/machine/MegaTreeFarmDefinition.java',
  'src/main/java/com/tstmodern/registry/machine/TSTMachineRegistry.java',
  'src/main/java/com/tstmodern/data/recipe/MegaStoneBreakerRecipes.java',
  'src/main/java/com/tstmodern/data/recipe/GiantVacuumDryingFurnaceRecipes.java',
  'src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java',
  'src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java',
  'src/main/java/com/tstmodern/data/recipe/MegaTreeFarmRecipes.java'
)
$missing = $required | Where-Object { -not (Test-Path -LiteralPath $_) }
if ($missing) { throw "Missing target modules: $($missing -join ', ')" }
~~~

Expected: FAIL with 'Missing target modules'.

- [ ] **Step 2: Capture the production baseline**

~~~powershell
.\gradlew.bat -g 'C:\Users\mtien\.gradle' build --offline
~~~

Expected: 'BUILD SUCCESSFUL'. Stop and report if the baseline itself fails.

---

### Task 2: Extract Mega Stone Breaker

**Files:**
- Create: 'registry/machine/MegaStoneBreakerDefinition.java'
- Create: 'data/recipe/MegaStoneBreakerRecipes.java'
- Modify: 'registry/TSTMachines.java:34-160,2407-2410'
- Modify: 'data/TSTRecipes.java:139-220,446-537'
- Modify: 'TSTModernGTAddon.java'

**Interfaces:**
- Consumes: 'MegaStoneBreakerMachine::parallelAndOutputMultiplier'.
- Produces: 'MegaStoneBreakerDefinition.MACHINE' and 'MegaStoneBreakerRecipes.register(...)'.

- [ ] **Step 1: Move the complete Registrate builder**

Create a final utility class in package 'com.tstmodern.registry.machine'. Move the builder from '.multiblock("mega_stone_breaker", ...)' through its matching '.register()' verbatim and rename the constant to 'MACHINE'.

The new class is final, has a private constructor, and exposes only the moved builder as `public static final MultiblockMachineDefinition MACHINE`.

- [ ] **Step 2: Move all owned recipes**

Create one public 'register' method calling private methods for:

- current 'addMegaStoneBreakerRecipes';
- compressed cobblestone compressor and crafting recipes;
- Advanced Iridium Casing;
- Cosmic Neutronium Frame;
- controller recipe;
- the two 'basic' helpers and 'recipe' helper.

Change only the controller output reference:

~~~java
.outputItems(MegaStoneBreakerDefinition.MACHINE)
~~~

- [ ] **Step 3: Wire temporary adapters**

In the existing 'TSTMachines.registerMachines', force definition initialization:

~~~java
var megaStoneBreaker = MegaStoneBreakerDefinition.MACHINE;
~~~

In the addon, replace the old processing call with:

~~~java
MegaStoneBreakerRecipes.register(provider);
~~~

Remove Mega Stone Breaker-owned calls from 'addConstructionRecipes'.

- [ ] **Step 4: Delete only the moved blocks from the monoliths**

Preserve the other four definitions and recipe groups.

- [ ] **Step 5: Compile**

~~~powershell
.\gradlew.bat -g 'C:\Users\mtien\.gradle' compileJava --offline
~~~

Expected: 'BUILD SUCCESSFUL'.

---

### Task 3: Extract Giant Vacuum Drying Furnace

**Files:**
- Create: 'registry/machine/GiantVacuumDryingFurnaceDefinition.java'
- Create: 'data/recipe/GiantVacuumDryingFurnaceRecipes.java'
- Modify: 'registry/TSTMachines.java:162-391,2407-2410'
- Modify: 'data/TSTRecipes.java:251-419'
- Modify: 'TSTModernGTAddon.java'

**Interfaces:**
- Consumes: 'GiantVacuumDryingFurnaceMachine::recipeModifier'.
- Produces: 'GiantVacuumDryingFurnaceDefinition.MACHINE' and 'GiantVacuumDryingFurnaceRecipes.register(...)'.

- [ ] **Step 1: Move the definition builder**

Move '.multiblock("giant_vacuum_drying_furnace", ...)' through its matching '.register()' verbatim into the new 'MACHINE' constant.

- [ ] **Step 2: Move recipes**

The public 'register' method owns:

- current 'addGiantVacuumDryingFurnaceRecipes';
- the HSSE and HSSS Vacuum Furnace recipes currently located after the Nether Interface dimensional-harvesting recipe;
- 'addVacuumCasingRecipe';
- 'addGiantVacuumDryingFurnaceControllerRecipe'.

Use:

~~~java
.outputItems(GiantVacuumDryingFurnaceDefinition.MACHINE)
~~~

- [ ] **Step 3: Rewire and delete old ownership**

Add the definition reference to the temporary registry method, replace the addon call, and remove the moved source blocks.

- [ ] **Step 4: Compile**

Run the Task 2 compile command. Expected: 'BUILD SUCCESSFUL'.

---

### Task 4: Extract Nether Interface

**Files:**
- Create: 'registry/machine/NetherInterfaceDefinition.java'
- Create: 'data/recipe/NetherInterfaceRecipes.java'
- Modify: 'registry/TSTMachines.java:393-496,2407-2410'
- Modify: 'data/TSTRecipes.java:222-249,421-444'
- Modify: 'TSTModernGTAddon.java'

**Interfaces:**
- Consumes: 'NetherInterfaceMachine::recipeModifier'.
- Produces: 'NetherInterfaceDefinition.MACHINE' and 'NetherInterfaceRecipes.register(...)'.

- [ ] **Step 1: Move the definition**

Move '.multiblock("nether_interface", ...)' through its matching '.register()' verbatim.

- [ ] **Step 2: Move recipes**

The module owns the Hellish Metal bootstrap, dimensional-harvesting processing recipe, controller recipe, and Mechanically Enhanced Obsidian recipe. The bootstrap is included so deleting `TSTRecipes` in Task 7 cannot silently remove this Nether-specific prerequisite. The misplaced HSSE/HSSS Vacuum Furnace recipes move to `GiantVacuumDryingFurnaceRecipes` in Task 3.

Use:

~~~java
.outputItems(NetherInterfaceDefinition.MACHINE)
~~~

- [ ] **Step 3: Rewire, remove, and compile**

Initialize the new definition from the temporary registry, update the addon call, remove moved blocks, and run 'compileJava --offline'. Expected: 'BUILD SUCCESSFUL'.

---

### Task 5: Extract Hyper Thermal Convector

**Files:**
- Create: 'registry/machine/HyperThermalConvectorDefinition.java'
- Create: 'data/recipe/HyperThermalConvectorRecipes.java'
- Modify: 'registry/TSTMachines.java:498-791,2407-2410'
- Modify: 'data/TSTRecipes.java:539-808'
- Modify: 'TSTModernGTAddon.java'

**Interfaces:**
- Consumes: 'HyperThermalConvectorMachine::recipeModifier'.
- Produces: 'HyperThermalConvectorDefinition.MACHINE' and 'HyperThermalConvectorRecipes.register(...)'.

- [ ] **Step 1: Move the definition**

Move '.multiblock("hyper_thermal_convector", ...)' through its matching '.register()' verbatim.

- [ ] **Step 2: Move processing and construction recipes**

The module owns:

- rapid heat exchange and cooling recipes;
- Dense Steam usage recipes;
- controller recipe;
- the three dedicated casing recipes.

Use:

~~~java
.outputItems(HyperThermalConvectorDefinition.MACHINE)
~~~

- [ ] **Step 3: Rewire, remove, and compile**

Replace both old addon calls with one:

~~~java
HyperThermalConvectorRecipes.register(provider);
~~~

Initialize the definition, remove moved blocks, and run 'compileJava --offline'. Expected: 'BUILD SUCCESSFUL'.

---

### Task 6: Consolidate and extract Mega Tree Farm

**Files:**
- Modify: 'machine/MegaTreeFarmMachine.java'
- Delete: 'machine/MegaTreeFarmMath.java'
- Create: 'registry/machine/MegaTreeFarmDefinition.java'
- Create: 'data/recipe/MegaTreeFarmRecipes.java'
- Modify: 'registry/TSTMachines.java:794-2393,2407-2410'
- Modify: 'data/TSTRecipes.java:813-1100'
- Modify: 'TSTModernGTAddon.java'

**Interfaces:**
- Consumes: current Mega Tree Farm processing behaviour and recipe values.
- Produces: 'MegaTreeFarmDefinition.MACHINE', 'MegaTreeFarmRecipes.register(...)', and the narrow output-mode interface.

- [ ] **Step 1: Run the failing consolidation assertion**

~~~powershell
if (Test-Path -LiteralPath 'src/main/java/com/tstmodern/machine/MegaTreeFarmMath.java') {
  throw 'MegaTreeFarmMath has not been consolidated'
}
~~~

Expected: FAIL.

- [ ] **Step 2: Move math into the machine class**

Move 'ProcessingProfile', water constants, tier multiplier, tree/aquatic profile calculation, EU calculation, and saturating helpers into 'MegaTreeFarmMachine'. Keep those members private.

Expose:

~~~java
public enum OutputMode {
    LOG, SAPLING, LEAVES, FRUIT
}

public static int baseOutputAmount(OutputMode mode) {
    return switch (mode) {
        case LOG -> 20;
        case SAPLING -> 3;
        case LEAVES -> 8;
        case FRUIT -> 1;
    };
}
~~~

Update 'recipeModifier' to call the private members directly, then delete 'MegaTreeFarmMath.java'.

- [ ] **Step 3: Move the complete 33-aisle definition**

Move '.multiblock("mega_tree_farm", ...)' through its matching '.register()' verbatim into 'MegaTreeFarmDefinition.MACHINE'. Do not reformat pattern strings.

- [ ] **Step 4: Move all Mega Tree recipes**

The module owns its controller, dedicated casings, all tree recipes, and aquatic recipe. Use 'MegaTreeFarmDefinition.MACHINE' for the controller and replace mode calls with:

~~~java
MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG)
~~~

Use the corresponding enum value for each output category.

- [ ] **Step 5: Rewire and remove old ownership**

Initialize the definition from the temporary registry, update the addon, and delete moved blocks.

- [ ] **Step 6: Verify consolidation and compile**

Re-run the Step 1 assertion; expected exit code 0. Then run 'compileJava --offline'; expected 'BUILD SUCCESSFUL'.

---

### Task 7: Replace the temporary registry and delete monoliths

**Files:**
- Create: 'registry/machine/TSTMachineRegistry.java'
- Modify: 'TSTModern.java:29'
- Modify: 'TSTModernGTAddon.java'
- Delete: 'registry/TSTMachines.java'
- Delete: 'data/TSTRecipes.java'

**Interfaces:**
- Consumes: five definition constants and five recipe registration methods.
- Produces: final lifecycle and recipe registration flow.

- [ ] **Step 1: Create the lifecycle adapter**

~~~java
public final class TSTMachineRegistry {
    private TSTMachineRegistry() {}

    public static void registerMachines(
            GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        var megaStoneBreaker = MegaStoneBreakerDefinition.MACHINE;
        var giantVacuumDryingFurnace = GiantVacuumDryingFurnaceDefinition.MACHINE;
        var netherInterface = NetherInterfaceDefinition.MACHINE;
        var hyperThermalConvector = HyperThermalConvectorDefinition.MACHINE;
        var megaTreeFarm = MegaTreeFarmDefinition.MACHINE;
    }
}
~~~

These variables intentionally force deterministic class initialization during the registry event.

- [ ] **Step 2: Switch the Forge listener**

Replace 'TSTMachines::registerMachines' with 'TSTMachineRegistry::registerMachines'.

- [ ] **Step 3: Confirm direct recipe registration**

'TSTModernGTAddon.addRecipes' contains exactly:

~~~java
MegaStoneBreakerRecipes.register(provider);
GiantVacuumDryingFurnaceRecipes.register(provider);
NetherInterfaceRecipes.register(provider);
HyperThermalConvectorRecipes.register(provider);
MegaTreeFarmRecipes.register(provider);
~~~

- [ ] **Step 4: Delete the monoliths and search references**

~~~powershell
rg -n "TSTMachines|TSTRecipes" src/main/java
~~~

Expected: no matches.

- [ ] **Step 5: Compile**

Run 'compileJava --offline'. Expected: 'BUILD SUCCESSFUL'.

---

### Task 8: Verify architecture and production build

**Files:**
- Verify: all target source modules.
- Verify: 'build/libs/tstmodern-1.0-SNAPSHOT.jar'.

**Interfaces:**
- Consumes: complete refactor.
- Produces: completion evidence.

- [ ] **Step 1: Re-run the architecture gate from Task 1**

Expected: exit code 0 and no missing modules.

- [ ] **Step 2: Verify obsolete files and references are absent**

~~~powershell
$obsolete = @(
  'src/main/java/com/tstmodern/registry/TSTMachines.java',
  'src/main/java/com/tstmodern/data/TSTRecipes.java',
  'src/main/java/com/tstmodern/machine/MegaTreeFarmMath.java'
)
$remaining = $obsolete | Where-Object { Test-Path -LiteralPath $_ }
if ($remaining) { throw "Obsolete modules remain: $($remaining -join ', ')" }
rg -n "TSTMachines|TSTRecipes|MegaTreeFarmMath" src/main/java
~~~

Expected: no obsolete files and no matches.

- [ ] **Step 3: Verify module counts**

~~~powershell
$definitions = rg -l "public static final MultiblockMachineDefinition MACHINE" src/main/java/com/tstmodern/registry/machine
$recipes = rg -l "public static void register\(Consumer<FinishedRecipe> provider\)" src/main/java/com/tstmodern/data/recipe
if ($definitions.Count -ne 5) { throw "Expected 5 definitions, got $($definitions.Count)" }
if ($recipes.Count -ne 5) { throw "Expected 5 recipe modules, got $($recipes.Count)" }
~~~

Expected: exit code 0.

- [ ] **Step 4: Check patch hygiene**

~~~powershell
git diff --check
~~~

Expected: no trailing-whitespace errors. Existing line-ending warnings are not failures.

- [ ] **Step 5: Run the full production build**

~~~powershell
.\gradlew.bat -g 'C:\Users\mtien\.gradle' build --offline
~~~

Expected: 'BUILD SUCCESSFUL', exit code 0, and a fresh JAR.

- [ ] **Step 6: Review intended paths**

~~~powershell
git status --short
git diff -- src/main/java/com/tstmodern/TSTModern.java src/main/java/com/tstmodern/TSTModernGTAddon.java src/main/java/com/tstmodern/machine src/main/java/com/tstmodern/registry/machine src/main/java/com/tstmodern/data/recipe
~~~

Confirm unrelated user edits remain untouched and report the known 'runData' issue separately.
