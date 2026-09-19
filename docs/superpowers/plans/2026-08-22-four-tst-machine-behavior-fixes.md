# Four TST Machine Behavior Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring Mega Stone Breaker, Giant Vacuum Drying Furnace, Nether Interface, and Hyper Thermal Convector in line with the approved TST/GTCEu Modern behavior contract, with deterministic tests for arithmetic and chance semantics.

**Architecture:** Keep GTCEu-facing machine classes as adapters and extract dependency-free arithmetic into `machine/logic` helpers. Register Nether-specific `ChanceLogic` implementations during GTCEu's generic registry event, then bind them to the Nether recipe. Preserve GTCEu recipe maps and mode persistence where the approved spec calls for Modern adaptations.

**Tech Stack:** Java 17, Forge 1.20.1, GTCEu 7.4.0, Gradle 8.8, JUnit Jupiter 5.10.2.

**Spec:** [2026-08-22-four-tst-machine-behavior-fixes-design.md](../specs/2026-08-22-four-tst-machine-behavior-fixes-design.md)

## Global Constraints

- Preserve the user's unrelated working-tree edits in `MegaTreeFarmRecipes.java` and `TSTMachineRegistry.java`; never stage them in these commits.
- Do not change any approved structures or casing mappings except Hyper Thermal Convector ability predicates R/S/T/U, G, and N.
- Keep the approved Modern recipe adaptations for Giant Vacuum Drying Furnace and Hyper Thermal Convector.
- Use saturating arithmetic for all base-plus-hatch limits and voltage-times-amperage calculations.
- Treat an in-world/JEI run as pending unless it is actually performed. The strongest headless completion claim is `build validated; gameplay validation pending`.
- Each behavioral task follows red → minimal implementation → green → focused commit.

---

## Task 1: Install the unit-test seam

**Files:**

- Modify: `build.gradle`

- [ ] **Step 1: Add JUnit Jupiter to the test classpath**

Add inside `dependencies`:

```groovy
testImplementation platform('org.junit:junit-bom:5.10.2')
testImplementation 'org.junit.jupiter:junit-jupiter'
```

Add after the existing Java compile configuration:

```groovy
tasks.named('test', Test).configure {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Verify Gradle discovers the test task**

Run:

```powershell
.\gradlew.bat test
```

Expected: `BUILD SUCCESSFUL`; `test` may still report `NO-SOURCE` at this point.

- [ ] **Step 3: Commit only the build configuration**

```powershell
git add build.gradle
git commit -m "test: enable junit for machine logic"
```

---

## Task 2: Fix Mega Stone Breaker parallel, output capacity, and boost timing

**Files:**

- Create: `src/main/java/com/tstmodern/machine/logic/MegaStoneBreakerLogic.java`
- Create: `src/test/java/com/tstmodern/machine/logic/MegaStoneBreakerLogicTest.java`
- Create: `src/test/java/com/tstmodern/machine/MegaStoneBreakerMachineContractTest.java`
- Modify: `src/main/java/com/tstmodern/machine/MegaStoneBreakerMachine.java`

- [ ] **Step 1: Write failing arithmetic and timing tests**

```java
package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

final class MegaStoneBreakerLogicTest {
    @Test
    void computesTstBaseParallelAndSaturates() {
        assertEquals(4, MegaStoneBreakerLogic.baseParallel(0));
        assertEquals(16, MegaStoneBreakerLogic.baseParallel(2));
        assertEquals(Integer.MAX_VALUE, MegaStoneBreakerLogic.baseParallel(29));
        assertEquals(Integer.MAX_VALUE,
                MegaStoneBreakerLogic.addParallel(Integer.MAX_VALUE - 2, 16));
    }

    @Test
    void selectsNormalAndBoostedOutputBonuses() {
        assertEquals(4, MegaStoneBreakerLogic.outputBonus(false));
        assertEquals(1_024, MegaStoneBreakerLogic.outputBonus(true));
    }

    @Test
    void drainsOnFirstBoostedTickAndEveryTwentyActiveTicks() {
        assertTrue(MegaStoneBreakerLogic.shouldDrainBoost(0));
        assertFalse(MegaStoneBreakerLogic.shouldDrainBoost(1));
        assertFalse(MegaStoneBreakerLogic.shouldDrainBoost(19));
        assertTrue(MegaStoneBreakerLogic.shouldDrainBoost(20));
        assertTrue(MegaStoneBreakerLogic.shouldDrainBoost(40));
    }
}
```

- [ ] **Step 2: Run the focused test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.MegaStoneBreakerLogicTest
```

Expected: compilation failure because `MegaStoneBreakerLogic` does not exist.

- [ ] **Step 3: Add the dependency-free logic helper**

```java
package com.tstmodern.machine.logic;

public final class MegaStoneBreakerLogic {
    private MegaStoneBreakerLogic() {}

    public static int baseParallel(int machineTier) {
        if (machineTier >= 29) return Integer.MAX_VALUE;
        return (int) Math.min(Integer.MAX_VALUE, 4L << Math.max(0, machineTier));
    }

    public static int addParallel(int base, int hatch) {
        return (int) Math.min(Integer.MAX_VALUE,
                Math.max(0L, (long) base) + Math.max(0L, (long) hatch));
    }

    public static int outputBonus(boolean boosted) {
        return boosted ? 1_024 : 4;
    }

    public static boolean shouldDrainBoost(int activeBoostTicks) {
        return activeBoostTicks >= 0 && activeBoostTicks % 20 == 0;
    }
}
```

- [ ] **Step 4: Make output capacity see the final bonus**

Before changing production code, add `MegaStoneBreakerMachineContractTest`. It reads the machine source and asserts that a copied recipe's `outputs` map is replaced with `ContentModifier.multiplier(outputBonus).applyContents(recipe.outputs)` before `ParallelLogic.getParallelAmount` is called. Run it once and confirm it fails against the old modifier.

Replace the modifier calculation with this sequence:

```java
int parallelLimit = MegaStoneBreakerLogic.addParallel(
        MegaStoneBreakerLogic.baseParallel(breaker.getTier()),
        breaker.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
int outputBonus = MegaStoneBreakerLogic.outputBonus(breaker.hasBoostFluids());

GTRecipe outputProbe = recipe.copy();
outputProbe.outputs.clear();
outputProbe.outputs.putAll(ContentModifier.multiplier(outputBonus).applyContents(recipe.outputs));

int parallel = ParallelLogic.getParallelAmount(machine, outputProbe, parallelLimit);
if (parallel <= 0) return ModifierFunction.NULL;

return ModifierFunction.builder()
        .inputModifier(ContentModifier.multiplier(parallel))
        .outputModifier(ContentModifier.multiplier((double) parallel * outputBonus))
        .eutMultiplier(parallel)
        .parallels(parallel)
        .build();
```

This probe changes only the copied output map; the actual modifier still scales inputs once and outputs by `parallel × bonus` once.

- [ ] **Step 5: Replace world-time cadence with a persisted active-tick counter**

Add:

```java
@Persisted
private int activeBoostTicks;
```

In `beforeWorking`, set `boosted = hasBoostFluids()` and `activeBoostTicks = 0`.

In `onWorking`, use a two-phase drain around the successful GTCEu work tick:

```java
boolean drainDue = boosted && MegaStoneBreakerLogic.shouldDrainBoost(activeBoostTicks);
if (drainDue && !canConsumeBoostFluids()) {
    boosted = false;
    return false;
}
if (!super.onWorking()) return false;
if (drainDue) executeBoostFluidDrain();
if (boosted) activeBoostTicks++;
return true;
```

`canConsumeBoostFluids()` must simulate both 1,000 mB drains before either is executed. `executeBoostFluidDrain()` then executes both confirmed drains. Reset both fields in `afterWorking`.

- [ ] **Step 6: Run focused tests and compile**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.MegaStoneBreakerLogicTest --tests com.tstmodern.machine.MegaStoneBreakerMachineContractTest
.\gradlew.bat compileJava
```

Expected: both commands succeed.

- [ ] **Step 7: Commit the Mega Stone Breaker fix**

```powershell
git add src/main/java/com/tstmodern/machine/MegaStoneBreakerMachine.java src/main/java/com/tstmodern/machine/logic/MegaStoneBreakerLogic.java src/test/java/com/tstmodern/machine/logic/MegaStoneBreakerLogicTest.java src/test/java/com/tstmodern/machine/MegaStoneBreakerMachineContractTest.java
git commit -m "fix: align mega stone breaker processing"
```

---

## Task 3: Fix Giant Vacuum Drying Furnace coil and duration formulas

**Files:**

- Create: `src/main/java/com/tstmodern/machine/logic/GiantVacuumDryingFurnaceLogic.java`
- Create: `src/test/java/com/tstmodern/machine/logic/GiantVacuumDryingFurnaceLogicTest.java`
- Modify: `src/main/java/com/tstmodern/machine/GiantVacuumDryingFurnaceMachine.java`

- [ ] **Step 1: Write failing formula tests**

```java
@Test
void convertsZeroBasedGtceuCoilTierToTstTier() {
    assertEquals(1, GiantVacuumDryingFurnaceLogic.sourceCoilTier(0));
    assertEquals(4, GiantVacuumDryingFurnaceLogic.sourceCoilTier(3));
}

@Test
void computesParallelAndDurationFromMachineTierOnly() {
    assertEquals(32, GiantVacuumDryingFurnaceLogic.parallelLimit(1, 0));
    assertEquals(144, GiantVacuumDryingFurnaceLogic.parallelLimit(4, 16));
    assertEquals(2.0, GiantVacuumDryingFurnaceLogic.durationMultiplier(0, 1), 1.0e-9);
    assertEquals(Math.pow(0.8, 4) / 1.5,
            GiantVacuumDryingFurnaceLogic.durationMultiplier(4, 3), 1.0e-9);
}
```

- [ ] **Step 2: Run the test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.GiantVacuumDryingFurnaceLogicTest
```

Expected: missing helper compilation failure.

- [ ] **Step 3: Implement the approved formulas**

```java
public static int sourceCoilTier(int gtceuTier) {
    return Math.max(1, gtceuTier + 1);
}

public static int parallelLimit(int sourceCoilTier, int hatchParallel) {
    long base = 32L * Math.max(1, sourceCoilTier);
    return (int) Math.min(Integer.MAX_VALUE, base + Math.max(0L, hatchParallel));
}

public static double durationMultiplier(int machineTier, int sourceCoilTier) {
    return Math.pow(0.8, Math.max(0, machineTier)) /
            (Math.max(1, sourceCoilTier) * 0.5);
}
```

- [ ] **Step 4: Adapt the GTCEu modifier**

In `GiantVacuumDryingFurnaceMachine.recipeModifier`:

```java
int coilTier = GiantVacuumDryingFurnaceLogic.sourceCoilTier(
        furnace.getCoilType().getTier());
int parallelLimit = GiantVacuumDryingFurnaceLogic.parallelLimit(
        coilTier,
        furnace.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
double durationMultiplier = GiantVacuumDryingFurnaceLogic.durationMultiplier(
        furnace.getTier(), coilTier);
```

Remove `RecipeHelper`, recipe-tier subtraction, tier delta, and the old `1 + 0.5 × (coilTier - 1)` formula. Keep the definition's `GTRecipeModifiers.OC_NON_PERFECT` after this custom modifier.

- [ ] **Step 5: Run tests and compile**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.GiantVacuumDryingFurnaceLogicTest
.\gradlew.bat compileJava
```

- [ ] **Step 6: Commit the furnace fix**

```powershell
git add src/main/java/com/tstmodern/machine/GiantVacuumDryingFurnaceMachine.java src/main/java/com/tstmodern/machine/logic/GiantVacuumDryingFurnaceLogic.java src/test/java/com/tstmodern/machine/logic/GiantVacuumDryingFurnaceLogicTest.java
git commit -m "fix: align giant vacuum furnace scaling"
```

---

## Task 4: Add Nether waste material and deterministic chance infrastructure

**Files:**

- Create: `src/main/java/com/tstmodern/machine/logic/NetherInterfaceLogic.java`
- Create: `src/test/java/com/tstmodern/machine/logic/NetherInterfaceLogicTest.java`
- Create: `src/main/java/com/tstmodern/recipe/chance/TSTChanceLogics.java`
- Create: `src/test/java/com/tstmodern/recipe/chance/TSTChanceRegistrationContractTest.java`
- Modify: `src/main/java/com/tstmodern/TSTModern.java`
- Modify: `src/main/java/com/tstmodern/registry/TSTMaterials.java`
- Modify: `src/main/resources/assets/tstmodern/lang/en_us.json`
- Modify: `src/main/resources/assets/tstmodern/lang/vi_vn.json`

- [ ] **Step 1: Write failing deterministic chance-helper tests**

```java
@Test
void mapsAllWeightedPackageBoundaries() {
    int[] weights = {1, 49, 30, 10, 10};
    assertEquals(0, NetherInterfaceLogic.selectWeightedIndex(0, weights));
    assertEquals(1, NetherInterfaceLogic.selectWeightedIndex(1, weights));
    assertEquals(1, NetherInterfaceLogic.selectWeightedIndex(49, weights));
    assertEquals(2, NetherInterfaceLogic.selectWeightedIndex(50, weights));
    assertEquals(2, NetherInterfaceLogic.selectWeightedIndex(79, weights));
    assertEquals(3, NetherInterfaceLogic.selectWeightedIndex(80, weights));
    assertEquals(4, NetherInterfaceLogic.selectWeightedIndex(99, weights));
}

@Test
void treatsThirtyPercentAsOneExactCycleRoll() {
    assertTrue(NetherInterfaceLogic.passesChance(2_999, 3_000));
    assertFalse(NetherInterfaceLogic.passesChance(3_000, 3_000));
}

@Test
void performsExactlyThreeSelectionsAndScalesOnePackage() {
    int[] scriptedRolls = {0, 50, 99};
    AtomicInteger calls = new AtomicInteger();
    int[] selected = NetherInterfaceLogic.selectThreeWeighted(
            bound -> scriptedRolls[calls.getAndIncrement()],
            new int[] {1, 49, 30, 10, 10});
    assertArrayEquals(new int[] {0, 2, 4}, selected);
    assertEquals(3, calls.get());
    assertEquals(64, NetherInterfaceLogic.scaledAmount(4, 16));
}

@Test
void performsOnlyOneFluidChanceRollPerCycle() {
    AtomicInteger calls = new AtomicInteger();
    assertTrue(NetherInterfaceLogic.rollOnce(bound -> {
        calls.incrementAndGet();
        return 2_999;
    }, 3_000, 10_000));
    assertEquals(1, calls.get());
}
```

- [ ] **Step 2: Run the focused test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.NetherInterfaceLogicTest
```

- [ ] **Step 3: Implement the pure weighted-selection seam**

`selectWeightedIndex` validates a non-empty, positive-total array, accepts `0 <= roll < total`, accumulates weights in order, and returns the first cumulative bucket containing the roll. `selectThreeWeighted(IntUnaryOperator, int[])` requests exactly three bounded rolls and returns three indexes. `rollOnce(IntUnaryOperator, chance, maxChance)` requests exactly one bounded roll. `scaledAmount` uses saturating multiplication. `passesChance(roll, chance)` returns `roll < chance`.

- [ ] **Step 4: Register two GTCEu 7.4 chance logics at the correct lifecycle point**

In the TST mod constructor add:

```java
modBus.addGenericListener(ChanceLogic.class, TSTChanceLogics::registerChanceLogics);
```

`ChanceLogic` registers itself in its constructor, so `registerChanceLogics` instantiates each implementation during the event and does not call `event.register` again:

```java
public static void registerChanceLogics(
        GTCEuAPI.RegisterEvent<String, ChanceLogic> event) {
    THREE_WEIGHTED_SCALED = new ThreeWeightedScaledChanceLogic();
    SINGLE_ROLL_SCALED = new SingleRollScaledChanceLogic();
}
```

Before production registration, add `TSTChanceRegistrationContractTest` that reads `TSTModern.java` and asserts the `ChanceLogic` generic listener is present, then reads `TSTChanceLogics.java` and asserts both `tstmodern:` registry IDs are declared. Run it once and confirm red.

The weighted logic must call `NetherInterfaceLogic.selectThreeWeighted`, make exactly three selections with replacement, use the chanced entries' raw `chance` values as weights, and return each selected entry through:

```java
selected.copyChanced(cap, ContentModifier.multiplier(times))
```

The fluid logic must call `NetherInterfaceLogic.rollOnce`, make exactly one raw chance roll per completed cycle and, on success, return:

```java
entry.copyChanced(cap, ContentModifier.multiplier(times))
```

Both override the GTCEu 7.4 signature including `ChanceBoostFunction`, recipe tier, chance tier, nullable cache, and `times`. They intentionally ignore tier chance boost and cache to preserve exact TST probabilities.

- [ ] **Step 5: Add the fluid-only Poor Nether Waste material**

Add `POOR_NETHER_WASTE` to `TSTMaterials` and register it with no ingot/dust form:

```java
POOR_NETHER_WASTE = new Material.Builder(TSTModern.id("poor_nether_waste"))
        .liquid(new FluidBuilder().temperature(330))
        .color(0x3A2420).secondaryColor(0x6B3A2D)
        .iconSet(MaterialIconSet.DULL)
        .buildAndRegister();
```

Add exact language keys:

```json
"material.tstmodern.poor_nether_waste": "Poor Nether Waste",
"fluid.tstmodern.poor_nether_waste": "Poor Nether Waste",
"tstmodern.chance_logic.three_weighted_scaled": "3 weighted package rolls",
"tstmodern.chance_logic.single_roll_scaled": "1 cycle chance roll"
```

Vietnamese values:

```json
"material.tstmodern.poor_nether_waste": "Phế Thải Nether Nghèo",
"fluid.tstmodern.poor_nether_waste": "Phế Thải Nether Nghèo",
"tstmodern.chance_logic.three_weighted_scaled": "3 lượt chọn gói theo trọng số",
"tstmodern.chance_logic.single_roll_scaled": "1 lượt xác suất mỗi chu kỳ"
```

- [ ] **Step 6: Run tests, resource processing, and compilation**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.NetherInterfaceLogicTest --tests com.tstmodern.recipe.chance.TSTChanceRegistrationContractTest
.\gradlew.bat processResources compileJava
```

- [ ] **Step 7: Commit the Nether foundation**

```powershell
git add src/main/java/com/tstmodern/TSTModern.java src/main/java/com/tstmodern/registry/TSTMaterials.java src/main/java/com/tstmodern/recipe/chance/TSTChanceLogics.java src/main/java/com/tstmodern/machine/logic/NetherInterfaceLogic.java src/test/java/com/tstmodern/machine/logic/NetherInterfaceLogicTest.java src/test/java/com/tstmodern/recipe/chance/TSTChanceRegistrationContractTest.java src/main/resources/assets/tstmodern/lang/en_us.json src/main/resources/assets/tstmodern/lang/vi_vn.json
git commit -m "feat: add nether interface waste and chance logic"
```

---

## Task 5: Fix Nether Interface recipe, power reserve, and outputs

**Files:**

- Modify: `src/test/java/com/tstmodern/machine/logic/NetherInterfaceLogicTest.java`
- Modify: `src/main/java/com/tstmodern/machine/logic/NetherInterfaceLogic.java`
- Modify: `src/main/java/com/tstmodern/machine/NetherInterfaceMachine.java`
- Modify: `src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/NetherInterfaceDefinition.java`

- [ ] **Step 1: Add failing power and saturation tests**

```java
private static final long IV_EUT = 7_680L;

@Test
void reservesTwoIvAmpsBeforeParallelWork() {
    assertEquals(0, NetherInterfaceLogic.powerParallel(2 * IV_EUT, IV_EUT));
    assertEquals(1, NetherInterfaceLogic.powerParallel(3 * IV_EUT, IV_EUT));
    assertEquals(64, NetherInterfaceLogic.powerParallel(66 * IV_EUT, IV_EUT));
}

@Test
void saturatesInputPowerAndParallelLimit() {
    assertEquals(Long.MAX_VALUE, NetherInterfaceLogic.saturatedMultiply(Long.MAX_VALUE, 2));
    assertEquals(Integer.MAX_VALUE,
            NetherInterfaceLogic.parallelLimit(64, Integer.MAX_VALUE));
}
```

- [ ] **Step 2: Run focused tests and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.NetherInterfaceLogicTest
```

- [ ] **Step 3: Implement the power helper**

```java
public static int powerParallel(long availableEUt, long ivEUt) {
    if (ivEUt <= 0 || availableEUt < 3L * ivEUt) return 0;
    return (int) Math.min(Integer.MAX_VALUE, availableEUt / ivEUt - 2L);
}
```

Add non-negative saturating `saturatedMultiply(long, long)` and `parallelLimit(int, int)` helpers.

- [ ] **Step 4: Cap parallel by resources and available input power**

In `NetherInterfaceMachine.recipeModifier`:

```java
int limit = NetherInterfaceLogic.parallelLimit(64,
        netherInterface.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
int resourceParallel = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, limit);
long availableEUt = NetherInterfaceLogic.saturatedMultiply(
        netherInterface.getEnergyContainer().getInputVoltage(),
        netherInterface.getEnergyContainer().getInputAmperage());
int powerParallel = NetherInterfaceLogic.powerParallel(availableEUt, VA[IV]);
int parallel = Math.min(resourceParallel, powerParallel);
if (parallel <= 0) return ModifierFunction.NULL;
```

Return input/output multipliers of `parallel`, EU/t multiplier `parallel + 2`, and `.parallels(parallel)`.

- [ ] **Step 5: Replace the dimensional harvesting recipe**

Use this exact core contract:

```java
.inputFluids(DistilledWater.getFluid(16_000))
.outputFluids(TSTMaterials.POOR_NETHER_WASTE.getFluid(16_000))
.chancedOutput(new ItemStack(Items.ANCIENT_DEBRIS), 100, 0)
.chancedOutput(new ItemStack(Blocks.NETHERRACK, 16), 4_900, 0)
.chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 3_000, 0)
.chancedOutput(new ItemStack(Items.NETHERITE_INGOT), 1_000, 0)
.chancedOutput(new ItemStack(Items.NETHER_STAR), 1_000, 0)
.chancedOutput(TSTMaterials.HELLISH_METAL.getFluid(288), 3_000, 0)
.chancedItemOutputLogic(TSTChanceLogics.THREE_WEIGHTED_SCALED)
.chancedFluidOutputLogic(TSTChanceLogics.SINGLE_ROLL_SCALED)
```

Remove the core recipe's Lava and Liquid Nether Air imports/usages. Keep Hellish Metal bootstrap and controller/casing recipes unchanged.

- [ ] **Step 6: Prevent a later modifier from breaking the exact power equation**

Change Nether Interface registration to:

```java
.recipeModifiers(NetherInterfaceMachine::recipeModifier)
```

Remove `GTRecipeModifiers.OC_NON_PERFECT` and its now-unused import from `NetherInterfaceDefinition`. This keeps final EU/t exactly `VA[IV] × (parallel + 2)`.

- [ ] **Step 7: Run tests, data generation checks, and compile**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.NetherInterfaceLogicTest
.\gradlew.bat processResources compileJava
```

Inspect the generated recipe JSON and confirm the ID remains `tstmodern:nether_interface/dimensional_harvesting`, its deterministic fluids are Distilled Water → Poor Nether Waste, and both chance logic IDs serialize under `tstmodern`.

- [ ] **Step 8: Commit the Nether behavior fix**

```powershell
git add src/main/java/com/tstmodern/machine/NetherInterfaceMachine.java src/main/java/com/tstmodern/machine/logic/NetherInterfaceLogic.java src/test/java/com/tstmodern/machine/logic/NetherInterfaceLogicTest.java src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java src/main/java/com/tstmodern/registry/machine/NetherInterfaceDefinition.java
git commit -m "fix: align nether interface cycle behavior"
```

---

## Task 6: Fix Hyper Thermal Convector mode parallel and dedicated abilities

**Files:**

- Create: `src/main/java/com/tstmodern/machine/logic/HyperThermalConvectorLogic.java`
- Create: `src/test/java/com/tstmodern/machine/logic/HyperThermalConvectorLogicTest.java`
- Create: `src/test/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinitionContractTest.java`
- Modify: `src/main/java/com/tstmodern/machine/HyperThermalConvectorMachine.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinition.java`
- Modify: `src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java`

- [ ] **Step 1: Write failing mode-parallel tests**

```java
@Test
void assignsApprovedBaseParallelPerMode() {
    assertEquals(128, HyperThermalConvectorLogic.baseParallel(true));
    assertEquals(16, HyperThermalConvectorLogic.baseParallel(false));
}

@Test
void addsRatherThanReplacesParallelHatch() {
    assertEquals(144, HyperThermalConvectorLogic.parallelLimit(128, 16));
    assertEquals(32, HyperThermalConvectorLogic.parallelLimit(16, 16));
    assertEquals(Integer.MAX_VALUE,
            HyperThermalConvectorLogic.parallelLimit(Integer.MAX_VALUE, 64));
}
```

- [ ] **Step 2: Run the focused test and confirm red**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.HyperThermalConvectorLogicTest
```

- [ ] **Step 3: Implement and connect the mode logic**

```java
boolean heatExchange = recipe.recipeType == TSTRecipeTypes.RAPID_HEAT_EXCHANGE;
int limit = HyperThermalConvectorLogic.parallelLimit(
        HyperThermalConvectorLogic.baseParallel(heatExchange),
        convector.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
int parallel = ParallelLogic.getParallelAmount(machine, recipe, limit);
```

Keep input, output, and EU/t scaling exactly once by actual parallel.

- [ ] **Step 4: Write a failing source contract for dedicated ability positions**

Read `HyperThermalConvectorDefinition.java` in the test and assert it contains all four exact predicates:

```java
.where('R', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
.where('U', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
.where('S', Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
.where('T', Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
```

Also assert the G predicate contains only maintenance/energy plus parallel auto-abilities, N is casing-only, and the source contains no `autoAbilities(definition.getRecipeTypes())`.

- [ ] **Step 5: Tighten the structure ability predicates**

- G: retain `blocks(ADVANCED_IRIDIUM_CASING)`, `autoAbilities(true, false, false)`, and `autoAbilities(false, false, true)`.
- N: retain only `blocks(IRIDIUM_REINFORCED_NEUTRONIUM_CASING)`.
- R and U: fixed `IMPORT_FLUIDS`, preview count 1.
- S and T: fixed `EXPORT_FLUIDS`, preview count 1.
- Do not add generic recipe I/O anywhere else.

- [ ] **Step 6: Add Distilled Water variants through one recipe helper**

Extract a helper that registers a base water recipe and a second recipe with the same hot input, outputs, duration, and UV EU/t, but with `DistilledWater` as cooling fluid and `_distilled_water` appended to the ID.

Call the pair helper exactly seven times for:

```text
helium_plasma
nitrogen_plasma
oxygen_plasma
argon_plasma
iron_plasma
nickel_plasma
lava_cooling
```

The resulting new IDs are:

```text
tstmodern:rapid_heat_exchange/helium_plasma_distilled_water
tstmodern:rapid_heat_exchange/nitrogen_plasma_distilled_water
tstmodern:rapid_heat_exchange/oxygen_plasma_distilled_water
tstmodern:rapid_heat_exchange/argon_plasma_distilled_water
tstmodern:rapid_heat_exchange/iron_plasma_distilled_water
tstmodern:rapid_heat_exchange/nickel_plasma_distilled_water
tstmodern:rapid_heat_exchange/lava_cooling_distilled_water
```

Do not change the hand-designed recipe amounts, Dense Steam usage recipes, or UV base EU/t.

- [ ] **Step 7: Extend the source contract for the recipe variants**

Assert the recipe source has exactly seven `addRapidHeatExchangePair(provider,` call sites and that the helper appends `"_distilled_water"`. This makes accidental omission of one variant fail the unit suite.

- [ ] **Step 8: Run focused tests and compile**

```powershell
.\gradlew.bat test --tests com.tstmodern.machine.logic.HyperThermalConvectorLogicTest --tests com.tstmodern.registry.machine.HyperThermalConvectorDefinitionContractTest
.\gradlew.bat processResources compileJava
```

- [ ] **Step 9: Commit the Hyper Thermal Convector fix**

```powershell
git add src/main/java/com/tstmodern/machine/HyperThermalConvectorMachine.java src/main/java/com/tstmodern/machine/logic/HyperThermalConvectorLogic.java src/test/java/com/tstmodern/machine/logic/HyperThermalConvectorLogicTest.java src/main/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinition.java src/test/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinitionContractTest.java src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java
git commit -m "fix: align hyper thermal convector modes"
```

---

## Task 7: Validate all four machines and close the port records

**Files:**

- Create: `src/test/java/com/tstmodern/data/recipe/PortRecipeCatalogContractTest.java`
- Modify: `docs/port-records/mega-stone-breaker.md`
- Modify: `docs/port-records/giant-vacuum-drying-furnace.md`
- Modify: `docs/port-records/nether-interface.md`
- Modify: `docs/port-records/hyper-thermal-convector.md`

- [ ] **Step 1: Add the final recipe catalog contract**

The test reads the four recipe modules and asserts:

- Mega Stone Breaker still has ten `basic(provider,` calls and four direct `recipe("` calls, totaling fourteen processing recipes.
- Giant Vacuum Drying Furnace still has seven `chemical_dehydrator/` processing IDs and ten `vacuum_furnace/` processing IDs.
- Nether Interface has exactly one `nether_interface/dimensional_harvesting` ID and no Lava or Liquid Nether Air in that recipe method.
- Hyper Thermal Convector has exactly seven heat-exchange pair registrations and the seven stable Distilled Water variant IDs produced by the pair helper.

Run:

```powershell
.\gradlew.bat test --tests com.tstmodern.data.recipe.PortRecipeCatalogContractTest
```

Expected: pass only when all approved counts and IDs are intact.

- [ ] **Step 2: Run the complete automated suite**

```powershell
.\gradlew.bat clean test compileJava processResources build
```

Expected: all JUnit tests pass, Java/resources compile, and the distributable JAR builds.

- [ ] **Step 3: Validate the TST casing catalog**

```powershell
node .agents/skills/port-tst-multiblock-gtceu/scripts/validate-casing-catalog.mjs
```

Expected: all catalog entries and per-machine manifests validate, including the four machines in this change.

- [ ] **Step 4: Run repository hygiene checks**

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors. The two pre-existing user files may remain modified, but no task commit includes them. Inspect compile output for unused imports or local variables and remove warnings introduced by this work.

- [ ] **Step 5: Update each port record with implementation evidence**

For each of the four records, add:

- final formula/recipe/ability summary;
- focused JUnit command and result;
- full `build` result;
- casing catalog validator result;
- deviation status already approved by the spec;
- final status exactly `build validated; gameplay validation pending`.

- [ ] **Step 6: Commit validation tests and records**

```powershell
git add src/test/java/com/tstmodern/data/recipe/PortRecipeCatalogContractTest.java docs/port-records/mega-stone-breaker.md docs/port-records/giant-vacuum-drying-furnace.md docs/port-records/nether-interface.md docs/port-records/hyper-thermal-convector.md
git commit -m "docs: record four machine validation"
```

- [ ] **Step 7: Final review against the approved spec**

Confirm every approved item is represented:

- Mega Stone Breaker: bonus-aware output capacity, additive hatch, local active cadence, atomic dual-fluid check.
- Giant Furnace: zero-based coil conversion, `32 × coilTier`, machine-tier-only `0.8^tier`, fixed one segment, Modern recipe maps retained.
- Nether Interface: Distilled Water → Poor Waste, exact three package selections, one 30% fluid roll, additive parallel, two-IV-amp reserve, no post-cap overclock.
- Hyper Convector: approved custom recipes retained, seven Distilled Water variants, 128/16 bases, additive hatch, fixed R/U imports and S/T exports, no generic G/N bypass.

Do not claim in-world structure formation, JEI layout, hatch routing, or runtime output distribution until those are manually exercised.
