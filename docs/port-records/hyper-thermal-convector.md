# Hyper Thermal Convector port record

## Identity and baseline

- Source controller: `TST_HyperThermalConvector`
- Source generator: `RapidHeatExchangeRecipePool`
- Source baseline: TST commit `73571514cf8a1527db4975680456748ba32f373d`
- Target: Minecraft 1.20.1, GTCEu 7.4.0
- Current behavior hash: `DBA8319E74F9C7CF6F9BAF619596D5992957818578D7E3A42C1816A62C91948D`
- Current definition hash: `B059D04A9AF16BCBA86C358E4DA6B06B0DDF80489A2B2B4206631E402FA8E6A6`
- Current recipe hash: `6B8263D077DD3917E5533B1FB728BBAF8CA302F744959B67DA3D188A168B4BBE`

## Source behavior and approved target

- Modes: Rapid Heat Exchange and Rapid Cooling; GTCEu mode UI and persistence retained.
- Target base parallel: 128 in Heat Exchange and 16 in Rapid Cooling, plus Parallel Hatch.
- Recipe policy: retain the current hand-designed GTCEu Modern catalog and Dense Steam usage recipes.
- Missing dependency decision: TST dynamically imports GoodGenerator Extreme Heat Exchanger recipes, but GTCEu 7.4.0 has no equivalent source map. Static Modern recipes are an approved semantic replacement.
- Energy deviation: current UV EU/t remains for Modern balance although source thermal processing has no equivalent EU requirement.
- Water behavior: applicable heat recipes support vanilla Water and Distilled Water variants.
- Structure: 21 x 15 x 14, aisle text unchanged.
- Ability deviation: R/U are fixed imports and S/T fixed exports. Standard hatches do not enforce hot/water or cold/steam identity separately; four custom hatch types are deliberately excluded.

## Implementation ownership

- Behavior: `machine/HyperThermalConvectorMachine.java`
- Definition: `registry/machine/HyperThermalConvectorDefinition.java`
- Recipes: `data/recipe/HyperThermalConvectorRecipes.java`
- Casing evidence: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/HyperThermalConvector.json`

## Validation status

- Recipe and ability summary: the catalog contract locks seven approved rapid heat-exchange pair registrations, the seven stable distilled-water variant IDs emitted by the helper, the 128/16 base parallel split, additive Parallel Hatch behavior, and fixed R/U imports with S/T exports without reopening the generic G/N bypass.
- Focused JUnit: `.\gradlew.bat test --tests com.tstmodern.data.recipe.PortRecipeCatalogContractTest` -> GREEN on August 24, 2026 (`4 tests`, `0 failures`, `BUILD SUCCESSFUL in 41s`).
- Full build: `.\gradlew.bat clean test compileJava processResources build` -> `BUILD SUCCESSFUL` on August 24, 2026.
- Catalog validator: `node C:/Users/mtien/IdeaProjects/TST-Modern/.agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs` -> `Casing catalog valid: 25 machines, 408 entries.`
- Deviation status: approved spec deviations unchanged; the hand-authored Modern rapid heat-exchange catalog and seven distilled-water variants remain the approved substitute for the missing GoodGenerator dynamic source map.
- Final status: build validated; gameplay validation pending
