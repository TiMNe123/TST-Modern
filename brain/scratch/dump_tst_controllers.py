"""Dump the TST 1.7.10 controller-crafting recipe block for each machine still
awaiting a port, so the ingredients can be transcribed exactly.

Source: D:/tmp/TST (local clone). A recipe block is delimited by `.addTo(` /
`.buildAndRegister()` / `GT_Values.RA` boundaries; here we just take a window
from the anchor line back to the nearest builder start and forward to `.addTo`.
"""
import re
import sys
from pathlib import Path

SRC = Path("D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology")

TARGETS = [
    ("LargeNeutronOscillator", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3150),
    ("MegaNqReactor", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3739),
    ("DSPLauncher", "recipe/machineRecipe/expanded/DSPRecipePool.java", 175),
    ("DSPReceiver", "recipe/machineRecipe/expanded/DSPRecipePool.java", 140),
    ("MicroSpaceTimeFabricatorio", "recipe/craftRecipe/item/CosmicProcessorCircuitRecipes.java", 455),
    ("MiracleTop", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 698),
    ("ArtificialStar", "recipe/machineRecipe/expanded/DSPRecipePool.java", 249),
    ("DeployedNanoCore", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 2039),
    ("DimensionallyTranscendentMatterPlasmaForgePrototypeMK2",
     "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3130),
    ("MassFabricatorGenesis", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3216),
    ("SuperWaterPurifier", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3634),
    ("StrangeMatterAggregator", "recipe/machineRecipe/expanded/DSPRecipePool.java", 918),
    ("AstralComputingArray", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 2071),
    ("LargeIndustrialCokingFactory", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 1403),
    ("OreProcessingFactory", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 1273),
    ("StarcoreMiner", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 2238),
    ("MeteorMiner", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3461),
]

START = re.compile(r"GT_Values\.RA|GTValues\.RA|RecipeBuilder|\.stdBuilder\(|TST_RecipeBuilder|"
                   r"GTCMRecipe|addAssemblylineRecipe|new GT_Recipe|RA\.stdBuilder")
END = re.compile(r"\.addTo\(|\.buildAndRegister\(|\);\s*$")


def dump(name, rel, anchor):
    path = SRC / rel
    lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    i = anchor - 1
    start = i
    for j in range(i, max(0, i - 60), -1):
        if START.search(lines[j]):
            start = j
            break
    end = i
    for j in range(i, min(len(lines), i + 60)):
        if ".addTo(" in lines[j] or ".buildAndRegister(" in lines[j]:
            end = j
            break
    print(f"\n{'=' * 100}\n### {name}   {rel}:{start + 1}-{end + 1}\n{'=' * 100}")
    for j in range(start, end + 1):
        print(f"{j + 1:5} {lines[j]}")


if __name__ == "__main__":
    want = sys.argv[1:] or [t[0] for t in TARGETS]
    for name, rel, anchor in TARGETS:
        if name in want:
            dump(name, rel, anchor)
