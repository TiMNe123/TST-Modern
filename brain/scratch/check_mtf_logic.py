import io, sys

with open('D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaTreeFarm.java', 'r', encoding='utf-8') as f:
    content = f.read()

lines = content.splitlines()

# Search for methods in processing logic
print("=== Processing Logic & Recipe Methods ===")
for i, l in enumerate(lines):
    if any(k in l for k in ['checkRecipe', 'getRecipeMap', 'recipeMap', 'checkProcessing', 'modeMsg', 'machineMode', 'allProducts', 'TreeGrowth', 'AquaticZone', 'ArtificialGreenHouse']):
        print(f"Line {i+1}: {l.strip()}")
