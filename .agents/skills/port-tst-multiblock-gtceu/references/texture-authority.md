# Texture authority and asset lock

Texture selection is source-controlled behavior, not a creative implementation task. Source authority and deployed assets are separate layers.

## Fixed source libraries

| Library | Authority | Rule |
|---|---|---|
| `textures/<number>_<MachineName>/` | Controller texture source for that machine | Match by `MachineName`; folder numbers are not shared with the casing library and must never be used as a cross-library key. |
| `texture_block_casing/<number>_<MachineName>/` | Structure casing texture source when the required casing is not reused directly from GTCEu | Resolve the structure symbol through the casing catalog first, then use the exact machine folder and recorded file. |
| `texture_block_casing/gtceu_reuse_manifest.json` | Approved direct GTCEu reuse decisions | Use the registered GTCEu block/resource directly; audit PNGs in the folder are evidence and must not be copied as local production textures. |
| `src/main/resources/assets/tstmodern/textures/` | Deployment destination | Never use this directory to decide what a controller or casing should look like. |

The two source libraries are byte-locked by [texture-source-baseline.json](texture-source-baseline.json). Deployed PNGs are separately locked by [texture-baseline.json](texture-baseline.json).

## Allowed authority

| Contract status | Meaning | Required evidence |
|---|---|---|
| `VERIFIED_LOCAL` | Existing TSTModern PNG verified against TST/GT5 or an approved baseline | Local path, SHA-256, resource ID, source evidence |
| `VERIFIED_NATIVE` | Direct GTCEu or vanilla resource | Exact `gtceu:` or `minecraft:` resource and target evidence |
| `APPROVED_REUSE` | User-approved reuse | Local path, SHA-256, resource ID, approval/source evidence |
| `APPROVED_MODIFIED` | User-approved modification | Local path, SHA-256, resource ID, original source and approval |
| `APPROVED_DESIGN` | User explicitly approved a new design | Local path, SHA-256, resource ID, `approvalRef` |
| `UNRESOLVED` | No verified authority | Blocks implementation |

`LOCKED_EXISTING_BASELINE` freezes current file bytes without claiming that every legacy file's original provenance has already been reconstructed.

## Required visual roles

Record every applicable controller unformed/formed base, idle/active/paused/emissive front overlay, fixed/dedicated casing, multi-face or CTM variant, ability-part base casing, native hatch overlay, and distinct item presentation.

A controller may reuse a verified GTCEu overlay or casing, but that decision remains explicit. Formed hatches never sample adjacency or majority-contact color.

## Forbidden substitutions

Never generate, draw, recolor, synthesize, or silently replace a texture. Never use stone bricks or another placeholder, choose by similar color/name/theme, flatten multi-face assets, copy native PNGs for `direct_native`, copy a golden machine's texture, or refresh the baseline merely to pass validation.

If authority is missing, set the role to `UNRESOLVED`, record missing evidence, and stop resource implementation.

## Baseline workflow

Validate the approved source libraries and deployed assets:

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-texture-sources.mjs
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-texture-assets.mjs
```

The source validator checks every `.png` and `.png.mcmeta` under `textures/` and `texture_block_casing/`, including role, size, hash, and undeclared files. The production validator checks deployed PNG hashes, rejects unlisted PNGs, and verifies every local `tstmodern:` reference inside model `textures` objects.

After explicit user approval of texture changes, stage a proposal:

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\stage-texture-source-baseline.mjs
node .agents\skills\port-tst-multiblock-gtceu\scripts\stage-texture-baseline.mjs
```

These write only to `build/generated/port-tst-multiblock-gtceu/`. Review exact hashes and promote either reference baseline only after approval.

Every dedicated casing still requires registration, BlockItem, blockstate, block/item models, approved PNGs, self-drop loot, localization, and an approved construction recipe.
