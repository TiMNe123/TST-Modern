# BigBroArray Geometry and JEI Preview Fix Design

**Date:** 2026-08-30
**Status:** Proposed for approval
**Target branch:** `dev`

## Goal

Make the BigBroArray runtime addon geometry and its JEI 3D previews match the original TST machine without reintroducing the manually flipped core that remains on `main`.

## Established facts

- `main` and `dev` intentionally contain different BigBroArray structures. This fix targets `dev` only.
- `dev` freezes the original TST raw core and addon matrices. `CORE_SOURCE`, `ADDON_RAW_SOURCE`, the core controller source coordinate `(5, 5, 4)`, and the four source offsets remain unchanged.
- Runtime core rows use `RelativeDirection.DOWN`. A larger source `down` coordinate is lower in the formed machine.
- JEI `MultiblockShapeInfo` writes its second array index directly to positive world Y. Passing a DOWN-oriented layout directly to it renders the machine upside down.
- Core Clean Stainless cells have controller-relative `down=+2`. Addon Clean Stainless cells also reach `down=+2`. Both are the coplanar bottom of the source machine.
- The current generic quarter-turn rotation is not equivalent to the four shapes built by TST. Addon 0 is correct; addon 1, 2, and 3 are not.
- Current preview pages contain casing-only `D` and `F` positions, so they violate mandatory core abilities and emit `Pattern formed checking failed`. The repeated null-world async error begins immediately after these failures and is most likely the lifecycle consequence of an unformed dummy controller; the fresh-client log gate must prove that causal link rather than assuming it.

## Runtime geometry contract

Treat every raw addon cell as `(x, y, z)` where `x` is right, `y` is down, and `z` is back. Apply the TST transforms before subtracting the original piece offsets:

| Addon | Transform `(right, back)` | Offset `(right, down, back)` | Final occupied bounds |
|---:|---|---|---|
| 0 | `(x, z)` | `(-6, 23, 6)` | `x=6..22, down=-23..2, z=-6..8` |
| 1 | `(z, x)` | `(7, 23, -7)` | `x=-7..7, down=-23..2, z=7..23` |
| 2 | `(16-x, z)` | `(22, 23, 6)` | `x=-22..-6, down=-23..2, z=-6..8` |
| 3 | `(z, 16-x)` | `(7, 23, 21)` | `x=-7..7, down=-23..2, z=-21..-5` |

Each addon contains exactly 900 occupied cells. Their sorted controller-relative coordinate digests are:

- addon 0: `5b278de36ece92b5af0d020bd491e25eb59d7739506c0e8ca5a5805145562da3`
- addon 1: `7858f67fda65479a8955aa0a2c72006676c68a07d44d51385b44439e122b6290`
- addon 2: `55c8b3440a1df6a09a764b7216a00279db7a0ec2b004209abc71ac0b1bee45dc`
- addon 3: `b182495736ba9421fb9725bbea126d6c0694e31fccd8575664a0d4a53f04953a`

The full machine envelope remains `45 x 26 x 45`, with controller-relative bounds `right=-22..22`, `down=-23..2`, and `back=-21..23`. Production matching continues to convert down/right/back into world coordinates through `RelativeDirection.offsetPos`; its vertical signs must not change.

## JEI conversion contract

Build preview geometry in controller-relative `(right, down, back)` first. Convert only the preview row coordinate from DOWN to world-UP:

```text
previewY = maxDown - down
```

For the full layout, the controller must appear at preview coordinate `(22, 2, 21)`. Clean Stainless cells from both core and addons with `down=+2` must appear on preview row `0`, the bottom row. The addon tops at `down=-23` must appear on preview row `25`.

Do not reverse `CORE_SOURCE`, `CORE_AISLES`, `ADDON_RAW_SOURCE`, or `BigBroArrayAddonMatcher` to repair a preview-only axis mismatch.

## Preview page contract

Use five cumulative pages matching TST construction behavior:

1. core only;
2. core plus addon 0;
3. core plus addons 0 and 1;
4. core plus addons 0 through 2;
5. core plus all four addons.

Occupied counts must be `321`, `1221`, `2121`, `3021`, and `3921`. No page may contain overlapping occupied cells.

## Preview ability contract

Preview-only symbols replace stable core `D/F` cells without changing runtime source geometry:

- one Maintenance Hatch;
- one Muffler Hatch;
- one Item Import Bus;
- one Item Export Bus;
- one Fluid Import Hatch;
- one Fluid Export Hatch;
- one Energy Input Hatch;
- one Energy Output Hatch.

Use IV preview parts. Remaining `D` cells stay Robust Tungstensteel and remaining `F` cells stay Clean Stainless. Every preview page must satisfy the formal core pattern and reach `onStructureFormed()` without a warning.

## Log acceptance

After a fresh client start and opening the BigBroArray JEI preview:

- no `Pattern formed checking failed: tstmodern:big_bro_array`;
- no repeating `MultiblockState.world is null` / `this.world is null` async assembly error;
- Trinium registry remap warnings in an existing save are not a failure;
- unrelated MegaTree recipe or removed loot-table errors are outside this geometry fix.

If the async null-world error remains after every preview page forms, treat it as a separate controller lifecycle defect: capture the fresh-run controller path and fix state creation/registration at its source. Do not suppress it with a broad exception catch or by disabling async multiblock checks.

## Non-goals

- Do not copy the old `main` core into `dev`.
- Do not change recipes, embedded-machine behavior, tier aggregation, or Trinium material generation.
- Do not make addons mandatory for core formation.
- Do not hide log errors without fixing preview formation.
