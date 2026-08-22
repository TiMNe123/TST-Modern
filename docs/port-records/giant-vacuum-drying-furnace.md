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

- Audit complete.
- Implementation and gameplay validation pending.
