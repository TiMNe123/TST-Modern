# Four TST machine behavior fixes

## Goal

Correct the audited recipe and runtime behavior of Mega Stone Breaker, Giant Vacuum Drying Furnace, Nether Interface, and Hyper Thermal Convector while preserving the approved GTCEu Modern adaptations already present in the project.

The functional source baseline is Twist Space Technology commit `73571514cf8a1527db4975680456748ba32f373d` and GTCEu Modern `7.4.0`. Explicit decisions approved in this conversation override the legacy implementation.

## Scope and invariants

The change may update the four machine behavior classes, four definition classes, four recipe modules, TST material and chance-logic registration, localization, tests, and per-machine port records.

The following must not change:

- controller registry IDs, structure aisle text, casing mappings, textures, models, or formed appearance;
- controller and casing progression recipes unless this document explicitly says otherwise;
- Mega Stone Breaker's fourteen processing recipes;
- Giant Vacuum Drying Furnace's current seventeen Modern processing recipes;
- Hyper Thermal Convector's hand-designed Modern recipe catalog and Dense Steam usage recipes;
- the fixed one-segment Giant structure;
- the existing Hellish Metal bootstrap.

All arithmetic that can exceed an integer must saturate. GTCEu parallel scales deterministic inputs, deterministic outputs, and EU/t exactly once. Chanced outputs must not be pre-multiplied and then rolled again by `recipe.parallels`.

## Mega Stone Breaker

### Parallel and output capacity

The base limit remains `4 * 2^machineTier`, saturated at `Integer.MAX_VALUE`. A present Parallel Control Hatch adds its configured amount to this limit.

Before asking `ParallelLogic` for the executable parallel, create an output-capacity probe in which deterministic outputs have already been multiplied by the active TST output bonus: `4` normally or `1024` when boost fluids are available. This makes output protection reduce parallel using the actual final output size. The returned parallel then scales recipe inputs and EU/t once, and final outputs by `parallel * outputBonus` once.

### Boost-fluid cadence

Replace the world-offset cadence with a machine-local running-tick counter. The counter advances only while a boosted recipe is working. It attempts the first 1,000 mB water plus 1,000 mB lava drain on the first working tick, then every twenty working ticks. Both drains are simulated before either is executed. Failure disables boost and stops that working tick, matching TST shutdown behavior.

The fixed L and W positions remain fluid-input-only. GTCEu standard fluid hatches cannot distinguish the two semantic roles without dedicated custom hatch types, so the Modern port continues to permit water and lava across the combined fixed inputs. This limitation is an approved structure/ability deviation.

## Giant Vacuum Drying Furnace

### Coil and speed

GTCEu heating-coil tiers are zero-based. The TST-equivalent coil tier is therefore:

```text
coilTier = GTCEu coil tier + 1
```

The base parallel is `1 * coilTier * 32`, plus the configured Parallel Control Hatch amount, with saturating arithmetic.

The TST speed multiplier is restored independently from GTCEu overclocking:

```text
durationMultiplier = 0.8 ^ machineTier / (coilTier * 0.5)
```

The existing non-perfect GTCEu overclock modifier is applied after this TST multiplier. Recipe tier is not subtracted from machine tier in the TST multiplier.

### Recipes and structure

The original GT++ Vacuum Furnace and Chemical Dehydrator maps do not exist in the target dependency set. The current seven Chemical Dehydrator and ten Vacuum Furnace recipes remain the approved Modern recipe catalog. The existing two GTCEu recipe-type modes remain in use and are persisted by GTCEu.

The fixed single tower segment remains an approved Modern structure deviation. No dynamic tower reconstruction is included.

## Nether Interface

### Core fluid transformation

Register a fluid-only TST material named Poor Nether Waste with English and Vietnamese localization. The processing recipe consumes 16,000 mB Distilled Water and deterministically produces 16,000 mB Poor Nether Waste for each actual parallel. Lava and Liquid Nether Air are removed from this recipe.

### Energy-limited parallel

The machine retains a base limit of 64 plus Parallel Control Hatch contribution. Before selecting actual parallel, reserve two IV amperes for portal maintenance. Resource/output parallel and power parallel are calculated separately:

```text
powerParallel = floor(availableEUt / IV_EUt) - 2
actualParallel = min(resourceParallel, powerParallel)
EUt = IV_EUt * (actualParallel + 2)
```

Values at or below zero cancel the recipe with insufficient power rather than selecting an impossible larger parallel.

### Exact TST-style output rolls

Add a registered TST chance logic for item outputs. Once per completed processing cycle it performs exactly three weighted selections, with replacement, using weights `1/49/30/10/10`. Each selected Modern package is multiplied by actual parallel. The existing Modern package identities and quantities remain unchanged:

1. Ancient Debris x1
2. Netherrack x16
3. Netherite Scrap x4
4. Netherite Ingot x1
5. Nether Star x1

A second chance logic performs one 30% Hellish Metal roll per completed cycle and multiplies 288 mB by actual parallel on success. It must not turn the single source roll into one roll per parallel.

The custom chance helpers expose deterministic selection seams so boundary, count, and scaling tests do not depend on global random state.

## Hyper Thermal Convector

### Approved Modern recipe policy

The hand-designed plasma, lava, liquefaction, steam-cascade, Dense Steam fuel, decompression, and cracking recipes are retained. They are an approved Modern replacement because TST dynamically imports GoodGenerator Extreme Heat Exchanger recipes and GTCEu 7.4.0 supplies no equivalent source recipe map.

The current UV EU/t requirement is retained as a balance deviation. These recipes and their tooltips must not be described as original static TST recipes.

Heat-exchange recipes that currently require vanilla water gain equivalent Distilled Water variants where the second input represents cooling water. IDs must be stable and distinct.

### Mode throughput and Parallel Hatch

GTCEu continues to own mode selection and persistence. The active recipe type selects the base parallel:

```text
Rapid Heat Exchange: 128
Rapid Cooling:       16
```

The Parallel Control Hatch adds to the selected base instead of replacing it. Inputs, deterministic outputs, and EU/t are multiplied once by actual parallel.

### Ability positions

R and U become fixed fluid-import positions. S and T become fixed fluid-export positions. Generic recipe I/O is removed from G and N so the four special positions cannot be bypassed. G retains only the approved energy, maintenance, and Parallel Control Hatch abilities needed by the Modern EU-consuming design.

Standard GTCEu hatches do not distinguish hot fluid from water or cold fluid from steam. Exact four-way semantic routing would require four new custom hatch types, so this port intentionally enforces direction and position but not fluid identity per position.

## Testing and validation

Add focused tests before production changes for:

- Mega Stone Breaker output-capacity probing and active-tick drain schedule;
- Giant coil-tier conversion, parallel, and duration formula;
- Nether Interface power reservation, three weighted selections, package scaling, and single Hellish Metal roll;
- Hyper mode-specific base parallel, additive hatch contribution, and recipe IDs for water variants;
- static ability predicates preventing generic Hyper fluid I/O outside R/S/T/U.

Then run:

1. casing catalog validation;
2. static structure and registration checks;
3. recipe ID/count checks;
4. `compileJava`, tests, and full build with the repository's configured dependency cache;
5. changed-file warning inspection.

Build success yields only `build validated; gameplay validation pending`. In-world validation must still cover both modes, no-hatch and hatch parallel, exact EU/t, fluid routing, chance outputs, Mega boost exhaustion, structure formation, persistence, JEI, and formed appearance.
