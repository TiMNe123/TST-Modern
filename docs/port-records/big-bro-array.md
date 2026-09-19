# Mega Array (BigBroArray) Port Record

## Identity and Baseline

- TST machine/display name: `TST_BigBroArray` / Mega Array
- Target dependency versions: Minecraft `1.20.1`, Forge `47.3.0`, GTCEu `7.4.0`, LDLib `1.0.40.b`, JEI `15.20.0.115`.
- Audit Scope: Outside the original 25-machine spreadsheet audit (`resolved_rows_gtceu_reuse.json`). Casing provenance and counts are maintained in this port record and tested via structure tests without altering the 25-machine / 408-entry catalog.

## Source Behavior

- Purpose: Embeds GT singleblock machines to parallelize processing & generation.
- Overclocking: Processor mode applies embedded-tier overclock first (capped by embedded machine tier), then parallel scaling, coil discount, and speed bonus. Generator mode receives parallel only (no overclocking, no speed bonus, no coil discount).
- Energy/Efficiency: -10% EU per Coil tier (`0.9^coilTier`) | +50% Speed per Parallel Casing tier (`0.66^parallelTier`).
- Parallelism: Base 64x (with no addons capped at 64) -> Up to 5,242,880x (MK4) and multi-trillion (MK5 with long saturation arithmetic).

## Structure & Casing Provenance

### Core Structure (11x11x8)
- Controller: `~`, 1 occurrence, facing outward.
- A (97): Uniform Glass channel (`BigBroArrayTierRules::glassTier`) -> `GTBlocks.CASING_TEMPERED_GLASS` / `GTBlocks.FUSION_GLASS`.
- B (40): Uniform Frame channel (`BigBroArrayTierRules::frameTier`) -> Titanium, TungstenSteel, NaquadahAlloy, Trinium, Neutronium, Tritanium frames. GTCEu 7.4 lacks native `frameGt` generation for Trinium, so TST Modern explicitly adds `GENERATE_FRAME` as an approved compatibility deviation.
- C (97): Uniform Machine Casing channel (`BigBroArrayTierRules::machineCasingTier`) -> LV..MAX Machine Casings.
- D (25): `GTBlocks.CASING_TUNGSTENSTEEL_ROBUST` or part abilities (Maintenance, Muffler, Import/Export Items, Import/Export Fluids).
- E (57): `GTBlocks.CASING_STAINLESS_CLEAN`.
- F (4): `GTBlocks.CASING_STAINLESS_CLEAN` or Energy Hatch abilities (1-4 hatches).

### Addon Structure (Rotational 4-direction Placements)
- Source Authority Counts: `G=134`, `H=44`, `I=42`, `J=64`, `K=530`, `L=86`.
- G (134): Parallelism Casing MK1 (`TSTBlocks.PARALLEL_CASING_MK1`) - Dedicated TSTModern block.
- H (44): Tritanium Frame (`frameGt, Tritanium`) - Native GTCEu frame.
- I (42): Cupronickel Coil (`GTBlocks.COIL_CUPRONICKEL`) - Native GTCEu heating coil.
- J (64): TungstenSteel Gearbox (`GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX`) - Native GTCEu gearbox.
- K (530): Fusion Glass (`GTBlocks.FUSION_GLASS`) - Glass role (preserves glass role without swapping count with L).
- L (86): Stainless Clean Casing (`GTBlocks.CASING_STAINLESS_CLEAN`) - Clean casing role.

## Overlap Decision

- Result: **Port**.
- Justification: GTCEu native does not have a 1:1 replacement for the Big Bro Array's ability to embed a singleblock machine and dynamically inherit its recipe type with massive parallel capabilities.

## Mapping and Approved Deviations

- **Fluid Extractor / Recycler Compatibility:** In GTCEu 7.4.0, separate `Fluid Extractor` and `Recycler` machines do not exist as distinct registered families. Fluid Extractor is subsumed under `Extractor`, and Recycler is excluded until registered as a dedicated machine/recipe type.
- **IV Superconducting Wire:** Controller and MK1 recipes use `TagPrefix.wireGtHex + GTMaterials.SamariumIronArsenicOxide` (verified from GTCEu 7.4 `FirstDegreeMaterials.java:1387` where `SamariumIronArsenicOxide` is registered with `cableProperties(V[IV], 6, 0, true, 30)`).
- **Recipes Construction Progression:** Controller uses Assembler (IV), MK1 uses Assembler (6,400 EU/t), MK2 uses Assembly Line (ZPM) with MK1 scanner research, MK3 uses Assembly Line (UHV) with MK2 scanner research, MK4 uses Researchable Assembly Line (UIV) with station research, MK5 uses Researchable Assembly Line (UXV) with station research.
- **Persistence & Migration:** Versioned NBT structure (`BigBroArrayEmbeddedState`, version 1) with migration from legacy 4-key NBT compounds.
- **Two-Phase Transfer:** All load and unload operations use two-phase transactions with snapshot/rollback on commit mismatch.
