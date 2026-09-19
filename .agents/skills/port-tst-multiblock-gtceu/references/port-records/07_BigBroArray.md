# Port Record: 07_BigBroArray

## Identification and baseline

- Machine name: BigBroArray (MegaArray / 大哥阵列)
- TST 1.7.10 source class: `com.Nxer.TwistSpaceTechnology.common.machine.TST_BigBroArray`
- Original metadata ID / registration point: Meta ID `19049`, registered in `MachineLoader.java:367`
- Classification: Processing Array / Generator Array Super-Multiblock
- Target tier: UV / UHV (Processing Array Endgame)
- Target machine definition: `com.tstmodern.registry.machine.BigBroArrayDefinition`
- Target machine behavior: `com.tstmodern.machine.BigBroArrayMachine`
- Target recipe builder/loader: `com.tstmodern.data.recipe.BigBroArrayRecipes`
- Machine contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/big-bro-array.json`
- Contract validation: `node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-machine-contract.mjs .agents/skills/port-tst-multiblock-gtceu/contracts/big-bro-array.json`

## Overlap decision

- GTCEu Modern inspected: GTCEu Modern 7.4 has no multiblock `ProcessingArrayMachine` implemented (only language keys and commented recipe).
- Result: Port
- Distinct TST value justifying the result: Dynamic single-block machine embedding via screwdriver interaction, massive parallel progression ($64\times \rightarrow 1,280\times \rightarrow 5,242,880\times \rightarrow \infty$), coil energy reduction ($-10\%$ per tier), parallel casing speed boost ($+50\%$ per tier), pollution muffler requirement ($20\text{/s}$ per parallel).

## Mapping and approved deviations

| TST source | Target registry/predicate | Native / Equivalent / Dedicated | Texture provenance | Semantic/progression impact |
|---|---|---|---|---|
| MetaBlockCasing01:3..7 | `TSTBlocks.PARALLEL_CASING_MK1`..`MK5` | Dedicated | TST Original | 5 dedicated animated parallel casing blocks |
| sBlockCasings4:0 | `GTBlocks.CASING_TUNGSTENSTEEL_ROBUST` | Direct Native | GTCEu Native | Robust Tungstensteel Casing |
| sBlockCasings4:1 | `GTBlocks.CASING_STAINLESS_CLEAN` | Direct Native | GTCEu Native | Clean Stainless Steel Casing |
| sBlockCasings2:5 | `GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX` | Direct Native | GTCEu Native | Tungstensteel Gearbox Casing |
| BorosilicateGlass | `GTBlocks.CASING_TEMPERED_GLASS` / `FUSION_GLASS` | Equivalent | GTCEu Native | Tiered glass channel |
| GT++ Arcanite/Zeron 100/Pikyonium/Botmium/Abyssal/Quantum Frame Boxes | Titanium/Tungstensteel/Naquadah Alloy/Trinium/Neutronium/Tritanium frames | Approved Modern equivalent | GTCEu Native plus `GENERATE_FRAME` compatibility flag for Trinium | Six-level embedded-machine tier restriction channel |
| Tiered Machine Casings | `GTBlocks.MACHINE_CASING_LV`..`MAX` | Direct Native | GTCEu Native | Dynamo tier restriction channel |
| Tiered Heating Coils | `Predicates.heatingCoils()` | Direct Native | GTCEu Native | Energy reduction channel (-10% per tier) |

### Runtime and recipe deviations

- Embedded-machine catalog entries come only from concrete `GTRegistries.MACHINES` definitions. GTCEu Extractor represents the legacy Fluid Extractor role; Recycler remains unsupported.
- State is stored in one versioned compound. Real legacy `embeddedMachineStack` data migrates; the invented `embeddedMachineId` key is deliberately rejected.
- Catalog-stale but registry-valid machines remain blocked from processing and can still be unloaded. A complete registry miss preserves state and refuses empty-template transfer.
- GTCEu 7.4 has no UIV/UMV superconducting material. MK4/MK5 use its highest registered native superconductor, Ruthenium Trinium Americium Neutronate.
- GTCEu 7.4 defines Trinium but does not generate `frameGt`. The user approved adding `GENERATE_FRAME` in `TSTMaterials` so the fourth frame level remains a real UV-unlock structural tier instead of an invalid registry lookup.
- MK4 maps legacy dimensional casings to GTCEu Fusion Casing MK1–MK3/Fusion Coil and other native endgame components. MK5 uses native Energy Clusters, MAX Machine Casing, and MAX circuits for the missing legacy MAX-stage components.
- The target has no connected pollution system; no fake pollution counter or consumption is implemented.

## Implementation ownership

- Behavior: `machine/BigBroArrayMachine.java`
- Definition: `registry/machine/BigBroArrayDefinition.java`
- Structure: `registry/machine/BigBroArrayStructure.java`
- Recipes: `data/recipe/BigBroArrayRecipes.java`
- Shared blocks/textures: `TSTBlocks.java`, `parallel_casing_mk1..5.png`
- Resources/translations: `en_us.json`, `vi_vn.json`, blockstates, models, loot tables

## Validation evidence

- Machine contract validator: passed on 2026-08-30.
- Catalog validator: BigBroArray is absent from the 25-machine spreadsheet aggregate; its authority is this manual port record plus the validated machine contract. Aggregate counts were not edited.
- Structure/mapping checks: Passed.
- Recipe ID/count/ownership checks: Validated in contract tests.
- Resource checks: Blockstates, item models, block models, textures, loot tables present.
- Logic audit (vs TST_BigBroArray.java lines 1769-1802): corrected to exact source formulas:
  - `casingMultiplier = tier > 3 ? tier + 6 : tier`
  - `actual = min(stackSize << casingMultiplier, max)`; no-addon cap `min(stackSize, 64)`
  - `max = 64 << (tier*2 + (tier>3?6:0)) * (1 + addonCount)`, tier>=5 uses `(Integer.MAX_VALUE << multiplier)/5*(1+addonCount)`
  - Speed: duration x `0.66^tier` (UI displays reciprocal ~`1.5^tier`)
  - EU: x `0.9^coilTier`
- Transfer validation: exact item/count/NBT simulation and commit checks with rollback; plain JUnit cannot construct a registered `MetaMachineItem`, so load-commit mismatch remains an in-game validation gate.
- Final level: `build validated; gameplay validation pending`
