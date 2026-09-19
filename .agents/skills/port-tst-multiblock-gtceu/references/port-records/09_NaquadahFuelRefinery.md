# Naquadah Fuel Refinery port record

## Identity and baseline

- Source: `MTENaquadahFuelRefinery`, meta ID `15536`, from local `D:/tmp/GT5`.
- Recipe map: `GoodGeneratorRecipeMaps.naquadahFuelRefineFactoryRecipes`; recipe owner `FuelRecipeLoader`.
- Target: GTCEu Modern 7.4.0 on branch `dev`; no native equivalent exists.
- Contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/naquadah-fuel-refinery.json`.

## Source behavior

- Produces Naquadah Fuel MkIII-MkVI.
- Uniform coil tier 1-4; recipe metadata is the minimum coil tier.
- Parallel is `4 * coilTier`: 4/8/12/16.
- Perfect overclock cap is `coilTier - recipeRequiredTier`; otherwise unlimited tier skips.
- Batch and void protection are supported. Input energy is required.
- The Modern structure accepts one or two input-energy hatches; two hatches provide the agreed one-tier aggregate headroom without an unlimited hatch-count exploit.
- Source structurally lists a Dynamo hatch although the machine never outputs EU and the tooltip omits it.

## Structure

- `27 x 27 x 5`, direct `RIGHT/DOWN/BACK`, controller at aisle 0 row 13 column 13 facing front.
- Counts: `A=489`, `B=72`, `C=192`, `E=124`, `F=64`, controller `1`, air `2703`.
- Minimum ordinary refinery casing count: 470.
- Source defines `D` but the shape contains zero `D` cells; it is dead configuration.

## Approved target mapping

- Reuse Field Restriction Coil T1 and Radiation Protection Steel Frame already ported for Astral Computing Array.
- Add exact-source dedicated refinery casing, coils T2-T4, field glass and Europium radiation-proof casing.
- Use static transparent source glass instead of recreating legacy OptiFine CTM.
- Omit unused Dynamo ability.
- Preserve the four standard MkIII-MkVI recipes, exact amounts/duration/EU/t/coil gates; omit five optional higher-yield recipes whose dependency chains are unavailable.
- Palette: dark-green bold controller; cyan/purple/dark-purple/gold coil tiers; distinct colored functional labels and exact Modern Edition footer.

### Exact dependency substitutions

| Legacy dependency | Modern target |
|---|---|
| Quantium | Naquadria |
| Draconium | Duranium |
| Awakened Draconium | Neutronium |
| Electrum Flux | Europium |
| Infinity | Neutronium |
| Radox Heavy | molten Naquadria |
| Shirabon | Metastable Oganesson |
| Manyullyn fine wire | Europium fine wire |
| Mysterious Crystal huge pipe | Neutronium huge fluid pipe |
| Black Plutonium medium pipe | Neutronium medium fluid pipe |
| NPIC wafer | High Power Integrated Circuit Wafer |
| PPIC wafer | Ultra High Power Integrated Circuit Wafer |
| Dimensionally Shifted Superfluid | TST Super Coolant |
| Transcendent Metal | TST Black Titanium |
| SpaceTime | Neutronium |
| Vibrant Alloy | Europium |
| Cosmic Neutronium frame | Neutronium frame |
| Superconductor ZPM | Uranium Rhodium Dinaquadide |
| Superconductor UV | Enriched Naquadah Trinium Europium Duranide |
| Superconductor UHV/UEV fallback | Ruthenium Trinium Americium Neutronate |

The following identities and acquisition chains are preserved rather than substituted:

- Extremely Unstable Naquadah: the source chemical chain from Naquadria and Fluoroantimonic Acid.
- Tiberium: Chemical Bath Firestone or Diamond routes using MkI/MkII Naquadah fuel.
- Orundum: Tiberium plus Silicon in the Forming Press.
- Astral Titanium: Titanium dust in the Laser Engraver with the reusable Special Laser Lens.
- Indalloy 140: the 47 Bi / 25 Pb / 13 Sn / 10 Cd / 5 In alloy-blast recipe.
- UHV Voltage Coil: Samarium Magnetic rod plus Tritanium fine wire.
- Atomic Separation Catalyst: raw Mixer catalyst, hot EBF ingot, vacuum-freezer ingot and generated dust forms. The approved Modern raw-catalyst variant uses Blaze, Duranium, Europium and molten Naquadah.
- High-Density Uranium and High-Density Plutonium: source mixture, wrapped-ingot, implosion-nugget and compressor chains.
- Radiation-Proof Prismatic Naquadah Composite Sheet remains a dedicated item; only the unavailable slurry input is replaced with Naquadria.
- Praseodymium remains the MkIV molten input; TST Modern registers the fluid property omitted by GTCEu 7.4.

### Naquadah rework closure

- `Acid Naquadah Emulsion` is neutralized with Quicklime into `Naquadah Emulsion`, centrifuged into `Naquadah Solution`, then distilled into the light/heavy refinery fuels. It is not the separate `Naquadah-Rich Solution` from neutron activation.
- `Naquadah-Adamantium Solution` is produced through the source P-507 extraction route from low-quality Naquadah solution.
- Adamantine, Naquadah Oxide Mixture, Concentrated Enriched-Naquadah Sludge, Enriched-Naquadah Sulphate, Sodium Sulfate, Low Quality Naquadria Sulphate and Naquadria Sulphate each retain a downstream recovery recipe from `NaquadahRecipeLoader` or the source material's generated decomposition rule.
- Source-only intermediate identities needed by that chain are kept dedicated; native GTCEu base chemicals are reused when their identity matches exactly.

Oganesson, high-tier field generators and pumps, and UEV/UIV/UXV circuit tags are native target dependencies and are used directly.

## Validation evidence

- Excel locally updated in `Danh sách máy`, `Mapping GTCEu`, and `Tổng quan`; backlog total 26.
- Structure extraction proved five aisles, 27 rows per aisle, width 27, and one controller.
- User approved the corrected full specification on 2026-09-08.
- Exact source PNGs for the refinery casing, coils T2-T4, field-restriction glass and Europium radiation-proof casing were copied into `texture_block_casing/26_NaquadahFuelRefinery` and verified by SHA-256.
- The 2026-09-08 `runServer` regression reproduced the missing Praseodymium fluid crash before the fix and reached `Done` after restoring the source-required molten form.
- Final audit state: `AUDIT_COMPLETE`; no unresolved contract items remain.
