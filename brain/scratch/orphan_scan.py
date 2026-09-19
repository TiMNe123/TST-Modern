"""Recheck: which TST-registered blocks/items have NO producing recipe.

Handles multi-line builders and Supplier-passed outputs by looking at the
enclosing recipe builder chain (blank-line / `.save(` delimited) rather than
one line at a time.
"""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RECIPE_DIR = ROOT / "src/main/java/com/tstmodern/data/recipe"
BLOCKS = ROOT / "src/main/java/com/tstmodern/registry/TSTBlocks.java"
ITEMS = ROOT / "src/main/java/com/tstmodern/registry/TSTItems.java"

# Collect the registered ids (the string names actually registered).
reg = {}
for f, kind in ((BLOCKS, "block"), (ITEMS, "item")):
    src = f.read_text(encoding="utf-8")
    for const, name in re.findall(
        r"RegistryObject<(?:Block|Item)> ([A-Z_0-9]+) =\s*(?:casing|item|blockWithItem|compressedCobble)\(\s*\"?([a-z_0-9]*)\"?",
        src,
    ):
        reg[const] = (kind, name)
# compressedCobble(N) has no string literal; rebuild those names
for i in range(1, 9):
    reg[f"COMPRESSED_COBBLESTONE_{i}"] = ("block", f"compressed_cobblestone_{i}")

recipe_src = {f.name: f.read_text(encoding="utf-8") for f in sorted(RECIPE_DIR.glob("*.java"))}
all_src = "\n".join(recipe_src.values())

# Split every recipe file into builder chunks ending at `.save(`.
chunks = []
for fname, src in recipe_src.items():
    for chunk in re.split(r"\.save\(provider\)\s*;?", src):
        chunks.append((fname, chunk))

def produced(const, name):
    """A block/item counts as produced if some builder chunk mentions it AND an output call."""
    hits = []
    for fname, chunk in chunks:
        if const not in chunk and f'"block/{name}"' not in chunk:
            continue
        if re.search(r"\.(outputItems|outputFluids|chancedOutput)\b", chunk):
            hits.append(fname)
    # helper-driven recipes: compressor()/crafting()/VanillaRecipeHelper take the RegistryObject as an arg
    if re.search(rf"(compressor|crafting|VanillaRecipeHelper\.addShapedRecipe)\([^;]*{const}", all_src, re.S):
        hits.append("helper")
    # DisassemblerRecipes DSL: recipe(..., block("name"), ...)
    if re.search(rf'recipe\(\s*"[^"]+",\s*RecipeType\.\w+,\s*(?:block|casing)\(\s*"?{name}"?', all_src):
        hits.append("DisassemblerRecipes.java")
    if re.search(rf'casing\("\s*\+?\s*tier', all_src) and name.startswith("component_assembly_line_casing_"):
        hits.append("DisassemblerRecipes.java(tiered)")
    return sorted(set(hits))

def consumed(const, name):
    hits = []
    for fname, chunk in chunks:
        if const not in chunk and f'"block/{name}"' not in chunk:
            continue
        if re.search(r"\.(inputItems|inputFluids|notConsumable|researchStack)\b", chunk):
            hits.append(fname)
    if re.search(rf'input\((?:block|casing)\(\s*"?{name}"?', all_src):
        hits.append("DisassemblerRecipes.java")
    return sorted(set(hits))

print(f"{'CONST':45} {'PRODUCED BY':45} CONSUMED BY")
orphans = []
for const, (kind, name) in sorted(reg.items()):
    p = produced(const, name)
    c = consumed(const, name)
    if not p:
        orphans.append(const)
    print(f"{const:45} {','.join(p) or '-- NONE --':45} {','.join(c) or '-'}")
print("\nORPHANS (no producing recipe):", len(orphans))
for o in orphans:
    print("  ", o)
