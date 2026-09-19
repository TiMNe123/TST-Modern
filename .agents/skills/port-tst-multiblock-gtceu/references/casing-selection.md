# Casing selection and texture fidelity

Read this reference before changing a structure predicate or registering a casing. The canonical machine-readable audit is [casing-catalog.json](casing-catalog.json), covering 408 source entries across 25 machines. Use the selected per-machine JSON for exact occurrences and provenance, and apply the independent asset lock in [texture-authority.md](texture-authority.md).

## Resolution contract

| Catalog resolution | Required implementation |
|---|---|
| `direct_native` | Use the listed GTCEu/vanilla block or predicate. Do not duplicate its texture in TSTModern. |
| `equivalent_review` | Keep equivalent provenance. Apply the listed target without another question only when the exact mapping is covered by `casing-catalog.json.reuseApproval`. |
| `dedicated_original` | Register one stable shared TSTModern block for the source block/meta and use the audited TST/GT5 texture. |
| `dedicated_designed` | Use the approved designed texture and a dedicated stable identity. |
| `dedicated_reused` | Use the approved reused texture; retain a dedicated identity when structure or progression requires it. |

The 10 mappings listed in `reuseApproval` were approved project-wide on 2026-08-21. Their classification remains `equivalent_review` for honest provenance, but the listed target may be used without asking again. If the exact entry is not covered, or the implementation target differs from the approved target, stop for a decision.

Approved project deviations recorded after the catalog audit take precedence over an older recommendation, but must be written into the port record and, when reusable, back into the catalog.

## Identity, abilities, and resources

- Key shared blocks by source registry plus meta, not machine name or pattern character.
- Keep pattern characters faithful to source; a display letter is not a casing identity.
- Preserve dynamic families such as coils, tiered glass, and frames as predicates rather than flattening them to one texture.
- Attach replaceable abilities to the intended source casing predicate so formed parts inherit the correct base appearance.
- Preserve fixed hatch-only positions and source minimum/maximum counts.
- Every dedicated block requires registration, BlockItem, blockstate, block/item models, texture, self-drop loot, and `en_us`/`vi_vn` localization.
- Add a construction recipe only at approved progression; otherwise record the block as unobtainable/pending.
- Preserve asymmetric top/side textures when present.
- Record texture provenance as TST, GT5, GTCEu, modified, or newly designed.

Do not point a dedicated casing at an unrelated native texture merely because its color is similar when an audited or approved texture exists.

Do not invent a mapping for a machine absent from the 25-machine catalog. Record its exact source/target/provenance in the per-machine contract and port record; keep the generated `25 / 408` aggregate unchanged unless the upstream audit source is explicitly expanded and regenerated.

## Catalog maintenance

Each catalog entry records source expression/location, occurrence count, resolution, target, texture provenance, and project texture folder. `texture_block_casing/gtceu_reuse_manifest.json` is the file manifest for approved native reuse; it does not erase original provenance.

After spreadsheet changes, rebuild into staging, review the diff, and validate the committed references:

```text
node .agents/skills/port-tst-multiblock-gtceu/scripts/build-casing-catalog.mjs
node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs
```

For an implemented machine, also prove that every `.where(...)` target is registered and every dedicated target has its complete resource set.
