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

# Let's inspect the top arch:
# In 3D structure:
# Height is Y in GTCEu (which was Z in Java shape, from 0 to 44).
# Z=0 is bottom or top?
# Let's check Z=0 vs Z=44:
print("=== Z=0 (Top or bottom?) ===")
for r in s[0][:10]:
    if any(c != ' ' for c in r):
        print(r)

print("=== Z=1 ===")
for r in s[1][:10]:
    if any(c != ' ' for c in r):
        print(r)

print("=== Z=2 ===")
for r in s[2][:10]:
    if any(c != ' ' for c in r):
        print(r)

print("=== Z=3 ===")
for r in s[3][:10]:
    if any(c != ' ' for c in r):
        print(r)

print("=== Z=4 ===")
for r in s[4][:10]:
    if any(c != ' ' for c in r):
        print(r)
