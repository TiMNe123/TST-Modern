# Ore Processing Factory port record

## Identity and baseline

- TST machine/display name: General Ore Processing Factory TST
- TST registry/meta ID: 19017
- Controller: `D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/system/OreProcess/machines/TST_OreProcessingFactory.java`
- Inheritance: `GTCM_MultiMachineBase`, `GTCM_ProcessingLogic`
- Recipe authority: `OP_NormalProcessing`, `OP_Values`, and the Ore Processing Factory region of `GTCMMachineRecipes`
- Target: Minecraft 1.20.1, GTCEu 7.4.0, LDLib 1.0.40.b, JEI 15.20.0.115
- Machine contract: `.agents/skills/port-tst-multiblock-gtceu/contracts/ore-processing-factory.json`
- Working-tree baseline: clean `dev` before audit; implementation completed in the direct checkout

## Source behavior

- Converts every recognized ore/raw ore directly into primary dust, byproduct dusts, and gem bonuses.
- Fixed 128 ticks and 30 EU/t per parallel; no overclock; maximum parallel is limited by available input, output space, and power.
- One normal energy hatch loses 1/16 input power; multiple/exotic hatches lose 1/32. No-energy-hatch structures use GTNH's owner wireless EU account.
- Consumes 3200 mB Lubricant every 256 active ticks, independent of parallel, and persists the active-tick counter.
- Unknown inputs are transferred to output in the legacy inventory implementation.
- The dedicated recipe map has maximum I/O 1 item in, 9 items out, 1 fluid in, 0 fluid out.

## Structure

- Dimensions: 32 wide × 13 high × 15 deep.
- Source axes: height/layer, depth/row, right/column; target axes: RIGHT=column, DOWN=layer, BACK=row.
- Controller: one `~` at source layer 11, row 0, column 30; offsets 30,11,0; source front faces outward.
- Symbol totals: `A40 B512 C128 D300 E165 F104 G1144 H289 I1 J7 K8 L146 M20 ~1`.
- `J` positions accept fluid input, `K` positions accept energy, and `L` positions accept item input/output.
- Catalog mappings are direct or project-approved equivalents. `G`, `H/K`, and `I` reuse the already registered source-identical Advanced Iridium Casing, High Power Casing, and Electromagnetic Computer Coil.

## Overlap decision

- Inspected GTCEu `OreRecipeHandler`, ore-processing JEI diagram, and GCYM Large Maceration Tower.
- Result: Port.
- GTCEu exposes the multi-step ore chain and a parallel macerator, but no one-step machine preserving TST's direct dust/byproduct/gem yield, fixed-time unlimited parallel, and periodic lubricant behavior.

## Approved Modern deviations

- Omit wireless account-EU mode because the installed Modern stack has no equivalent network; require energy at source `K` positions.
- Generate recipes from GTCEu's live material registry and ore prefixes; map unavailable GTNH intermediate products to useful native dusts.
- Leave non-ore items untouched instead of moving them between player inventories.
- Preserve periodic lubricant behavior in machine state, not as a parallel-scaled recipe input.
- Close the UEV controller recipe with the exact substitutions recorded in the JSON contract and Research Station/Data Module progression.
- Keep the dark aqua controller identity and original TST tooltip wording; use the established multicolor gray/dark-aqua/white/gold/aqua/yellow/green/magenta layout and Modern Edition footer. Color Advanced Iridium Casing aqua, High Power Casing red, and Electromagnetic Computer Coil magenta. Omit the legacy wireless and non-ore-transfer claims because those behaviors are approved Modern deviations.

## Validation evidence

- Contract validator: passed after approval and implementation.
- Casing catalog and texture source/assets validators: passed; 153 production PNGs and 844 source assets locked.
- Structure/mapping checks: source shape parsed as 13 layers × 15 rows × 32 columns; symbol counts match the casing catalog.
- Build/tests: full Gradle test suite passed; focused power-loss/lubricant and exact-structure checks passed.
- Client smoke: reached the title screen with the controller, models, textures, and dynamic renderer registered; no Ore Processing Factory errors in the log.
- Datagen: recipe provider started, then the existing `mega_stone_breaker` generated-model check failed because GTCEu's template model is unavailable to Forge datagen; unrelated to this port.
- In-world formation, operation, and JEI interaction: pending user runtime validation.
- User approved the full audited specification on 2026-09-06.
- Current level: implementation and automated verification complete; runtime acceptance pending.
