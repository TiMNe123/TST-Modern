# Machine source contract

The machine contract is the implementation authority produced from original TST/GT5 evidence and approved Modern deviations. Production code must not be created or changed before this contract validates and the user approves it.

Create a blocking skeleton and store it under the skill contracts directory:

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\create-machine-contract.mjs <MachineName>
```

The generated contract intentionally fails validation until the audit is complete.

## Required fields

| Field | Requirement |
|---|---|
| `schemaVersion` | `1` |
| `machine` | Stable machine name |
| `auditStatus` | `AUDIT_COMPLETE` before implementation |
| `branch` | `dev` for a new machine port |
| `sourceEvidence` | Controller, every superclass/helper carrying behavior, and recipe-pool evidence |
| `structure` | Dimensions, source axes, GTCEu axis map, controller orientation, and every symbol |
| `recipes` | Every construction/processing/bootstrap recipe in scope |
| `textures` | Every controller, casing, and multi-face visual role |
| `approvedDeviations` | Explicit user-approved semantic, progression, structure, or visual deviations |
| `unresolved` | Must be empty before implementation |

Each controller, superclass, helper, or recipe-pool entry contains exact `path`, `symbol`, and a 64-character SHA-256 `hash`. An empty `inheritance` array means inheritance was checked and no inherited behavior applies; it must not mean inheritance was skipped.

## Structure evidence

Record dimensions as `width × height × depth`, the source axis order, and the exact mapping to GTCEu `RIGHT`, `DOWN`, and `BACK`. The controller has a pattern symbol, occurrence count of exactly one, and an outward-facing direction.

Every non-space symbol records its occurrence count, exact source registry/meta and evidence, plus exact target registry/predicate and decision. Do not derive axes by visually rotating until the machine forms. Prove row width, aisle height, aisle count, controller coordinate/facing, and symbol totals before creating the GTCEu pattern.

## Recipe authority

Every recipe is either `VERIFIED_SOURCE`, with exact source evidence, or `APPROVED_DEVIATION`, with an `approvalRef` recording the user's decision. Record exact recipe type, items/forms, fluids/amounts, outputs, chances, duration, EU/t, circuits, catalysts, research, and optional dependencies in the port record.

Missing Thaumcraft, GT5 addon content, material forms, or registry IDs remain unresolved until the user approves a semantic replacement. Never choose a nearby tier or thematically similar material automatically.

## Texture authority

Every controller and casing visual role follows [texture-authority.md](texture-authority.md). A machine with any `UNRESOLVED` texture cannot pass validation.

## Required command

```powershell
node .agents\skills\port-tst-multiblock-gtceu\scripts\validate-machine-contract.mjs `
  .agents\skills\port-tst-multiblock-gtceu\contracts\<machine>.json
```

Schema validation proves structural completeness, not truthfulness. Review the cited source and hashes before approving the contract.
