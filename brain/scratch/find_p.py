import re

with open('D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaTreeFarm.java', 'r', encoding='utf-8') as f:
    content = f.read()

m_shape = re.search(r'String\s*\[\]\s*\[\]\s*shape\s*=\s*new\s+String\s*\[\]\s*\[\]\s*\{([\s\S]*?)\};', content)

aisles = re.findall(r'\{([^{}]*)\}', m_shape.group(1))
p_count = 0
for z_idx, a in enumerate(aisles):
    rows = re.findall(r'"([^"]*)"', a)
    for y_idx, r in enumerate(rows):
        if 'P' in r:
            print(f'P found in shape: Z={z_idx}, Y={y_idx}: {r}')
            p_count += 1

print('Total rows with P in shape:', p_count)
