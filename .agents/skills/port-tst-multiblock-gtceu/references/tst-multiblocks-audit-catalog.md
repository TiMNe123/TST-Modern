# TST Multiblocks & Casing Audit Catalog (25 Machines)

This document is the consolidated reference for the 25 audited Twist Space Technology 1.7.10 multiblocks planned for GregTechCEu Modern 1.20.1, based on the primary audit workbook:
[`outputs/tst-casing-texture-audit-gtceu-reuse-20260820/TST_Multiblocks_Casing_Texture_Audit_GTCEu_Reuse.xlsx`](../../../../outputs/tst-casing-texture-audit-gtceu-reuse-20260820/TST_Multiblocks_Casing_Texture_Audit_GTCEu_Reuse.xlsx).

---

## 1. Audit Summary & Scope

- **Initial Active Controllers Audited**: 77 controllers from TST `MachineLoader.java`.
- **Selected Machine Backlog**: **25 multiblocks** with unique mechanics, custom processing loops, or non-overlapping gameplay.
- **Excluded Machines**:
  - **26 machines**: Excluded due to direct overlap with GTCEu `large_machine` / GCYM multiblocks.
  - **23 machines**: Outside ZPM-to-Endgame scope or below ZPM tier.
  - **3 machines**: Directly excluded by user (*LargeCanner, BeeEngineer, MegaSolarPanelFactory*).
- **Structure Lines Audited**: 408 distinct structure pieces across all 25 machines; 100% texture coverage resolved in `texture_block_casing/` and `textures/`.

---

## 2. 25 Audited Machines Master List

| STT | Machine Name | Tier | TST 1.7.10 Class | Meta ID | Function / Mechanics Summary | Port Status |
| :---: | :--- | :---: | :--- | :---: | :--- | :---: |
| **1** | **GiantVacuumDryingFurnace** | ZPM | `TST_GiantVacuumDryingFurnace` | 19065 | Vacuum drying & chemical dehydration; coil-scaled parallel & speed boost | **Ported** |
| **2** | **NetherInterface** | ZPM | `TST_NetherInterface` | 19077 | Nether resource & Hellish Metal extraction; base 64 parallel | **Ported** |
| **3** | **HyperThermalConvector** | UV | `TST_HyperThermalConvector` | 19069 | Rapid heat exchange & dense steam cycle (573K/1073K); 128 parallel | **Ported** |
| **4** | **MegaTreeFarm** | UHV | `TST_MegaTreeFarm` | 19051 | EcoSphere tree/plant growth simulator; multiblock greenhouse | **Ported** |
| **5** | **Disassembler** | UEV | `TST_Disassembler` | 19041 | High-tech item disassembly & component recovery | **Ported** |
| **6** | **IncompactCyclotron** | UEV | `TST_IncompactCyclotron` | 19058 | Compact particle accelerator & isotope transformation | *Planned* |
| **7** | **MegaStoneBreaker** | UEV | `TST_MegaStoneBreaker` | 19062 | Rock/stone synthesizer; 4x normal / 1024x boost mode (Water+Lava) | **Ported (Ref)** |
| **8** | **LargeNeutronOscillator** | UIV | `MM_LargeNeutronOscillator` | 19055 | EU-powered neutron activator & isotope irradiation | *Planned* |
| **9** | **MegaNqReactor** | UIV | `TST_MegaNqReactor` | 19081 | Ultra-scale Naquadah fuel generator | *Planned* |
| **10** | **DSPLauncher** | UMV | `TST_DSPLauncher` | 19013 | Dyson Sphere component launcher (Solar Sails / Nodes) | *Planned* |
| **11** | **DSPReceiver** | UMV | `TST_DSPReceiver` | 19014 | Dyson Sphere power reception & beam processing | *Planned* |
| **12** | **MicroSpaceTimeFabricatorio** | UMV | `TST_MicroSpaceTimeFabricatorio` | 19060 | Multi-mode spacetime fabrication & spatial manipulation | *Planned* |
| **13** | **MiracleTop** | UMV | `GT_TileEntity_MiracleTop` | 19003 | Endgame multi-recipe synthesis powerhouse | *Planned* |
| **14** | **ArtificialStar** | UXV | `TST_ArtificialStar` | 19015 | Endgame stellar generator with extreme EU generation | *Planned* |
| **15** | **DeployedNanoCore** | UXV | `TST_DeployedNanoCore` | 19038 | High-density nano-fabrication core | *Planned* |
| **16** | **DimensionallyTranscendentMatterPlasmaForgePrototypeMK2** | UXV | `MM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2` | 19054 | Extreme-temperature plasma transmutation (TecTech forge) | *Planned* |
| **17** | **MassFabricatorGenesis** | UXV | `MM_MassFabricatorGenesis` | 19057 | High-efficiency UU-Matter & Concentrated UU synthesizer | *Planned* |
| **18** | **SuperWaterPurifier** | UXV | `TST_SuperWaterPurifier` | 19078 | Multi-staged extreme-purity water refinement chain | *Planned* |
| **19** | **StrangeMatterAggregator** | MAX | `TST_StrangeMatterAggregator` | 19059 | MAX-tier strange matter containment & synthesis | *Planned* |
| **20** | **HyperSpacetimeTransformer** | Endgame | `GTCM_HyperSpacetimeTransformer` | 19501 | Molecular transformation & cosmic reality bending | *Planned* |
| **21** | **AstralComputingArray** | UEV | `TST_Computer` | 19029 | High-performance computing array & computational research | *Planned* |
| **22** | **LargeIndustrialCokingFactory** | UHV | `TST_LargeIndustrialCokingFactory` | 19021 | Large-scale coking & pyrolysis; infinite parallel; coil speed boost | *Planned* |
| **23** | **OreProcessingFactory** | UEV | `TST_OreProcessingFactory` | 19017 | Automated end-to-end ore processing pipeline | *Planned* |
| **24** | **StarcoreMiner** | UIV | `TST_StarcoreMiner` | 19040 | Deep starcore & stellar matter extraction | *Planned* |
| **25** | **MeteorMiner** | - | `TST_LaserMeteorMiner` | 19072 | Laser schematic-guided meteor mining | *Planned* |

---

## 3. GTCEu Native & Reuse Mappings (41 Mappings)

### A. 31 "DÙNG TRỰC TIẾP" (Direct Native GTCEu / Vanilla)
Use these native blocks directly without creating new blocks or duplicating textures:

1. `Blocks.bedrock` $\to$ `Blocks.BEDROCK`
2. `Blocks.obsidian` $\to$ `Blocks.OBSIDIAN`
3. `Block.getBlockById(1)` (Stone) $\to$ `Blocks.STONE`
4. `sBlockCasings2:1` (Frostproof) $\to$ `GTBlocks.CASING_ALUMINIUM_FROSTPROOF`
5. `sBlockCasings1:10` (Bronze Plated Bricks) $\to$ `GTBlocks.CASING_BRONZE_BRICKS`
6. `sBlockCasings1:11` (Heatproof) $\to$ `GTBlocks.CASING_INVAR_HEATPROOF`
7. `sBlockCasings8:1` (PTFE Pipe) $\to$ `GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE`
8. `sBlockCasings8:0` (Inert PTFE) $\to$ `GTBlocks.CASING_PTFE_INERT`
9. `sBlockCasings4:1` (Clean Stainless) $\to$ `GTBlocks.CASING_STAINLESS_CLEAN`
10. `sBlockCasings2:0` (Solid Steel) $\to$ `GTBlocks.CASING_STEEL_SOLID`
11. `sBlockCasings2:4` (Titanium Gearbox) $\to$ `GTBlocks.CASING_TITANIUM_GEARBOX`
12. `sBlockCasings2:5` (Tungstensteel Gearbox) $\to$ `GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX`
13. `sBlockCasings2:15` (Tungstensteel Pipe) $\to$ `GTBlocks.CASING_TUNGSTENSTEEL_PIPE`
14. `sBlockCasings4:0` (Robust Tungstensteel) $\to$ `GTBlocks.CASING_TUNGSTENSTEEL_ROBUST`
15. `sBlockCasings5:0` (Cupronickel Coil) $\to$ `GTBlocks.COIL_CUPRONICKEL`
16. `sBlockCasings5:4` (HSS-G Coil) $\to$ `GTBlocks.COIL_HSSG`
17. `sBlockCasings5:1` (Kanthal Coil) $\to$ `GTBlocks.COIL_KANTHAL`
18. `sBlockCasings5:5` (Naquadah Coil) $\to$ `GTBlocks.COIL_NAQUADAH`
19. `sBlockCasings5:2` (Nichrome Coil) $\to$ `GTBlocks.COIL_NICHROME`
20. `sBlockCasings5:10` (Trinium Coil) $\to$ `GTBlocks.COIL_TRINIUM`
21. `sBlockCasings3:15` (Tungstensteel Firebox) $\to$ `GTBlocks.FIREBOX_TUNGSTENSTEEL`
22. `sBlockCasings4:7` (Fusion Coil) $\to$ `GTBlocks.FUSION_COIL`
23. `fluidDistilledWater` $\to `GTMaterials.DistilledWater` (fluid block/state)
24. `Materials.Iridium` Frame $\to$ `Predicates.frames(GTMaterials.Iridium)`
25. `Materials.TungstenSteel` Frame $\to$ `Predicates.frames(GTMaterials.TungstenSteel)`
26. `Materials.Titanium` Frame $\to$ `Predicates.frames(GTMaterials.Titanium)`
27. `Materials.StainlessSteel` Frame $\to$ `Predicates.frames(GTMaterials.StainlessSteel)`
28. `Materials.Steel` Frame $\to$ `Predicates.frames(GTMaterials.Steel)`
29. `Materials.Bronze` Frame $\to$ `Predicates.frames(GTMaterials.Bronze)`
30. `Materials.NaquadahAlloy` Frame $\to$ `Predicates.frames(GTMaterials.NaquadahAlloy)`
31. `Materials.Osmiridium` Frame $\to$ `Predicates.frames(GTMaterials.Osmiridium)`

### B. 10 "GTCEu TƯƠNG ĐƯƠNG" (Approved Equivalent Reuses)
Approved substitutions across all 25 machines:

1. `sBlockCasings2:9` (Assembler Casing) $\to$ `GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING`
2. `Loaders.pressureResistantWalls` $\to$ `GCYMBlocks.CASING_STRESS_PROOF`
3. `chainAllGlasses()` $\to$ `GTBlocks.CASING_TEMPERED_GLASS` / `GTBlocks.FUSION_GLASS`
4. `sBlockCasings2:6` (Processor Casing) $\to$ `GTBlocks.COMPUTER_CASING`
5. `sBlockCasings9:1` (Advanced Filter Casing) $\to$ `GTBlocks.FILTER_CASING`
6. `sBlockCasings2:8` (Containment Field Casing) $\to$ `GTBlocks.FUSION_CASING`
7. `BorosilicateGlass` $\to$ `GTBlocks.FUSION_GLASS`
8. `BlockQuantumGlass` $\to$ `GTBlocks.FUSION_GLASS`
9. `ModBlocksHandler.PurpleLight` $\to$ `GTBlocks.LAMPS.get(DyeColor.PURPLE)`
10. `Materials.CosmicNeutronium` Frame $\to$ `Predicates.frames(GTMaterials.Neutronium)` *(unless local dedicated frame is specified)*

---

## 4. Texture Directory Source Map

When porting any machine:
1. **Structure Casings**: Source from [`texture_block_casing/<STT>_<MachineName>/`](../../../../texture_block_casing/) $\to$ destination `src/main/resources/assets/tstmodern/textures/block/casings/`.
2. **Controller Overlays**: Source from [`textures/<STT>_<MachineName>/`](../../../../textures/) $\to$ destination `src/main/resources/assets/tstmodern/textures/block/multiblock/<machine_registry_name>/`.
