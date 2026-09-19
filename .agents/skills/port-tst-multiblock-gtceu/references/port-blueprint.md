# Port blueprint distilled from completed machines

Completed ports establish a reusable module form. They do not establish another machine's facts.

## Standard ownership

| Responsibility | Default location |
|---|---|
| Runtime state, lifecycle, recipe modifier | `machine/<Machine>Machine.java` |
| Pure arithmetic or isolated policies | `machine/logic/<Machine>*.java` |
| Definition, pattern, abilities, rendering | `registry/machine/<Machine>Definition.java` |
| Large immutable geometry/addon transforms | `registry/machine/<Machine>Structure.java` |
| Construction, processing, casing, bootstrap recipes | `data/recipe/<Machine>Recipes.java` |
| Source/deviation authority | Skill contract and `docs/port-records/<machine>.md` |
| Focused verification | `src/test/java/.../<Machine>*Test.java` |

Use the three core modules unless complexity justifies a helper. BigBroArray and Disassembler show when immutable structure data and pure policy modules are warranted; they are not templates for every machine.

## Registration

- `TSTMachineRegistry.registerMachines` initializes each definition exactly once.
- `TSTModernGTAddon.addRecipes` calls each recipe module exactly once.
- Shared registries own genuinely shared blocks, materials, and recipe types.
- Machine prerequisites stay with their semantic owner so splitting a monolith cannot make a port unobtainable.

## Structure and mechanics

Implementation consumes the approved contract instead of copying another definition. Tests enforce dimensions, equal rows, controller count, axes, symbols, casing minima, fixed positions, and preview orientation.

Separately audit recipe type, modes, voltage gates, overclock, speed, EU discount, TST multiplier, GTCEu parallel, Parallel Hatch contribution, chances, periodic resources, persistence, and shutdown. Extract pure arithmetic when boundary/overflow behavior matters. Apply every factor once and saturate overflow.

## Golden-reference boundary

Existing ports may supply package style, exact-version API usage, pure-logic seams, tests, localization, and resource wiring patterns. They must not supply another machine's geometry, orientation, transpose, controller coordinate, casing/meta identity, texture, recipe, multiplier, parallel, persistence, or failure behavior.

Every machine fact comes from original TST/GT5 source or an explicit approved deviation.
