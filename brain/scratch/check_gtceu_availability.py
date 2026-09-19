"""Check which TST 1.7.10 ingredient materials/items exist in GTCEu 7.4.0.

Reads the CONSTANT_Utf8 pool of the relevant GTCEu classes and reports each
candidate name as present/absent, so the Excel annotation column states facts
rather than guesses.
"""
import struct
import zipfile

JAR = (r"C:/Users/mtien/.gradle/caches/modules-2/files-2.1/com.gregtechceu.gtceu"
       r"/gtceu-1.20.1/7.4.0/7a1e85994ff9add24d399ec6b64a087df0adc798"
       r"/gtceu-1.20.1-7.4.0-slim.jar")

MATERIAL_CLASSES = [
    "com/gregtechceu/gtceu/common/data/GTMaterials.class",
    "com/gregtechceu/gtceu/common/data/materials/UnknownCompositionMaterials.class",
    "com/gregtechceu/gtceu/common/data/materials/HighDegreeMaterials.class",
    "com/gregtechceu/gtceu/common/data/materials/ElementMaterials.class",
    "com/gregtechceu/gtceu/common/data/materials/FirstDegreeMaterials.class",
    "com/gregtechceu/gtceu/common/data/materials/SecondDegreeMaterials.class",
    "com/gregtechceu/gtceu/common/data/materials/HigherDegreeMaterials.class",
]
ITEM_CLASSES = [
    "com/gregtechceu/gtceu/common/data/GTItems.class",
    "com/gregtechceu/gtceu/common/data/GTBlocks.class",
    "com/gregtechceu/gtceu/common/data/GTMachines.class",
    "com/gregtechceu/gtceu/api/data/tag/TagPrefix.class",
    "com/gregtechceu/gtceu/data/recipe/CustomTags.class",
    "com/gregtechceu/gtceu/common/data/GTRecipeTypes.class",
]

# TST ingredient -> candidate GTCEu symbol to look for
MATERIALS = [
    "CosmicNeutronium", "Neutronium", "NaquadahAlloy", "Osmiridium", "TungstenSteel",
    "BlackSteel", "Iridium", "StainlessSteel", "Duranium", "Tin", "Silver", "Hydrogen",
    "SolderingAlloy", "UUMatter", "Infinity", "SpaceTime", "TranscendentMetal", "Quantium",
    "Eternity", "Universium", "MHDCSM", "Void", "Space", "Time", "Shirabon", "Hypogen",
    "StableBaryonicMatter", "SuperCoolant", "PCBCoolant", "BlackTitanium",
    "MetastableOganesson", "Dalisenite", "ExcitedDTSC", "DTR",
    "Grade1PurifiedWater", "Grade8PurifiedWater", "Naquadria", "Trinium", "Tritanium",
    "Europium", "Darmstadtium", "Americium", "RutheniumTriniumAmericiumNeutronate",
    "UraniumRhodiumDinaquadide", "SamariumIronArsenicOxide", "Palladium", "Titanium",
    "Helium", "Lubricant", "DistilledWater",
]
ITEMS = [
    "FIELD_GENERATOR_UEV", "FIELD_GENERATOR_UIV", "FIELD_GENERATOR_UXV", "FIELD_GENERATOR_OpV",
    "EMITTER_UEV", "EMITTER_UIV", "EMITTER_UXV", "SENSOR_UEV", "SENSOR_UIV", "SENSOR_UXV",
    "ELECTRIC_MOTOR_UEV", "ELECTRIC_PISTON_UEV", "ROBOT_ARM_UEV", "ELECTRIC_PUMP_UEV",
    "CONVEYOR_MODULE_UEV", "ELECTRIC_PUMP_UIV", "ELECTRIC_PUMP_UXV", "ELECTRIC_PUMP_OpV",
    "ENERGY_MODULE", "ENERGY_CLUSTER", "QUANTUM_STAR", "GRAVI_STAR", "QUANTUM_EYE",
    "TOOL_DATA_ORB", "TOOL_DATA_MODULE", "TOOL_DATA_STICK",
    "WETWARE_MAINFRAME_UHV", "NANITES", "FUSION_COIL", "FUSION_CASING", "FUSION_CASING_MK2",
    "FUSION_CASING_MK3", "MACHINE_CASING_MAX", "CASING_TEMPERED_GLASS", "FILTER_CASING",
    "HULL", "ASSEMBLER", "ENERGY_INPUT_HATCH", "ROCK_CRUSHER", "ELECTRIC_FURNACE",
    "LARGE_MINER", "FLUID_DRILLING_RIG", "CLEANROOM", "HPCA_COMPUTATION_COMPONENT",
    "HIGH_PERFORMANCE_COMPUTING_ARRAY", "NETWORK_SWITCH", "RESEARCH_STATION",
    "ASSEMBLY_LINE_RECIPES", "ASSEMBLER_RECIPES", "PLASMA_GENERATOR_FUELS",
    "UIV_CIRCUITS", "UMV_CIRCUITS", "UXV_CIRCUITS", "MAX_CIRCUITS", "OpV_CIRCUITS",
    "plateSuperdense", "plateDense", "wireGtHex", "wireGtOctal", "frameGt", "nanite",
    "rotor", "pipeHugeFluid", "toolHeadDrill", "itemCasing",
]


def utf8_pool(data: bytes) -> set:
    count = struct.unpack_from(">H", data, 8)[0]
    out, off, i = set(), 10, 1
    while i < count:
        tag = data[off]
        off += 1
        if tag == 1:
            ln = struct.unpack_from(">H", data, off)[0]
            out.add(data[off + 2:off + 2 + ln].decode("utf-8", "replace"))
            off += 2 + ln
        elif tag in (7, 8, 16, 19, 20):
            off += 2
        elif tag == 15:
            off += 3
        elif tag in (5, 6):
            off += 8
            i += 1
        else:
            off += 4
        i += 1
    return out


def pool_for(z, classes):
    pool = set()
    for c in classes:
        try:
            pool |= utf8_pool(z.read(c))
        except KeyError:
            pass
    return pool


def main():
    z = zipfile.ZipFile(JAR)
    mats = pool_for(z, MATERIAL_CLASSES)
    items = pool_for(z, ITEM_CLASSES)
    # material *ids* also appear as lowercase snake in the registry strings
    lower = {s for s in mats | items if s.islower() or "_" in s}

    print("### MATERIALS")
    for name in MATERIALS:
        snake = "".join(("_" + ch.lower()) if ch.isupper() else ch for ch in name).lstrip("_")
        ok = name in mats or snake in lower
        print(f"  {'YES' if ok else 'no '}  {name}")
    print("\n### ITEMS / BLOCKS / MACHINES / TAGS")
    for name in ITEMS:
        ok = name in items
        print(f"  {'YES' if ok else 'no '}  {name}")


if __name__ == "__main__":
    main()
