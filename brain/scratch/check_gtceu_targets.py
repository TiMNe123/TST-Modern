"""Third verification pass: candidate GTCEu 7.4.0 mapping targets that the Excel
annotation column will cite. Only names that come back YES may be written.
"""
import struct
import zipfile

JAR = (r"C:/Users/mtien/.gradle/caches/modules-2/files-2.1/com.gregtechceu.gtceu"
       r"/gtceu-1.20.1/7.4.0/7a1e85994ff9add24d399ec6b64a087df0adc798"
       r"/gtceu-1.20.1-7.4.0-slim.jar")

CANDIDATES = [
    # machines that could host the ported recipes
    "FUSION_REACTOR", "LARGE_CHEMICAL_REACTOR", "IMPLOSION_COMPRESSOR", "ELECTRIC_BLAST_FURNACE",
    "PYROLYSE_OVEN", "COKE_OVEN", "LARGE_MACERATOR", "ORE_WASHER", "DISTILLATION_TOWER",
    "ASSEMBLY_LINE", "PROCESSING_ARRAY", "CRACKER", "STEAM_TURBINE", "PLASMA_GENERATOR",
    "NAQUADAH_REACTOR", "LARGE_STEAM_TURBINE", "LARGE_PLASMA_TURBINE", "CREATIVE_ENERGY",
    "MINER", "BEDROCK_ORE_MINER", "VACUUM_FREEZER", "GREENHOUSE",
    # components
    "SENSOR_OpV", "EMITTER_OpV", "ROBOT_ARM_MAX", "FIELD_GENERATOR_MAX",
    "ULTIMATE_BATTERY", "QUANTUM_TANK", "SUPER_TANK",
    "COVER_SOLAR_PANEL", "NEUTRON_REFLECTOR", "COMPUTER_MONITOR_COVER",
    # blocks / casings
    "CASING_HIGH_TEMPERATURE_SMELTING", "CASING_STRESS_PROOF", "CASING_VIBRATION_SAFE",
    "CASING_ATOMIC", "HERMETIC_CASING", "COIL_NAQUADAH", "COIL_TRINIUM", "COIL_TRITANIUM",
    "COIL_URANIUM", "PLASMA_FORGE_CASING", "DIMENSIONALLY_TRANSCENDENT_CASING",
    "CLEANROOM_GLASS", "FUSION_GLASS", "LASER_PIPE", "HIGH_POWER_CASING",
    # tags / prefixes
    "wireGtSingle", "wireGtQuadruple", "wireGtHex", "gear", "gearSmall", "screw", "ring",
    "foil", "plate", "dust", "ingot", "block", "nugget", "rodLong", "pipeLargeFluid",
    "UEV_CIRCUITS", "UHV_CIRCUITS", "UV_CIRCUITS",
    # materials that may substitute the missing TST end-game ones
    "Neutronium", "Duranium", "Adamantium", "Vibranium", "Taranium",
    "Legendarium", "Draconium", "Infinity", "CosmicNeutronium",
    "Polybenzimidazole", "Zeron100", "HastelloyX", "Naquadah",
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


def main():
    z = zipfile.ZipFile(JAR)
    pool = set()
    holder = {}
    for n in z.namelist():
        if not n.endswith(".class"):
            continue
        try:
            p = utf8_pool(z.read(n))
        except Exception:
            continue
        for c in CANDIDATES:
            if c in p and c not in holder:
                holder[c] = n.split("/")[-1]
        pool |= p
    for c in CANDIDATES:
        print(f"  {'YES' if c in holder else 'no ':4} {c:38} {holder.get(c, '')}")


if __name__ == "__main__":
    main()
