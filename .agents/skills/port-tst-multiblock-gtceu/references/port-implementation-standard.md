# TST multiblock implementation standard

This is the repository-wide contract distilled from the first five ports: Mega Stone Breaker, Giant Vacuum Drying Furnace, Nether Interface, Hyper Thermal Convector, and Mega Tree Farm.

## 1. Evidence and deviations

The functional target is original TST/GT5 source. Use this precedence when sources conflict:

1. Explicit current user requirements and approved deviations.
2. The current working-tree implementation when confirmed as the approved baseline.
3. Original TST controller, inherited code, recipe pools, registration, configuration, resources, and GT5 dependencies.
4. Exact-version GTCEu Modern, LDLib, and JEI source or bytecode for target APIs and runtime behavior.
5. Runtime logs/screenshots as reproduction evidence.
6. Planning documents and inference only as leads.

Capture file hashes or exact snippets before a refactor. Do not restore older `HEAD` behavior over an approved uncommitted change. Record every intentional semantic, progression, structure, or visual deviation in the port record.

For a new machine, materialize these facts in the JSON defined by [source-contract.md](source-contract.md). Production implementation starts only after that contract validates, has no unresolved entries, and is approved by the user. Existing completed ports supply architecture/API examples only; they never supply another machine's facts.

## 2. Per-machine module contract

Use these seams unless a machine demonstrably needs an additional shared subsystem:

| Responsibility | Path | Public surface |
|---|---|---|
| Runtime behavior | `machine/<Machine>Machine.java` | GTCEu overrides and intentional recipe-facing helpers only |
| Controller/structure/rendering | `registry/machine/<Machine>Definition.java` | `public static final ... MACHINE` |
| Processing, controller, casing, and private prerequisites | `data/recipe/<Machine>Recipes.java` | `public static void register(provider)` |

`TSTMachineRegistry` initializes each definition exactly once. `TSTModernGTAddon.addRecipes` calls each recipe module exactly once. Keep blocks, materials, recipe types, and genuine cross-machine infrastructure in their shared registries.

Recipe ownership is semantic, not based on where a recipe happened to sit in a former monolith. A prerequisite belongs to the machine module that needs it unless it is intentionally shared. The first five ports exposed both failure modes: HSSE/HSSS belong with Giant Vacuum Drying Furnace, while the Hellish Metal bootstrap must move with Nether Interface or the port becomes unobtainable.

## 3. Structure contract

Before writing a GTCEu pattern, record:

- dimensions as width × height × depth;
- source axis order and target `RIGHT`, `DOWN`, `BACK` mapping;
- exact aisle text or a reproducible extraction;
- controller character, count, outward-facing side, and preview orientation;
- every character's source registry/meta or dynamic predicate;
- fixed hatch positions, replaceable ability positions, and minimum/maximum counts;
- air, any, glass, coil, frame, and tiered families.

Then prove:

- all rows have equal width and all aisles equal height;
- the controller occurs exactly once;
- every used character has exactly one intended predicate;
- no fixed hatch, required casing minimum, air, or `any` cell was relaxed for convenience;
- the builder/pattern matches the captured approved baseline after refactors.

Attach an ability to the source casing predicate whose base appearance should be inherited. `autoAbilities(recipeTypes)` covers recipe I/O and energy only; maintenance, parallel, muffler, or fixed special abilities remain explicit machine decisions.

## 4. Casing, texture, and resources

Resolve each exact source block/meta through the machine catalog:

- `direct_native`: use the listed native block/predicate and do not duplicate its texture.
- `equivalent_review`: keep equivalent provenance. Apply the listed target without another question only when covered by `casing-catalog.json.reuseApproval`.
- dedicated classes: use the audited original, approved reused, or approved designed texture with a stable TSTModern registry identity.

If the actual target differs from the approved catalog target, stop for approval. Shared source identities map to one shared block, not one block per machine or pattern letter.

All local PNG bytes and model texture references are additionally governed by [texture-authority.md](texture-authority.md) and its locked SHA-256 baseline. Updating the baseline is not a substitute for proving texture authority.

Every dedicated block requires registration, BlockItem, blockstate, block model, item model, texture, self-drop loot, and `en_us`/`vi_vn` localization. Construction recipes require approved progression. Record texture provenance as TST, GT5, GTCEu, modified, or newly designed.

## 5. Mechanics and arithmetic

Audit and port these independently:

- supported recipe types and modes;
- voltage/tier gates and overclock rules;
- speed, EU, and TST output multipliers;
- GTCEu recipe parallel and Parallel Control Hatch contribution;
- deterministic and chanced outputs;
- periodic item/fluid consumption, persistence, failure, and shutdown behavior.

When both systems are intentionally retained, a deterministic output follows:

```text
base output × TST multiplier × GTCEu parallel
```

Apply every factor exactly once. Parallel must also scale inputs, EU/t, and independent recipe executions according to GTCEu semantics. Do not pre-multiply chanced output and then let `recipe.parallels` roll it again. Use saturating arithmetic for all values that can exceed an integer or long limit.

## 6. Recipes and progression

Compare IDs, types, circuits, catalysts, items, fluids, chances, duration, EU/t, metadata, and optional dependencies with source. Verify controller, dedicated casing, and required bootstrap recipes are obtainable at the approved tier. Recipe IDs must be unique and registration cardinality must be one.

Record the source circuit tier independently from its standalone fallback. UHV and lower use the
matching `CustomTags` input. UEV, UIV, UXV, OpV, and MAX use
`TSTCircuitTags.get(GTValues.<tier>)`: StarT and Sky of Grind populate the standard
`gtceu:circuits/<tier>` tags. The resolver recognizes StarT tiers UEV through UXV and Sky of Grind
tiers UEV through MAX, returning `UHV_CIRCUITS` when the requested tier is unavailable in the
loaded integration. Never solve standalone compatibility by changing the verified source tier to
UHV, naming a KubeJS/pack-owned item, or inserting UHV circuits into an endgame global tag.

For isotope production and decay chains, verify and prefer the original TST/GT5 or TST/GTNH chain before proposing or implementing an invented recipe.

Do not reproduce an impossible legacy dependency. For content absent from 1.20.1, such as Thaumcraft in this project, propose a native semantic equivalent or new dedicated content and record the approved progression/semantic loss.

Trace the complete registered dependency closure, not only item IDs. Every referenced material form and fluid storage key must exist in the exact target GTCEu version before recipe registration. In particular, `material.getFluid(PLASMA, ...)` is invalid unless that material's fluid property registers the plasma storage key. A recipe compiling does not prove its fluid exists at runtime.

For ZPM and above, every controller and dedicated casing construction recipe must use Assembly Line research generated by the Research Station. Use the green Data Orb (`GTItems.TOOL_DATA_ORB`) for each casing and the purple Data Module (`GTItems.TOOL_DATA_MODULE`) for each controller. Set `dataStack(...)` explicitly and assign every generated research recipe a unique `researchId(...)`; the default ID is derived from `researchStack` and can silently collide when outputs share the same predecessor. Scanner research, duplicate research IDs, or omitted research at these tiers blocks completion.

## 7. Rendering and localization

Treat controller overlays, unformed controller casing, formed controller casing, and formed ability-part base textures as independent paths. Formed hatches do not sample adjacency or majority contact. Preserve native hatch overlays.

Use translatable keys instead of hard-coded player-facing English. Preserve approved tooltip ordering, semantics, and color formatting, but do not make one machine's decorative color scheme a universal requirement.

Every new controller, dedicated block, item, material/fluid, and recipe type needs a human-readable entry in both `en_us.json` and `vi_vn.json`. Every `Component.translatable(...)` key actually called by the definition or machine must exist in both files. `langValue(...)`, generated names, English fallback, or a raw registry/name tag is not accepted as localization evidence. Preserve source `§` formatting or the user-approved Modern color design exactly, including reset codes.

Run the deterministic localization check against the machine definition and every machine-owned recipe/source file:

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-localization.mjs <Definition.java> <Recipes.java>
```

Then verify the selected client language in JEI: controller, casing, item, fluid, recipe category, and tooltip must show the intended formatted display text rather than a translation key or generated registry name.

## 8. Completion gates

A port is complete only when the achieved validation level is named accurately:

1. Source audit and port record complete.
2. Casing catalog validator passes.
3. Static structure, mapping, recipe ownership/ID, resource, and registration checks pass.
4. Build passes and changed files introduce no unresolved warnings.
5. Client reaches a world.
6. Structure forms with correct orientation, abilities, controller, casing, and hatch appearance.
7. Every mode runs with correct inputs, outputs, EU, duration, multiplier, parallel, chance, persistence, and failure behavior.
8. JEI controller/output routes, preview, casing navigation, and tier display work.

Compilation proves only `build validated; gameplay validation pending`. Keep missing runtime gates explicit instead of inferring success.
