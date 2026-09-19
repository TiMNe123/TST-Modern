# BigBroArray fidelity fix design

**Date:** 2026-08-26  
**Status:** Written design approved on 2026-08-26; optional-addon mechanism and minimum-coil rule approved on 2026-08-26  
**Target:** Minecraft 1.20.1, Forge 47.3.0, GTCEu Modern 7.4.0

## Goal

Turn the current BigBroArray prototype into a faithful Modern port of TST's `TST_BigBroArray`: one machine can embed an approved single-block processor or generator, multiply it with TST's Parallelism Casing formula, apply the source coil/speed rules, and load or unload machines through multiblock buses without item loss.

The machine follows TST's modular layout: the `11 × 8 × 11` core forms and runs by itself, while zero to four independently validated addons may be built around it. Each addon retains its original shape, rotation, offset, blocks, coil channel, and Parallelism Casing channel.

## Source audit

- Original controller: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_BigBroArray.java`
- Original core: `11 × 8 × 11`, controller at raw `(5, 5, 4)`.
- Original addon: `17 × 26 × 15`.
- Four addon checks use one source matrix with fixed rotations and controller-relative offsets: addon 0/original `(-6,23,6)`, addon 1/90° `(7,23,-7)`, addon 2/180° `(22,23,6)`, and addon 3/270° `(7,23,21)`.
- Core occupied count is `321`, with counts `~=1 A=97 B=40 C=97 D=25 E=57 F=4`.
- Each addon occupied count is `900`; after the source's addon rename its counts are `G=134 H=44 I=42 J=64 K=86 L=530`.
- Core plus all four addons has `3,921` occupied positions and maximum controller-relative bounds `x=-22..22`, `y=-23..2`, `z=-21..23`, equivalent to a `45 × 26 × 45` envelope.
- Current target core is only `11 × 8 × 11` and has moved coil/parallel casings into the core. That layout is not source-faithful.
- GTCEu Modern has no native Processing Array implementation to reuse; the decision remains **Port**.

## Approved and proposed decisions

### Already approved

1. The core forms independently; `addonCount` may be `0..4` exactly as in TST.
2. The four addons use their original rotations, offsets, and optional validation behavior.
3. Across valid addons, effective Parallelism Casing, frame, glass, and coil tiers use the lowest participating tier. The minimum-coil rule is an approved correction to TST's order-dependent setter bug, where the last checked addon overwrote earlier coil tiers.
4. Coil blocks are allowed even though low-tier coils appear early in GTCEu Modern.
5. The custom TSTModern Mass Fabricator family must be accepted so BigBroArray can produce UU Matter fluid through its normal recipes.

### Decisions approved together with this design

1. Restore the source split between processor and generator mode. Native GTCEu Combustion Generator, Steam Turbine, and Gas Turbine families are supported. Semi-Fluid, Naquadah, ASP Solar, and EMT Solar stay excluded because this target currently has no faithful single-block family for them.
2. Restore a strict machine catalog. A random GT machine with a recipe type, and every multiblock controller, must be rejected.
3. Use a two-step glass approximation because GTCEu Modern has no tiered Borosilicate family: Tempered Glass permits input energy through IV; Fusion Glass permits every registered GTCEu high tier through MAX. Every valid piece must be internally uniform. The effective machine glass tier is the minimum across the core and valid addons.
4. Map the six source frame levels to Titanium, Tungsten Steel, Naquadah Alloy, Trinium, Neutronium, and Tritanium. They unlock embedded tiers IV, LuV, ZPM, UV, UHV, and unrestricted high tier respectively. GTCEu 7.4 does not natively generate `frameGt` for Trinium, so the approved TST Modern compatibility layer adds `GENERATE_FRAME` during `MaterialEvent`. Effective frame tier is the minimum across the core and valid addons.
5. Preserve TST recipe tiers, durations, component classes, predecessor chain, and research shape. Missing legacy addon materials use explicit native semantic substitutes; the port record must list every substitution.

## Structure contract

`BigBroArrayStructure` owns two static source-derived matrices: one core and one unrotated addon. It also owns the four immutable placement transforms. Production code never reads the old source tree.

The core is the only formal `FactoryBlockPattern`. Once it forms, `BigBroArrayAddonScanner` checks all four addon placements independently. It rotates the frozen addon coordinates, reads only loaded world positions, and returns an immutable `AddonMatch` for each complete piece. A missing, partially built, mixed-tier, or unloaded addon is not counted; it does not invalidate a valid core.

Addon discovery runs immediately after core formation and then as a staggered scan: one placement every five ticks, so all four refresh within twenty ticks without scanning a `45 × 26 × 45` volume at once. A changed effective addon state stops/reset recipe lookup before another recipe is admitted and synchronizes the new tiers/count to clients. It does not destroy the embedded machine. Standard preview shows the core, while four separate addon preview/hint pages show the fixed placements and rotations.

### Core symbols

| Symbol | Count | Target meaning |
|---|---:|---|
| `~` | 1 | BigBroArray controller |
| `A` | 97 | uniform Tempered Glass or uniform Fusion Glass |
| `B` | 40 | uniform frame from the six-level table |
| `C` | 97 | uniform native GT machine casing LV through MAX |
| `D` | 25 | Robust Tungstensteel Casing or core item/fluid/maintenance/muffler ability |
| `E` | 57 | Clean Stainless Steel Casing |
| `F` | 4 | Clean Stainless base or energy input/output ability |

Every core `A`, `B`, and `C` family is internally uniform.

### Per-addon symbols

| Symbol | Count | Target meaning |
|---|---:|---|
| `G` | 134 | internally uniform TSTModern Parallelism Casing MK1 through MK5 |
| `H` | 44 | internally uniform frame from the same six-level table as core `B` |
| `I` | 42 | internally uniform GTCEu heating coil channel |
| `J` | 64 | Tungstensteel Gearbox Casing |
| `K` | 86 | internally uniform Tempered Glass or Fusion Glass |
| `L` | 530 | Clean Stainless Steel Casing |

Mixed tiers make only that addon invalid. Across complete addons, effective `G`, `H`, `I`, and `K` tiers are their respective minima. Core/addon frame and glass minima then determine machine and hatch gates. Scanning for a maximum matching block is forbidden.

The D positions allow only the source-relevant maintenance, muffler, item import/export, and fluid import/export abilities. The F positions allow only energy input or energy output families that BigBroArray can actually use. Mode-specific requirements are validated when an embedded machine is loaded and whenever the structure reforms.

## Tier and parallel rules

For a formed core with no valid addon, `addonCount=0`, `parallelTier=0`, `coilTier=0`, `maxParallel=64`, and both processor multipliers are `1.0`. With one or more valid addons, `parallelTier` and `coilTier` are the minima across valid addons. The formulas remain source-derived:

```text
casingMultiplier = parallelTier > 3 ? parallelTier + 6 : parallelTier
maxParallel = parallelTier >= 5
    ? ((2,147,483,647 << casingMultiplier) / 5) × (1 + addonCount)
    : (64 << (parallelTier × 2 + (parallelTier > 3 ? 6 : 0))) × (1 + addonCount)
actualParallel = min(machineCount << casingMultiplier, maxParallel)
processorDurationMultiplier = 0.66 ^ parallelTier
processorEnergyMultiplier = actualParallel × (0.9 ^ coilTier)
```

Calculations use saturating `long` arithmetic and clamp only at GTCEu recipe-content boundaries. They must never wrap negative. `ModifierFunction.parallels()` records parallel metadata only; inputs, outputs, and EU must still be multiplied exactly once because GTCEu does not scale content from that metadata.

The embedded machine tier is the maximum overclock tier. A high-tier energy hatch must not overclock a lower-tier embedded machine beyond its own voltage.

## Embedded-machine catalog

`BigBroArrayMachineCatalog` is the sole authority for accepted controller items and provides a stable definition ID, mode, recipe type, and tier.

Processor families mirror the original whitelist where a native target exists: Macerator, Ore Washer, Chemical Bath, Thermal Centrifuge, Electric Furnace, Arc Furnace, Bender, Wiremill, Lathe, Forge Hammer, Extruder, Fluid Extractor, Compressor, Forming Press, Fluid Solidifier, Extractor, Laser Engraver, Autoclave, Mixer, Alloy Smelter, Electrolyzer, Sifter, Chemical Reactor, Electromagnetic Separator, Recycler, Centrifuge, Cutter, Assembler, Circuit Assembler, plus TSTModern Mass Fabricator.

Generator families are GTCEu Combustion Generator, Steam Turbine, and Gas Turbine. Their recipe outputs energy; they do not use processor duration reduction or coil EU discount. Fuel/content and EU output scale by actual parallel. Generator mode requires an output-energy ability and does not require input energy.

## Loading, persistence, and unloading

Screwdriver interaction loads from item import buses, never from the player's offhand. It scans in stable handler/slot order, chooses the first catalog entry, and aggregates only stacks representing that same machine definition and compatible item data. Unrelated items remain untouched.

Transfer is transactional:

1. simulate extracting the complete selected amount;
2. create and validate the new embedded state;
3. execute extraction only if validation succeeds;
4. reset recipe logic so dynamic recipe lookup wakes immediately.

Unloading simulates insertion into output buses first. Only after the full machine count fits does it insert and clear state. If output is full, nothing changes. Returned stacks are split at their maximum stack size and preserve the stored machine item data.

Persist a versioned `EmbeddedMachineState` containing registry ID, mode, tier, count, and item data. On load, resolve it through the catalog; stale or removed IDs become a safe invalid state rather than an arbitrary recipe type. Structure invalidation must not destroy embedded machines.

## Recipe behavior

Processor mode dynamically selects the catalog recipe type, rejects recipes above the embedded tier, computes input/output-limited parallel through GTCEu's parallel helper, applies content multipliers once, applies `0.66^parallelTier` duration, and applies parallel EU with `0.9^coilTier` discount. It then caps overclocking at the embedded tier.

Generator mode dynamically selects the generator fuel recipe type, uses input/output-limited parallel, scales consumed fuel and produced EU, and skips the processor speed and coil-discount modifiers. Output capacity must participate in recipe admission so generated EU cannot disappear.

The original pollution formula has no equivalent target system currently wired into this project. Remove misleading claims from the tooltip and mark pollution as an unavailable Modern deviation in the port record; do not invent a fake counter.

## Construction recipe progression

The six recipes keep source stage and duration:

| Output | Recipe map / EU tier | Duration | Required shape |
|---|---|---:|---|
| Controller | Assembler / IV | 24,000 t | 16 Data Orbs as Ancient-PA research equivalent; IV arms, emitters, field generators, IV superconducting wire; 24,576 mB Titanium as Nitinol60 substitute |
| MK1 | Assembler / 6,400 EU/t | 3,000 t | Robust Tungstensteel base; IV field generators, arms, emitters, wires, circuits; 9,216 mB Soldering Alloy |
| MK2 | Assembly Line / ZPM | 12,000 t | MK1 predecessor; ZPM components; Stable Titanium base; four-hour scanner research; Soldering Alloy + Naquadah Alloy substitute fluids |
| MK3 | Assembly Line / UHV | 24,000 t | MK2 predecessor; UHV components; Clean Stainless base; eight-hour scanner research; Soldering Alloy + Naquadah Alloy + Europium substitute fluids |
| MK4 | Researchable Assembly Line / UIV | 24,000 t | 16 MK3; UIV components/circuits; fusion/endgame native components; Neutronium-family fluids |
| MK5 | Researchable Assembly Line / UXV | 24,000 t | 16 MK4; UXV components/circuits; MAX-stage native components; Tritanium-family fluids |

The implementation must first enumerate the exact registered item/tag/fluid IDs in a recipe contract test. If a named form is not generated by GTCEu, replace it with an obtainable form of equal material amount and record the conversion. Do not retain the current HV→ZPM compressed progression.

## Validation contract

- Unit tests cover tier tables, catalog acceptance/rejection, persistence, transactional transfer, processor/generator modifier plans, saturation, and every source formula boundary.
- Structure tests freeze core/addon dimensions, all four transforms, per-piece occupied/symbol counts, normalized SHA-256 digests, full-envelope bounds, and the no-overlap contract.
- Recipe tests freeze recipe IDs, maps, EU tiers, durations, predecessor chain, research mode, and obtainable ingredient forms.
- Registration/resource tests ensure one controller registration, five casing registrations, complete block assets, localization, and no duplicate recipe IDs.
- Full Gradle test/build validation is followed by a manual client gate: form core alone; add/remove each rotated addon while formed; verify counts `0..4`; verify incomplete and mixed-tier addons are ignored; verify minimum parallel/frame/glass/coil aggregation; load/unload through buses; run one processor, Mass Fabricator, and each supported generator family; block on full outputs; save/reload; and verify JEI/preview pages.
- Each task gets at most three test/fix loops. If the same environment/tooling failure persists on the third attempt, record the exact failure and continue; never claim that gate as passed.
- A headless build can establish only `build validated; gameplay validation pending`.

## Out of scope

- Creating new Semi-Fluid, Naquadah, ASP Solar, or EMT Solar single-block generators.
- Requiring addons for core formation or compressing an addon into the core.
- Inventing a pollution subsystem.
- Changing Mass Fabricator UU Matter recipes themselves.
- Replacing the approved Parallelism Casing textures.
