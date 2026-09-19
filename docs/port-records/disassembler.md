# Disassembler port record

## Identity and baseline

- TST machine/display name: `TST_Disassembler` / TST Large Disassembler
- TST registry/meta ID: `19041`
- Controller class/path and inheritance chain: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/system/Disassembler/TST_Disassembler.java`; inherited TST multiblock bases are audited through the controller's processing and capability overrides.
- Recipe pool/map class/path: `TST_DisassemblerRecipeHandler.java` and `TST_SimpleDisassemblyRecipe.java` in the same source package.
- Registration/config/localization: source registration meta `19041`; source config enables the machine and sets cost to `100` ticks.
- Target dependency versions: Minecraft `1.20.1`, Forge `47.3.0`, GTCEu `7.4.0`, LDLib `1.0.40.b`, JEI `15.20.0.115`.
- Current working-tree baseline: `main` at `c65c6bc`; pre-existing uncommitted Mega Tree Farm multi-face resource changes are preserved and excluded from this port.
- Approved design: `docs/superpowers/specs/2026-08-24-disassembler-design.md`.
- High-Tier policy: `TSTModernGTAddon.requiresHighTier()` returns `true`, approved on 2026-08-24. GTCEu High-Tier content is enabled globally even when its configuration flag is `false`.

## Source behavior

- Purpose: reverse qualifying assembly recipes and return consumed item/fluid inputs.
- Original sources: Component Assembly Line, Miracle Top, Assembly Line, Assembler, and Photon Controller; current target sources are Assembly Line and Assembler with an extension API for the others.
- Energy/overclock: no EU, no overclock.
- Parallel: source maximum is one processing operation; the controller internally processes all qualifying input stacks. No GTCEu or Parallel Hatch parallel is added.
- Duration: `max(1, processed / (4 × casingTier)) × 100` ticks with integer division.
- Tier gate: casing tiers 1–13 allow recipe tiers through `casingTier + 1`; casing tier 14 is unlimited.
- Output protection: disabled in source; target voids overflow.
- Recipe matching: ignores input NBT; non-consumable circuits/catalysts are not returned; deterministic first ingredient representative is used.
- Missing dependencies: Thermal Cloth and legacy storage-cell recipes are deliberately omitted.

## Structure

- Dimensions: `27 × 23 × 28`.
- Axes: `RIGHT`, `DOWN`, `BACK`.
- Controller: `~`, once, at `x=13, y=21, z=0`, facing outward.
- Counts: A 36, B 66, C 4, D 60, E 56, F 92, G 318, H 4, I 8, J 84.
- B: all Component Assembly Line Casings must be the same tier, meta 0–13 mapping to tier 1–14.
- I: Molecular Casing or allowed abilities; requires at least one Input Bus, one Output Bus, and one Output Hatch.
- No Energy, Maintenance, Muffler, Parallel, fluid import, or generic auto abilities.

## Overlap decision

- Inspected target role: GTCEu native material recycling, including Arc Furnace recycling, plus Assembly Line and Assembler recipe APIs.
- Result: **Port**.
- Justification: native recycling returns material forms and does not reverse arbitrary consumed item/fluid inputs from assembly recipes. The Disassembler retains a distinct endgame automation role.

## Mapping and approved deviations

| TST source | Target registry/predicate | Class | Texture provenance | Approval | Impact |
|---|---|---|---|---|---|
| Borosilicate glass family (A) | `GTBlocks.FUSION_GLASS` | Equivalent | GTCEu | catalog reuse approval 2026-08-21 | family is narrowed to approved Fusion Glass |
| Component Assembly Line Casing metas 0–13 (B) | fourteen dedicated TSTModern blocks | Dedicated | audited GT5 `0.png`–`13.png` | design approval 2026-08-24 | preserves tier identity; new self-contained high-tier progression |
| GT casing2:5 (C) | `GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX` | Native | GTCEu | catalog | no intended semantic loss |
| GT casing2:8 (D) | `GTBlocks.FUSION_CASING` | Equivalent | GTCEu | catalog reuse approval | approved Modern equivalent |
| GT casing2:9 (E) | `GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING` | Equivalent | GTCEu/GCYM | catalog reuse approval | approved Modern equivalent |
| GT casing9:1 (F) | `GTBlocks.FILTER_CASING` | Equivalent | GTCEu | catalog reuse approval | approved Modern equivalent |
| TecTech casingTT:4 (G/I) | dedicated Molecular Casing | Dedicated | audited GT5 `EM_CASING.png` | design approval | shared stable identity and formed ability base |
| TecTech casingTT:8 (H) | dedicated Hollow Casing | Dedicated | audited GT5 `EM_HOLLOW.png` | design approval | preserves source identity |
| Cosmic Neutronium frame (J) | `Predicates.frames(GTMaterials.Neutronium)` | Equivalent | GTCEu | catalog reuse approval | approved material downgrade |

Approved controller recipe substitutions: `eM_Power ×64` to UHV Energy Input Hatch ×64; Cosmic Neutronium Frame ×16 to Neutronium Frame ×16; solder plasma to equal-volume Soldering Alloy. Unavailable legacy special recipes are omitted. Source integrations absent from the target are exposed through a future adapter seam rather than fake registry mappings.

The approved High-Tier activation is required because GTCEu 7.4.0 defines UHV Assembler, Motor, Pump, Conveyor, Robot Arm, and Field Generator entries only when `GTCEuAPI.isHighTier()` is true. `IGTAddon.requiresHighTier()` is the supported lifecycle hook used by `GTCEuAPI.initializeHighTier()`; no direct internal API call or config rewrite is allowed.

GTCEu 7.4.0 registry flags do not generate dense plates for Osmiridium, Europium, Aluminium, Stainless Steel, or Titanium. The port represents each unavailable dense plate with nine regular plates of the same material. This preserves material mass and avoids saving recipes against empty `plateDense` tags; every affected count is frozen in the implementation plan.

## Implementation ownership

- Behavior: `machine/DisassemblerMachine.java`
- Definition: `registry/machine/DisassemblerDefinition.java`
- Recipes: `data/recipe/DisassemblerRecipes.java`
- Reverse subsystem: `recipe/disassembler/DisassemblerRecipeIndex.java`, `DisassemblerRecipeAdapter.java`, and `DisassemblerRecipePolicy.java`
- Shared registration: existing TST block, recipe-type, machine, and addon recipe registries, each exactly once.
- Dedicated resources: blockstates, block/item models, textures, self-drop loot, and `en_us`/`vi_vn` localization.

## Rendering

- Controller formed/base texture: Dimensional Transcendent Casing.
- Idle/active/emissive overlays: `OVERLAY_DTPF_OFF`, `OVERLAY_DTPF_ON`, and `OVERLAY_FUSION1_GLOW`.
- Formed ability-part base: Molecular Casing through `texture_overrides.all`; native hatch overlays remain intact.

## Validation evidence

- Source/catalog audit: complete and verified with `validate-casing-catalog.mjs`.
- Structure/mapping checks: verified (`28 × 23 × 27`, controller at `x=13, y=21, z=0`, hash `f133bc1aac5518bba6b594eca4ef9b76a84c27cdb0ae5a8b884f29888642201a`).
- Recipe ID/count/ownership checks: verified (17 construction recipes registered; reverse recipes dynamically synthesized from Assembly Line & Assembler).
- Resource checks: complete (blockstates, models, textures, loot tables, and `en_us`/`vi_vn` lang keys).
- Compile/build and warning inspection: verified with `./gradlew.bat compileJava test build` (BUILD SUCCESSFUL).
- Client formation, appearance, operation, reload, and JEI: pending in-game execution.
- Current level: **build validated; gameplay validation pending**.
