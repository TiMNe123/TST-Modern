"""Confirm every GTCEu symbol used by TST-Modern controller recipes really exists
in the gtceu 7.4.0 jar, by parsing each class file's constant pool (UTF8 entries).
"""
import struct
import zipfile

JAR = (r"C:/Users/mtien/.gradle/caches/modules-2/files-2.1/com.gregtechceu.gtceu"
       r"/gtceu-1.20.1/7.4.0/7a1e85994ff9add24d399ec6b64a087df0adc798"
       r"/gtceu-1.20.1-7.4.0-slim.jar")

CLASSES = {
    "GTItems": "com/gregtechceu/gtceu/common/data/GTItems.class",
    "GTMachines": "com/gregtechceu/gtceu/common/data/GTMachines.class",
    "GTBlocks": "com/gregtechceu/gtceu/common/data/GTBlocks.class",
    "GTMaterials": "com/gregtechceu/gtceu/common/data/GTMaterials.class",
    "CustomTags": "com/gregtechceu/gtceu/data/recipe/CustomTags.class",
    "GTRecipeTypes": "com/gregtechceu/gtceu/common/data/GTRecipeTypes.class",
    "TagPrefix": "com/gregtechceu/gtceu/api/data/tag/TagPrefix.class",
}

WANTED = {
    "GTMachines": ["HULL", "ELECTRIC_FURNACE", "ROCK_CRUSHER", "ASSEMBLER", "ENERGY_INPUT_HATCH"],
    "GTItems": [
        "ROBOT_ARM_ZPM", "CONVEYOR_MODULE_ZPM", "ELECTRIC_PUMP_ZPM", "FIELD_GENERATOR_ZPM",
        "FIELD_GENERATOR_LuV", "ROBOT_ARM_IV", "EMITTER_IV", "FIELD_GENERATOR_IV",
        "TOOL_DATA_ORB", "TOOL_DATA_MODULE", "EMITTER_UEV", "QUANTUM_EYE", "ENERGY_CLUSTER",
        "FIELD_GENERATOR_UHV", "ROBOT_ARM_UHV", "ELECTRIC_PUMP_UHV", "CONVEYOR_MODULE_UHV",
        "WETWARE_MAINFRAME_UHV", "FIELD_GENERATOR_UEV", "ROBOT_ARM_UEV", "ELECTRIC_PUMP_UEV",
        "FIELD_GENERATOR_UIV", "ROBOT_ARM_UIV", "ELECTRIC_PUMP_UIV",
        "FIELD_GENERATOR_UXV", "ROBOT_ARM_UXV", "ELECTRIC_PUMP_UXV",
        "FIELD_GENERATOR_OpV", "ROBOT_ARM_OpV", "ELECTRIC_PUMP_OpV",
    ],
    "GTBlocks": ["FUSION_COIL", "FUSION_CASING_MK2", "FUSION_CASING_MK3", "MACHINE_CASING_UEV"],
    "GTMaterials": [
        "SolderingAlloy", "Iridium", "NaquadahAlloy", "UraniumRhodiumDinaquadide", "Neutronium",
        "Obsidian", "Netherite", "Europium", "Titanium", "Tritanium", "Helium", "Osmiridium",
        "PCBCoolant", "UUMatter", "SamariumIronArsenicOxide", "DistilledWater", "Lubricant",
    ],
    "CustomTags": ["ZPM_CIRCUITS", "UV_CIRCUITS", "UHV_CIRCUITS", "MAX_CIRCUITS", "UXV_CIRCUITS"],
    "TagPrefix": ["frameGt", "plate", "plateDense", "pipeLargeFluid", "gear", "screw",
                  "cableGtSingle", "wireGtSingle", "dust", "ingot"],
    "GTRecipeTypes": ["ASSEMBLER_RECIPES", "ASSEMBLY_LINE_RECIPES"],
}


def utf8_pool(data: bytes) -> set:
    """Return every CONSTANT_Utf8 string in a .class constant pool."""
    assert data[:4] == b"\xca\xfe\xba\xbe", "not a class file"
    count = struct.unpack_from(">H", data, 8)[0]
    out, off, i = set(), 10, 1
    while i < count:
        tag = data[off]
        off += 1
        if tag == 1:                                    # Utf8
            ln = struct.unpack_from(">H", data, off)[0]
            out.add(data[off + 2:off + 2 + ln].decode("utf-8", "replace"))
            off += 2 + ln
        elif tag in (7, 8, 16, 19, 20):
            off += 2
        elif tag == 15:
            off += 3
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
            off += 4
        elif tag in (5, 6):                             # long/double take two slots
            off += 8
            i += 1
        else:
            raise ValueError(f"unknown constant tag {tag} at {off - 1}")
        i += 1
    return out


def main():
    z = zipfile.ZipFile(JAR)
    names = set(z.namelist())
    missing_total = []
    for holder, path in CLASSES.items():
        if path not in names:
            print(f"!! class not in jar: {path}")
            continue
        pool = utf8_pool(z.read(path))
        want = WANTED.get(holder, [])
        missing = [w for w in want if w not in pool]
        print(f"{holder:14} checked {len(want):3}  missing {len(missing)}"
              + (f"  -> {missing}" if missing else ""))
        missing_total += [f"{holder}.{m}" for m in missing]
    print("\nMISSING SYMBOLS:", missing_total or "none")


if __name__ == "__main__":
    main()
