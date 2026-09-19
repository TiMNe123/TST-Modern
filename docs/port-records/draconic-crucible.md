# Draconic Crucible port record

## Identity and baseline

- Name: Draconic Crucible.
- Source: new TST Modern machine governed by the user-approved design evidence; no legacy TST controller or inheritance applies.
- Evidence: `.agents/skills/port-tst-multiblock-gtceu/evidence/draconic-crucible-design.md`.
- Contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/draconic-crucible.json`.
- Branch and baseline: clean `dev` checkout before implementation.
- Target: Minecraft 1.20.1, Forge, GTCEu 7.4.

## Source behavior

- Purpose: UHV batch processing for Draconium ore and Awakened Draconium.
- Recipe type: `tstmodern:draconic_crucible`; fixed parallel 1, no overclocking, no chance boost.
- Power: `VA[UHV]` (1,966,080 EU/t); one or two energy inputs, with one UHV input sufficient.
- Recipe 1: 64 Draconium Ore -> guaranteed 64 Draconium Dust plus one mutually-exclusive roll:
  84.5% 16 dust, 10% 2 ore, 5% 1 Dragon Heart, or 0.5% 1 Awakened Draconium Nugget;
  400 ticks.
- Recipe 2: 4 Draconium Ingots + 6 Draconium Cores + 1 Dragon Heart ->
  4 Awakened Draconium Ingots; 1,000 ticks.
- Draconic Evolution compatibility: use its exact registered forms when loaded; otherwise use TST Modern fallback content.
- Fallback ore: The End only, Y 10..80, cluster 16..24, density 0.20, weight 5. Starcore Miner unlocks it at UIV.

## Structure

- Dimensions: 61 wide x 15 high x 45 deep.
- Axes: source `+X/+Y/+Z` maps to GTCEu `RIGHT/+X`, `DOWN/-Y`, `BACK/+Z`.
- Controller: `(30,1,0)`, outward-facing front.
- Fixed abilities: one item input, one item output, and one or two energy inputs at the approved front positions.
- The volcano is centered at `(20,*,22)` and uses monotonically shrinking rounded ellipses from
  its broad foot to the crater. Its Obsidian shell, Stabaloy heat liner, Field Restriction rim,
  Fusion Coil core, pipe ribs, and right-side technology wing are frozen in the machine contract.
- Dragon Egg core: one non-teleporting dedicated block using the native Dragon Egg model.

## Overlap decision

- Result: Port.
- GTCEu has no machine combining the approved Draconium batch table, Awakened conversion, End-only progression,
  optional Draconic Evolution substitution, and the approved volcano/dragon presentation.

## Approved deviations

- This is a user-designed Modern machine, not inferred legacy geometry.
- Field Restriction Coil T1 replaces the visual T4 proposal so the UHV machine is obtainable.
- Draconic Evolution's block-form Awakened recipe is adapted to the approved ingot forms and UHV timing.
- The fallback material/worldgen path avoids making Draconic Evolution mandatory.
- Visual design: Radiant Naquadah base, native Fusion Reactor overlay, native Dragon Egg model,
  and the exact Ice and Fire Stage 5 red Fire Dragon model, six-frame flight cycle, breath pose, skin,
  eye texture, and Dragon Flame particle from commit `0526aad94119b705a86207623481b087c4d052ca`. The client-only renderer
  loads the original `.tbl` assets without requiring Ice and Fire or Citadel. The dragon lies across the
  machine and is retreated 18 blocks to the relative-right side so the flame stream travels right-to-left
  into the crater; it has no world entity or collision.

## Implementation ownership

- Behavior: `machine/DraconicCrucibleMachine.java`.
- Structure/registration/rendering: `registry/machine/DraconicCrucibleDefinition.java` and client renderer.
- Recipes and dependency closure: `data/recipe/DraconicCrucibleRecipes.java`.
- Shared registrations: `TSTBlocks`, `TSTItems`, `TSTRecipeTypes`, machine/recipe registries,
  and Starcore Miner output policy.

## Validation evidence

- Machine contract: passed after final user approval.
- Structure/count checks: 61x15x45 tapered-volcano rules and all symbol totals passed.
- Casing catalog, formed appearance, localization, focused tests, and full build passed before the
  Stage 5 model replacement. The source texture lock must be rerun for the exact imported dragon assets.
- Normal-world client load and compilation of the 61x15x45 JEI multiblock preview passed without a crash.
- Formation of the enlarged structure, full recipe operation, and optional Draconic Evolution integration
  remain pending gameplay checks.
- Normal-world client load now renders the exact Stage 5 red skin without a permanent hurt overlay and
  the Ice and Fire Dragon Flame particle stream without the rejected custom cylinder beam.
- Current level: build validated; full machine gameplay and JEI validation pending.
