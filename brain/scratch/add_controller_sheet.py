"""Add the 'Nguyên liệu controller' sheet to the project workbook.

Every row is transcribed from the controller (machine-item) recipe in
src/main/java/com/tstmodern/data/recipe/*.java. The GTCEu annotation column
carries the exact Java symbol; each symbol was confirmed present in
gtceu-1.20.1-7.4.0-slim.jar by brain/scratch/verify_gtceu_symbols.py.

Styling mirrors the sibling sheets: Play 18 white title on #17324D, Aptos 10
subtitle on #EAF2F8, white bold header on #0F766E, native table from row 4.
"""
from pathlib import Path

from openpyxl import load_workbook
from openpyxl.styles import Alignment, Font, PatternFill
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.table import Table, TableStyleInfo

ROOT = Path(__file__).resolve().parents[2]
BOOK = ROOT / "TST_Multiblocks_ZPM_Endgame_GTCEu_1.20.1 (2)_ColorRules_Updated.xlsx"
SHEET = "Nguyên liệu controller"

GT = "GTCEu"
TST = "TST-Modern"
VAN = "Vanilla MC"

HEADERS = [
    "STT", "Máy (controller)", "Công thức / tier", "Nguyên liệu", "Số lượng",
    "Nguồn", "Chú thích GTCEu (symbol đã xác minh trong gtceu 7.4.0)",
    "Nếu TST: máy sản xuất nguyên liệu", "Vị trí code",
]

# machine, recipe/tier, ingredient, qty, source, gtceu note, tst producer, code location
ROWS = [
    # ---------- Giant Vacuum Drying Furnace ----------
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "ZPM Machine Hull", "2", GT, "GTMachines.HULL[ZPM]", "",
     "GiantVacuumDryingFurnaceRecipes.java:224"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "ZPM Electric Furnace", "4", GT, "GTMachines.ELECTRIC_FURNACE[ZPM]", "",
     "GiantVacuumDryingFurnaceRecipes.java:225"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Robot Arm (ZPM)", "16", GT, "GTItems.ROBOT_ARM_ZPM", "",
     "GiantVacuumDryingFurnaceRecipes.java:226"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Conveyor Module (ZPM)", "16", GT, "GTItems.CONVEYOR_MODULE_ZPM", "",
     "GiantVacuumDryingFurnaceRecipes.java:227"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Mạch ZPM (tag)", "32", GT, "CustomTags.ZPM_CIRCUITS — tag, nhận mọi mạch ZPM", "",
     "GiantVacuumDryingFurnaceRecipes.java:228"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Large Iridium Fluid Pipe", "16", GT, "TagPrefix.pipeLargeFluid + GTMaterials.Iridium", "",
     "GiantVacuumDryingFurnaceRecipes.java:229"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Naquadah Alloy Plate", "16", GT, "TagPrefix.plate + GTMaterials.NaquadahAlloy", "",
     "GiantVacuumDryingFurnaceRecipes.java:230"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Uranium Rhodium Dinaquadide Plate", "16", GT,
     "TagPrefix.plate + GTMaterials.UraniumRhodiumDinaquadide", "",
     "GiantVacuumDryingFurnaceRecipes.java:231"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Vacuum Casing", "4", TST, "Không có trong GTCEu — block riêng của TST-Modern",
     "Giant Vacuum Drying Furnace (Assembler, HV) — GiantVacuumDryingFurnaceRecipes.java:211",
     "GiantVacuumDryingFurnaceRecipes.java:232"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Soldering Alloy (lỏng)", "9.216 mB", GT, "GTMaterials.SolderingAlloy", "",
     "GiantVacuumDryingFurnaceRecipes.java:233"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Iridium (lỏng)", "4.608 mB", GT, "GTMaterials.Iridium", "",
     "GiantVacuumDryingFurnaceRecipes.java:234"),
    ("Giant Vacuum Drying Furnace", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Research: Scanner", "1200t · VA[LuV]", GT,
     "scannerResearch(GTMachines.ELECTRIC_FURNACE[ZPM])", "",
     "GiantVacuumDryingFurnaceRecipes.java:236"),

    # ---------- Nether Interface ----------
    ("Nether Interface", "Assembler · LuV · 7200t · VA[LuV]",
     "Obsidian Frame Box", "16", GT,
     "TagPrefix.frameGt + GTMaterials.Obsidian — dạng frame do TST bật "
     "(MaterialFlags.GENERATE_FRAME, TSTMaterials.java:27)", "",
     "NetherInterfaceRecipes.java:61"),
    ("Nether Interface", "Assembler · LuV · 7200t · VA[LuV]",
     "Field Generator (LuV)", "4", GT, "GTItems.FIELD_GENERATOR_LuV", "",
     "NetherInterfaceRecipes.java:62"),
    ("Nether Interface", "Assembler · LuV · 7200t · VA[LuV]",
     "Mạch ZPM (tag)", "16", GT, "CustomTags.ZPM_CIRCUITS", "",
     "NetherInterfaceRecipes.java:63"),
    ("Nether Interface", "Assembler · LuV · 7200t · VA[LuV]",
     "Netherite Dense Plate", "16", GT,
     "TagPrefix.plateDense + GTMaterials.Netherite — dense plate do TST bật "
     "(GENERATE_PLATE/GENERATE_DENSE, TSTMaterials.java:40)", "",
     "NetherInterfaceRecipes.java:64"),
    ("Nether Interface", "Assembler · LuV · 7200t · VA[LuV]",
     "Molten Hellish Metal", "9.216 mB", TST,
     "Không có trong GTCEu — material riêng (TSTMaterials.java:76)",
     "Blast Furnace (bootstrap, EV) — NetherInterfaceRecipes.java:85; "
     "sau đó Nether Interface tự sinh (chanced 30%)",
     "NetherInterfaceRecipes.java:65"),

    # ---------- Hyper Thermal Convector ----------
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "UV Machine Hull", "2", GT, "GTMachines.HULL[UV]", "",
     "HyperThermalConvectorRecipes.java:68"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Electric Pump (ZPM)", "16", GT, "GTItems.ELECTRIC_PUMP_ZPM", "",
     "HyperThermalConvectorRecipes.java:69"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Field Generator (ZPM)", "8", GT, "GTItems.FIELD_GENERATOR_ZPM", "",
     "HyperThermalConvectorRecipes.java:70"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Mạch UV (tag)", "16", GT, "CustomTags.UV_CIRCUITS", "",
     "HyperThermalConvectorRecipes.java:71"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Large Iridium Fluid Pipe", "16", GT, "TagPrefix.pipeLargeFluid + GTMaterials.Iridium", "",
     "HyperThermalConvectorRecipes.java:72"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Neutronium Plate", "16", GT, "TagPrefix.plate + GTMaterials.Neutronium", "",
     "HyperThermalConvectorRecipes.java:73"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Naquadah Alloy Plate", "16", GT, "TagPrefix.plate + GTMaterials.NaquadahAlloy", "",
     "HyperThermalConvectorRecipes.java:74"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Iridium Reinforced Neutronium Casing", "4", TST,
     "Không có trong GTCEu — block riêng của TST-Modern",
     "Hyper Thermal Convector (Assembler, UV) — HyperThermalConvectorRecipes.java:90",
     "HyperThermalConvectorRecipes.java:75"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Soldering Alloy (lỏng)", "9.216 mB", GT, "GTMaterials.SolderingAlloy", "",
     "HyperThermalConvectorRecipes.java:76"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Iridium (lỏng)", "4.608 mB", GT, "GTMaterials.Iridium", "",
     "HyperThermalConvectorRecipes.java:77"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Water", "64.000 mB", VAN, "net.minecraft Fluids.WATER — không phải fluid GTCEu", "",
     "HyperThermalConvectorRecipes.java:78"),
    ("Hyper Thermal Convector", "Assembly Line · UV · 1200t · VA[UV]",
     "Research: Station", "CWU/t 64 · 128.000 CWU · VA[UV]", GT,
     "stationResearch(GTMachines.HULL[UV])", "",
     "HyperThermalConvectorRecipes.java:80"),

    # ---------- Mega Tree Farm ----------
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "UHV Machine Hull", "2", GT, "GTMachines.HULL[UHV]", "", "MegaTreeFarmRecipes.java:77"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Mạch UHV (tag)", "16", GT, "CustomTags.UHV_CIRCUITS", "", "MegaTreeFarmRecipes.java:78"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Electric Pump (ZPM)", "16", GT, "GTItems.ELECTRIC_PUMP_ZPM", "",
     "MegaTreeFarmRecipes.java:79"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Robot Arm (ZPM)", "16", GT, "GTItems.ROBOT_ARM_ZPM", "", "MegaTreeFarmRecipes.java:80"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Conveyor Module (ZPM)", "16", GT, "GTItems.CONVEYOR_MODULE_ZPM", "",
     "MegaTreeFarmRecipes.java:81"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Field Generator (ZPM)", "8", GT, "GTItems.FIELD_GENERATOR_ZPM", "",
     "MegaTreeFarmRecipes.java:82"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Naquadah Alloy Dense Plate", "16", GT,
     "TagPrefix.plateDense + GTMaterials.NaquadahAlloy", "", "MegaTreeFarmRecipes.java:83"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Sterile Machine Casing", "16", TST, "Không có trong GTCEu — block riêng của TST-Modern",
     "Mega Tree Farm (Assembler, IV) — MegaTreeFarmRecipes.java:110",
     "MegaTreeFarmRecipes.java:84"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Cultivation Soil Casing", "16", TST, "Không có trong GTCEu — block riêng của TST-Modern",
     "Mega Tree Farm (Assembler, EV) — MegaTreeFarmRecipes.java:239",
     "MegaTreeFarmRecipes.java:85"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Soldering Alloy (lỏng)", "9.216 mB", GT, "GTMaterials.SolderingAlloy", "",
     "MegaTreeFarmRecipes.java:86"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Distilled Water", "64.000 mB", GT, "GTMaterials.DistilledWater", "",
     "MegaTreeFarmRecipes.java:87"),
    ("Mega Tree Farm", "Assembly Line · UHV · 1200t · VA[UHV]",
     "Research: Station", "CWU/t 64 · 128.000 CWU · VA[UHV]", GT,
     "stationResearch(GTMachines.HULL[UHV])", "", "MegaTreeFarmRecipes.java:89"),

    # ---------- Mega Stone Breaker ----------
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "ZPM Machine Hull", "2", GT, "GTMachines.HULL[ZPM]", "", "MegaStoneBreakerRecipes.java:171"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "ZPM Rock Crusher", "4", GT, "GTMachines.ROCK_CRUSHER[ZPM]", "",
     "MegaStoneBreakerRecipes.java:172"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Robot Arm (ZPM)", "16", GT, "GTItems.ROBOT_ARM_ZPM", "",
     "MegaStoneBreakerRecipes.java:173"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Electric Pump (ZPM)", "8", GT, "GTItems.ELECTRIC_PUMP_ZPM", "",
     "MegaStoneBreakerRecipes.java:174"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Large Iridium Fluid Pipe", "16", GT,
     "TagPrefix.pipeLargeFluid + GTMaterials.Iridium — thay Ultimet pipe (GTCEu 7.4 không sinh)",
     "", "MegaStoneBreakerRecipes.java:177"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Naquadah Alloy Plate", "16", GT, "TagPrefix.plate + GTMaterials.NaquadahAlloy", "",
     "MegaStoneBreakerRecipes.java:178"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Uranium Rhodium Dinaquadide Plate", "16", GT,
     "TagPrefix.plate + GTMaterials.UraniumRhodiumDinaquadide", "",
     "MegaStoneBreakerRecipes.java:179"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Quadruple Compressed Cobblestone", "4", TST,
     "Không có trong GTCEu — block riêng của TST-Modern",
     "Mega Stone Breaker (recipe map riêng) + Compressor + crafting 3×3 — "
     "MegaStoneBreakerRecipes.java:56/95/112",
     "MegaStoneBreakerRecipes.java:180"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Soldering Alloy (lỏng)", "9.216 mB", GT, "GTMaterials.SolderingAlloy", "",
     "MegaStoneBreakerRecipes.java:181"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Lava", "64.000 mB", VAN, "net.minecraft Fluids.LAVA — không phải fluid GTCEu", "",
     "MegaStoneBreakerRecipes.java:182"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Water", "64.000 mB", VAN, "net.minecraft Fluids.WATER — không phải fluid GTCEu", "",
     "MegaStoneBreakerRecipes.java:183"),
    ("Mega Stone Breaker", "Assembly Line · ZPM · 1200t · VA[ZPM]",
     "Research: Scanner", "1200t · VA[LuV]", GT,
     "scannerResearch(GTMachines.ROCK_CRUSHER[ZPM])", "", "MegaStoneBreakerRecipes.java:185"),

    # ---------- Incompact Cyclotron ----------
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "UEV Machine Hull", "64", GT, "GTMachines.HULL[UEV]", "",
     "IncompactCyclotronRecipes.java:223"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Emitter (UEV)", "64", GT, "GTItems.EMITTER_UEV", "", "IncompactCyclotronRecipes.java:224"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Fusion Coil Block", "8", GT, "GTBlocks.FUSION_COIL", "",
     "IncompactCyclotronRecipes.java:225"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Quantum Eye", "4", GT, "GTItems.QUANTUM_EYE", "", "IncompactCyclotronRecipes.java:226"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Field Generator (UHV)", "16", GT, "GTItems.FIELD_GENERATOR_UHV", "",
     "IncompactCyclotronRecipes.java:227"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Energy Cluster", "32", GT, "GTItems.ENERGY_CLUSTER", "",
     "IncompactCyclotronRecipes.java:228"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Mạch UHV (tag)", "16", GT, "CustomTags.UHV_CIRCUITS", "",
     "IncompactCyclotronRecipes.java:229"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Naquadah Alloy Dense Plate", "16", GT,
     "TagPrefix.plateDense + GTMaterials.NaquadahAlloy", "",
     "IncompactCyclotronRecipes.java:230"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Naquadah Alloy Gear", "16", GT, "TagPrefix.gear + GTMaterials.NaquadahAlloy", "",
     "IncompactCyclotronRecipes.java:231"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Naquadah Alloy Screw", "64", GT, "TagPrefix.screw + GTMaterials.NaquadahAlloy", "",
     "IncompactCyclotronRecipes.java:232"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Naquadah Alloy (lỏng)", "36.864 mB", GT, "GTMaterials.NaquadahAlloy", "",
     "IncompactCyclotronRecipes.java:233"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Liquid Helium", "1.000.000 mB", GT,
     "GTMaterials.Helium + FluidStorageKeys.LIQUID", "", "IncompactCyclotronRecipes.java:234"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Tritanium (lỏng)", "288 mB", GT, "GTMaterials.Tritanium", "",
     "IncompactCyclotronRecipes.java:235"),
    ("Incompact Cyclotron", "Assembly Line · UEV · 18.000t · 8.000.000 EU/t",
     "Research: Station", "CWU/t 64 · 144.000 CWU · VA[UEV]", GT,
     "stationResearch(GTBlocks.FUSION_COIL) + GTItems.TOOL_DATA_MODULE", "",
     "IncompactCyclotronRecipes.java:239"),

    # ---------- Disassembler ----------
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "UHV Assembler", "64", GT, "GTMachines.ASSEMBLER[UHV]", "", "DisassemblerRecipes.java:103"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Field Generator (UHV)", "16", GT, "GTItems.FIELD_GENERATOR_UHV", "",
     "DisassemblerRecipes.java:104"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Electric Pump (UHV)", "64", GT, "GTItems.ELECTRIC_PUMP_UHV", "",
     "DisassemblerRecipes.java:105"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Conveyor Module (UHV)", "64", GT, "GTItems.CONVEYOR_MODULE_UHV", "",
     "DisassemblerRecipes.java:106"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Robot Arm (UHV)", "256 (4 × 64)", GT,
     "GTItems.ROBOT_ARM_UHV — DSL tự chia thành 4 slot 64", "",
     "DisassemblerRecipes.java:107"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "UHV Energy Input Hatch", "64", GT, "GTMachines.ENERGY_INPUT_HATCH[UHV]", "",
     "DisassemblerRecipes.java:108"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Neutronium Frame Box", "16", GT, "TagPrefix.frameGt + GTMaterials.Neutronium", "",
     "DisassemblerRecipes.java:109"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Osmiridium Plate", "144 (3 × 64 + 48)", GT, "TagPrefix.plate + GTMaterials.Osmiridium", "",
     "DisassemblerRecipes.java:110"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Soldering Alloy (lỏng)", "147.456 mB", GT, "GTMaterials.SolderingAlloy", "",
     "DisassemblerRecipes.java:112"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "UU-Matter", "128.000 mB", GT,
     "GTMaterials.UUMatter — material CÓ trong GTCEu 7.4 nhưng KHÔNG máy GTCEu nào tạo ra "
     "(đã quét jar: chỉ GTMaterials/UnknownCompositionMaterials tham chiếu, không có recipe)",
     "Mass Fabricator (TST-Modern) — MassFabricatorRecipes.java:30 & 38",
     "DisassemblerRecipes.java:113"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "PCB Coolant", "768.000 mB", GT,
     "GTMaterials.PCBCoolant — thay Super Coolant của TST 1.7.10", "",
     "DisassemblerRecipes.java:114"),
    ("Disassembler", "Assembly Line · UEV · 72.000t · VA[UEV]",
     "Research: Station", "CWU/t 256 · 72.000 CWU · VA[UHV]", GT,
     "stationResearch(GTMachines.ASSEMBLER[UHV]) + GTItems.TOOL_DATA_ORB", "",
     "DisassemblerRecipes.java:115"),

    # ---------- Big Bro Array ----------
    ("Big Bro Array", "Assembler · IV · 24.000t · 6.400 EU/t",
     "Data Orb", "16", GT, "GTItems.TOOL_DATA_ORB", "", "BigBroArrayRecipes.java:31"),
    ("Big Bro Array", "Assembler · IV · 24.000t · 6.400 EU/t",
     "Robot Arm (IV)", "32", GT, "GTItems.ROBOT_ARM_IV", "", "BigBroArrayRecipes.java:32"),
    ("Big Bro Array", "Assembler · IV · 24.000t · 6.400 EU/t",
     "Emitter (IV)", "32", GT, "GTItems.EMITTER_IV", "", "BigBroArrayRecipes.java:33"),
    ("Big Bro Array", "Assembler · IV · 24.000t · 6.400 EU/t",
     "Field Generator (IV)", "32", GT, "GTItems.FIELD_GENERATOR_IV", "",
     "BigBroArrayRecipes.java:34"),
    ("Big Bro Array", "Assembler · IV · 24.000t · 6.400 EU/t",
     "Samarium Iron Arsenic Oxide Wire (1x)", "256 (4 × 64)", GT,
     "TagPrefix.wireGtSingle + GTMaterials.SamariumIronArsenicOxide — "
     "thay SuperconductorIV của TST", "", "BigBroArrayRecipes.java:35-38"),
    ("Big Bro Array", "Assembler · IV · 24.000t · 6.400 EU/t",
     "Titanium (lỏng)", "24.576 mB", GT,
     "GTMaterials.Titanium — thay Nitinol-60 (không có trong GTCEu)", "",
     "BigBroArrayRecipes.java:39"),

    # ---------- Mass Fabricator (single-block, 6 tier) ----------
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Machine Hull theo tier", "1", GT,
     "GTMachines.HULL[UHV] / [UEV] / [UIV] / [UXV] / [OpV] / [MAX]", "",
     "MassFabricatorRecipes.java:51"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Mạch UHV (tag) — chỉ tier UHV", "4", GT, "CustomTags.UHV_CIRCUITS", "",
     "MassFabricatorRecipes.java:52"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Wetware Mainframe (UHV) — tier UEV trở lên", "8 → 64", GT,
     "GTItems.WETWARE_MAINFRAME_UHV (UEV:8, UIV:16, UXV:32, OpV:64, MAX:64)", "",
     "MassFabricatorRecipes.java:67"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Field Generator theo tier", "2 (MAX: 4)", GT,
     "GTItems.FIELD_GENERATOR_UHV/UEV/UIV/UXV/OpV", "", "MassFabricatorRecipes.java:53"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Robot Arm theo tier", "2 (MAX: 4)", GT,
     "GTItems.ROBOT_ARM_UHV/UEV/UIV/UXV/OpV", "", "MassFabricatorRecipes.java:54"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Electric Pump theo tier", "2 (MAX: 4)", GT,
     "GTItems.ELECTRIC_PUMP_UHV/UEV/UIV/UXV/OpV", "", "MassFabricatorRecipes.java:55"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Europium Cable (1x)", "4 → 64", GT,
     "TagPrefix.cableGtSingle + GTMaterials.Europium (UHV:4 … MAX:64)", "",
     "MassFabricatorRecipes.java:56"),
    ("Mass Fabricator", "Assembler · UHV→MAX · 600→1.600t",
     "Soldering Alloy (lỏng)", "1.152 → 6.912 mB", GT,
     "GTMaterials.SolderingAlloy — L×8 (UHV) … L×48 (MAX), L = 144 mB", "",
     "MassFabricatorRecipes.java:57"),
]

TITLE_FILL = PatternFill("solid", fgColor="FF17324D")
SUB_FILL = PatternFill("solid", fgColor="FFEAF2F8")
HEAD_FILL = PatternFill("solid", fgColor="FF0F766E")
GT_FILL = PatternFill("solid", fgColor="FFE8F5E9")
TST_FILL = PatternFill("solid", fgColor="FFFFF8E1")
VAN_FILL = PatternFill("solid", fgColor="FFF1F5F9")

TITLE_FONT = Font(name="Play", size=18, bold=True, color="FFFFFFFF")
SUB_FONT = Font(name="Aptos", size=10, color="FF334155")
HEAD_FONT = Font(name="Aptos", size=10, bold=True, color="FFFFFFFF")
BODY_FONT = Font(name="Aptos", size=10)
GT_FONT = Font(name="Aptos", size=10, bold=True, color="FF1B5E20")
TST_FONT = Font(name="Aptos", size=10, bold=True, color="FF3B2F00")
VAN_FONT = Font(name="Aptos", size=10, bold=True, color="FF475569")

WIDTHS = {"A": 6, "B": 30, "C": 34, "D": 40, "E": 22, "F": 13, "G": 72, "H": 52, "I": 40}
SRC_STYLE = {GT: (GT_FILL, GT_FONT), TST: (TST_FILL, TST_FONT), VAN: (VAN_FILL, VAN_FONT)}


def main():
    wb = load_workbook(BOOK)
    if SHEET in wb.sheetnames:
        del wb[SHEET]
    ws = wb.create_sheet(SHEET, wb.sheetnames.index("Danh sách máy") + 1)
    ncol = len(HEADERS)
    last_col = get_column_letter(ncol)

    ws["A1"] = "Nguyên liệu chế tạo controller — 9 multiblock/máy đã port"
    ws["A1"].font, ws["A1"].fill = TITLE_FONT, TITLE_FILL
    ws["A1"].alignment = Alignment(vertical="center")
    ws.merge_cells(f"A1:{last_col}1")
    ws.row_dimensions[1].height = 36

    ws["A2"] = ("Chỉ gồm nguyên liệu của công thức tạo ra chính controller (machine item), "
                "đọc trực tiếp từ src/main/java/com/tstmodern/data/recipe/*.java. "
                "Cột Nguồn phân loại GTCEu / TST-Modern / Vanilla; cột Chú thích GTCEu ghi symbol Java "
                "đã xác minh có trong gtceu-1.20.1-7.4.0. Các block cấu trúc (structure) và "
                "recipe vận hành không nằm trong sheet này.")
    ws["A2"].font, ws["A2"].fill = SUB_FONT, SUB_FILL
    ws["A2"].alignment = Alignment(vertical="center", wrap_text=True)
    ws.merge_cells(f"A2:{last_col}2")
    ws.row_dimensions[2].height = 46

    for i, head in enumerate(HEADERS, 1):
        c = ws.cell(row=4, column=i, value=head)
        c.font, c.fill = HEAD_FONT, HEAD_FILL
        c.alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
    ws.row_dimensions[4].height = 33.75

    for n, row in enumerate(ROWS, start=1):
        r = 4 + n
        machine, recipe, ing, qty, src, gtnote, producer, loc = row
        values = [n, machine, recipe, ing, qty, src, gtnote, producer, loc]
        for i, v in enumerate(values, 1):
            c = ws.cell(row=r, column=i, value=v)
            c.font = BODY_FONT
            c.alignment = Alignment(vertical="center", wrap_text=(i in (2, 3, 4, 7, 8, 9)))
        fill, font = SRC_STYLE[src]
        ws.cell(row=r, column=6).fill = fill
        ws.cell(row=r, column=6).font = font
        ws.cell(row=r, column=6).alignment = Alignment(horizontal="center", vertical="center")

    last_row = 4 + len(ROWS)
    for col, width in WIDTHS.items():
        ws.column_dimensions[col].width = width
    table = Table(displayName="ControllerIngredients", ref=f"A4:{last_col}{last_row}")
    table.tableStyleInfo = TableStyleInfo(name=f"{SHEET}-style", showRowStripes=True)
    ws.add_table(table)
    ws.freeze_panes = "A5"

    wb.calculation.fullCalcOnLoad = True
    wb.save(BOOK)
    print(f"sheet '{SHEET}' written: {len(ROWS)} rows, A4:{last_col}{last_row}")
    print("sheet order:", wb.sheetnames)
    counts = {}
    for row in ROWS:
        counts[row[4]] = counts.get(row[4], 0) + 1
    print("by source:", counts)
    machines = []
    for row in ROWS:
        if row[0] not in machines:
            machines.append(row[0])
    print("machines:", len(machines), machines)


if __name__ == "__main__":
    main()
