# Mega Tree Farm port record

## Identity and baseline

- TST machine/display name: `TST_MegaTreeFarm` / Mega Tree Farm (Eco-Sphere Simulator)
- TST registry/meta ID: `19051`
- Target implementation: `MegaTreeFarmMachine`, `MegaTreeFarmDefinition`, and `MegaTreeFarmRecipes`
- Target dependency versions: Minecraft `1.20.1`, Forge `47.3.0`, GTCEu `7.4.0`, LDLib `1.0.40.b`, JEI `15.20.0.115`
- Working-tree baseline: `main` at `c65c6bc`; this change is limited to casing resources/models and audit provenance.
- User-approved deviation: apply the supplied multi-face designs for the dedicated Mega Tree Farm casings.

## Source behavior and approved target

- Recipe maps: Tree Growth Simulator and Aquatic Zone Simulator.
- TST tier output multiplier is applied once per run; GTCEu Parallel Hatch runs multiply the complete run independently.
- The current approved implementation keeps the original tier-scaled EU/fluid rules and fixed 100-tick base-controller duration.
- Structure is `33 × 45 × 33` using `RIGHT`, `DOWN`, `BACK`; this resource-only change does not alter its pattern or abilities.
- Final in-world rendering, formation, operation, and JEI behavior remain pending gameplay validation.

## Casing texture mapping

| Symbol | Target block | Resolution | Approved texture behavior |
|---|---|---|---|
| J | `TSTBlocks.ARCANE_TRANSLUCENT_CASING` | `dedicated_designed` | separate side/top/bottom; animated four-frame top |
| K | `TSTBlocks.AIR_CRYSTAL_CASING` | `dedicated_designed` | separate side/top/bottom; animated four-frame top |
| L | `TSTBlocks.WATER_CRYSTAL_CASING` | `dedicated_designed` | separate side/top/bottom; animated four-frame top |
| M | `TSTBlocks.EARTH_CRYSTAL_CASING` | `dedicated_designed` | separate side/top/bottom; animated four-frame top |
| N | `TSTBlocks.CULTIVATION_SOIL_CASING` | `dedicated_designed` | separate side/top/bottom; static top |
| O | `GTBlocks.LAMPS.get(DyeColor.PURPLE)` | `equivalent_review`, approved native reuse | unchanged; supplied Astral Pylon art is reserved for Astral Computing Array |

- Texture provenance: newly designed package `MegaTreeFarm_Casing_Textures_MultiFace`, approved by the user on 2026-08-24.
- Models use `minecraft:block/cube_bottom_top`; animated top sheets use vanilla `.png.mcmeta` metadata.
- Block registration, BlockItems, blockstates, item models, loot, localization, construction recipes, structure predicates, and formed appearance mappings are unchanged.

## Validation status

- Resource/model checks: passed for all five `cube_bottom_top` models, 15 PNG faces, four animated top sheets and four valid `.mcmeta` files; processed resources contain no legacy single-face texture.
- Casing catalog validator: `Casing catalog valid: 25 machines, 408 entries.`
- Full build: `BUILD SUCCESSFUL in 1m 5s`; JUnit reported `34 tests`, `0 failures`, `0 errors`.
- Gameplay/JEI: pending.
- Final status: build validated; gameplay validation pending
