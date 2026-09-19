"""Scan TST-Modern for cross-machine dependencies on TST-custom blocks/items/materials.

Produces JSON: for each custom symbol, who produces it (recipe file = owning machine),
who consumes it as a recipe input, and which machine structures require it.
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RECIPE_DIR = ROOT / "src/main/java/com/tstmodern/data/recipe"
MACHINE_DEF_DIR = ROOT / "src/main/java/com/tstmodern/registry/machine"
MACHINE_DIR = ROOT / "src/main/java/com/tstmodern/machine"

SYM = re.compile(r"TST(?:Blocks|Items|Materials)\.([A-Z][A-Z_0-9]*)")
OUT = re.compile(r"\.(outputItems|outputFluids|chancedOutput)\b")
IN = re.compile(r"\.(inputItems|inputFluids|notConsumable)\b")

# also track native GT symbols that TST files bridge across machines
NATIVE = ["UUMatter"]


def collect(path, kinds):
    """Return {symbol: set(context)} for lines matching the given regexes."""
    hits = {}
    text = path.read_text(encoding="utf-8").splitlines()
    for n, line in enumerate(text, 1):
        for kind, rx in kinds.items():
            if not rx.search(line):
                continue
            names = SYM.findall(line)
            for nat in NATIVE:
                if f"GTMaterials.{nat}" in line:
                    names.append(nat)
            for name in names:
                hits.setdefault(name, []).append((kind, f"{path.name}:{n}"))
    return hits


def main():
    produced, consumed, structure = {}, {}, {}

    for f in sorted(RECIPE_DIR.glob("*.java")):
        for name, refs in collect(f, {"out": OUT}).items():
            produced.setdefault(name, []).extend(r[1] for r in refs)
        for name, refs in collect(f, {"in": IN}).items():
            consumed.setdefault(name, []).extend(r[1] for r in refs)

    # structures: any TSTBlocks reference inside a machine definition / structure file
    for f in sorted(list(MACHINE_DEF_DIR.glob("*.java")) + list(MACHINE_DIR.rglob("*.java"))):
        text = f.read_text(encoding="utf-8").splitlines()
        for n, line in enumerate(text, 1):
            for name in SYM.findall(line):
                structure.setdefault(name, []).append(f"{f.name}:{n}")

    names = sorted(set(produced) | set(consumed) | set(structure))
    report = {}
    for name in names:
        report[name] = {
            "produced_by": sorted(set(produced.get(name, []))),
            "consumed_by": sorted(set(consumed.get(name, []))),
            "structure_use": sorted(set(structure.get(name, []))),
        }
    print(json.dumps(report, indent=1))


if __name__ == "__main__":
    main()
