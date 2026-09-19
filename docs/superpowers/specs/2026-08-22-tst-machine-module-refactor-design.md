# TST machine module refactor

## Goal

Replace the monolithic `TSTMachines` and `TSTRecipes` classes with one definition class and one recipe class per currently ported multiblock. Keep the existing flat `com.tstmodern.machine` package for machine behaviour classes. Merge `MegaTreeFarmMath` into `MegaTreeFarmMachine` as internal processing logic.

This is a structural refactor. Machine IDs, recipe IDs, recipe values, structures, textures, tooltips, casing mappings, recipe modifiers, and runtime behaviour must not change.

## Scope

The refactor covers all five currently registered multiblocks:

1. Mega Stone Breaker
2. Giant Vacuum Drying Furnace
3. Nether Interface
4. Hyper Thermal Convector
5. Mega Tree Farm

`TSTBlocks`, `TSTRecipeTypes`, materials, assets, mixins, and the casing texture audit are outside the refactor unless an import must be updated.

## Target layout

```text
src/main/java/com/tstmodern/
  machine/
    MegaStoneBreakerMachine.java
    GiantVacuumDryingFurnaceMachine.java
    NetherInterfaceMachine.java
    HyperThermalConvectorMachine.java
    MegaTreeFarmMachine.java

  registry/machine/
    MegaStoneBreakerDefinition.java
    GiantVacuumDryingFurnaceDefinition.java
    NetherInterfaceDefinition.java
    HyperThermalConvectorDefinition.java
    MegaTreeFarmDefinition.java
    TSTMachineRegistry.java

  data/recipe/
    MegaStoneBreakerRecipes.java
    GiantVacuumDryingFurnaceRecipes.java
    NetherInterfaceRecipes.java
    HyperThermalConvectorRecipes.java
    MegaTreeFarmRecipes.java
```

The old `registry/TSTMachines.java`, `data/TSTRecipes.java`, and `machine/MegaTreeFarmMath.java` are removed after all callers are migrated.

## Module interfaces and ownership

Each `*Definition` class exposes exactly one public machine definition constant named `MACHINE`. Its implementation owns the complete Registrate builder chain for that machine: machine ID, constructor, recipe types and modifiers, appearance casing, structure pattern, predicates, model, and tooltips.

Each `*Recipes` class exposes exactly one public method:

```java
public static void register(Consumer<FinishedRecipe> provider)
```

That method owns every recipe specific to its machine, including processing recipes, the controller recipe, and recipes for dedicated structure blocks.

Ownership of the existing shared-looking recipes is resolved as follows:

- Mega Stone Breaker owns compressed cobblestone, Advanced Iridium Casing, Cosmic Neutronium Frame, and its controller recipe.
- Giant Vacuum Drying Furnace owns Vacuum Casing and its controller recipe.
- Nether Interface owns its Hellish Metal bootstrap, Mechanically Enhanced Obsidian, and controller recipes.
- Hyper Thermal Convector owns its three dedicated casings, controller recipe, and Dense Steam usage recipes.
- Mega Tree Farm owns its controller, dedicated casing recipes, tree recipes, and aquatic recipe.

Recipe helper methods remain private inside their owning recipe class.

## Registration flow

`TSTMachineRegistry` is a small lifecycle adapter. Its public `registerMachines` method is the listener used by `TSTModern`. The method explicitly initializes the five definition classes in a stable order. It contains no structure, recipe, or gameplay logic.

`TSTModernGTAddon.addRecipes` calls the five `register(provider)` methods directly. No second recipe facade is introduced.

Controller recipes reference their owning definition's `MACHINE` constant. Code outside a machine module must no longer reference `TSTMachines` or `TSTRecipes`.

## Mega Tree Farm consolidation

`MegaTreeFarmMath` becomes private implementation inside `MegaTreeFarmMachine`. The following remain available to the machine implementation without becoming a second public module:

- the original TST tier multiplier formula;
- output-mode multipliers;
- tree and aquatic processing profile calculation;
- saturating arithmetic helpers.

`MegaTreeFarmRecipes` needs the four base output-mode quantities. `MegaTreeFarmMachine` therefore keeps `OutputMode` as a public nested enum and exposes one narrow static method, `baseOutputAmount(OutputMode)`. All other profile and arithmetic helpers are private implementation. This keeps one source of truth without exposing the full processing calculation.

No multiplier, fluid-cost, EU/t, duration, chance, or parallel behaviour may change during this consolidation.

## Compatibility requirements

- Preserve all existing registry names and resource locations.
- Preserve static initialization before recipe registration.
- Preserve all five machine definition object identities within a game run.
- Preserve the current controller recipe outputs.
- Preserve machine class constructors and recipe modifier method references.
- Do not change localization keys or asset paths.
- Do not regenerate structures or recipes mechanically from another source.

## Verification

Before moving production code, run an architecture check that fails because the target per-machine files do not yet exist. After the refactor, the same check must pass and confirm that the three obsolete monolithic files are gone.

Then verify:

1. No Java reference to `TSTMachines`, `TSTRecipes`, or `MegaTreeFarmMath` remains.
2. Exactly five `MultiblockMachineDefinition` constants are present in the new definition classes.
3. `TSTModern` still subscribes one machine registry listener.
4. `TSTModernGTAddon` registers exactly five recipe modules.
5. `gradlew build --offline` exits successfully and produces the mod JAR.

The existing unrelated `runData` failure caused by the missing GTCEu machine template model is not part of this refactor and is not used as its completion gate.
