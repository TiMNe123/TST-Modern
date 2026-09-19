# TST port validation checklist

Validation is cumulative. Report the highest gate actually proven; never promote build evidence to gameplay evidence.

## Gate 1: audit and provenance

- Machine JSON reports `AUDIT_COMPLETE`, cites exact hashes, contains no unresolved entries, and passes `validate-machine-contract.mjs`.
- The user approved the audited contract and every deviation before production implementation.
- Port record identifies exact TST/GT5 and target-version sources.
- Approved working-tree baseline is captured before extraction/refactoring.
- Every deviation and missing dependency has an explicit decision.
- Overlap result is Exclude, Review, or Port with justification.

## Gate 2: catalog and resources

- Run `node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs`.
- Run `node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-texture-sources.mjs`.
- Run `node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-texture-assets.mjs`.
- Every controller/casing visual role has verified or approved authority; no role is `UNRESOLVED`.
- Every controller and casing source asset is in the locked source baseline with the same SHA-256.
- Every production PNG is in the locked production baseline with the same SHA-256; a regenerated baseline was not used to hide an unapproved change.
- Every source block/meta uses its exact catalog entry.
- `equivalent_review` reuse is covered by `reuseApproval`; uncovered or changed targets are approved separately.
- Repeated source identities share one registry block.
- Every dedicated block has registration, BlockItem, blockstate, block/item model, texture, self-drop loot, and `en_us`/`vi_vn` localization.
- Run `validate-localization.mjs` on the definition and machine-owned recipe/source modules. Controller, block, item, material/fluid, recipe-type, and every used tooltip key exist with non-raw values in both `en_us` and `vi_vn`; approved `§` colors and resets match.
- Texture provenance and approved construction progression are recorded.

## Gate 3: static structure and registration

- All rows have equal width; all aisles have equal height; dimensions match the port record.
- Axis order, preview orientation, and outward controller direction match source.
- Controller occurs once and every used character has an intended predicate.
- Air/any, dynamic families, fixed hatches, casing minimums, and ability limits are not relaxed.
- Recipe I/O/energy, maintenance, parallel, muffler, and special abilities are handled intentionally.
- Ability predicates use the intended formed base casing.
- Distinct formed-part casing roles are counted. Run `validate-formed-appearance.mjs` in uniform or mixed mode;
  mixed roles have a registered and model-declared `IControllerModelRenderer`.
- One behavior, definition, and recipe module own the machine.
- Machine definition and recipe module are each registered exactly once.

## Gate 4: mechanics and recipes

- Recipe IDs are unique; expected counts and semantic ownership match the audit.
- Types, circuits, catalysts, items, fluids, chances, duration, EU/t, metadata, controller/casing/bootstrap recipes, and tier progression are checked.
- Every material form and fluid storage key referenced by a recipe is registered in the exact target version; custom plasma/gas/liquid variants are explicitly included in dependency closure.
- Modes, overclock, speed/EU modifiers, TST multiplier, GTCEu parallel, hatch parallel, and chance rolls are traced independently.
- Each factor is applied once and large arithmetic saturates safely.
- Periodic consumption, persistence, failure, shutdown, save/reload, and missing-dependency replacements are covered.

## Gate 5: build

- Use the repository's configured JDK and dependency versions.
- Catalog/static checks pass before the full build.
- Full build succeeds and changed files have no unresolved warnings.
- Rebuild after removing temporary logs, mixins, and diagnostic accessors.

Passing this gate means only: `build validated; gameplay validation pending`.

## Gate 6: client and in-world

- Client reaches a world with the expected rendering/JEI stack.
- Preview dimensions, rotation, top/bottom, and controller direction are correct.
- Structure forms; casing minimums and all intended abilities work.
- Unformed and formed controller states use the intended textures.
- Formed I/O, energy, maintenance, and parallel parts keep native overlays and use the intended base casing.
- Test without a Parallel Hatch, with a native hatch, and with addon ranges when supported.
- Run every mode and verify inputs, outputs, EU, duration, multiplier, parallel, chances, resource cadence, persistence, and failure behavior.

## Gate 7: JEI/LDLib

- Controller catalyst and output item both open the correct recipe category.
- With each supported locale selected, JEI shows formatted display names for controller, blocks, items, fluids, category, and tooltips; no raw translation/name tag remains.
- Recipe type I/O limits cover the largest recipe.
- Multiblock preview and casing navigation work without malformed proxy-slot crashes.
- Voltage/tier controls and native GTCEu font/colors remain correct.
- For affected LDLib versions, categories over ten recipes use consistent draw/click wrapper state.

Passing all applicable gates means: `gameplay and JEI validated`.
