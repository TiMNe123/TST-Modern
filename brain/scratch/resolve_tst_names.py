"""Resolve TST GTCMItemList / MetaItem01 tokens to their English display names,
so the Excel sheet shows a human-readable ingredient instead of a Java symbol.

Mapping chain: ItemRegister.java `X.set(MetaItem01.registerVariantWithTooltips(<meta>, ...))`
-> lang key `item.MetaItem01.<meta>.name` in assets/gtnhcommunitymod/lang/en_US.lang.
"""
import json
import re
from pathlib import Path

TST = Path("D:/tmp/TST")
REG = TST / "src/main/java/com/Nxer/TwistSpaceTechnology/common/init/ItemRegister.java"
BLK = TST / "src/main/java/com/Nxer/TwistSpaceTechnology/common/init/BlockRegister.java"
LANG = TST / "src/main/resources/assets/gtnhcommunitymod/lang/en_US.lang"
OUT = Path(__file__).with_name("tst_names.json")


def lang_map():
    out = {}
    for line in LANG.read_text(encoding="utf-8", errors="replace").splitlines():
        if "=" in line and not line.startswith("#"):
            k, v = line.split("=", 1)
            out[k.strip()] = v.strip()
    return out


def main():
    lang = lang_map()
    names = {}

    src = REG.read_text(encoding="utf-8", errors="replace")
    # GTCMItemList.X.set(MetaItemNN.registerVariantWithTooltips(<meta>, ...
    for sym, holder, meta in re.findall(
            r"GTCMItemList\.(\w+)\.set\(\s*(MetaItem\w+)\.registerVariantWithTooltips\(\s*(\d+)", src):
        key = f"item.{holder}.{meta}.name"
        if key in lang:
            names[sym] = lang[key]

    bsrc = BLK.read_text(encoding="utf-8", errors="replace")
    for sym, holder, meta in re.findall(
            r"GTCMItemList\.(\w+)[\s\S]{0,120}?(MetaBlock\w+)\.registerVariantWithTooltips\(\s*(\d+)", bsrc):
        key = f"tile.{holder}.{meta}.name"
        if key in lang:
            names.setdefault(sym, lang[key])

    # machine controllers: MachineLoader passes an unlocalized key like "NameMiracleTop"
    ml = (TST / "src/main/java/com/Nxer/TwistSpaceTechnology/loader/MachineLoader.java") \
        .read_text(encoding="utf-8", errors="replace")
    for sym, key in re.findall(r"GTCMItemList\.(\w+)\.set\([\s\S]{0,400}?\"(Name\w+)\"", ml):
        if key in lang:
            names.setdefault(sym, lang[key])

    OUT.write_text(json.dumps(names, indent=1, ensure_ascii=False), encoding="utf-8")
    print(f"resolved {len(names)} TST symbols")
    wanted = ["SpaceWarper", "OpticalSOC", "GravitationalLens", "AnnihilationConstrainer",
              "AntimatterFuelRod", "DysonSphereFrameComponent", "StellarConstructionFrameMaterial",
              "SpaceScaler", "PerfectLapotronCrystal", "EnergyFluctuationSelfHarmonizer",
              "PacketInformationTranslationArray", "AdvancedHighPowerCoilBlock",
              "AdvCircuitAssemblyLine", "MiracleTop", "MassFabricatorGenesis"]
    for w in wanted:
        print(f"  {w:36} {names.get(w, '-- unresolved --')}")


if __name__ == "__main__":
    main()
