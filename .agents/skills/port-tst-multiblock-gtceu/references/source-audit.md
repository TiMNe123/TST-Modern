# Source audit and overlap gate

Complete this audit before implementation. The goal is to distinguish source facts, target-platform facts, and approved deviations. Materialize the result in the validated machine JSON defined by [source-contract.md](source-contract.md); prose notes alone do not open the implementation gate.

## Evidence order

1. Explicit current user requirements and approved deviations.
2. Current working-tree code when confirmed as an approved baseline, including uncommitted changes.
3. Exact TST controller, inherited base classes, recipe pools/maps, registration, configuration, resources, and GT5 dependencies.
4. Exact-version GTCEu Modern, LDLib, and JEI source or bytecode.
5. Runtime logs and screenshots as reproduction evidence.
6. Planning workbooks and summaries only as leads, never machine facts.

Record exact paths, classes, methods, fields, registry IDs, source lines, and dependency versions. Before extraction or refactoring, capture hashes or exact snippets for approved working-tree behavior; do not use `HEAD` alone as the baseline.

Do not edit production code while source controller, inheritance, structure axes, casing/meta identity, recipe authority, or texture authority remains unresolved. A plausible inference is not audit evidence.

## Machine search checklist

Find and record:

- Controller class, inheritance chain, constructor, registration/meta ID, configuration gate, display name, tooltip, and localization keys.
- Structure arrays, dimensions, axis order, controller character/facing, block/meta identities, dynamic predicates, frames, coils, glass, air/any, casing minimums, and hatch positions.
- Formation checks, processing logic, voltage tier, speed, EU, overclock, parallel, output multipliers, modes, resource cadence, persistence, failure, and shutdown behavior.
- Recipe pools/maps, exact recipe count/IDs, circuits, catalysts, chances, fluids, items, EU/t, duration, metadata, and optional-mod dependencies.
- Controller, casing, bootstrap, and research recipes plus tier/progression gates.
- Controller overlays, casing textures, formed appearance, and connection behavior.

Do not assume machines sharing a superclass share mechanics.

## GTCEu overlap gate

Inspect the exact target source for machines and recipe types with the same input/output transformation, plus native modifiers, coil/cleanroom logic, abilities, hatches, casings, maintenance, research, and JEI integration.

Classify the result:

- **Exclude**: GTCEu already supplies effectively the same gameplay role, or a missing dependency has no supported/approved substitute.
- **Review**: the role overlaps but TST may preserve distinct recipes, structure, progression, or mechanics.
- **Port**: the audited TST machine provides a distinct justified role.

Visual structure difference alone does not justify a port.

## Missing dependency decision

For every unavailable dependency, record one approved outcome:

- native GTCEu equivalent with the same semantic role;
- new dedicated block/item/material;
- supported modern-mod equivalent;
- deliberate semantic replacement with documented loss;
- exclusion when substitution erases the machine's purpose.

Never silently keep an impossible 1.7.10 dependency or invent a registry mapping.
