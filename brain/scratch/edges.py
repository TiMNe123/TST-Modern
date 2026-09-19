"""Final cross-machine dependency scan (DSL-aware) for TST-Modern.

Resolves three indirection styles this codebase uses:
 1. direct  : `.inputItems(TSTBlocks.VACUUM_CASING...)`
 2. Disassembler string DSL: recipe(..., block("x"), ...) / input(block("x")) / fluid("x")
 3. helper params: addRapidHeatExchangePair(..., MAT.getFluid(n)), compressor()/crafting()
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RECIPE_DIR = ROOT / "src/main/java/com/tstmodern/data/recipe"
DEF_DIR = ROOT / "src/main/java/com/tstmodern/registry/machine"
LOGIC_DIR = ROOT / "src/main/java/com/tstmodern/machine"

MACHINE = {
    "BigBroArray": "Big Bro Array",
    "Disassembler": "Disassembler",
    "GiantVacuumDryingFurnace": "Giant Vacuum Drying Furnace",
    "HyperThermalConvector": "Hyper Thermal Convector",
    "IncompactCyclotron": "Incompact Cyclotron",
    "MassFabricator": "Mass Fabricator",
    "MegaStoneBreaker": "Mega Stone Breaker",
    "MegaTreeFarm": "Mega Tree Farm",
    "NetherInterface": "Nether Interface",
}


def machine_of(fname):
    stem = fname.replace("Recipes.java", "").replace("Definition.java", "") \
                .replace("Structure.java", "").replace("Machine.java", "").replace(".java", "")
    return MACHINE.get(stem, stem)


SYM = re.compile(r"\bTST(?:Blocks|Items|Materials)\.([A-Z][A-Z_0-9]*)\b")
UU = re.compile(r"\bGTMaterials\.(UUMatter)\b")
OUT = re.compile(r"\.(outputItems|outputFluids|chancedOutput)\s*\(")
INP = re.compile(r"\.(inputItems|inputFluids|notConsumable)\s*\(")
RES = re.compile(r"\.researchStack\s*\(")

# DisassemblerRecipes DSL: material key -> canonical symbol name
DSL_MATERIAL = {"uu_matter": "UUMatter"}

edges = []   # (symbol, role, machine, location, snippet)


def add(sym, role, fname, line_no, text):
    edges.append({
        "symbol": sym, "role": role, "machine": machine_of(fname),
        "at": f"{fname}:{line_no}", "snippet": " ".join(text.split())[:150],
    })


def scan_recipes():
    for f in sorted(RECIPE_DIR.glob("*.java")):
        lines = f.read_text(encoding="utf-8").splitlines()
        for n, line in enumerate(lines, 1):
            syms = SYM.findall(line) + UU.findall(line)
            for s in syms:
                if OUT.search(line):
                    add(s, "produces", f.name, n, line)
                if INP.search(line):
                    add(s, "consumes", f.name, n, line)
                if RES.search(line):
                    add(s, "research", f.name, n, line)

            # Style 3a: helper call passing a Material fluid as an OUTPUT argument
            if "addRapidHeatExchangePair(" in line or (
                    f.name == "HyperThermalConvectorRecipes.java" and re.search(r"TSTMaterials\.\w+\.getFluid", line)
                    and not INP.search(line) and not OUT.search(line)):
                for s in SYM.findall(line):
                    add(s, "produces", f.name, n, line)

            # Style 3b: MegaStoneBreaker helpers -- output is the 3rd/2nd arg
            m = re.search(r"\b(compressor|crafting|basic)\(", line)
            if m:
                for s in SYM.findall(line):
                    add(s, "produces" if "COMPRESSED_COBBLESTONE" in s else "consumes", f.name, n, line)

            # Style 2: Disassembler string DSL
            if f.name == "DisassemblerRecipes.java":
                for key, amount in re.findall(r'fluid\(\s*"([a-z_0-9]+)"\s*,\s*([0-9_]+)', line):
                    sym = DSL_MATERIAL.get(key)
                    if sym:
                        add(sym, "consumes", f.name, n, line)
                for key in re.findall(r'recipe\(\s*"[^"]+",\s*RecipeType\.\w+,\s*block\(\s*"([a-z_0-9]+)"', line):
                    add(key.upper(), "produces", f.name, n, line)
                for key in re.findall(r'input\(\s*block\(\s*"([a-z_0-9]+)"', line):
                    add(key.upper(), "consumes", f.name, n, line)
                if re.search(r"RecipeType\.\w+,\s*casing\(tier\)", line) or "casing(tier), 1," in line:
                    add("COMPONENT_ASSEMBLY_LINE_CASING_*", "produces", f.name, n, line)


def scan_structures():
    for d in (DEF_DIR, LOGIC_DIR):
        for f in sorted(d.rglob("*.java")):
            for n, line in enumerate(f.read_text(encoding="utf-8").splitlines(), 1):
                for s in SYM.findall(line):
                    add(s, "structure", f.name, n, line)


def main():
    scan_recipes()
    scan_structures()
    (ROOT / "brain/scratch/edges.json").write_text(json.dumps(edges, indent=1), encoding="utf-8")

    by_sym = {}
    for e in edges:
        by_sym.setdefault(e["symbol"], []).append(e)

    print("=== SPECIAL (cross-machine) DEPENDENCIES ===")
    rows = []
    for sym in sorted(by_sym):
        es = by_sym[sym]
        prod = sorted({e["machine"] for e in es if e["role"] == "produces"})
        cons = sorted({e["machine"] for e in es if e["role"] == "consumes"})
        struct = sorted({e["machine"] for e in es if e["role"] == "structure"})
        cross_c = [m for m in cons if m not in prod]
        cross_s = [m for m in struct if m not in prod and m in MACHINE.values()]
        if not (cross_c or cross_s):
            continue
        rows.append((sym, prod, cross_c, cross_s))
        print(f"\n{sym}")
        print(f"   producer : {prod or 'NONE (unobtainable)'}")
        if cross_c:
            print(f"   consumer : {cross_c}")
            for e in es:
                if e["role"] == "consumes" and e["machine"] in cross_c:
                    print(f"        {e['at']}  {e['snippet']}")
        if cross_s:
            print(f"   structure: {cross_s}")
    print(f"\ntotal cross-machine symbols: {len(rows)}")


if __name__ == "__main__":
    main()
