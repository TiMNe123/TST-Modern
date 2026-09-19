with open('src/main/java/com/tstmodern/registry/TSTMachines.java', 'r', encoding='utf-8') as f:
    c = f.read()

start = c.find('MEGA_TREE_FARM =')
end = c.find('.register();', start)
section = c[start:end]

chars_in_aisles = set()
for line in section.splitlines():
    line = line.strip()
    if line.startswith('"') and (line.endswith('",') or line.endswith('")')):
        s = line[1:-2] if line.endswith('",') else line[1:-2]
        for ch in s:
            chars_in_aisles.add(ch)

print('Unique characters in MEGA_TREE_FARM pattern aisles:')
for ch in sorted(chars_in_aisles):
    print(repr(ch))

# Also check 1.7.10 source
with open('D:/tmp/TST/src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_MegaTreeFarm.java', 'r', encoding='utf-8') as f:
    c_17 = f.read()

print('\n1.7.10 where elements defined:')
for line in c_17.splitlines():
    if '.addElement(' in line:
        print(line.strip())
