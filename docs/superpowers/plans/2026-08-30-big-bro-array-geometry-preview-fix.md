# BigBroArray Geometry and JEI Preview Fix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Correct the four BigBroArray addon placements and produce formed, right-side-up cumulative JEI previews on branch `dev`.

**Architecture:** Keep source-derived runtime geometry immutable and express the four legacy transforms explicitly. Assemble previews in controller-relative DOWN coordinates, then perform one isolated DOWN-to-UP conversion before passing them to `MultiblockShapeInfo`; inject preview-only hatches after geometry assembly.

**Tech Stack:** Java 17, Minecraft Forge 1.20.1, GTCEu Modern 7.4.0, JUnit 5, Gradle.

**Spec:** `docs/superpowers/specs/2026-08-30-big-bro-array-geometry-preview-fix-design.md`

## Global Constraints

- Work only on branch `dev` in `C:/Users/mtien/TST-Modern`.
- Preserve all existing user changes and the dirty working tree.
- Do not modify `CORE_SOURCE`, `ADDON_RAW_SOURCE`, the four TST offsets, recipes, or machine processing logic.
- Use at most three test/fix loops for the same failing environment gate.
- A build pass does not replace the manual JEI and formed-machine gate.

## File responsibilities

- `BigBroArrayStructure.java`: source geometry, exact TST transforms, cumulative preview assembly, and preview coordinate conversion.
- `BigBroArrayDefinition.java`: runtime pattern predicates and mapping preview-only symbols to concrete GTCEu hatches.
- `BigBroArrayDefinitionContractTest.java`: frozen source/runtime coordinate and preview topology contract.
- `BigBroArrayAddonMatcherTest.java`: proof that runtime world lookup uses the corrected placement coordinates.

---

### Task 1: Freeze the four original TST transforms

**Files:**
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayStructure.java`
- Test: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`
- Test: `src/test/java/com/tstmodern/machine/logic/BigBroArrayAddonMatcherTest.java`

**Interfaces:**
- Consumes: `ADDON_SOURCE`, `PlacementSeed`, `RelativeCell`.
- Produces: unchanged `List<AddonPlacement> ADDON_PLACEMENTS`, now with source-faithful cells.

- [ ] **Step 1: Replace the generic-rotation assertions with exact bounds and digest assertions**

Add assertions for the four bounds and SHA-256 values from the design spec. Hash sorted strings formatted exactly as `right,down,back,symbol` joined by `\n`. Add one matcher test per addon that places valid states only at its corrected coordinates and proves the other three placements do not match that coordinate map.

- [ ] **Step 2: Run the focused tests and confirm red**

Run:

```powershell
./gradlew test --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest --tests com.tstmodern.machine.logic.BigBroArrayAddonMatcherTest
```

Expected: addon 1, 2, and 3 coordinate digest/bounds assertions fail; addon 0 passes.

- [ ] **Step 3: Implement the four TST transforms explicitly**

Replace the current rotation switch with:

```java
private static RelativeCell transform(
                                      int right,
                                      int down,
                                      int back,
                                      char symbol,
                                      int variant,
                                      int width) {
    return switch (variant) {
        case 0 -> new RelativeCell(right, down, back, symbol);
        case 1 -> new RelativeCell(back, down, right, symbol);
        case 2 -> new RelativeCell(width - 1 - right, down, back, symbol);
        case 3 -> new RelativeCell(back, down, width - 1 - right, symbol);
        default -> throw new IllegalArgumentException("addon variant must be 0..3");
    };
}
```

Keep the four existing offsets unchanged.

- [ ] **Step 4: Run focused tests and confirm green**

Run the Task 1 command again. Expected: all selected tests pass.

---

### Task 2: Build cumulative previews and convert DOWN to JEI UP

**Files:**
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayStructure.java`
- Test: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`

**Interfaces:**
- Consumes: corrected `ADDON_PLACEMENTS`.
- Produces: `previewLayouts()` with five cumulative, JEI-oriented layouts.

- [ ] **Step 1: Write failing cumulative-page and orientation tests**

Replace the single-addon preview test with assertions that:

```java
assertEquals(List.of(321, 1221, 2121, 3021, 3921),
        previews.stream().map(BigBroArrayDefinitionContractTest::occupiedCount).toList());
```

For the full preview assert dimensions `45 x 26 x 45`, controller coordinate `(22, 2, 21)`, all core/addon Clean Stainless cells at relative `down=2` map to preview row `0`, and cells at relative `down=-23` map to row `25`.

- [ ] **Step 2: Run the structure contract and confirm red**

```powershell
./gradlew test --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest
```

Expected: current pages have `321,1221,1221,1221,1221` occupied cells and vertical assertions fail.

- [ ] **Step 3: Generalize preview assembly to zero through four addons**

Replace `coreWithAddon(AddonPlacement)` with a method accepting a prefix list:

```java
static String[][] coreWithAddons(List<AddonPlacement> placements)
```

Compute one common relative bounding box, insert core and every requested addon, and retain the existing overlap exception.

- [ ] **Step 4: Convert the preview row coordinate exactly once**

When writing a controller-relative cell to the JEI canvas, use:

```java
int previewRow = maxDown - relativeDown;
```

Do not change the stored `RelativeCell.down`, runtime pattern direction, or matcher signs.

- [ ] **Step 5: Generate cumulative prefix pages**

Build page `n` from `ADDON_PLACEMENTS.subList(0, n)` for `n=0..4` and return an immutable list.

- [ ] **Step 6: Run the structure contract and confirm green**

Run the Task 2 command again. Expected: all topology, orientation, count, and overlap assertions pass.

---

### Task 3: Make every JEI preview satisfy the formal core pattern

**Files:**
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayStructure.java`
- Modify: `src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java`
- Test: `src/test/java/com/tstmodern/registry/machine/BigBroArrayDefinitionContractTest.java`

**Interfaces:**
- Consumes: JEI-oriented cumulative layouts from Task 2.
- Produces: preview layouts containing stable symbols `M` through `T`, and `shapeInfo()` mappings for those symbols.

- [ ] **Step 1: Write failing preview-ability symbol tests**

For every page assert exactly one each of `M,N,O,P,Q,R,S,T`. Assert replacing these symbols with their base `D` or `F` symbol restores the 321-cell core digest, proving preview injection cannot mutate source geometry.

- [ ] **Step 2: Run the structure contract and confirm red**

```powershell
./gradlew test --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest
```

Expected: symbols `M..T` are absent.

- [ ] **Step 3: Inject preview-only ability symbols at fixed core-relative cells**

Use these occupied core cells before the DOWN-to-UP conversion:

```text
M Maintenance    (-1, 0, 0) replaces D
N Muffler         (1, 0, 0) replaces D
O Item Import    (-1, 0, 1) replaces D
P Item Export     (1, 0, 1) replaces D
Q Fluid Import   (-1, 0, 2) replaces D
R Fluid Export    (1, 0, 2) replaces D
S Energy Input    (0, 2,-4) replaces F
T Energy Output   (0, 2, 6) replaces F
```

Reject an injection if the expected base symbol is absent so a later source change fails loudly.

- [ ] **Step 4: Map preview symbols to IV GTCEu parts**

In `shapeInfo()` map:

```java
.where('M', GTMachines.MAINTENANCE_HATCH, Direction.NORTH)
.where('N', GTMachines.MUFFLER_HATCH[GTValues.IV], Direction.UP)
.where('O', GTMachines.ITEM_IMPORT_BUS[GTValues.IV], Direction.NORTH)
.where('P', GTMachines.ITEM_EXPORT_BUS[GTValues.IV], Direction.NORTH)
.where('Q', GTMachines.FLUID_IMPORT_HATCH[GTValues.IV], Direction.NORTH)
.where('R', GTMachines.FLUID_EXPORT_HATCH[GTValues.IV], Direction.NORTH)
.where('S', GTMachines.ENERGY_INPUT_HATCH[GTValues.IV], Direction.NORTH)
.where('T', GTMachines.ENERGY_OUTPUT_HATCH[GTValues.IV], Direction.NORTH)
```

Keep all runtime `D/F` predicates unchanged.

- [ ] **Step 5: Run focused tests and compile**

```powershell
./gradlew test --tests com.tstmodern.registry.machine.BigBroArrayDefinitionContractTest compileJava
```

Expected: tests pass and every referenced GTMachines field resolves under GTCEu 7.4.0.

---

### Task 4: Regression and client log gate

**Files:**
- Verify: `run/logs/latest.log`
- Verify: `run/logs/debug.log`

**Interfaces:**
- Consumes: Tasks 1 through 3.
- Produces: build evidence and a manual-test checklist for the user.

- [ ] **Step 1: Run all BigBroArray tests**

```powershell
./gradlew test --tests "com.tstmodern.*BigBroArray*"
```

Expected: all BigBroArray suites pass.

- [ ] **Step 2: Run the full build**

```powershell
./gradlew build
```

Expected: `BUILD SUCCESSFUL`. Stop after the third repetition of the same environment failure and record it without claiming success.

- [ ] **Step 3: Hand off the manual client gate**

Ask the user to start a fresh client, open all five BigBroArray JEI pages, and verify:

- core glass is above and Clean Stainless is below;
- pages show 0, 1, 2, 3, then 4 addons;
- addons appear east, south, west, and north around the core;
- addon Clean Stainless bases are coplanar with the core base;
- no preview page reports a failed formation.

- [ ] **Step 4: Inspect the resulting logs**

```powershell
rg -n "Pattern formed checking failed: tstmodern:big_bro_array|Error while assembling multiblock.*BigBroArray|this\.world.*null" run/logs/latest.log run/logs/debug.log
```

Expected: no matches from the fresh run. Treat Trinium registry remap messages and unrelated machine/loot errors according to the design spec.

If preview formation warnings are gone but the async null-world error remains, preserve that red result and trace when the affected `BigBroArrayMachine` first creates its `MultiblockState`. Add a focused lifecycle regression before changing `onLoad()` or async registration; do not add a catch-and-ignore workaround. Apply the global three-loop limit to this follow-up gate.

---

## Self-review result

- Spec coverage: runtime transforms, four directions, vertical preview conversion, cumulative pages, mandatory hatches, async log failure, and manual validation each have an implementing task.
- Placeholder scan: no deferred implementation steps remain.
- Type consistency: Tasks 1-3 preserve `AddonPlacement` and expose only `coreWithAddons(List<AddonPlacement>)` plus the existing `previewLayouts()` public entry point.
