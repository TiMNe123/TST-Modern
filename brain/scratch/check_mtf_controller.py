import io, sys, re

with open('D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaTreeFarm.java', 'r', encoding='utf-8') as f:
    content = f.read()

m_shape = re.search(r'String\s*\[\]\s*\[\]\s*shape\s*=\s*new\s+String\s*\[\]\s*\[\]\s*\{([\s\S]*?)\};', content)

def parse_shape(s):
    aisles = re.findall(r'\{([^{}]*)\}', s)
    parsed = []
    for a in aisles:
        rows = [x for x in re.findall(r'"([^"]*)"', a)]
        parsed.append(rows)
    return parsed

s = parse_shape(m_shape.group(1))

# In TST:
# shape is 45 layers, each layer has 33 rows, each row has 33 chars.
# transpose(shape) in StructureLib transposes the 3D matrix.
# In StructureLib:
# shape[Z_orig][Y_orig][X_orig]
# transpose(shape) produces a transposed array where:
# aisle = Y_orig (33 aisles)
# row = Z_orig (45 rows per aisle)
# col = X_orig (33 cols per row)
# OR aisle = X_orig (33 aisles), row = Z_orig (45 rows), col = Y_orig (33 cols)?

# Let's check the offset (16, 38, 7):
# In TST buildPiece:
# offset is (x=16, y=38, z=7) relative to the controller.
# At offset, the block is the controller!
# Let's check what character is at various index interpretations:

print("Checking character at coordinates:")
# If shape[layer=45][row=33][col=33]:
# Option A: layer=7, row=38 (invalid since row max is 32)
# Option B: layer=38, row=7, col=16:
print(f"shape[38][7][16] = '{s[38][7][16]}'")
# Option C: layer=38, row=16, col=7:
print(f"shape[38][16][7] = '{s[38][16][7]}'")
# Option D: layer=7, row=16, col=38 (invalid since col max is 32)

print("\nSurrounding blocks around shape[38][7][16]:")
for dy in range(5, 10):
    row = s[38][dy]
    print(f"layer 38, row {dy:2d}: {row[10:23]} (center at 16 is '{row[16]}')")
