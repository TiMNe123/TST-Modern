# Starcore Miner port record

## Identity and baseline

- TST machine/display name: `TST_StarcoreMiner` / Starcore Miner.
- TST registry/meta ID: `19040` (`MachineLoader.java:318-326`).
- Controller source: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_StarcoreMiner.java`, SHA-256 `37b82963dba6f8c550f1c10d6531066dda5140e436cf3f60a7a4b5dde15df8ad`.
- Inheritance source: `GTCM_MultiMachineBase.java`, SHA-256 `9f0897e5739c09a3ff3a95675bb2e8c4326aaccaf01a657b02a82034400f8138`.
- Recipe source: `GTCMMachineRecipes.java`, SHA-256 `204f61d57387a2c0073f8dac762aff4bff2986d151acbf7c397907dc7299a3d4`.
- Target: Minecraft 1.20.1, Forge 47.3.0, GTCEu 7.4.0, Java 17.
- Approved baseline: existing uncommitted Starcore Miner implementation on `dev` at base commit `ff7c6460ee3e0b55d123250ed0427a73ea1c15c5`.
- Machine contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/starcore-miner.json`.

## Source behavior

- Purpose: dimension-aware endgame void miner producing 24 independently selected ore stacks per cycle.
- Runtime pool: live GTCEu ore definitions valid for the controller dimension; vein weight is multiplied by entry chance and identical item/component outputs are coalesced before selection.
- Output: 131,072 items per selected stack, 24 stacks, 128 ticks, 2,013,265,920 EU/t, no overclock and fixed void-all behavior.
- Booster: an Astral Array Fabricator in the controller slot changes stack size to `131072 * 2 * ceil(count^1.5)`; registry ID is reserved until that machine is ported.
- Energy: normal, laser, substation, and compatible `INPUT_ENERGY` hatches; GTMThings wireless hatch needs no compile-time adapter.
- Persistence: the one-slot booster inventory is persisted and filtered to the reserved booster ID.

## Structure

- Main dimensions: 21 × 26 × 31; target axes are source character → RIGHT, row → DOWN, outer array → BACK.
- Controller: one `~`, offset right 10/down 22/back 1, horizontal rotation only.
- Pipe: 9 × 1 × 9 pieces, `max(0, controllerY - 24)` slices starting at controller Y - 4 and ending at Y=21; bedrock at the center may terminate the pipe early.
- Static symbol counts and all casing mappings are asserted by `StarcoreMinerStructureTest` and recorded in the machine contract/casing catalog.
- Ability cells are the 23 `L` positions, based on Space Elevator Base Casing; at least one item output and one accepted energy input are required.

## Overlap decision

- Inspected GTCEu miner and ore-vein APIs plus the original GalacticGreg void-miner map behavior.
- Result: **Port**. The source-scale, variable bedrock pipe, dimension-weighted 24-stack output, and Astral booster are distinct from native GTCEu miners.

## Mapping and approved deviations

- Exact structure/casing/texture mappings are authoritative in the machine contract and `casing-catalog/StarcoreMiner.json`.
- GalacticGreg dimension maps are replaced by the live GTCEu ore-vein registry for the current dimension.
- GTMThings wireless energy uses its native `INPUT_ENERGY` ability.
- Missing legacy recipe dependencies use the approved Modern endgame substitutions listed in the contract.
- The unavailable standalone Dimensional Transcendent Casing is represented in construction progression by the already registered UIV Component Assembly Line Casing; the dedicated output remains the exact-texture Dimensional Bridge Casing.
- The UIV casing recipe uses Research Station research with a Data Orb; the controller uses a Data Module.
- Identity palette: aqua/bold controller, aqua casing, gray controller description, cyan/yellow/green/dark-purple functional accents, light-purple Modern Edition footer.

## Implementation ownership

- Behavior: `machine/StarcoreMinerMachine.java` and `machine/logic/StarcoreMinerLogic.java`.
- Dynamic recipes: `recipe/starcore/StarcoreMinerRecipeLogic.java`.
- Definition/structure: `registry/machine/StarcoreMinerDefinition.java` and `StarcoreMinerStructure.java`.
- Construction recipes: `data/recipe/StarcoreMinerRecipes.java`.
- Shared registration: `TSTBlocks`, `TSTRecipeTypes`, `TSTMachineRegistry`, and `TSTModernGTAddon` each register the new content once.
- Resources: controller models/textures, Dimensional Bridge Casing assets/loot, and `en_us`/`vi_vn` translations.

## Validation evidence

- Machine contract validator: pass (`Machine contract valid: StarcoreMiner`).
- Casing catalog validator: pass (25 machines, 408 entries).
- Locked source texture validator: pass (155 controller and 689 casing assets).
- Locked production texture validator: pass (160 PNGs).
- Formed appearance validator: pass with uniform Space Elevator Base Casing.
- Localization validator: pass (21 keys in both locales).
- Focused Starcore tests: pass (structure, machine/UI contract, item-stack aggregation/arithmetic, construction recipes, and tooltip/localization).
- Pipe orientation regression: pass; runtime validation now derives RIGHT/BACK from the controller's actual upwards-facing and flipped state instead of collapsing RIGHT onto DOWN.
- Full `gradlew.bat build`: pass on 2026-09-07.
- Client reached world: pass; main structure matched in the test world, while final post-fix pipe formation remains pending.
- Formation/orientation/abilities: pending in-world validation.
- Controller/casing/formed hatch appearance: static model validation complete; in-world validation pending.
- Runtime outputs, booster slot, energy, duration, persistence, and failure behavior: implementation and static/unit validation complete; in-world validation pending.
- JEI controller/output routes and preview: pending.
- Remaining issues: gameplay and JEI smoke validation.
- Final level: build validated; gameplay validation pending.
