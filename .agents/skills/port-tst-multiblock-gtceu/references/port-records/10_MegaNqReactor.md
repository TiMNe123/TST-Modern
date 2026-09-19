# Mega Naquadah Reactor port record

## Recipe closure

- Source authority: local `D:/tmp/GT5/src/main/java/goodgenerator/loader/RecipeLoader.java` and `FuelRecipeLoader.java`.
- Added the complete Thorium-232 chemistry, high-density Thorium, base Thorium fuel, and Helium-plasma excitation chain.
- Restored the source acquisition routes for Uranium, Plutonium, MkI, and MkII reactor fuels. Previously approved Modern substitutions remain Quantium to Naquadria, Draconium to Duranium, and Electrum Flux to Europium.
- MkII remains a circuit-controlled Large Chemical Reactor recipe, matching the source multiblock chemical reactor and avoiding the single-block chemical reactor input limit.
- All nine depleted reactor fuels now have the source centrifuge recovery recipe. Repeated chanced outputs remain separate because each source entry is an independent probability roll.
- Naquadah Solution distillation also emits the source-ratio Naquadah Gas needed by MkII.
- The approved Modern Super Coolant bootstrap uses PCB Coolant and Helium in a UV Large Chemical Reactor; stored GT5 sources only provide the Hot Super Coolant recovery loop and depend on unavailable pack materials for initial production.

## Modern material compatibility

- GTCEu 7.4 native Calcium Chloride lacks the source-required fluid form; the liquid property is registered at its 1,045 K melting point.
- GTCEu 7.4 native Praseodymium and Californium lack dust forms. Their guarded dust properties restore the source depleted-fuel recovery outputs instead of leaving empty JEI chance slots.
- Naquadah Alloy frame, Duranium dense plate, and Black Titanium frame forms are generated because the approved construction recipes consume them.
- Particle Beam Guidance Pipe Casing uses an existing Huge Naquadah Fluid Pipe. No unsupported Naquadah Alloy pipe property or invented pipe statistics were added.

## Validation evidence

- Recipe closure check: one Thorium excited-fuel producer and consumers for all nine depleted fuels.
- Machine contract, casing catalog, locked texture source, locked production texture, localization, and formed-appearance validators pass.
- `gradlew test build --no-daemon` passes.
- Dedicated server reaches `Done` after registering the restored material and recipe chains.
- Super Coolant dependency regression test, full `gradlew test build --no-daemon`, and dedicated-server startup passed on 2026-09-08; the approved recipe produced no registration error.
- Runtime registry evidence includes `gtceu:praseodymium_dust` and `gtceu:californium_dust` (plus their small and tiny forms).
