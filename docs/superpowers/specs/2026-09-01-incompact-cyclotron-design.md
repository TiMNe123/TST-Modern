# Incompact Cyclotron Design

## Goal

Port TST's PULSAR Incompact Cyclotron to GTCEu Modern 1.20.1 with exact source geometry, controller/casing textures, processing modifiers, a dedicated JEI recipe map, and the smallest source-grounded recipe chain that makes the machine usable.

## Approved scope

- Preserve the 47 × 7 × 47 TST structure and controller offset `(23, 3, 40)` on `RIGHT`, `DOWN`, `BACK` axes.
- Map A/E to native Fusion Glass; add dedicated Quantum Frame, Compact Cyclotron Coil, and Dense Cyclotron Outer Casing blocks using locked local texture bytes.
- Preserve E as Fusion-Glass-backed recipe I/O/energy positions and F as Dense-Casing-backed energy positions. Use a dynamic part renderer because one global formed texture cannot represent both roles.
- Register `CYCLOTRON_RECIPES` with max IO `9/9/1/1`.
- Apply normal overclock, `0.5×` duration, `1.6×` EU/t, and a parallel cap of `256 + Parallel Control Hatch`.
- Register Hydrogen Ion, Proton, Electron, Neutron, and Unknown Particle plus four source-grounded recipes that close the dependency chain.
- Add survival recipes for all three dedicated casings and the controller using approved native GTCEu semantic substitutes at UHV/UEV progression.

## Explicit exclusions

Do not port the complete GT++ isotope, quark, lepton, boson, arbitrary-material ion, RTG pellet, Strange Dust, or DSP recipe ecosystems. Do not invent textures, add dependencies, add maintenance/muffler behavior, or alter structure geometry for convenience.

## Validation

Contract validation, locked texture baseline, exact dimensions/symbol counts/controller coordinate, recipe IDs and cardinality, pure modifier arithmetic, resource completeness, compilation, tests, and client startup are required. Gameplay structure formation, mixed hatch appearance, JEI preview, and actual recipe execution remain explicit runtime gates.
