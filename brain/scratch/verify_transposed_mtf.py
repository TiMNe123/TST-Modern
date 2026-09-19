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

# Transpose: new_matrix[y][z][x] = s[z][y][x]
# y goes 0 to 32 (33 aisles)
# z goes 0 to 44 (45 rows per aisle)
# x goes 0 to 32 (33 cols per row)

num_z = len(s) # 45
num_y = len(s[0]) # 33
num_x = len(s[0][0]) # 33

transposed = []
for y in range(num_y):
    aisle = []
    for z in range(num_z):
        row_chars = "".join(s[z][y][x] for x in range(num_x))
        aisle.append(row_chars)
    transposed.append(aisle)

print(f"Transposed GTCEu pattern: {len(transposed)} aisles, {len(transposed[0])} rows per aisle, {len(transposed[0][0])} cols")
print(f"Controller position in transposed pattern: aisle 7, row 38, col 16: '{transposed[7][38][16]}'")
print(f"Row 38 in aisle 7: {transposed[7][38]}")

# Count all symbols in transposed vs original
counts_orig = {}
for z in range(num_z):
    for y in range(num_y):
        for x in range(num_x):
            c = s[z][y][x]
            if c != ' ':
                counts_orig[c] = counts_orig.get(c, 0) + 1

counts_trans = {}
for y in range(num_y):
    for z in range(num_z):
        for x in range(num_x):
            c = transposed[y][z][x]
            if c != ' ':
                counts_trans[c] = counts_trans.get(c, 0) + 1

print("Counts match exactly:", counts_orig == counts_trans)
print("Symbol counts:", sorted(counts_trans.items()))
