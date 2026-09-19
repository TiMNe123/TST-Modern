# TST Disassembler port design

**Date:** 2026-08-24  
**Status:** Written design approved on 2026-08-24; High-Tier activation amendment approved on 2026-08-24; obtainable dense-plate equivalence clarified from GTCEu registry evidence on 2026-08-24

## Goal

Port the Twist Space Technology Large Disassembler to Minecraft 1.20.1 / GTCEu Modern 7.4.0 while preserving its defining behavior: it consumes crafted machines or components, finds the recipe that produced them, and returns the consumed ingredients without using energy.

The target is a distinct reverse-assembly machine, not a replacement for GTCEu recycling. GTCEu Arc Furnace recycling recovers material forms; it does not reconstruct the item and fluid inputs of Assembly Line and Assembler recipes. The overlap result is therefore **Port**.

## Scope

The port includes:

- the original `27 × 23 × 28` multiblock structure and its tiered casing rule;
- fourteen Component Assembly Line Casing blocks from LV through MAX;
- dedicated Molecular Casing and Hollow Casing blocks;
- a reload-aware reverse-recipe index for GTCEu Assembly Line and Assembler recipes;
- dynamic no-energy disassembly processing, tier gating, deterministic ingredient selection, JEI representatives, controller/casing recipes, localization, textures, models, loot, and tests;
- a registration seam for future Miracle Top, Component Assembly Line, and Photon Controller recipe sources.

TSTModern declares `IGTAddon.requiresHighTier() = true`. GTCEu therefore registers its UHV-and-above machines and components even when `run/config/gtceu.yaml` keeps `highTierContent: false`. This is a deliberate project-wide effect: installing TSTModern enables GTCEu High-Tier content so the approved original-style progression cannot contain null or unobtainable UHV inputs.

The port deliberately excludes unavailable legacy Thermal Cloth and storage-cell special recipes. It does not add Energy, Maintenance, Muffler, Parallel, or other unrelated hatches. It does not change the unapproved Astral Pylon/Mega Tree Farm work.

## Source and target baseline

- TST controller: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/system/Disassembler/TST_Disassembler.java`
- TST reverse handler: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/system/Disassembler/TST_DisassemblerRecipeHandler.java`
- TST recipe representation: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/system/Disassembler/TST_SimpleDisassemblyRecipe.java`
- Original registration: meta ID `19041`, display name `TST Large Disassembler`
- Original process cost: `100` ticks, enabled by default, no energy consumption
- Target: Minecraft `1.20.1`, Forge `47.3.0`, GTCEu `7.4.0`, LDLib `1.0.40.b`, JEI `15.20.0.115`
- Working-tree baseline: `main` at `c65c6bc`; existing uncommitted Mega Tree Farm resource changes are outside this port and must be preserved.

The original handler searches Component Assembly Line, Miracle Top, Assembly Line, Assembler, and Photon Controller recipes and accepts the first output match. Only GTCEu Assembly Line and Assembler are currently available in the target. Assembly Line has higher lookup priority than Assembler when both produce the same item. Additional sources register with an explicit stable priority rather than relying on collection iteration order.

## Architecture

The port uses the repository's per-machine module contract:

| Unit | Responsibility |
|---|---|
| `machine/DisassemblerMachine.java` | formation-time casing-tier validation, aggregate input scanning, duration/tier rules, and no-energy processing behavior |
| `registry/machine/DisassemblerDefinition.java` | controller definition, exact structure, predicates, abilities, rendering, and preview |
| `data/recipe/DisassemblerRecipes.java` | controller, casing, Molecular/Hollow, and private prerequisite recipes |
| `recipe/disassembler/DisassemblerRecipeIndex.java` | registered source adapters, output-keyed cache, reload invalidation, deterministic priority, and JEI representative generation |
| `recipe/disassembler/DisassemblerRecipeAdapter.java` | converts one eligible GTCEu source recipe into an immutable reverse-recipe description |
| `recipe/disassembler/DisassemblerRecipePolicy.java` | eligibility, blacklist, representative ingredient selection, NBT-insensitive matching, tier gate, and saturating calculations |

The Disassembler recipe type uses GTCEu custom recipe logic. A normal static recipe lookup remains empty; custom logic asks the reverse index to synthesize one aggregate recipe from the current input buses. This keeps datapack recipes authoritative and avoids generating a permanent reversed copy during data generation.

The reverse index owns an ordered list of source adapters. At recipe/resource reload completion, it discards every output lookup and JEI representative derived from the prior recipe set. The first query in the new reload generation rebuilds the needed output entry from current recipes. Cache keys use the output item identity and intentionally omit NBT. The cache stores immutable descriptions only, never mutable recipe or stack instances.

Future recipe sources use the same registration interface and provide a source ID, priority, recipe enumeration function, and adapter. Adding one must not change machine logic.

## Structure

- Dimensions: `27 × 23 × 28` (`width × height × depth`)
- Target axes: `RIGHT`, `DOWN`, `BACK`
- Controller: symbol `~`, exactly once, at `x=13, y=21, z=0`, facing outward
- Every aisle has 23 rows and every row has width 27.

| Symbol | Count | Target predicate | Rule |
|---|---:|---|---|
| A | 36 | `GTBlocks.FUSION_GLASS` | approved GTCEu equivalent for Borosilicate glass family |
| B | 66 | one of fourteen dedicated Component Assembly Line Casings | all 66 blocks must have the same tier |
| C | 4 | `GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX` | direct native mapping |
| D | 60 | `GTBlocks.FUSION_CASING` | approved equivalent |
| E | 56 | `GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING` | approved equivalent |
| F | 92 | `GTBlocks.FILTER_CASING` | approved equivalent |
| G | 318 | dedicated Molecular Casing | fixed casing |
| H | 4 | dedicated Hollow Casing | fixed casing |
| I | 8 | Molecular Casing or allowed ability | at least one Input Bus, one Output Bus, and one Output Hatch in total |
| J | 84 | `Predicates.frames(GTMaterials.Neutronium)` | approved replacement for Cosmic Neutronium frame |

The B predicate accepts all fourteen blocks for pattern matching, then `formStructure` scans the matched B positions. It stores `casingTier = sourceMeta + 1` and rejects the formed state with a clear block-matching error if any B tier differs. The fourteen target tiers are LV, MV, HV, EV, IV, LuV, ZPM, UV, UHV, UEV, UIV, UMV, UXV, and MAX, represented internally as `1..14`.

The eight I positions chain only Molecular Casing, item import, item export, and fluid export. There is no Energy Input, Maintenance, Muffler, Parallel Control Hatch, fluid input, or generic auto-ability expansion.

## Casing identity, textures, and progression

The casing catalog mappings approved on 2026-08-21 are authoritative for A, C, D, E, F, and J. Native blocks reuse their own assets and are not duplicated in TSTModern.

B is a fourteen-block dedicated family with stable registry identities and the audited GT5 textures `0.png` through `13.png` from `texture_block_casing/05_Disassembler`. G and I share one Molecular Casing identity and `EM_CASING.png`; H uses one Hollow Casing identity and `EM_HOLLOW.png`. Each dedicated block receives a BlockItem, blockstate, block model, item model, self-drop loot entry, English and Vietnamese names, and an obtainable construction recipe.

Progression follows the approved policy:

- LV through IV Component Assembly Line Casings use GT5-style Assembler recipes.
- LuV through UHV use GT5-style Assembly Line recipes, preserving the research chain and semantic component classes.
- UEV through MAX form a predecessor chain. Each recipe consumes the immediately previous casing and scales UHV-class machine components and circuits with endgame materials available in this project. The implementation plan freezes the exact material and count table after registry verification. Every step remains craftable when the configuration file says `highTierContent: false` because the addon explicitly requires High-Tier before GTCEu registers those inputs.
- Molecular Casing uses the audited TecTech High Power Casing/Osmiridium-family progression translated to available Modern materials at UV.
- Hollow Casing uses Molecular Casing plus the audited Europium/dense-plate progression and produces two blocks.

No recipe may silently substitute an unavailable item. Any necessary semantic replacement is recorded in the port record and must remain within this approved progression policy.

Registry verification found that GTCEu 7.4.0 does not generate `plateDense` forms for Osmiridium, Europium, Aluminium, Stainless Steel, or Titanium. Wherever the approved progression calls for one of those unavailable dense plates, the implementation uses exactly nine obtainable regular plates of the same material per dense plate. This preserves the material amount and prevents an uncraftable empty-tag ingredient; the implementation plan freezes each affected count.

## Reverse-recipe eligibility

A source recipe is eligible only when all of these conditions hold:

1. It has exactly one non-empty item output stack.
2. It has no fluid outputs.
3. Its output is not a GT tool and is not in the Disassembler blacklist.
4. Every returned input can be represented as an item or fluid output.

The blacklist preserves the semantic exclusions from TST for tools and destructive/special legacy outputs. Modern-only entries are listed explicitly in code and tests; blacklist checks use registry identity or tags, not localized names.

Consumed item and fluid inputs become outputs of disassembly. Inputs marked non-consumable by GTCEu, including configuration circuits and catalysts with zero consumption chance, are ignored. For an ingredient with alternatives, the adapter selects the first registered representative after GTCEu's stable ingredient expansion; this choice is deterministic across lookups in one recipe generation. Input matching compares item identity and ignores NBT, matching TST behavior.

When multiple source recipes produce the same item, the lowest numeric source priority wins; ties use the source recipe ID in ascending lexical order. Current priority is Assembly Line before Assembler. JEI uses the same winner, so displayed and executed recovery cannot diverge.

## Processing behavior

One machine run scans every non-empty stack in all input buses. For each stack with an eligible winning source recipe:

```text
batchForStack = stackCount / sourceOutputCount
consumedForStack = batchForStack × sourceOutputCount
processed = sum(batchForStack)
```

Stacks with `batchForStack == 0`, blacklisted outputs, ineligible source recipes, or recipes above the tier gate remain untouched. Valid stacks may correspond to different source recipes in the same run. The synthetic aggregate recipe consumes only the calculated whole batches and combines all returned item and fluid inputs using saturating arithmetic.

Recipe tier is obtained through `RecipeHelper.getRecipeEUtTier` using GTCEu's tier numbering. The gate is:

```text
if casingTier < 14 and recipeTier > casingTier + 1: reject that source recipe
if casingTier == 14: no recipe-tier ceiling
```

Duration preserves the original integer-floor behavior:

```text
duration = max(1, processed / (4 × casingTier)) × 100 ticks
```

The division is integer division. A successful run therefore lasts at least 100 ticks. Item counts, fluid amounts, `processed`, multiplications, merged outputs, and final duration use saturating arithmetic. Final recipe fields are clamped to the maximum supported GTCEu representation without wrapping negative.

The machine consumes no EU and does not overclock. It has no GTCEu parallel, Parallel Hatch multiplier, batch mode, recipe locking, or input separation. Required Output Bus and Output Hatch positions establish valid destinations, but insufficient output capacity does not block a run: excess item or fluid output is voided, preserving TST's `supportsVoidProtection = false` behavior. Input consumption and the accepted aggregate are committed atomically at recipe start.

Fusion Coil or other Modern outputs require no hard-coded special case. If a current Assembly Line or Assembler recipe satisfies the policy, it is reversible automatically.

## Controller recipe

The controller remains an Assembly Line recipe at UEV with the original quantities and `72,000` tick duration:

- UHV Assembling Machine ×64
- UHV Field Generator ×16
- UHV Electric Pump ×64
- UHV Conveyor Module ×64
- UHV Robot Arm ×256
- UHV Energy Input Hatch ×64, replacing legacy `eM_Power ×64`
- Neutronium Frame ×16, replacing Cosmic Neutronium Frame ×16
- Osmiridium Plate ×144, preserving the material amount of the unavailable Dense Osmiridium Plate ×16 form
- Soldering Alloy `147,456 mB`, replacing the same amount of solder plasma
- UU-Matter `128,000 mB`
- Super Coolant `768,000 mB`

Research uses an LV Assembler target with the original eight-hour scan duration. All substitutions above are approved Modern adaptations and are not represented as original TST ingredients.

## Rendering and localization

The controller's base/formed casing is Dimensional Transcendent Casing. Its overlays are:

- idle front: `OVERLAY_DTPF_OFF`
- active front: `OVERLAY_DTPF_ON`
- active emissive front: `OVERLAY_FUSION1_GLOW`

The explicit machine JSON separates controller and ability-part rendering. Formed controller variants use Dimensional Transcendent Casing, while top-level `texture_overrides.all` uses Molecular Casing so the I-position parts retain their native Input/Output overlays on the correct base. Hatches do not sample adjacent blocks or inherit the controller's rendered pixels.

All names, tooltips, tier errors, and rejection messages use translation keys in `en_us` and `vi_vn`. Tooltips state no-energy operation, casing-tier gate, all-input batching, and voided overflow without claiming unavailable legacy integrations.

## JEI and reload behavior

JEI receives one representative reverse recipe for each eligible winning output, deduplicated by output item identity and using the same source priority as runtime. It shows the crafted item as input and only consumed source ingredients as item/fluid outputs. Non-consumable circuits and catalysts are absent.

Recipe/resource reload invalidates both runtime output lookups and JEI representatives. No recipe object from the previous generation may remain reachable from the active index. Reload tests use two generations with different source recipes to prove removal as well as addition.

## Error handling and edge cases

- Mixed B casing tiers prevent formation and identify the expected same-tier rule.
- A stack with an unmatched remainder consumes only complete source-output batches.
- A source recipe with alternatives always uses its deterministic first representative.
- Duplicate producer recipes follow source priority and lexical recipe ID, never nondeterministic map order.
- Blacklisted, tool, fluid-output, and multi-item-output recipes are skipped without consuming their input item.
- Missing output capacity voids only overflow; it does not duplicate recovered ingredients or roll back consumed valid batches.
- Saturation prevents negative counts or durations even when many large stacks are aggregated.
- A reload cannot execute or display a recipe removed from the current datapack set.

## Testing and validation

Implementation is test-first. Focused tests cover:

- structure dimensions, row widths, controller count/position, symbol counts, and exact ability set;
- all-same B tiers, mixed-tier formation rejection, and `meta + 1` tier capture;
- recipe eligibility, GT tool/blacklist exclusions, fluid-output and multi-output rejection;
- NBT-insensitive matching, non-consumable catalyst omission, and deterministic alternatives;
- Assembly Line versus Assembler priority and recipe-ID tie breaking;
- tier gate at ordinary casings and unlimited MAX casing;
- multi-bus/multi-stack aggregation, whole-batch remainders, duration floor, and zero-valid-input behavior;
- item/fluid merge saturation, duration saturation, and overflow voiding;
- reload invalidation and JEI/runtime agreement;
- unique recipe IDs, registration cardinality, dedicated resource completeness, and controller recipe substitutions.

Validation order is casing catalog validator, static structure/resource/registration tests, focused unit tests, `compileJava`, full test suite, full build, then client/world formation, operation, rendering, reload, and JEI inspection. Build success can establish only **build validated; gameplay validation pending**. The port reaches **gameplay and JEI validated** only after the in-world gates pass.

## Approved deviations from TST 1.7.10

- Only GTCEu Assembly Line and Assembler are active reverse sources initially; a stable adapter API replaces hard dependencies on unavailable GoodGenerator/TecTech integrations.
- Thermal Cloth and legacy storage-cell special recipes are omitted because their dependencies do not exist.
- Cosmic Neutronium Frame becomes Neutronium Frame.
- `eM_Power` becomes UHV Energy Input Hatch.
- Solder plasma becomes Soldering Alloy at the same amount.
- Unavailable dense-plate forms become nine regular plates of the same material per dense plate, preserving total material amount.
- UEV through MAX Component Assembly Line Casing progression is made self-contained for `highTierContent: false`.
- TSTModern declares `requiresHighTier() = true`; this overrides the configuration flag through GTCEu's supported addon lifecycle and enables GTCEu High-Tier content globally.
- Native GTCEu/GCYM equivalents replace approved source casings A, C, D, E, F, and J.
- Controller and ability-part formed textures are expressed through GTCEu machine-model paths while preserving the original visual roles.
