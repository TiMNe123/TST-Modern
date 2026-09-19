# Galactic Armillary design record

## Identity and baseline

- Display name: Galactic Armillary
- Registry ID: `tstmodern:galactic_armillary`
- Source authority: user-approved original visual design, recorded in `.agents/skills/port-tst-multiblock-gtceu/evidence/galactic-armillary-design.md`
- Target dependency: GTCEu 1.20.1 7.4.0
- Machine contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/galactic-armillary.json`
- Implemented scope: structure/controller, fixed-energy ignition buffer, UEV recipe chain, conditional Re-Avaritia outputs, and formed/stable-only visibility for the existing galaxy renderer.
- Implemented non-render behavior: the cold core must accumulate `2,560,000,000 EU`; uninterrupted consecutive recipes reuse the stable core without charging again. Satellite light, beam, expanding core, and harmless ignition remain deferred render work.

## Structure

- Dimensions: `41 x 39 x 41`
- Axis mapping: design `X/Y/Z` to GTCEu `RIGHT/+X`, `DOWN/-Y`, `BACK/+Z`
- Controller: `~`, one occurrence at `(20, 3, 20)`, directly above the center of the 5x5 service pad
- `D`: Extreme Density Casing, black foundation/arches/crown
- `H`: High Power Casing, cyan platform/crown inlays and the service pad's 3x3 center; exactly one item input and one item output bus may replace `H` positions
- `E`: the 16-block Astral Pylon perimeter of the centered 5x5 service pad; only these positions accept UEV+ Energy Input Hatches, with 1-16 hatches allowed
- `I`: High Power Casing, radius-9 ring tilted +30 degrees from horizontal and stretched 1.5x on Y
- `O`: High Power Casing, radius-9 ring tilted -30 degrees from horizontal and stretched 1.5x on Y
- `A`: Astral Pylon Casing, purple field nodes including the two physical ring intersections on the front/back axis
- Fusion Glass, the four inner pillars, and the small horizontal cyan ring are absent; all four large outer arches remain.
- A required physical satellite shell is centered at `(20,37,20)`: radius-9 horizontal Extreme Density ring, four cardinal framed High Power wings, two opposite Astral Pylon capacitors, and a three-layer 7x7 open housing. Its cannon consists of three connected hollow rounded rings at each diameter: 7x7 at Y=35..33, 5x5 at Y=32..30, and High Power 3x3 at Y=29..27. The final two 3x3 rings retain only their four corners; the full center axis remains open for the future beam.
- Counts: `~1`, `D5358`, `H291`, `E16`, `I112`, `O112`, `A72`, and `59597` unconstrained spaces.

## Overlap and progression

- Result: approved original UEV progression machine, not a legacy TST gameplay port.
- Recipe type: `tstmodern:galactic_armillary`; parallel is fixed at 1 and recipe voltage is fixed at UEV.

## Ignition behavior

- One bilaterally symmetric satellite remains fixed at the top-center axis and fires a vertical plasma beam into the core.
- Before the first recipe of a cold session, the machine drains available hatch energy until the persistent core buffer reaches exactly `2,560,000,000 EU`.
- Hatch count changes charge throughput only. It does not overclock recipes. Startup also requires the configurable `galacticArmillaryStartupSeconds` warm-up (default `120`).
- Consecutive recipes share the stable core and do not recharge. Structure invalidation, disabled work, or an interrupted eligible-recipe chain resets the buffer.
- Charge/session state persists across chunk unload and server restart and synchronizes from server to client.
- Once stable, recipes consume fixed UEV voltage. Satellite/core animation remains deferred.

## Approved recipes

- Crafting: four Diamond Blocks, four Emerald Blocks, and one Nether Star produce one Diamond Lattice.
- EBF: 64 Diamond Lattice plus `16,000 mB` UU Matter produce 16 Crystal Matrix Ingots at `9,001 K`, ZPM, in 20 seconds.
- Galactic Armillary: Programmed Circuit configuration 1 and energy only produce one Infinity Catalyst Nugget at UEV in 5 seconds.
- Crafting: nine Infinity Catalyst Nuggets produce one Infinity Catalyst.
- Galactic Armillary: four Infinity Catalysts, 16 Neutronium Blocks, and 32 Crystal Matrix Ingots produce one Infinity Ingot at UEV in 10 seconds.
- When `avaritia` is installed, its Diamond Lattice, Crystal Matrix Ingot, Infinity Catalyst, and Infinity Ingot are used. Otherwise, the internal TSTModern equivalents are used.

## Texture authority

- Structure visuals reuse existing TSTModern models/textures; Astral Pylon uses the user-approved animated `cosmic_glass` sides and `cosmic_glass_pane_top` top/bottom supplied in `files.zip`.
- The controller exactly follows the locally audited Fornax Universi model: native GCYM High Temperature Smelting Casing with the native Fusion Reactor overlay.
- Fallback progression items use byte-identical original assets from local Avaritia and GT5 sources, including their source animation metadata; they no longer use generated GTCEu material icons.

## Implementation ownership

- Geometry: `registry/machine/GalacticArmillaryStructure.java`
- Definition: `registry/machine/GalacticArmillaryDefinition.java`
- Registration: one entry in `registry/machine/TSTMachineRegistry.java`
- Localization: `en_us.json` and `vi_vn.json`
- Behavior: `machine/GalacticArmillaryMachine.java`
- Recipes: `data/recipe/GalacticArmillaryRecipes.java`
- The approved charge/continuous-operation state machine is implemented without its deferred visual effects.
- The static satellite shell is part of the structure; satellite illumination, beam, core, and animation remain deferred.

## Validation evidence

- Contract validator: passed (`Machine contract valid: GalacticArmillary`)
- Structure/mapping checks: passed (`GalacticArmillaryStructureTest`)
- Contract, casing catalog, locked texture source/assets, localization (20 keys), formed appearance, targeted structure/state tests, and full build: passed after the UEV gameplay implementation.
- In-world/JEI structure preview: pending user gameplay validation
- Final level: build validated; gameplay validation pending
