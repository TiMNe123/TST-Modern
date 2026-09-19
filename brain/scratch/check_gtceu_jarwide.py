"""Second pass: search the WHOLE gtceu jar's constant pools for the names that
came back 'no' from the narrow per-class scan, so an absent verdict is real
rather than an artefact of looking in the wrong class.
"""
import struct
import zipfile

JAR = (r"C:/Users/mtien/.gradle/caches/modules-2/files-2.1/com.gregtechceu.gtceu"
       r"/gtceu-1.20.1/7.4.0/7a1e85994ff9add24d399ec6b64a087df0adc798"
       r"/gtceu-1.20.1-7.4.0-slim.jar")

CANDIDATES = [
    "CosmicNeutronium", "Infinity", "SpaceTime", "TranscendentMetal", "Quantium", "Eternity",
    "Universium", "MHDCSM", "Shirabon", "Hypogen", "StableBaryonicMatter", "SuperCoolant",
    "BlackTitanium", "MetastableOganesson", "Dalisenite", "Grade1PurifiedWater",
    "NANITES", "LARGE_MINER", "FLUID_DRILLING_RIG", "HPCA_COMPUTATION_COMPONENT",
    "HIGH_PERFORMANCE_COMPUTING_ARRAY", "NETWORK_SWITCH", "RESEARCH_STATION",
    "UMV_CIRCUITS", "plateSuperdense", "nanite", "itemCasing", "PLASMA_FORGE",
    "PURIFICATION_PLANT", "ACTIVE_TRANSFORMER", "POWER_SUBSTATION",
    "MASS_FABRICATOR", "REPLICATOR", "NANO_FORGE", "COMPONENT_ASSEMBLY_LINE",
    "PURIFIED_WATER", "MOLECULAR_TRANSFORMER", "NEUTRON_ACTIVATOR",
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
    found = {c: [] for c in CANDIDATES}
    for n in z.namelist():
        if not n.endswith(".class"):
            continue
        try:
            pool = utf8_pool(z.read(n))
        except Exception:
            continue
        for c in CANDIDATES:
            if c in pool:
                found[c].append(n)
    # asset/lang side too (registry names live there)
    lang = ""
    for n in z.namelist():
        if n.endswith("en_us.json") or n.endswith(".lang"):
            lang += z.read(n).decode("utf-8", "replace")

    for c in CANDIDATES:
        hits = found[c]
        where = hits[0].split("/")[-1] if hits else "-"
        extra = f" (+{len(hits) - 1})" if len(hits) > 1 else ""
        in_lang = "lang" if c.lower() in lang.lower() else ""
        print(f"  {'YES' if hits else 'no ':4} {c:36} {where}{extra} {in_lang}")


if __name__ == "__main__":
    main()
