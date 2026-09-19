"""Extract the controller (machine-item) recipe of every ported TST-Modern multiblock
and classify each ingredient as GTCEu-native or TST-custom.

Controller recipes are located by the recipe id used in the builder call, then the
builder chain is read until `.save(provider)`.
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RECIPE_DIR = ROOT / "src/main/java/com/tstmodern/data/recipe"

# machine display name -> (recipe file, recipe-id fragment that outputs the controller)
CONTROLLERS = [
    ("Giant Vacuum Drying Furnace", "GiantVacuumDryingFurnaceRecipes.java",
     "assembly_line/giant_vacuum_drying_furnace"),
    ("Nether Interface", "NetherInterfaceRecipes.java", "assembler/nether_interface"),
    ("Hyper Thermal Convector", "HyperThermalConvectorRecipes.java",
     "assembly_line/hyper_thermal_convector"),
    ("Mega Tree Farm", "MegaTreeFarmRecipes.java", "assembly_line/mega_tree_farm"),
    ("Mega Stone Breaker", "MegaStoneBreakerRecipes.java", "assembly_line/mega_stone_breaker"),
    ("Incompact Cyclotron", "IncompactCyclotronRecipes.java", "incompact_cyclotron"),
    ("Big Bro Array", "BigBroArrayRecipes.java", "big_bro_array"),
]

ING = re.compile(r"\.(inputItems|inputFluids|notConsumable)\s*\((.*)")
META = re.compile(r"\.(duration|EUt|scannerResearch|stationResearch|researchStack|CWUt)\b")


def block_of(text, anchor):
    """Return the builder chain starting at `anchor` up to and including `.save(`."""
    i = text.index(anchor)
    start = text.rfind("\n", 0, text.rfind("recipeBuilder", 0, i + len(anchor)))
    end = text.index(".save(provider)", i)
    return text[start:end]


def main():
    out = []
    for machine, fname, frag in CONTROLLERS:
        text = (RECIPE_DIR / fname).read_text(encoding="utf-8")
        chain = block_of(text, frag)
        base_line = text[:text.index(frag)].count("\n") + 1
        ings = []
        for off, line in enumerate(chain.splitlines()):
            m = ING.search(line)
            if not m:
                continue
            ings.append({
                "kind": m.group(1),
                "raw": " ".join(line.split()),
            })
        meta = [" ".join(l.split()) for l in chain.splitlines() if META.search(l)]
        out.append({"machine": machine, "file": fname, "line": base_line,
                    "ingredients": ings, "meta": meta})

    (ROOT / "brain/scratch/controllers.json").write_text(
        json.dumps(out, indent=1, ensure_ascii=False), encoding="utf-8")

    for c in out:
        print(f"\n===== {c['machine']}  ({c['file']}:{c['line']})  [{len(c['ingredients'])} inputs]")
        for i in c["ingredients"]:
            tst = "TST" if "TST" in i["raw"] else "GT "
            print(f"  {tst} {i['raw']}")
        for m in c["meta"]:
            print(f"      · {m}")


if __name__ == "__main__":
    main()
