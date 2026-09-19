"""Line-level cross-machine dependency scan for TST-Modern custom blocks/items/fluids.

In this codebase every recipe builder call keeps the symbol on the same line as
the input/output method, so line-level attribution is exact. Symbol names are
matched with word boundaries (avoids NEUTRON matching NEUTRONIUM).
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RECIPE_DIR = ROOT / "src/main/java/com/tstmodern/data/recipe"
SCAN_DIRS = [
    ROOT / "src/main/java/com/tstmodern/registry/machine",
    ROOT / "src/main/java/com/tstmodern/machine",
]

SYM = re.compile(r"\bTST(?:Blocks|Items|Materials)\.([A-Z][A-Z_0-9]*)\b")
NATIVE = re.compile(r"\bGTMaterials\.(UUMatter)\b")
OUT = re.compile(r"\.(outputItems|outputFluids|chancedOutput)\s*\(")
INP = re.compile(r"\.(inputItems|inputFluids|notConsumable)\s*\(")
RESEARCH = re.compile(r"\.researchStack\s*\(")

MACHINE = {
    "BigBroArrayRecipes.java": "Big Bro Array",
    "DisassemblerRecipes.java": "Disassembler",
    "GiantVacuumDryingFurnaceRecipes.java": "Giant Vacuum Drying Furnace",
    "HyperThermalConvectorRecipes.java": "Hyper Thermal Convector",
    "IncompactCyclotronRecipes.java": "Incompact Cyclotron",
    "MassFabricatorRecipes.java": "Mass Fabricator",
    "MegaStoneBreakerRecipes.java": "Mega Stone Breaker",
    "MegaTreeFarmRecipes.java": "Mega Tree Farm",
    "NetherInterfaceRecipes.java": "Nether Interface",
    "BigBroArrayDefinition.java": "Big Bro Array",
    "DisassemblerDefinition.java": "Disassembler",
    "GiantVacuumDryingFurnaceDefinition.java": "Giant Vacuum Drying Furnace",
    "HyperThermalConvectorDefinition.java": "Hyper Thermal Convector",
    "IncompactCyclotronDefinition.java": "Incompact Cyclotron",
    "MegaStoneBreakerDefinition.java": "Mega Stone Breaker",
    "MegaTreeFarmDefinition.java": "Mega Tree Farm",
    "NetherInterfaceDefinition.java": "Nether Interface",
}


def machine_of(fname):
    return MACHINE.get(fname, fname)


def scan():
    data = {}

    def note(sym, bucket, fname, lineno, text):
        e = data.setdefault(sym, {"produced": [], "consumed": [], "research": [], "structure": []})
        e[bucket].append({"machine": machine_of(fname), "at": f"{fname}:{lineno}", "line": text.strip()})

    for f in sorted(RECIPE_DIR.glob("*.java")):
        for n, line in enumerate(f.read_text(encoding="utf-8").splitlines(), 1):
            syms = SYM.findall(line) + NATIVE.findall(line)
            if not syms:
                continue
            for s in syms:
                if OUT.search(line):
                    note(s, "produced", f.name, n, line)
                if INP.search(line):
                    note(s, "consumed", f.name, n, line)
                if RESEARCH.search(line):
                    note(s, "research", f.name, n, line)

    for d in SCAN_DIRS:
        for f in sorted(d.rglob("*.java")):
            for n, line in enumerate(f.read_text(encoding="utf-8").splitlines(), 1):
                for s in SYM.findall(line):
                    e = data.setdefault(s, {"produced": [], "consumed": [], "research": [], "structure": []})
                    e["structure"].append({"machine": machine_of(f.name), "at": f"{f.name}:{n}",
                                           "line": line.strip()})
    return data


def main():
    data = scan()
    (ROOT / "brain/scratch/xref2.json").write_text(json.dumps(data, indent=1), encoding="utf-8")

    print("=== CROSS-MACHINE EDGES (producer machine != consumer/structure machine) ===")
    for sym in sorted(data):
        e = data[sym]
        prod = {r["machine"] for r in e["produced"]}
        cons = {r["machine"] for r in e["consumed"]}
        struct = {r["machine"] for r in e["structure"]}
        cross_cons = cons - prod
        cross_struct = struct - prod
        if not (cross_cons or cross_struct):
            continue
        print(f"\n{sym}")
        print(f"  produced by : {sorted(prod) or '-- none --'}")
        if cross_cons:
            print(f"  recipe input in (other machines): {sorted(cross_cons)}")
            for r in e["consumed"]:
                if r["machine"] in cross_cons:
                    print(f"      {r['at']}  {r['line'][:110]}")
        if cross_struct:
            print(f"  structure block of (other machines): {sorted(cross_struct)}")


if __name__ == "__main__":
    main()
