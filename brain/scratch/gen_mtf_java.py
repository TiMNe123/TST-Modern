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

# Generate Java code
out = []
out.append("        public static final MultiblockMachineDefinition MEGA_TREE_FARM = TSTModern.REGISTRATE")
out.append("                        .multiblock(\"mega_tree_farm\", MegaTreeFarmMachine::new)")
out.append("                        .langValue(\"Mega Tree Farm\")")
out.append("                        .rotationState(RotationState.NON_Y_AXIS)")
out.append("                        .recipeTypes(TSTRecipeTypes.TREE_GROWTH_SIMULATOR, TSTRecipeTypes.AQUATIC_ZONE_SIMULATOR)")
out.append("                        .recipeModifiers(MegaTreeFarmMachine::recipeModifier, GTRecipeModifiers.OC_NON_PERFECT)")
out.append("                        .appearanceBlock(TSTBlocks.STERILE_CASING)")
out.append("                        .pattern(definition -> FactoryBlockPattern.start(")
out.append("                                        RelativeDirection.RIGHT,")
out.append("                                        RelativeDirection.DOWN,")
out.append("                                        RelativeDirection.BACK)")

for y_idx, aisle in enumerate(transposed):
    out.append(f"                                        // === Aisle {y_idx} ===")
    out.append("                                        .aisle(")
    for r_idx, row in enumerate(aisle):
        comma = "," if r_idx < len(aisle) - 1 else ")"
        out.append(f"                                                        \"{row}\"{comma}")

out.append("                                        .where('~', Predicates.controller(blocks(definition.get())))")
out.append("                                        .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()).or(blocks(GTBlocks.FUSION_GLASS.get())))")
out.append("                                        .where('B', blocks(TSTBlocks.EXTREME_HEAT_RESISTANT_CASING.get()))")
out.append("                                        .where('C', blocks(TSTBlocks.SUPERCONDUCTING_MAGNETIC_CASING.get()))")
out.append("                                        .where('D', blocks(GTBlocks.CASING_BRONZE_BRICKS.get()).or(blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())))")
out.append("                                        .where('E', blocks(TSTBlocks.ADVANCED_RADIATION_PROOF_CASING.get()))")
out.append("                                        .where('F', blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get()))")
out.append("                                        .where('G', blocks(TSTBlocks.VENT_T2_CASING.get()))")
out.append("                                        .where('H', blocks(TSTBlocks.STERILE_CASING.get()))")
out.append("                                        .where('I', blocks(TSTBlocks.INTEGRAL_FRAMEWORK_UV_CASING.get()))")
out.append("                                        .where('J', blocks(TSTBlocks.ARCANE_TRANSLUCENT_CASING.get()))")
out.append("                                        .where('K', blocks(TSTBlocks.AIR_CRYSTAL_CASING.get()))")
out.append("                                        .where('L', blocks(TSTBlocks.WATER_CRYSTAL_CASING.get()))")
out.append("                                        .where('M', blocks(TSTBlocks.EARTH_CRYSTAL_CASING.get()))")
out.append("                                        .where('N', blocks(TSTBlocks.CULTIVATION_SOIL_CASING.get()))")
out.append("                                        .where('O', blocks(GTBlocks.LAMPS.get(DyeColor.PURPLE).get()))")
out.append("                                        .where('P', blocks(Blocks.WATER))")
out.append("                                        .where('Q', blocks(TSTBlocks.STERILE_CASING.get())")
out.append("                                                        .or(Predicates.autoAbilities(definition.getRecipeTypes()))")
out.append("                                                        .or(Predicates.autoAbilities(true, false, false))")
out.append("                                                        .or(Predicates.autoAbilities(false, false, true)))")
out.append("                                        .where('R', blocks(TSTBlocks.STERILE_CASING.get())")
out.append("                                                        .or(Predicates.autoAbilities(true, false, false)))")
out.append("                                        .where('S', Predicates.frames(GTMaterials.Mithril).or(Predicates.frames(GTMaterials.Silver)).or(Predicates.frames(GTMaterials.Steel)))")
out.append("                                        .where(' ', Predicates.any())")
out.append("                                        .build())")
out.append("                        .workableCasingModel(")
out.append("                                        TSTModern.id(\"block/casings/sterile_casing\"),")
out.append("                                        TSTModern.id(\"block/multiblock/mega_tree_farm\"))")
out.append("                        .tooltips(")
out.append("                                        Component.translatable(\"tstmodern.machine.mega_tree_farm.tooltip.0\"),")
out.append("                                        Component.translatable(\"tstmodern.machine.mega_tree_farm.tooltip.1\"),")
out.append("                                        Component.translatable(\"tstmodern.machine.mega_tree_farm.tooltip.2\"),")
out.append("                                        Component.translatable(\"tstmodern.machine.mega_tree_farm.tooltip.3\"),")
out.append("                                        Component.translatable(\"tstmodern.machine.mega_tree_farm.tooltip.4\"))")
out.append("                        .register();")

code = "\n".join(out)
with open('brain/scratch/mega_tree_farm_code.java', 'w', encoding='utf-8') as f:
    f.write(code)

print(f"Generated {len(out)} lines of Java code for MEGA_TREE_FARM")
