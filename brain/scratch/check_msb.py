import re

with open('D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaStoneBreaker.java', 'r', encoding='utf-8') as f:
    c = f.read()

m = re.search(r'String\s*\[\]\s*\[\]\s*shapeMain\s*=\s*new\s+String\s*\[\]\s*\[\]\s*\{([\s\S]*?)\};', c)
aisles = re.findall(r'\{([^{}]*)\}', m.group(1))
print('shapeMain slices (Z in Java array):', len(aisles))
rows = re.findall(r'"([^"]*)"', aisles[0])
print('shapeMain rows in slice 0 (Y in Java array):', len(rows))
print('shapeMain string length (X in Java array):', len(rows[0]))

for l in c.splitlines():
    if 'horizontalOffSet' in l or 'verticalOffSet' in l or 'depthOffSet' in l:
        print('Offset line:', l.strip())
