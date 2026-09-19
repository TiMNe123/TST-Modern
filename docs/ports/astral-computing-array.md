# Astral Computing Array port record

## Identity and baseline

- Source: `TST_Computer`, meta 19029, tier UEV; local authority `D:/tmp/TST`.
- Controller SHA-256: `677e11e5a63b281a7fc4af5f7434f84749fc68ccb0a00c761b850223445fe9d1`.
- Recipe authority: `GTCMMachineRecipes.java` SHA-256 `204f61d57387a2c0073f8dac762aff4bff2986d151acbf7c397907dc7299a3d4`.
- Target: Minecraft 1.20.1, GTCEu 7.4.0, branch `dev`, direct checkout (no worktree).
- Contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/astral-computing-array.json`.
- Approval: user approved the complete audit/deviation proposal on 2026-09-05.

## Source behavior

- Continuous optical computation provider; it has no processing recipe map.
- One 64-slot rack accepts LV-UEV circuit tiers and cooling parts using the audited GTNH computation, heat, reliability and destruction values.
- Overclock range 0-3; overvoltage range 0.7-2.0; UV base voltage and source amperage/coolant multiplier math remain intact.
- Coolants: PCB Coolant coefficient 0.001, dedicated source-identity Super Coolant 0.01, liquid Helium 0.1.
- Rack heat, component destruction, passive cooling, energy starvation and settings persist or synchronize as appropriate.
- Team-global wireless transport/update cards are omitted until a wireless computation consumer is ported.

## Structure

- Exact dimensions: 47 × 35 × 47; axes RIGHT=source column, DOWN=source row, BACK=source outer layer.
- Exact controller: `~`, one occurrence, offset 23,34,0, source front.
- Pattern is a lossless gzip/base64 representation of all 1,645 source rows; test locks every symbol count.
- E positions accept exactly one Astral rack, exactly one computation transmitter, at least one fluid import, and at least one energy/substation/laser input; formed parts inherit Internal Structure appearance.
- Quantum Glass maps to native Fusion Glass. Botania pylon meta 1 maps to the approved designed Astral Pylon casing.

## Overlap decision

- Inspected native GTCEu HPCA, optical hatches and network switch at tag `v7.4.0-1.20.1`.
- Result: Port. Astral retains a distinct huge structure, 64-slot tiered rack, tunable OC/OV, logarithmic coolant boost and destructive heat model.
- Native optical `IOpticalComputationProvider` and HPCA components are reused where approved.

## Implementation ownership

- Runtime: `machine/AstralComputingArrayMachine.java`, `machine/AstralComputationRackMachine.java`.
- Pure math: `machine/logic/AstralComputingLogic.java`.
- Definition/shape: `registry/machine/AstralComputingArrayDefinition.java`, `AstralComputingArrayStructure.java`.
- Recipes: `data/recipe/AstralComputingArrayRecipes.java`.
- Blocks/material: `TSTBlocks`, `TSTMaterials.SUPER_COOLANT`.
- Resources: `assets/tstmodern` controller overlays, twelve casing identities, models and translations.
- Registration cardinality: one controller, one rack part, one recipe registration call.

## Validation evidence

- Machine contract: passed 2026-09-05.
- Locked source textures: passed 2026-09-05.
- Locked production textures: passed before implementation 2026-09-05.
- `compileJava`: passed after runtime and recipe implementation 2026-09-06.
- Structure and pure rack-math tests: passed as part of all 200 tests on 2026-09-06.
- Machine contract, casing catalog, locked source textures, 149 locked production textures, formed appearance, and EN/VI localization: passed 2026-09-06.
- `gradlew build`: passed 2026-09-06.
- Client quick-play reached `New World` and JEI registration after correcting LV-UHV circuit tag routing on 2026-09-06; the previous `Unsupported endgame circuit tier: 7` crash no longer reproduces.
- Compact Fusion Coil T0 now researches from the unique High Computation Station T5 prerequisite, avoiding the GTCEu Research Station input conflict with Compact Cyclotron Coil; rack localization, the bold light-purple controller name, colored EN/VI controller tooltips, and all twelve Astral casing names are validated on 2026-09-06.
- `runData`: reached registration of the Astral rack/controller, then failed in the pre-existing `mega_stone_breaker` generator because `gtceu:block/machine/template/cube_all/sided` is absent; no Astral failure was reported.
- Remaining runtime gates: resolve the reported empty material-form inputs, then verify in-world formation/rendering/operation and JEI recipes.
- Final level: build and static validation complete; gameplay validation pending.
