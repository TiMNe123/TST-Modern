# Port Record: Large Disassembler

## Identity and baseline

- TST machine/display name: Large Disassembler
- TST registry/meta ID: `TST_Disassembler` / 19041
- Controller class/path and inheritance chain: `com.tstmodern.machine.DisassemblerMachine` extends `WorkableMultiblockMachine`
- Recipe pool/map class/path: `com.tstmodern.recipe.disassembler.DisassemblerRecipeIndex` with the generation-tracked `DisassemblerRecipeSources` registry
- Registration/config/localization paths: `DisassemblerDefinition.java`, `TSTRecipeTypes.java`, `en_us.json`, `vi_vn.json`
- Target dependency versions: GTCEu Modern 1.20.1 (7.4), Forge 47.3.0, LDLib
- Current working-tree files/hashes captured: `DisassemblerMachine.java`, `DisassemblerStructure.java`, `DisassemblerDefinition.java`, `DisassemblerRecipes.java`, `DisassemblerRecipeIndex.java`, `DisassemblerRecipePolicy.java`, `DisassemblerRecipeAdapter.java`, `DisassemblerRecipeDescriptor.java`, `DisassemblerRecipeSource.java`, `DisassemblerRecipeSources.java`, `DisassemblerSpecialRecipes.java`
- User-approved baseline or deviations already present: 66 uniform Component Assembly Line Casings (LV-MAX) determine casing tier (1..14); Zero EU operating cost; speed scales with `4 * casingTier`.

## Source behavior

- Purpose and distinct gameplay value: Disassembles items crafted via Assembler and Assembly Line back into their component materials and fluids.
- Recipe maps and exact recipe count/IDs: Dynamic reverse indexing with TST priority `special (0) -> component assembly equivalent (10) -> MiracleTop extension seam (20) -> assembly line without research equivalent (30) -> assembler (40) -> Photon Controller (50)`. Component recipes are filtered from `ASSEMBLER_RECIPES` by `tstmodern:component_assembly_line/`; Photon recipes are filtered from `LASER_ENGRAVER_RECIPES` by `tstmodern:photon_controller/`. MiracleTop is intentionally inactive until its real recipe type is ported and registered.
- Inputs/outputs and special abilities: 1-16 item inputs, 1-16 item outputs, 0-4 fluid outputs. Hatches restricted to closed set on `I` (Input Bus, Output Bus, Output Hatch, ME Pattern Buffers).
- EU/t, duration, voltage, and overclock rules: 0 EU/t required. Duration = `Math.max(1, processed / (4 * casingTier)) * 100` ticks.
- Base parallel and Parallel Hatch interaction: Batched processing across available matching input items.
- Speed/EU/output modifiers: Speed increases with higher Component Assembly Line Casing tier.
- Modes and special recipes: Full item & fluid recovery according to recipe definition; zero-chance items/fluids omitted; blacklist applied. Fusion Coil has the highest-priority TST special return: 1 Superconducting Coil, 2 Neutron Reflectors, 2 MV Field Generators, and 4 deterministic LuV-tag circuits.
- Chanced-output semantics: Preserves deterministic returns; omits 0% outputs.
- Periodic consumption, persistence, failure, and shutdown: Dynamic continuous operation as long as valid reversible items are present.
- Missing/optional dependencies: PCB Coolant substituted for legacy Super Coolant in casing recipes.

## Structure

- Dimensions `(width × height × depth)`: 23 × 28 × 5 (aisles defined via `RelativeDirection.RIGHT`, `DOWN`, `BACK`)
- Source and target axis order: Standard 23×28 matrix mapped to `RIGHT`, `DOWN`, `BACK`
- Exact aisle source/hash: Transposed in `DisassemblerStructure.AISLES`
- Controller character/count/facing: `~` (1 controller, front face)
- Character-to-source block/meta/predicate mapping:
  - `A`: `GTBlocks.FUSION_GLASS`
  - `B`: `TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASINGS` (66 total required)
  - `C`: `GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX`
  - `D`: `GTBlocks.FUSION_CASING`
  - `E`: `GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING`
  - `F`: `GTBlocks.FILTER_CASING`
  - `G`: `TSTBlocks.MOLECULAR_CASING`
  - `H`: `TSTBlocks.HOLLOW_CASING`
  - `I`: `TSTBlocks.MOLECULAR_CASING` or closed I-abilities (Import Items, Export Items, Export Fluids)
  - `J`: `Predicates.frames(GTMaterials.Neutronium)`
  - ` `: `Predicates.any()`
- Fixed hatch positions: Replaceable along `I` locations.
- Replaceable abilities and base casings: Base casing `MOLECULAR_CASING`.
- Minimum/maximum counts: Exactly 66 `B` casings of uniform tier. At least 1 Import Item, 1 Export Item, 1 Export Fluid hatch.
- Air/any positions: Preserved in matrix ` `.
- Static structure checks: Passed.

## Overlap decision

- GTCEu machines/recipe types/source inspected: GTCEu basic Disassembler operates only on single-block LV-IV scale without custom multiblock casing tier mechanics or high-volume assembly line reversal.
- Result: Port
- Distinct TST value justifying the result: Large-scale assembly line reversal, 14-tier casing progression, multi-item batch disassembly without EU cost.

## Mapping and approved deviations

| TST source | Target registry/predicate | Native / Equivalent / Dedicated | Texture provenance | Approval/catalog entry | Semantic/progression impact |
|---|---|---|---|---|---|
| BorosilicateGlass | `GTBlocks.FUSION_GLASS` | Equivalent | GTCEu Native | `casing-catalog/Disassembler.json` | Approved glass family reuse |
| Component Casings 0-13 | `TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_LV`..`MAX` | Dedicated | TST Original | `casing-catalog/Disassembler.json` | 14 dedicated casing blocks |
| sBlockCasings2:5 | `GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX` | Direct Native | GTCEu Native | `casing-catalog/Disassembler.json` | Exact match |
| sBlockCasings2:8 | `GTBlocks.FUSION_CASING` | Equivalent | GTCEu Native | `casing-catalog/Disassembler.json` | Approved reuse |
| sBlockCasings2:9 | `GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING` | Equivalent | GCYM Native | `casing-catalog/Disassembler.json` | Approved reuse |
| sBlockCasings9:1 | `GTBlocks.FILTER_CASING` | Equivalent | GTCEu Native | `casing-catalog/Disassembler.json` | Approved reuse |
| sBlockCasingsTT:4 | `TSTBlocks.MOLECULAR_CASING` | Dedicated | TST Original | `casing-catalog/Disassembler.json` | Dedicated casing |
| sBlockCasingsTT:8 | `TSTBlocks.HOLLOW_CASING` | Dedicated | TST Original | `casing-catalog/Disassembler.json` | Dedicated casing |
| Materials.CosmicNeutronium Frame | `Predicates.frames(GTMaterials.Neutronium)` | Equivalent | GTCEu Native | `casing-catalog/Disassembler.json` | Approved frame reuse |

## Implementation ownership

- Behavior: `machine/DisassemblerMachine.java`
- Definition: `registry/machine/DisassemblerDefinition.java`
- Recipes: `data/recipe/DisassemblerRecipes.java`
- Controller/casing/bootstrap recipe ownership: `DisassemblerRecipes.java`
- Shared blocks/materials/recipe types: `TSTBlocks.java`, `TSTRecipeTypes.java`
- Resources/translations: `en_us.json`, `vi_vn.json`, blockstates, models, textures
- Compatibility code: `DisassemblerRecipeIndex.java`, `DisassemblerRecipePolicy.java`, `DisassemblerRecipeAdapter.java`
- Machine initialization cardinality: 1 (`TSTMachineRegistry.java`)
- Recipe registration cardinality: 1 (`TSTModernGTAddon.java`)

## Validation evidence

- Catalog validator: 25 machines, 408 entries valid.
- Structure/mapping checks: Passed.
- Recipe ID/count/ownership checks: 17 recipe specs validated in `PortRecipeCatalogContractTest.java`.
- Resource checks: Blockstates, item models, block models, textures, loot tables present.
- Compile/build and changed-file warnings: Full named Disassembler suite passed 24/24 tests (machine contract 7, adapter 4, policy 7, sources 4, definition contract 2); `git diff --check` clean apart from existing line-ending notices.
- Final level: `build validated; gameplay validation pending`
