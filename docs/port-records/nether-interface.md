# Nether Interface port record

## Identity and baseline

- Source controller: `TST_NetherInterface`
- Source visual recipe: `NetherInterfaceVisualRecipePool`
- Source baseline: TST commit `73571514cf8a1527db4975680456748ba32f373d`
- Target: Minecraft 1.20.1, GTCEu 7.4.0
- Current behavior hash: `3852ECD6A9D1DE92CF6158FA1C7D79D0698C61628C8FCD81B7628F0376A37456`
- Current definition hash: `40AA3981C0ED8625EEDCADF969AACF0173F828FD8BC6218EEED5D798BB9745C9`
- Current recipe hash: `70B53FAB0501781BC2ADC97173C7EF048EB11D0A8EEBC508345A01B7620B3953`

## Source behavior and approved target

- Fluid transformation: 16,000 mB Distilled Water to 16,000 mB Poor Nether Waste per parallel.
- Base parallel: 64 plus the approved GTCEu Parallel Hatch contribution.
- Power: two IV amperes maintenance plus one IV ampere per actual parallel.
- Items: exactly three weighted package selections with replacement per processing cycle, then package amount multiplied by parallel.
- Approved Modern packages retain their current identities and quantities with source weights `1/49/30/10/10`.
- Hellish Metal: one 30% roll per processing cycle, yielding `288 mB * parallel`.
- Missing dependency decisions: vanilla Modern outputs replace Et Futurum, GT5 item packages, and Thaumic Tinkerer content. Poor Nether Waste becomes a dedicated TSTModern fluid.
- Hellish Metal bootstrap remains required and owned by this module.

## Implementation ownership

- Behavior: `machine/NetherInterfaceMachine.java`
- Definition: `registry/machine/NetherInterfaceDefinition.java`
- Recipes: `data/recipe/NetherInterfaceRecipes.java`
- Chance logic: shared TSTModern recipe/chance registration used only where exact legacy roll semantics require it.
- Casing evidence: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/NetherInterface.json`

## Validation status

- Recipe contract summary: the catalog contract locks exactly one `nether_interface/dimensional_harvesting` builder, preserves the `DistilledWater -> Poor Nether Waste` core fluid path, and excludes Lava / Liquid Nether Air references from that core recipe statement.
- Behavior and definition coverage: the focused Nether logic/definition tests plus the full automated suite remain green for the exact three weighted package selections, one 30% Hellish Metal fluid roll, additive parallel hatch behavior, two-IV-amp reserve, and no post-cap overclock reintroduction.
- Focused JUnit: `.\gradlew.bat test --tests com.tstmodern.data.recipe.PortRecipeCatalogContractTest` -> GREEN on August 24, 2026 (`4 tests`, `0 failures`, `BUILD SUCCESSFUL in 41s`).
- Full build: `.\gradlew.bat clean test compileJava processResources build` -> `BUILD SUCCESSFUL` on August 24, 2026.
- Catalog validator: `node C:/Users/mtien/IdeaProjects/TST-Modern/.agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs` -> `Casing catalog valid: 25 machines, 408 entries.`
- Deviation status: approved spec deviations unchanged; the exact three Modern package replacements and dedicated Poor Nether Waste fluid remain the recorded replacements for missing legacy dependencies.
- Final status: build validated; gameplay validation pending
