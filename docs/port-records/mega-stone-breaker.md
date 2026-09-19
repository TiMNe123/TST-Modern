# Mega Stone Breaker port record

## Identity and baseline

- Source controller: `TST_MegaStoneBreaker`
- Source recipe pool: `MegaStoneBreakerRecipePool`
- Source baseline: TST commit `73571514cf8a1527db4975680456748ba32f373d`
- Target: Minecraft 1.20.1, GTCEu 7.4.0
- Current behavior hash: `00EBF287D288F27E8006CB99FF1745A4EA47FFCC5453E3F6C005090A181DCFBB`
- Current definition hash: `A55158013FB9453DCBF627DA0772627E477EF70735D552AF7A7E054CBE96CBA2`
- Current recipe hash: `A67A22163D5C0875730C2B443DC3E9A276F405CBD3239104A13970A824CEDC10`

## Source behavior and approved target

- Recipe map: fourteen circuit-selected stone recipes; retained exactly.
- Parallel: `4 * 2^tier`; GTCEu Parallel Hatch adds to the base.
- Output multiplier: x4, or x1024 with water and lava boost.
- Boost cost: 1,000 mB water plus 1,000 mB lava every twenty active ticks, including the first active tick.
- Overclock: GTCEu non-perfect overclock retained.
- Structure: 21 x 8 x 9, unchanged.
- L/W remain fixed input positions, but combined-fluid lookup is an approved Modern deviation because no dedicated water/lava hatch types are registered.

## Implementation ownership

- Behavior: `machine/MegaStoneBreakerMachine.java`
- Definition: `registry/machine/MegaStoneBreakerDefinition.java`
- Recipes: `data/recipe/MegaStoneBreakerRecipes.java`
- Casing evidence: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/MegaStoneBreaker.json`

## Validation status

- Recipe formula summary: fourteen processing recipes locked by the catalog contract: ten `basic(...)` registrations plus four direct `recipe("...")` registrations for `obsidian`, `netherrack`, `basalt`, and `cobbled_deepslate`.
- Focused JUnit: `.\gradlew.bat test --tests com.tstmodern.data.recipe.PortRecipeCatalogContractTest` -> GREEN on August 24, 2026 (`4 tests`, `0 failures`, `BUILD SUCCESSFUL in 41s`).
- Full build: `.\gradlew.bat clean test compileJava processResources build` -> `BUILD SUCCESSFUL` on August 24, 2026.
- Catalog validator: `node C:/Users/mtien/IdeaProjects/TST-Modern/.agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs` -> `Casing catalog valid: 25 machines, 408 entries.`
- Deviation status: approved spec deviations unchanged; combined-fluid lookup for the fixed L/W positions remains the recorded Modern replacement for dedicated water/lava hatches.
- Final status: build validated; gameplay validation pending
