# Incompact Cyclotron Port Record

## Identity and baseline

- TST machine/display name: `TST_IncompactCyclotron` / `PULSAR - Incompact Cyclotron`.
- TST registry/meta ID: `19058`.
- Controller: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_IncompactCyclotron.java`.
- Recipe authority: TST `ParticleColliderRecipePool`, GT5 `RecipeMaps.cyclotronRecipes` and `RecipesGregTech.cyclotronRecipes`.
- Target: Minecraft 1.20.1, Forge 47.3.0, GTCEu 7.4.0.
- Contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/incompact-cyclotron.json`.
- Approval: user approved the 2026-09-01 minimal dependency-closure spec.

## Source behavior

- Dedicated Cyclotron map; normal overclock; maximum source parallel 256.
- Duration multiplier `0.5`; EU/t multiplier `1.6`.
- Modern Parallel Control Hatch contribution is added to the source limit.
- No maintenance, muffler, mode, persistence, or periodic-consumption behavior.
- Chanced outputs roll independently per GTCEu parallel execution.

## Structure and mapping

- Dimensions: `47 × 7 × 47`; target axes `RIGHT`, `DOWN`, `BACK`.
- Controller: one `~` at offset `(23, 3, 40)`, facing outward.
- Counts: `A=31`, `B=128`, `C=560`, `D=1664`, `E=64`, `F=32`.
- A/E: native `GTBlocks.FUSION_GLASS`.
- B: dedicated Quantum Frame using locked GT5 frame texture.
- C: dedicated Compact Cyclotron Coil using TST MetaBlockCasing01:12.
- D/F: dedicated Dense Cyclotron Outer Casing using TST MetaBlockCasing01:11.
- E accepts recipe I/O and energy; F accepts energy only.
- E/F require a mixed-casing controller renderer; adjacency/majority sampling is forbidden.

## Approved deviations

- Only the minimal closed Hydrogen Ion/Proton/Electron/Neutron/Unknown particle chain is ported, not the complete GT++ addon inventory.
- Hydrogen charge states collapse to one item.
- Unavailable legacy construction components use approved native UHV/UEV semantic replacements.
- All three dedicated casing recipes use Research Station with the green Data Orb; the controller uses Research Station with the purple Data Module.

## Validation evidence

- Audit status: complete.
- Implementation/build/client/gameplay/JEI: pending.
