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

s1 = parse_shape(m_shape.group(1))

# TST uses transpose(shape)
# Original shape array in Java: shape[Z][Y] has String of length X (33)
# There are 45 aisles in shape, 33 rows per aisle, each row has 33 chars.
# In StructureLib: transpose(shape) means:
# shape[Z][Y][X] -> transposed:
# Let's check StructureLib transpose:
# transpose(String[][] structure): structure[slice][row][col] -> structure[col][row][slice] or structure[row][slice][col]

print(f"Number of outer arrays (Z in shape): {len(s1)}")
print(f"Number of strings in each outer array (Y in shape): {len(s1[0])}")
print(f"Length of each string (X in shape): {len(s1[0][0])}")

# Let's check the center of shape:
# Middle of 33 is 16. Middle of 45 is 22.
# Let's check what is at various coordinates:
for z in range(len(s1)):
    row_str = s1[z][16] # middle row Y=16
    # print non-empty slices
    if any(c != ' ' for c in row_str):
        print(f"Z={z:2d}, Y=16: {row_str}")
