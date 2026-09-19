"""Parse the TST 1.7.10 controller-crafting recipe of each machine still to be ported
and emit one normalised ingredient row per input.

Reads D:/tmp/TST (local clone of Nxer/Twist-Space-Technology-Mod). No ingredient text
is hand-typed: the display column is the cleaned Java expression from source, the
quantity is parsed from it, and the GTCEu verdict comes from TOKEN_INFO which was
built from constant-pool scans of gtceu-1.20.1-7.4.0-slim.jar
(brain/scratch/check_gtceu_availability.py + check_gtceu_jarwide.py).
"""
import json
import re
from pathlib import Path

SRC = Path("D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology")
OUT = Path(__file__).with_name("port_ingredients.json")

# machine, tier (from Excel 'Danh sách máy'), source file, anchor line of .itemOutputs
TARGETS = [
    ("LargeNeutronOscillator", "UIV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3150),
    ("MegaNqReactor", "UIV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3739),
    ("DSPLauncher", "UMV", "recipe/machineRecipe/expanded/DSPRecipePool.java", 175),
    ("DSPReceiver", "UMV", "recipe/machineRecipe/expanded/DSPRecipePool.java", 140),
    ("MicroSpaceTimeFabricatorio", "UMV", "recipe/craftRecipe/item/CosmicProcessorCircuitRecipes.java", 455),
    ("MiracleTop", "UMV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 698),
    ("ArtificialStar", "UXV", "recipe/machineRecipe/expanded/DSPRecipePool.java", 249),
    ("DeployedNanoCore", "UXV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 2039),
    ("DimensionallyTranscendentMatterPlasmaForgePrototypeMK2", "UXV",
     "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3130),
    ("MassFabricatorGenesis", "UXV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3216),
    ("SuperWaterPurifier", "UXV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3634),
    ("StrangeMatterAggregator", "MAX", "recipe/machineRecipe/expanded/DSPRecipePool.java", 918),
    ("AstralComputingArray", "UEV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 2071),
    ("LargeIndustrialCokingFactory", "UHV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 1403),
    ("OreProcessingFactory", "UEV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 1273),
    ("StarcoreMiner", "UIV", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 2238),
    ("MeteorMiner", "LuV*", "recipe/craftRecipe/machine/GTCMMachineRecipes.java", 3461),
]

BUILDER_START = re.compile(r"stdBuilder\(\)|TST_RecipeBuilder|\.builder\(\)")
NUM = re.compile(r"^[\d_\s*+]+$")


def split_args(text):
    """Split a Java argument list on top-level commas."""
    out, depth, cur, instr = [], 0, [], False
    for ch in text:
        if instr:
            cur.append(ch)
            if ch == '"':
                instr = False
            continue
        if ch == '"':
            instr = True
            cur.append(ch)
        elif ch in "([{":
            depth += 1
            cur.append(ch)
        elif ch in ")]}":
            depth -= 1
            cur.append(ch)
        elif ch == "," and depth == 0:
            out.append("".join(cur).strip())
            cur = []
        else:
            cur.append(ch)
    if "".join(cur).strip():
        out.append("".join(cur).strip())
    return [a for a in out if a]


def grab_call(block, name):
    """Return the argument text of `.name(...)` from a builder chain, or ''."""
    i = block.find("." + name + "(")
    if i < 0:
        return ""
    j = i + len(name) + 2
    depth, start = 1, j
    while depth and j < len(block):
        if block[j] in "([{":
            depth += 1
        elif block[j] in ")]}":
            depth -= 1
        j += 1
    return block[start:j - 1]


WATER_GRADE_COUNT = 100_000_000  # GTCMMachineRecipes.java:3610


def evalnum(expr):
    # SuperWaterPurifier uses a local constant for every purified-water grade
    expr = expr.replace("waterGradeCount", str(WATER_GRADE_COUNT))
    expr = expr.replace("_", "").strip()
    if not re.match(r"^[\d\s*+/]+$", expr):
        return None
    try:
        return int(eval(expr, {"__builtins__": {}}, {}))  # digits and * + / only
    except Exception:
        return None


def qty_of(arg):
    """Best-effort quantity for one ingredient expression."""
    m = re.search(r"getCircuits\(\s*Materials\.(\w+)\s*,\s*([\d_]+)\s*\)", arg)
    if m:
        return evalnum(m.group(2))
    m = re.search(r"copyAmountUnsafe\(\s*([\d_]+)\s*,", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"setStackSize\(.*,\s*([\d_]+)\s*\)\s*$", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"OrePrefixes\.\w+\.get\([^)]*\)\s*,\s*([\d_]+)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"GTOreDictUnificator\.get\([^,]+,[^,]+,\s*([\d_]+)\s*\)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"getNanite\(\s*([\d_]+)\s*\)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"new ItemStack\([^,]+,\s*([\d_]+)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"getModItem\([^,]+,[^,]+,\s*([\d_]+)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"\.get\(\s*([\d_]+)\s*\)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"getIntegratedCircuit\(\s*(\d+)\s*\)", arg)
    if m:
        return f"circuit meta {m.group(1)}"
    return None


def fluid_qty(arg):
    m = re.search(r"(?:getFluid|getMolten|getPlasma|getFluidStack|getGas)\(\s*([\w\s*+/_]+?)\s*\)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"new FluidStack\([^,]+,\s*([\w\s*+/_]+?)\s*\)", arg)
    if m:
        return evalnum(m.group(1))
    m = re.search(r"getFluidStack\(\s*\"[^\"]+\"\s*,\s*([\w\s*+/_]+?)\s*\)", arg)
    if m:
        return evalnum(m.group(1))
    return None


def clean(arg):
    return " ".join(arg.split())


def token_of(arg):
    """Key used to classify the ingredient: '<holder>.<name>' or a bare name."""
    for rx in (
        r"getCircuits\(\s*Materials\.(\w+)\s*,",
        r"OrePrefixes\.(\w+)\.get\(Materials\.(\w+)\)",
        r"GTOreDictUnificator\.get\(OrePrefixes\.(\w+),\s*(?:Materials|MaterialsUEVplus)\.(\w+)",
        r"(GTCMItemList)\.(\w+)",
        r"(NHItemList)\.(\w+)",
        r"(ItemRefer)\.(\w+)",
        r"(MaterialsElements\.STANDALONE)\.(\w+)",
        r"(MaterialsAlloy)\.(\w+)",
        r"(GGMaterial)\.(\w+)",
        r"(Materials)\.(\w+)\.get(?:Fluid|Molten|Plasma|Nanite|Gas)",
        r"(Materials)\.(\w+)\.getFluidStack",
        r"(ItemList)\.(\w+)",
        r"(GCBlocks)\.(\w+)",
        r"(GregTechAPI)\.(\w+)",
        r"(TTCasingsContainer)\.(\w+)",
        r"(Loaders)\.(\w+)",
        r"(Mods)\.(\w+)",
        r"copyAmountUnsafe\(\s*[\d_]+\s*,\s*(?:Materials\.)?(\w+)",
        r"()\b(sBlockCasingsSE|sBlockCasingsDyson|solderPlasma|MUTATED_LIVING_SOLDER)\b",
        r"()\b(eM_\w+|Machine_Multi_\w+|HiC_T\d|ZPM\d|OpticalSOC|voidminer|"
        r"Laser_Lens_Special|Industrial_MassFab|Component_Assembly_Line|"
        r"HighEnergyFlowCircuit|AdvancedHighPowerCoilBlock|eternal_singularity|"
        r"LightWeightPlate|ItemMiningDrones)\b",
        r"()\b([A-Z][A-Za-z_]{3,})\b",
    ):
        m = re.search(rx, arg)
        if m:
            groups = [g for g in m.groups() if g]
            return ".".join(groups) if len(groups) > 1 else groups[0]
    return clean(arg)[:40]


def parse(name, tier, rel, anchor):
    lines = (SRC / rel).read_text(encoding="utf-8", errors="replace").splitlines()
    i = anchor - 1
    start = next((j for j in range(i, max(0, i - 70), -1) if BUILDER_START.search(lines[j])), i)
    end = next((j for j in range(i, min(len(lines), i + 40))
                if ".addTo(" in lines[j] or ".buildAndRegister(" in lines[j]), i)
    block = "\n".join(lines[start:end + 1])
    # strip // comments so commented-out inputs are not counted
    block = "\n".join(l.split("//")[0] for l in block.splitlines())

    rows = []
    for arg in split_args(grab_call(block, "itemInputs")):
        rows.append({"kind": "item", "raw": clean(arg), "token": token_of(arg), "qty": qty_of(arg)})
    for arg in split_args(grab_call(block, "fluidInputs")):
        rows.append({"kind": "fluid", "raw": clean(arg), "token": token_of(arg), "qty": fluid_qty(arg)})

    meta = grab_call(block, "metadata")
    research = clean(meta) if "RESEARCH_ITEM" in meta else ""
    eut = clean(grab_call(block, "eut"))
    dur = clean(grab_call(block, "duration"))
    addto = clean(grab_call(block, "addTo"))
    return {"machine": name, "tier": tier, "file": rel, "lines": f"{start + 1}-{end + 1}",
            "eut": eut, "duration": dur, "addTo": addto, "research": research, "rows": rows}


def main():
    data = [parse(*t) for t in TARGETS]
    OUT.write_text(json.dumps(data, indent=1, ensure_ascii=False), encoding="utf-8")
    tokens = {}
    for d in data:
        for r in d["rows"]:
            tokens.setdefault(r["token"], 0)
            tokens[r["token"]] += 1
        print(f"{d['machine'][:44]:46} {len(d['rows']):3} inputs   {d['addTo'][:34]:36} "
              f"{d['eut'][:18]:20} {d['duration'][:14]}")
    print(f"\ntotal rows: {sum(len(d['rows']) for d in data)}")
    print(f"distinct tokens: {len(tokens)}")
    for t, n in sorted(tokens.items(), key=lambda kv: -kv[1]):
        print(f"  {n:3}  {t}")


if __name__ == "__main__":
    main()
