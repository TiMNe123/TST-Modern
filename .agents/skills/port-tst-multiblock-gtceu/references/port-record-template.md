# Per-machine port record

Create this record before implementation. Update it after every approved deviation and validation gate.

## Identity and baseline

- TST machine/display name:
- TST registry/meta ID:
- Controller class/path and inheritance chain:
- Recipe pool/map class/path:
- Registration/config/localization paths:
- Target dependency versions:
- Current working-tree files/hashes captured:
- Machine contract path and validation command:
- User-approved baseline or deviations already present:

## Source behavior

- Purpose and distinct gameplay value:
- Recipe maps and exact recipe count/IDs:
- Circuit tiers from source and UEV+ `TSTCircuitTags` fallback mapping:
- Inputs/outputs and special abilities:
- EU/t, duration, voltage, and overclock rules:
- Base parallel and Parallel Hatch interaction:
- Speed/EU/output modifiers:
- Modes and special recipes:
- Chanced-output semantics:
- Periodic consumption, persistence, failure, and shutdown:
- Missing/optional dependencies:

## Structure

- Dimensions `(width × height × depth)`:
- Source and target axis order:
- Exact aisle source/hash:
- Controller character/count/facing:
- Character-to-source block/meta/predicate mapping:
- Fixed hatch positions:
- Replaceable abilities and base casings:
- Minimum/maximum counts:
- Air/any positions:
- Static structure checks:

## Overlap decision

- GTCEu machines/recipe types/source inspected:
- Result: Exclude / Review / Port
- Distinct TST value justifying the result:

## Mapping and approved deviations

| TST source | Target registry/predicate | Native / Equivalent / Dedicated | Texture provenance | Approval/catalog entry | Semantic/progression impact |
|---|---|---|---|---|---|
| | | | | | |

### Texture authority

| Visual role | Status | Resource/local path | SHA-256 or native target | Source evidence | Approval reference |
|---|---|---|---|---|---|
| | | | | | |

- Texture baseline validation:
- Unresolved visual roles:

- Missing dependency decisions:
- Behavior deviations:
- Structure/ability deviations:
- Tier/progression deviations:
- Visual/localization deviations:
- Unresolved decisions:

## Implementation ownership

- Behavior: `machine/<Machine>Machine.java`
- Definition: `registry/machine/<Machine>Definition.java`
- Recipes: `data/recipe/<Machine>Recipes.java`
- Controller/casing/bootstrap recipe ownership:
- Shared blocks/materials/recipe types:
- Resources/translations:
- Compatibility code:
- Machine initialization cardinality:
- Recipe registration cardinality:

## Validation evidence

- Catalog validator:
- Machine contract validator:
- Locked texture baseline validator:
- Structure/mapping checks:
- Recipe ID/count/ownership checks:
- Resource checks:
- Compile/build and changed-file warnings:
- Client reached world:
- Formation/orientation/abilities:
- Controller, casing, and formed hatch appearance:
- Every mode and exact recipe behavior:
- Parallel/multiplier/chance arithmetic:
- Persistence/failure/reload:
- JEI controller/output routes and preview:
- Debug code removed:
- Remaining issues:
- Final level: audit complete / implementation complete; build pending / build validated; gameplay validation pending / gameplay and JEI validated
