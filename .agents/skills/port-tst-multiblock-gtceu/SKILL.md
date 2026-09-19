---
name: port-tst-multiblock-gtceu
description: Use when auditing, porting, reviewing, or debugging Twist Space Technology 1.7.10 multiblocks in GregTechCEu Modern 1.20.1, especially structure orientation, TST/GT5 casing or texture provenance, recipes, abilities, formed appearance, parallel logic, persistence, and JEI/LDLib behavior.
---

# Port TST multiblocks to GTCEu Modern

Preserve verified TST behavior while adapting it to the exact dependency versions in this repository. Evidence precedes implementation. An unresolved source fact is a blocker, not permission to infer a replacement.

## Branch gate

For every new machine port:

1. Run `git branch --show-current` and `git status --short`.
2. Work on branch `dev`. If another branch is clean, switch to `dev`; if it is dirty, stop and report the changes.
3. Do not create a worktree or feature branch unless the user explicitly requests one.
4. Do not merge, push, delete branches, or move changes to `main` without explicit approval.

Reviews on another branch are read-only unless the user asks for fixes there.

## Route only to relevant detail

| Work | Required references or commands |
|---|---|
| New port or full re-audit | Read [source-contract.md](references/source-contract.md), [source-audit.md](references/source-audit.md), [port-blueprint.md](references/port-blueprint.md), and create/update the machine port record from [port-record-template.md](references/port-record-template.md). |
| Structure or casing | Read [casing-selection.md](references/casing-selection.md), then the exact machine entry in [casing-catalog.json](references/casing-catalog.json) when present. |
| Texture, model, overlay, or appearance | Read [texture-authority.md](references/texture-authority.md); for formed-part bugs also read [formed-appearance-debugging.md](references/formed-appearance-debugging.md). |
| Mechanics, recipes, progression, persistence | Read the relevant sections of [port-implementation-standard.md](references/port-implementation-standard.md). |
| Names, colors, tooltips, or JEI labels | Read the localization rules in [port-implementation-standard.md](references/port-implementation-standard.md), then run `scripts/validate-localization.mjs`. |
| Backlog selection only | Use [tst-multiblocks-audit-catalog.md](references/tst-multiblocks-audit-catalog.md); it is not behavior authority. |
| Completion claim | Read and execute [validation-checklist.md](references/validation-checklist.md). |

## Hard gates

### Source evidence

Before editing production code, create `.agents/skills/port-tst-multiblock-gtceu/contracts/<machine>.json` using [source-contract.md](references/source-contract.md):

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\create-machine-contract.mjs <MachineName>
```

Complete the evidence, then run:

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-machine-contract.mjs <contract.json>
```

The contract must have `AUDIT_COMPLETE`, explicit source hashes, inheritance, axes, controller orientation, every symbol mapping, recipe authority, texture authority, zero unresolved decisions, and user-approved deviations. User approval of the audited contract is required before implementation.

### Texture authority

The port source libraries are fixed: controller textures come from `textures/<machine-folder>/`; non-native structure casing textures come from `texture_block_casing/<machine-folder>/`. Match folders by machine name, never by numeric prefix. Their bytes are locked by [texture-source-baseline.json](references/texture-source-baseline.json). Production copies are separately locked by [texture-baseline.json](references/texture-baseline.json).

Never treat `src/main/resources` as source authority. Never invent, generate, recolor, substitute, or silently modify a controller/casing texture. Never promote a staged baseline without explicit user approval. Validate both source libraries and production before and after any resource work:

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-texture-sources.mjs
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-texture-assets.mjs
```

`UNRESOLVED` texture authority blocks implementation. Native GTCEu/vanilla resources are references, not copied PNGs.

If formed ability parts may occupy more than one casing role, one top-level
`texture_overrides.all` cannot represent the structure. Follow
[formed-appearance-debugging.md](references/formed-appearance-debugging.md) and validate the selected uniform or
mixed-casing model contract with `scripts/validate-formed-appearance.mjs`.

### Golden-port boundary

Existing ports define module layout and testing seams only. They are never authority for another machine's geometry, axes, casing/meta identity, recipes, multipliers, persistence, or textures.

### Endgame circuit compatibility

Preserve the verified circuit tier. For every UEV, UIV, UXV, OpV, or MAX recipe input, use
`TSTCircuitTags.get(GTValues.<tier>)`; never replace the source tier with a hard-coded UHV circuit.
The shared resolver accepts the standard `gtceu:circuits/<tier>` tags supplied by StarT
(`start_core`, currently UEV through UXV) and Sky of Grind (`soggtaddon`, UEV through MAX), and
falls back to `UHV_CIRCUITS` when the loaded integration does not provide the requested tier. Do
not reference pack-owned item IDs or add UHV entries to global endgame circuit tags. Exact
UHV-and-lower inputs continue to use their native `CustomTags` directly.

### Mandatory display colors

Color is a blocking port requirement, not optional polish. Apply every rule below to both `en_us`
and `vi_vn`; a violation blocks implementation completion:

- Every controller display name must use `§<identity-color>§l<Name>§r`. Plain, white-only, or
  non-bold controller names are invalid. Use the same identity color in both locales.
- Every newly registered dedicated casing, structural block, machine-owned hatch, and machine part
  must use `§<color><Name>§r`. Reused shared blocks retain their existing registered colors. Never
  leave a newly added machine-owned block with a plain generated or white-only name.
- Controller tooltip line 0 is the gray controller-description line and starts with `§7`.
- Controller tooltip line 1 is a visibly colored source identity or flavor line. It must not use
  the same color as line 0.
- Functional tooltip lines use a colored label followed by a gray body:
  `§<accent><Label>§7: <source-backed text>§r`. Use at least three distinct visible accent colors
  across the tooltip; never paint every functional line with one accent.
- Preserve the original TST tooltip meaning and ordering. Color and concise labels may be adapted
  to the Modern layout, but do not invent mechanics. Any text changed because Modern behavior
  differs from TST requires an approved deviation in the machine contract and port record.
- The final English line must be exactly
  `§dTwist Space Technology - Modern Edition§r`; the final Vietnamese line must be exactly
  `§dTwist Space Technology - Phiên bản Hiện đại§r`.
- Every colored controller, casing, part, and tooltip localization must end with `§r`. Embedded
  formatting must return to the intended body color before continuing.
- Choose a coherent per-machine identity color, keep neighboring machine identities distinguishable,
  and record the user-approved palette in the machine contract and port record.

Before completion, add or update a runnable localization test that asserts the controller name
format, tooltip ordering, at least three tooltip accent colors, the exact Modern Edition footer,
and colored names for every newly added machine-owned casing/block/part in both locales. Run that
test and `scripts/validate-localization.mjs` before the full build.

## Implementation workflow

1. Capture the approved working-tree baseline and exact TST/GT5/GTCEu evidence.
2. Apply the GTCEu overlap gate: Exclude, Review, or Port.
3. Complete and validate the machine contract; stop on unresolved facts.
4. Obtain user approval of deviations and the audited contract.
5. Implement the blueprint seams: runtime behavior, definition/structure/rendering, recipe ownership, resources, and focused pure logic where needed.
6. Apply structure axes, casing decisions, recipe facts, and mechanics exactly from the contract. Select controller files from `textures/<machine-name>/`; select non-native casing files from `texture_block_casing/<machine-name>/`; deploy approved copies into `src/main/resources`.
7. Validate contracts, catalog, resources, localization, registration cardinality, tests, build, client/world behavior, formation, rendering, operation, and JEI at the level available. For localization run:
   `node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-localization.mjs <Definition.java> <Recipes.java>`.
8. Update the port record with evidence, approved deviations, commands, and remaining runtime gates. Remove temporary diagnostics.

Attempt the same unchanged environment/test failure at most three times. Record the evidence and continue with an independent task after the third identical failure.

## Stop conditions

Stop and request a decision when:

- original TST/GT5 source or an inherited behavior cannot be located;
- source axes, transpose, controller facing, or symbol counts are not proven;
- a casing source registry/meta lacks an approved target;
- a recipe dependency is absent from 1.20.1 and no deviation is approved;
- a controller/casing texture is not verified native, locked local, or explicitly approved;
- implementation would differ from the approved contract.

Do not substitute a plausible value, similar color, nearby tier, existing machine pattern, or convenient API.

## Reporting contract

Report exactly one achieved level:

- `audit complete`
- `implementation complete; build pending`
- `build validated; gameplay validation pending`
- `gameplay and JEI validated`

Compilation alone never proves gameplay, structure orientation, formed appearance, or JEI behavior.
