import io, sys, re

with open('D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaTreeFarm.java', 'r', encoding='utf-8') as f:
    content = f.read()

m_shape = re.search(r'String\s*\[\]\s*\[\]\s*shape\s*=\s*new\s+String\s*\[\]\s*\[\]\s*\{([\s\S]*?)\};', content)
m_shape2 = re.search(r'String\s*\[\]\s*\[\]\s*shape2\s*=\s*new\s+String\s*\[\]\s*\[\]\s*\{([\s\S]*?)\};', content)

def parse_shape(s):
    aisles = re.findall(r'\{([^{}]*)\}', s)
    parsed = []
    for a in aisles:
        rows = [x for x in re.findall(r'"([^"]*)"', a)]
        parsed.append(rows)
    return parsed

s1 = parse_shape(m_shape.group(1))
s2 = parse_shape(m_shape2.group(1))

print(f'Shape 1 (shape): {len(s1)} aisles, {len(s1[0])} rows, {len(s1[0][0])} cols')
print(f'Shape 2 (shape2): {len(s2)} aisles, {len(s2[0])} rows, {len(s2[0][0])} cols')

diff_map = {}
for a_idx in range(len(s1)):
    for r_idx in range(len(s1[a_idx])):
        for c_idx in range(len(s1[a_idx][r_idx])):
            c1 = s1[a_idx][r_idx][c_idx]
            c2 = s2[a_idx][r_idx][c_idx]
            if c1 != c2:
                diff_map[(c1, c2)] = diff_map.get((c1, c2), 0) + 1

print('Difference mapping (c1 in shape -> c2 in shape2):')
for k, v in sorted(diff_map.items()):
    print(f"'{k[0]}' in shape -> '{k[1]}' in shape2: {v} occurrences")
