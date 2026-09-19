# Port Record: 08_LargeNeutronOscillator

## Identification

- Source controller: `MM_LargeNeutronOscillator` (`D:/tmp/TST`)
- Recipe authorities: TST `NeutronActivatorWithEURecipePool`, GT5 GoodGenerator `NeutronActivatorLoader`, `NaquadahRecipeLoader`, and `RecipeLoader2`
- Target definition: `com.tstmodern.registry.machine.LargeNeutronOscillatorDefinition`
- Target recipes: `com.tstmodern.data.recipe.LargeNeutronOscillatorRecipes`
- Machine contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/large-neutron-oscillator.json`

## Source fidelity and deviations

- Structure is the exact 23×40×13 TST shape with controller offset `(3,36,1)` and `RIGHT, DOWN, BACK` axis mapping.
- TST modular execution cores are represented by one GTCEu Parallel Control Hatch; normal GTCEu electric overclocking remains enabled.
- The 12 source Neutron Activator recipes are ported with `EU/t = maxNKE²`.
- Compact Fusion Coil T3 keeps its original GoodGenerator identity, animated texture, duration and UV tier. Missing GT++ Internal Fusion Casing, Energy Core HV, Energy Crystal and Laurenium dependencies are mapped to native GTCEu Fusion Casing, Energy Cluster, Naquadah Alloy and Rhodium-Plated Palladium.
- The unavailable precise assembler is represented by Research Station plus Assembly Line. Coil T3 uses a green Data Orb; the controller uses a purple Data Module.
- Speeding Pipe preserves the original animated SIDE and distinct TOP textures.
- GTCEu 7.4 compatibility: generate the source-required Blue Alloy frame and Titanium Plasma forms, and attach a gas property to native Oganesson for the verified 250 mB output recipe.
- Approved dependency closure: Fusion Mk3 combines 16 mB Potassium + 16 mB Lithium into 16 mB Titanium Plasma, and 16 mB Iron + 16 mB Lithium into 16 mB Copper Plasma. Both follow GTCEu's two-input fusion convention.

## Validation evidence

- Contract, casing catalog, source texture, production texture, localization and mixed formed-appearance validators passed on 2026-09-04.
- Targeted recipe/resource tests passed.
- Full `gradlew build` passed on 2026-09-04.
- `runClient` loaded a world and completed JEI startup with zero fatal/null-fluid errors after the material-form compatibility fix on 2026-09-04.
- Plasma dependency regression tests, full `gradlew test build --no-daemon`, and dedicated-server startup passed on 2026-09-08; neither approved plasma recipe produced a registration error.
- Final level: `build validated; gameplay validation pending`. In-world structure, JEI and recipe execution remain user validation gates.
