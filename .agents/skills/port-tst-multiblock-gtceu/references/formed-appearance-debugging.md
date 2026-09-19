# Debug formed controller and hatch appearance

Before changing any controller, casing, or overlay resource, read [texture-authority.md](texture-authority.md) and validate the locked texture baseline. Appearance debugging does not authorize drawing, recoloring, substituting, or regenerating an asset.

Use this reference when a formed controller or an Input/Output, Energy, Maintenance, or Parallel part keeps an old/default casing color.

## Rendering contract

GTCEu uses separate paths:

| Rendered state | Effective path |
|---|---|
| Controller, unformed | `variants[is_formed=false,...].model.textures.all` |
| Controller, formed | `variants[is_formed=true,...].model.textures.all` |
| Formed part, one casing role | controller model's top-level `texture_overrides.all` |
| Formed parts, multiple casing roles | controller dynamic render implementing `IControllerModelRenderer` |
| Block appearance/CTM query | `MultiblockMachineDefinition.partAppearance` |

`partAppearance` does **not** replace the baked base quads of a formed hatch. In GTCEu 7.4, the actual path is
`MachineModel.renderMachine()` -> `replacePartBaseModel()` -> `renderPartOverrides()`. The default branch reads the
controller model's single `textureOverrides` map, which explains why every part becomes the controller casing even
when a `partAppearance` callback returns another block.

The hatch does not sample adjacent blocks, count majority contact, or copy rendered controller pixels. GTCEu replaces
only registered replaceable base textures and retains the part's native Energy/Input/Output/etc. overlay.

## Diagnosis

1. Confirm the machine is truly formed.
2. Map every ability position to its pattern symbol and intended base casing. Count distinct casing roles.
3. In `registry/machine/<Machine>Definition.java`, identify the appearance block, workable-casing base, ability
   predicates, and `partAppearance` callback if present.
4. Open the source and processed machine-model JSON.
5. Inspect `MachineModel.renderPartOverrides()` in the exact GTCEu source version before changing predicates or assets.
6. Classify the mismatch:
   - controller wrong, parts right: formed variants are stale;
   - controller right, parts wrong: override is stale or the part is not replaceable;
   - both share the old color: both JSON paths are stale;
   - all formed parts share one casing although the pattern has multiple roles: the global override is working as
     designed; a per-part controller renderer is required;
   - `partAppearance` changes nothing in JEI and the world: the baked-model path is still using the global override.

## Select the model contract

### Uniform casing

When every replaceable formed part has the same casing role, set `texture_overrides.all` and every
`is_formed=true` variant's `model.textures.all` to the approved casing texture. Keep `is_formed=false` unchanged when
required. Preserve front, active, paused, and emissive overlay paths.

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-formed-appearance.mjs `
  <machine-model.json> --uniform-texture <namespace:texture>
```

### Multiple casing roles

One global override cannot encode multiple structural roles. Add a client-side dynamic render that:

1. extends `DynamicRender<Machine, Renderer>` and implements `IControllerModelRenderer`;
2. registers its `DynamicRenderType` with `DynamicRenderManager` before machine models are parsed;
3. is declared in the controller JSON's `dynamic_renders` array;
4. classifies the part by the approved pattern role using controller-relative coordinates and
   `RelativeDirection`, never absolute world X/Y/Z;
5. emits the approved casing block model. GTCEu blanks the replaceable base quad and keeps the hatch overlay.

Use GTCEu's `BoilerMultiPartRender` as the API example, then adapt only the structural-role classifier. A class or
ability check is acceptable only when the audited pattern proves that class/ability uniquely identifies one casing
role; otherwise use the frozen relative positions or planes.

```json
"dynamic_renders": [
  { "type": "tstmodern:<machine>_parts" }
]
```

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-formed-appearance.mjs `
  <machine-model.json> --mixed-casing-renderer tstmodern:<machine>_parts
```

Do not disable formed-part replacement, null the appearance block, recolor native hatch overlays, or add adjacency/majority-contact logic.

## Verification

Parse the JSON, run the formed-appearance and casing validators, compile, and run focused tests. A newly registered
dynamic-render type requires a full client restart; `F3+T` is insufficient. Re-form the structure and visually inspect
every ability role in both JEI preview and the world. Build success alone does not prove the render fix.
