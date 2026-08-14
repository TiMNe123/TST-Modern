# Mega Stone Breaker reference port

This project is a Java/Forge 1.20.1 reference port of one Twist Space Technology multiblock.
It targets Java 17, Forge 47.3.0 and GTCEu Modern 7.4.0. It is intended to be copied when
porting the remaining TST machines.

## Source of truth

No machine data was taken from the planning spreadsheet.

- TST controller and structure:
  `src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaStoneBreaker.java`
- TST processing recipes:
  `src/main/java/com/Nxer/TwistSpaceTechnology/recipe/machineRecipe/expanded/MegaStoneBreakerRecipePool.java`
- TST recipe map:
  `src/main/java/com/Nxer/TwistSpaceTechnology/common/recipeMap/GTCMRecipe.java`
- TST controller assembly-line recipe:
  `src/main/java/com/Nxer/TwistSpaceTechnology/recipe/craftRecipe/machine/GTCMMachineRecipes.java`
- TST registration:
  `src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/MachineLoader.java`
- Source revision inspected: `73571514cf8a1527db4975680456748ba32f373d`

The modern registration layout follows Star Technology's public GTCEu multiblock example:
`kubejs/startup_scripts/machines/multiblocks/ore_factories/ore_processing_plant.js` in
`StarT-Dev-Team/Star-Technology`. The Java implementation uses the equivalent GTCEu APIs:
`GTRegistrate.multiblock`, `FactoryBlockPattern`, `Predicates`, recipe types and recipe modifiers.

## Behaviour preserved

- Recipe EU/t and duration are unchanged.
- Imperfect overclocking, 100% speed and 100% EU multiplier are unchanged.
- Maximum parallel is `4 * 2^voltageTier`, capped at `Integer.MAX_VALUE`.
- One optional GTCEu Parallel Control Hatch may replace the fourth front `J` casing. Its configured
  value is added to the TST base limit; the standard multiplicative parallel-hatch modifier is not
  applied a second time.
- Normal item output is multiplied by 4.
- If at least 1,000 mB water and 1,000 mB lava are available at recipe start, output is
  multiplied by 1,024.
- Boost mode consumes 1,000 mB water and 1,000 mB lava every 20 running ticks. Failure stops
  the active recipe, matching TST's `onRunningTick` return value.
- TST passes its `8 x 9 x 21` source matrix through StructureLib's `transpose(shapeMain)`.
  The GTCEu pattern therefore has 9 aisles, 8 rows and width 21, with the controller at
  `(x=10, y=7, z=2)`, matching TST's construction offsets exactly.
- TST's zero-count Et Futurum catalyst stacks are represented by GTCEu non-consumable inputs.
- Maintenance follows the native GTCEu configuration: when maintenance is enabled, exactly one
  Maintenance Hatch (including compatible automatic/configurable variants) must replace an `F`
  Large Scale Assembling Casing. This matches the controller's formed casing model and avoids a
  Large Scale Assembling texture appearing in a Stress-Proof Casing position. When a modpack
  disables maintenance globally, the pattern does not require it.
- The `J` positions use GTCEu's `autoAbilities(definition.getRecipeTypes())` to derive item I/O and
  energy hatches from the recipe map. Parallel support uses `autoAbilities(false, false, true)`;
  the two dedicated water/lava positions remain explicit fluid-import hatches.
- GTCEu 7.4.0 can create a visible JEI overclock tier label with an undersized or misplaced click
  target for addon recipe types. A client-only mixin adds a slightly expanded invisible target for
  `tstmodern` recipes while preserving native controls: left-click raises tier, right-click lowers
  tier, middle-click resets, and Shift selects perfect overclocking. The fallback target uses the
  label's local/self coordinates because it is added after the label has already been parented.

## Structure-block mapping

The pattern now uses native GTCEu blocks wherever a progression-appropriate equivalent exists:
Stable Titanium, Tungstensteel Pipe, ZPM Machine Casing, Large Scale Assembling, Stress Proof,
Fusion Casing MK I and Corrosion Proof. Only Advanced Iridium Casing and the visually preserved
Cosmic Neutronium Frame remain required local structure blocks. The older A/B/D/F/G/H/I registry
entries remain registered only to avoid missing block IDs in worlds created with an earlier
development build; the machine no longer accepts them.

Advanced Iridium Casing is available at IV, matching GTCEu's first complete Iridium production
chain. Cosmic Neutronium Frame is synthesized at ZPM from a Naquadah Alloy frame, six Naquadah
Alloy plates and 576 mB Soldering Alloy, producing two frame blocks. This keeps the original
129-frame structure practical at the beginning of ZPM without introducing UHV Neutronium.

The eight Extra Utilities compressed-cobblestone levels remain local because Extra Utilities has
no 1.20.1 dependency in this port. Each level is produced from nine blocks of the previous level in
a GTCEu Compressor.

## Known integration boundary

GTCEu 7.4.0's JEI multiblock preview can create a proxy slot named `slot_21` while its flat widget
list only contains indexes 0-20. Hovering that slot throws `IndexOutOfBoundsException` in
`MultiblockInfoCategory$1ProxyRecipeWidget`. `MultiblockInfoSlotGuardMixin` validates the synthetic
slot suffix before GTCEu indexes the widget list. Invalid and out-of-range slots are ignored; valid
slots continue through the original code so JEI hover, R/U and click navigation remain available in
the 3D preview. Remove this compatibility mixin after upgrading to a GTCEu version where the
upstream lookup is bounds-checked.

GTCEu 7.4.0 has no built-in "water-only" and "lava-only" multiblock abilities. The L and W
positions therefore require two ordinary fluid import hatches, while runtime code still checks and
drains the exact fluids and quantities. A later refinement may register two custom part abilities
to enforce the fluid type in the structure preview itself.

The controller is intentionally rebalanced as an early-ZPM utility machine because modern 1.20.1
packs commonly provide cheap or infinite cobblestone through storage and resource-generation mods.
Its Assembly Line recipe is always registered and uses two ZPM hulls, four ZPM Rock Crushers,
16 ZPM Robot Arms, eight ZPM Electric Pumps, 16 Large Iridium Fluid Pipes, 16 Naquadah Alloy plates,
16 Uranium-Rhodium-Dinaquadide plates, four tier-4 Compressed Cobblestone, 9,216 mB Soldering Alloy,
and 64,000 mB each of water and lava. It runs for 1,200 ticks at ZPM. A ZPM Rock Crusher is scanned
for 1,200 ticks at LuV; no Research Station, Data Module or `highTierContent` setting is required.
The original structure, processing recipes, parallel scaling and water/lava output multiplier remain
unchanged.

## Build

Use JDK 17 for the Gradle JVM and Java toolchain. IDEA itself may run on another JDK, but the project
build must remain on 17. The produced jar is compiled against Forge 47.3.0; the metadata accepts
Forge 47.3.x and 47.4.x and requires GTCEu `[7.4.0, 8.0.0)`.
