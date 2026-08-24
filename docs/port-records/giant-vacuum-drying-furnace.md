# Giant Vacuum Drying Furnace port record

## Identity and baseline

- Source controller: `TST_GiantVacuumDryingFurnace`
- Source maps: GT++ Vacuum Furnace and Chemical Dehydrator Non-Cell
- Source baseline: TST commit `73571514cf8a1527db4975680456748ba32f373d`
- Target: Minecraft 1.20.1, GTCEu 7.4.0
- Current behavior hash: `631F2CD545614965B7A2D05E42B9A273B95D01D04A52D02638029636868D4443`
- Current definition hash: `EB33271F75BB210209BA5E90E587D39DA3AFE08AD684A2431D12AC74A0F5D686`
- Current recipe hash: `3A54910001A5C263D6B9547B318E78991D94DB4753DA21846ADCEF63B2A39AE4`

## Source behavior and approved target

- Modes: Vacuum Furnace and Chemical Dehydrator; GTCEu mode UI and persistence retained.
- Source parallel: `piece * coilTier * 32`, where TST derives `coilTier` as the legacy coil tier plus one.
- Target parallel: one fixed piece times `(GTCEu coil tier + 1) * 32`, plus Parallel Hatch.
- TST duration multiplier: `0.8^machineTier / (coilTier * 0.5)` before GTCEu non-perfect overclock.
- The dynamic source tower is deliberately fixed to one segment.
- Missing dependency decision: GT++ maps have no target equivalent. The current seven dehydrator and ten vacuum recipes are the approved Modern catalog.

## Implementation ownership

- Behavior: `machine/GiantVacuumDryingFurnaceMachine.java`
- Definition: `registry/machine/GiantVacuumDryingFurnaceDefinition.java`
- Recipes: `data/recipe/GiantVacuumDryingFurnaceRecipes.java`
- Casing evidence: `.agents/skills/port-tst-multiblock-gtceu/references/casing-catalog/GiantVacuumDryingFurnace.json`

## Validation status

- Recipe formula summary: seventeen approved Modern processing IDs locked by the catalog contract: seven `chemical_dehydrator/...` recipes and ten `vacuum_furnace/...` recipes.
- Focused JUnit: `.\gradlew.bat test --tests com.tstmodern.data.recipe.PortRecipeCatalogContractTest` -> GREEN on August 24, 2026 (`4 tests`, `0 failures`, `BUILD SUCCESSFUL in 41s`). RED proof before GREEN: the same command failed intentionally while the temporary sentinel `vacuum_furnace/should_fail_red` was present, proving the contract catches count/ID drift.
- Full build: `.\gradlew.bat clean test compileJava processResources build` -> `BUILD SUCCESSFUL` on August 24, 2026.
- Catalog validator: `node C:/Users/mtien/IdeaProjects/TST-Modern/.agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs` -> `Casing catalog valid: 25 machines, 408 entries.`
- Deviation status: approved spec deviations unchanged; the single fixed segment, zero-based GTCEu coil conversion, `32 x coilTier` parallel scaling, and retained Modern recipe maps remain the validated contract.
- Final status: build validated; gameplay validation pending
