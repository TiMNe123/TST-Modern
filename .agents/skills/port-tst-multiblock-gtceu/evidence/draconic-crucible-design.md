# Draconic Crucible approved-design evidence

Status: `AUDIT_COMPLETE`

This is a new TST Modern machine rather than a port of an existing TST controller. The current
user-approved visual and recipe decisions are the primary design authority. Draconic Evolution
1.20 is recipe and optional-compatibility evidence only.

## Purpose and progression

- The Draconic Crucible replaces the Draconium ore-processing and Awakened Draconium fusion steps
  required by this project without making Draconic Evolution mandatory.
- Both processing recipes run at `GTValues.VA[GTValues.UHV]` (`1,966,080 EU/t`).
- The machine has fixed parallel `1`, no chance boost, and normal GTCEu non-perfect overclocking.
  At UEV, the UHV 20-second ore batch uses `7,864,320 EU/t` and completes in 10 seconds.
- At least one item input bus and one item output bus are required. Up to ten item/energy
  buses or hatches may replace Radiant Naquadah Alloy Casings anywhere in the structure.
- One or two energy inputs are allowed within that total. A single UHV input is sufficient.

## Processing recipes

### Draconium ore batch

- Input: 64 Draconium Ore.
- Guaranteed output: 64 Draconium Dust.
- One mutually-exclusive bonus roll per completed recipe:
  - 84.5%: 16 Draconium Dust.
  - 10.0%: 2 Draconium Ore.
  - 5.0%: 1 Dragon Heart.
  - 0.5%: 1 Awakened Draconium Nugget.
- Duration: 400 ticks (20 seconds).
- EU/t: 1,966,080.
- Native GTCEu XOR chance logic is sufficient because the machine has fixed parallel 1.

### Awakened Draconium

- Inputs: 4 Draconium Ingots, 6 Draconium Cores, and 1 Dragon Heart.
- Output: 4 Awakened Draconium Ingots.
- Duration: 1,000 ticks (50 seconds).
- EU/t: 1,966,080.
- This is the approved ingot-form adaptation of Draconic Evolution 1.20's block-form fusion recipe.

### Dependency closure

- When Draconic Evolution is absent, Draconium Core uses the official 3x3 recipe: four Draconium
  Ingots, four Gold Ingots, and one Diamond produce one Draconium Core.
- Fallback Draconium provides dust and ingot forms. Fallback Awakened Draconium provides ingot and
  nugget forms.
- Fallback material forms use GTCEu's native tinted material-item renderer. The exact fallback ore
  ID is `tstmodern:draconium_ore`; it uses GTCEu's native Endstone ore model with the Draconium
  material tint and requires no new PNG.
- Fallback Draconium Core and Dragon Heart use GTCEu's native tinted gem and exquisite-gem models,
  respectively; their custom IDs and localized names remain distinct.
- Dragon Heart is bootstrapped by the 5% ore-processing bonus; Awakened Draconium is bootstrapped by
  the 0.5% nugget bonus and the main Awakened recipe.
- When `draconicevolution` is loaded, processing recipes resolve the mod's exact ore, dust, ingot,
  core, heart, nugget, and Awakened ingot registry entries instead of the fallback forms.

## End-only ore and Starcore Miner

- Without Draconic Evolution, a dedicated Draconium Ore block is registered and a GTCEu ore vein is
  registered only for `minecraft:the_end` and `#minecraft:is_end` biomes.
- Proposed fallback vein: Endstone layer, uniform Y 10..80, cluster size 16..24, density 0.20,
  weight 5, and no surface indicator.
- The vein generator places the dedicated ore block explicitly, so the Starcore Miner returns the
  same ore block rather than a generic host-rock form.
- With Draconic Evolution installed, its native End ore generation is retained and the fallback vein
  is not registered. The Starcore Miner adds `draconicevolution:end_draconium_ore` to its End-only
  weighted pool with the same effective weight, avoiding duplicate world generation.

## Structure

- Dimensions: 61 wide x 15 high x 45 deep. These bounds were approved to support the maximum
  Stage 5 dragon presentation without compressing the machine beneath it.
- Source coordinates: X increases left-to-right, Y increases bottom-to-top, Z increases
  front-to-back.
- GTCEu axes: `RIGHT = +X`, `DOWN = -Y`, `BACK = +Z`.
- Controller: `(30, 1, 0)`, symbol `~`, outward-facing `FRONT`.
- The implementation generates the aisles deterministically from the coordinate rules below.

### Coordinate rules

- `y=0`: all 61x45 positions are `D`.
- `y=1`: all positions are `R` except the controller at `(30,1,0)=~`.
- Volcano center is `(20, *, 22)`. Its outer X/Z radii for `y=2..12` are respectively
  `(19,20)`, `(18,19)`, `(17,18)`, `(16,17)`, `(15,16)`, `(14,15)`, `(13,14)`,
  `(12,13)`, `(10,11)`, `(8,9)`, and `(6,7)`. The monotonic rounded ellipses make the
  mountain taper continuously from its broad foot to its narrow summit.
- Each volcano band is a filled Euclidean ellipse divided into functional rings: the outer two
  blocks are `M` Mechanically Enhanced Obsidian, the next two blocks are `S` Stabaloy Firebox,
  and the enclosed chamber is required air `#`.
- Sloped east/west `P` Neutronium Pipe ribs, a front `G` observation strip, a lower Stabaloy heat
  chamber, and a central `C` Compact Fusion Coil column are embedded into those bands.
- `y=13`: a rounded `(5,6)` crater rim uses `F` Field Restriction Casing with eight `T` Field
  Restriction Coil T1 anchors; its center remains required air.
- `y=14`: the exposed `K` Dragon Egg core is at `(20,14,22)`.
- The right-side technology wing uses `D`, `R`, `G`, `B`, and `P`; its beam guide enters the east
  side of the volcano without changing the rounded outer profile.
- All unspecified positions are unconstrained preview space.

### Symbol counts and mappings

| Symbol | Count | Target |
|---|---:|---|
| `~` | 1 | Draconic Crucible controller |
| `D` | 2,826 | `tstmodern:extreme_density_casing` |
| `R` | 2,914 | Radiant Naquadah Alloy Casing, or an allowed item/energy bus or hatch |
| `M` | 1,738 | `tstmodern:mechanically_enhanced_obsidian` |
| `S` | 1,728 | `tstmodern:stabaloy_firebox_casing` |
| `T` | 8 | `tstmodern:field_restriction_coil_t1` |
| `F` | 48 | `tstmodern:field_restriction_casing` |
| `C` | 55 | `tstmodern:compact_fusion_coil_t3` |
| `P` | 26 | `tstmodern:neutronium_pipe_casing` |
| `G` | 30 | `tstmodern:field_restriction_glass` |
| `B` | 13 | `tstmodern:particle_beam_guidance_pipe_casing` |
| `K` | 1 | `tstmodern:draconic_crucible_core` |
| `#` | 3,677 | Required air |

`FIELD_RESTRICTION_COIL_T1` replaces the earlier visual proposal's T4 coil because T4 requires
UIV components and a UXV circuit. T1 preserves the approved visual role and is obtainable by UHV.

## Dedicated core and construction

- `tstmodern:draconic_crucible_core` is a normal non-teleporting block that uses the native
  `minecraft:block/dragon_egg` model. It is the visible Dragon Egg catalyst at the volcano center.
- Proposed core Assembly Line recipe: 1 Dragon Egg, 8 Field Restriction Casings, 8 Compact Fusion
  Coil T3, 4 UHV Field Generators, and 16 Neutronium Plates; 1,000 ticks at VA[UHV]. It uses Data
  Orb Research Station research with a unique research ID.
- Proposed controller Assembly Line recipe: 16 Field Restriction Casings, 8 Compact Fusion Coil T3,
  8 Particle Beam Guidance Pipe Casings, 4 UHV Field Generators, 4 UHV Emitters, and 4 UHV circuits;
  1,200 ticks at VA[UHV]. It uses Data Module Research Station research keyed from the Draconic
  Crucible Core with a unique research ID.
- Exactly one Dragon Egg is required for the whole structure; the controller recipe does not consume
  a second egg.

## Rendering and localization

- The approved dragon is a client-only, no-collision maximum Stage 5 Fire Dragon perched on the
  volcano. Its forelegs grip the crater rim, its hindquarters rest on the relative-right/rear slope,
  its neck arches while keeping the mouth raised above the crater for the fire stream, and its
  five-joint tail is pitched down at the root and lengthened 1.45x along its local axis to complete
  a broad C-shaped curve across the relative-right/rear deck
  while avoiding the controller, hatches, and technology wing. It is not part of formation.
- Model authority is Ice and Fire branch `1.20`, commit
  `0526aad94119b705a86207623481b087c4d052ca`. The implementation reuses the exact Tabula assets
  with a small local Java-standard-library loader, so neither Ice and Fire nor Citadel is required.
- Landed geometry/pose: `firedragon_ground.tbl`, SHA-256
  `4891325a2d6c76ec5cc7a912e6f9f8078cc678da108719fd787412d914b318ff`.
- Sleeping idle pose: `firedragon_sleeping.tbl`, SHA-256
  `8669057246a2764650dad4cf2e39250f48bf1e685c62d1744c4d161ee47885a7`.
- Sleeping Stage 5 red texture: `red_5_sleeping.png`, SHA-256
  `1f72c1d108418b08ed7db0b707a8bcd29b5eb9de5afa6b4a52df7acb507a03f1`.
- Fire Dragon Egg visual authority is the unformed core's `minecraft:block/dragon_egg` baked model.
  Both states use the exact Ice and Fire `textures/item/dragonegg_red.png` sprite, SHA-256
  `a632070a33092bbe7db94a5f4d3a2b61b0540ef23a05ee414b45ddaf7d5b07bb`; the formed dynamic render
  reuses that baked model so the formed and unformed silhouettes and surface layers are identical.
- Working charge pose: `firedragon_attack_blast_charge3.tbl`, SHA-256
  `baa521804de979942d7619e486a2b8a27c744af0797d816b32d60306eceae72b`.
- Working breath pose: `firedragon_attack_stream_breath.tbl`, SHA-256
  `5c7f44e77ba3cddd5406e86bf67f33d84196f8326f3524fc12b35ec6f3e427b5`.
- Flight poses: exact Ice and Fire `firedragon_flight1.tbl` through `firedragon_flight6.tbl`,
  deployed respectively with SHA-256 `0d3a6b68c72ce8015654eb0e1ca41ea55bbafafe414d4ab38d0521c034ae3cf4`,
  `4e6e7b85c553b604253a38049710bc4fdbef02cc3ae179ae5a75bbe6dcc6f5dd`,
  `6396e7db525b1c82d71e3f3dc6f673733ca99a483e4e121bd61ce4221a7c6050`,
  `ff8d4468dd590c46fb2c0c9688169aea4830de8ff63a902cfa510a081896fc39`,
  `debd84f75e26a45ce230593899e91dc79be9650b4bc1d10483754444c0a04fb7`, and
  `92f9f9ff25b01d8ce4c9e5aff668881f131692c68d78272cfe0879588e5ec51a`.
- All three pose files contain the same 104 named parts and 256x128 atlas layout. The ground pose
  supplies the original stance. The approved local pose correction changes only named part
  transforms: the two wing roots are raised to expose the supporting legs, the lower body and legs
  remain locked to the volcano, a four-part idle neck correction forms the raised-mouth arch, and
  `Tail1` through `Tail5` form the extended floor-level curve with a downward root pitch. The neck
  correction fades into the exact charge and stream-breath poses while working. Idle additionally
  tightens the source sleeping neck and tail curve, uses the source sleeping texture with closed
  eyes, suppresses the glow overlay, and lowers only the roof anchor to meet the structure surface.
- Stage 5 red texture: `red_5.png`, SHA-256
  `b102f889461787378d7cf29440daf5cf025d94de09169dbceffe684d60b91412`; eye overlay:
  `red_5_eyes.png`, SHA-256 `2be77e04908d0931ae3aa05cd816152b7a717b56a93b71c62fb162dafcf998f7`.
- Fire stream authority is Ice and Fire's `ParticleDragonFlame` pipeline and exact
  `textures/particles/dragon_flame.png`, SHA-256
  `d222603d5dda6008a40911996e34adda08ce5a919046d944db568aa85373e9d0`. The local renderer
  reproduces the source particle stream toward the core without an Ice and Fire runtime entity.
- The Stage 5 scale is the source renderer's maximum `renderSize / 3 = 30 / 3 = 10`.
- The user-approved presentation places the body on the structure's relative-right/rear side and
  the head over the crater, facing relative-left. The mouth remains right of the Dragon Egg target,
  so fire travels right-to-left into the crater.
- Formed idle uses the exact sleeping asset with the approved local curl correction on the
  technology-wing roof, closed eyes, no glow overlay, and no fire. After an
  eligible recipe is found, a 200-tick startup runs without consuming recipe input or EU: wake 10,
  takeoff 30, one 360-degree orbit around the vertical Dragon Egg axis 100, landing 40, and charge
  20 ticks. The controller displays `Animation starting...` during this gate. Recipe progress and
  the fire stream begin only after startup completes. Consecutive recipes reuse the landed dragon
  without another startup; losing the eligible recipe during startup cancels it without input/EU
  loss. Paused/stopped removes the stream and returns the dragon to the technology wing. Unformed:
  hidden.
- While formed, the static core model is hidden and the controller renders the exact Fire Dragon
  Egg block model in its place. It renders full-bright so the enclosed core light does not turn the
  red texture black. Using the same baked model and texture makes the formed render identical to
  the unformed block instead of the smoother four-cube entity egg. The dynamic path emits every
  baked quad through the opaque block atlas; Forge's default `renderSingleBlock` path is forbidden
  here because it converts solid blocks to a cutout sheet and exposes transparent gaps in this sprite.
  Once the breath stream starts, the egg reproduces Ice and Fire's
  heat-incubation `walk` and `flap` motion (`speed=0.3`, `degree=0.3`), scaled by breath strength;
  it remains still during startup, pause, and idle.
- The existing user-finished landed/charge/breath transform is locked. Flight and landing may only
  interpolate into that exact anchor, yaw, named-part correction, and mouth-to-core alignment; they
  must not alter its model pose, position, rotation, tail layout, or beam target.
- The six flight poses face opposite the renderer's former orbit yaw. A flight-only 180-degree yaw
  correction keeps the head tangent to the approved clockwise path so the tail never leads; the
  correction is removed while landing into the locked perched yaw.
- The controller uses the existing Radiant Naquadah Alloy Casing base and the native GTCEu Fusion
  Reactor workable overlay. The core uses the Ice and Fire red Fire Dragon Egg presentation.
- Controller identity palette: `§c§lDraconic Crucible§r`; functional tooltip accents use at least
  `§6`, `§d`, `§b`, and `§c`; the core name uses `§5...§r`; both locales use the mandatory Modern
  Edition footer.
- Fallback material and component icons are native GTCEu material models tinted purple for
  Draconium and orange for Awakened Draconium; no texture bytes are copied or generated.

## Primary external evidence

- Draconic Evolution 1.20 Awakened Draconium fusion recipe:
  `https://github.com/Draconic-Inc/Draconic-Evolution/blob/1.20/src/main/generated/data/draconicevolution/recipes/awakened_draconium_block.json`
- Draconic Evolution 1.20 Draconium Core recipe:
  `https://github.com/Draconic-Inc/Draconic-Evolution/blob/1.20/src/main/generated/data/draconicevolution/recipes/components/draconium_core.json`
- Draconic Evolution 1.20 registry names:
  `https://github.com/Draconic-Inc/Draconic-Evolution/blob/1.20/src/main/generated/assets/draconicevolution/lang/en_us.json`

## Final approved contract

- Field Restriction Coil T4 is replaced with T1 for UHV progression.
- The generated structure is exactly 61x15x45 with contract-locked symbol counts.
- The End vein parameters and optional-mod behavior are approved.
- The core/controller construction recipes are approved.
- The native Fusion Reactor overlay, exact Ice and Fire Stage 5 red dragon, and red/purple palette
  are approved.
- The gameplay screenshot review approved an open rounded volcano with an exposed Dragon Egg. The
  subsequent Stage 5 revision approved the 61x15x45 bounds and a realistic, continuously tapered
  volcano whose casing roles follow their physical function.
- The perched-dragon revision locks the exact landed ground, blast-charge, and stream-breath poses:
  the dragon grips the volcano, its arched neck holds the mouth above the crater, its lengthened tail
  wraps broadly across the floor instead of pointing upward, and the right-to-left fire stream exists
  only while the machine is working. The final startup revision adds the approved 10-second
  wake/takeoff/orbit/landing/charge sequence and must finish seamlessly at that locked landed pose.
