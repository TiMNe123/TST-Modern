package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.MAX;
import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.OpV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.UXV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.machine.DisassemblerDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Construction and progression recipes owned by the Large Disassembler. */
public final class DisassemblerRecipes {
    private static final int MAX_STACK_SIZE = 64;

    private static final List<RecipeSpec> RECIPE_SPECS = List.of(
            assemblerCasing("lv", "steel", "plate_dense", "steel", "steel", "tin", 4, 8, 10,
                    List.of(input(circuit("lv"), 16)), 288),
            assemblerCasing("mv", "aluminium", "plate", "aluminium", "aluminium", "annealed_copper", 4, 8,
                    10, List.of(input(circuit("mv"), 8), input(circuit("lv"), 16)), 432),
            assemblerCasing("hv", "stainless_steel", "plate", "stainless_steel", "stainless_steel", "gold", 4,
                    8, 10, List.of(input(circuit("hv"), 8), input(circuit("mv"), 16)), 576),
            assemblerCasing("ev", "titanium", "plate", "titanium", "titanium", "aluminium", 4, 8, 10,
                    List.of(input(circuit("ev"), 8), input(circuit("hv"), 16)), 720),
            assemblerCasing("iv", "tungsten_steel", "plate_dense", "tungsten_steel", "tungsten_steel",
                    "tungsten", 4, 8, 10, List.of(input(circuit("iv"), 8), input(circuit("ev"), 16)), 864),

            tierAssemblyLine("luv", "iv", "europium", "rhodium_plated_palladium", "ruridit",
                    "rhodium_plated_palladium", "vanadium_gallium", "rhodium_plated_palladium", 3456, 1728,
                    4000, IV),
            tierAssemblyLine("zpm", "luv", "iridium", "rhodium_plated_palladium", "iridium",
                    "rhodium_plated_palladium", "naquadah", "iridium", 4032, 2016, 5000, LuV),
            tierAssemblyLine("uv", "zpm", "tritanium", "rhodium_plated_palladium", "tritanium", "tritanium",
                    "naquadah_alloy", "osmium", 4608, 2304, 6000, ZPM),
            tierAssemblyLine("uhv", "uv", "neutronium", "darmstadtium", "neutronium", "darmstadtium",
                    "tritanium", "neutronium", 5184, 2592, 7000, UV),

            endgameCasing("uev", "uhv", "neutronium", 1, 2, "naquadria", 4, "darmstadtium", 16, 8, 8,
                    10, 16, 8, 6912, "darmstadtium", 3456, 8000, 900, UHV, 3600),
            endgameCasing("uiv", "uev", "tritanium", 2, 4, "tritanium", 8, "tritanium", 24, 16, 12,
                    16, 24, 12, 10368, "tritanium", 5184, 12000, 1200, UEV, 4800),
            endgameCasing("umv", "uiv", "neutronium", 4, 6, "neutronium", 12, "tritanium", 32, 24, 16,
                    24, 32, 16, 13824, "neutronium", 6912, 16000, 1500, UIV, 6000),
            endgameCasing("uxv", "umv", "neutronium", 8, 8, "neutronium", 16, "tritanium", 48, 32, 24,
                    32, 48, 24, 18432, "neutronium", 9216, 24000, 1800, UXV, 7200),
            endgameCasing("max", "uxv", "neutronium", 16, 16, "neutronium", 32, "tritanium", 64, 64, 32,
                    48, 64, 32, 27648, "neutronium", 13824, 32000, 2400, OpV, 9600),

            recipe("assembler/molecular_casing", RecipeType.ASSEMBLER, block("molecular_casing"), 1,
                    inputs(
                            input(machine("energy_input_hatch", "uhv"), 1),
                            input(ore("plate", "osmiridium"), 54),
                            input(ore("foil", "osmium"), 12),
                            input(ore("screw", "tungsten_steel"), 24),
                            input(ore("ring", "tungsten_steel"), 24),
                            input(component("field_generator", "iv"), 1)),
                    fluids(fluid("osmium", 1296)), 800, EutSpec.va(UV), ResearchSpec.NONE),
            recipe("assembly_line/hollow_casing", RecipeType.ASSEMBLY_LINE, block("hollow_casing"), 2,
                    inputs(
                            input(block("molecular_casing"), 1),
                            input(ore("plate", "europium"), 18),
                            input(ore("plate", "plutonium"), 16),
                            input(ore("plate", "lead"), 16),
                            input(ore("plate", "uranium"), 16),
                            input(ore("screw", "europium"), 16)),
                    fluids(
                            fluid("osmium", 1296),
                            // P7: GTCEu 7.4 has no legacy Super Coolant; PCB Coolant is the approved replacement.
                            fluid("pcb_coolant", 2000),
                            fluid("argon", 1000)),
                    200, EutSpec.literal(200_000), ResearchSpec.scanner(block("molecular_casing"), 1000, LuV)),
            recipe("assembly_line/disassembler", RecipeType.ASSEMBLY_LINE, machine("disassembler", null), 1,
                    inputs(
                            input(machine("assembler", "uhv"), 64),
                            input(component("field_generator", "uhv"), 16),
                            input(component("electric_pump", "uhv"), 64),
                            input(component("conveyor_module", "uhv"), 64),
                            input(component("robot_arm", "uhv"), 256),
                            input(machine("energy_input_hatch", "uhv"), 64),
                            input(ore("frame_gt", "neutronium"), 16),
                            input(ore("plate", "osmiridium"), 144)),
                    fluids(
                            fluid("soldering_alloy", 147_456),
                            fluid("uu_matter", 128_000),
                            fluid("pcb_coolant", 768_000)),
                    72_000, EutSpec.va(UEV), ResearchSpec.station(machine("assembler", "uhv"), 72_000, UHV, 256, tool("data_orb"))));

    private DisassemblerRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        RECIPE_SPECS.forEach(spec -> register(spec, provider));
    }

    static List<RecipeSpec> recipeSpecs() {
        return RECIPE_SPECS;
    }

    private static RecipeSpec assemblerCasing(String tier, String frameMaterial, String platePrefix,
                                               String gearMaterial, String smallGearMaterial, String cableMaterial,
                                               int arms, int pistons, int motors, List<ItemInputSpec> circuits,
                                               int solder) {
        List<ItemInputSpec> itemInputs = new ArrayList<>(inputs(
                input(ore("frame_gt", frameMaterial), 1),
                input(ore(platePrefix, frameMaterial), platePrefix.equals("plate") ? 9 : 1),
                input(ore("gear", gearMaterial), 4),
                input(ore("gear_small", smallGearMaterial), 16),
                input(ore("cable_gt_single", cableMaterial), 8),
                input(component("robot_arm", tier), arms),
                input(component("electric_piston", tier), pistons),
                input(component("electric_motor", tier), motors)));
        itemInputs.addAll(circuits);
        return recipe("assembler/component_assembly_line_casing_" + tier, RecipeType.ASSEMBLER, casing(tier), 1,
                itemInputs, fluids(fluid("soldering_alloy", solder)), 320, EutSpec.va(tierIndex(tier)),
                ResearchSpec.NONE);
    }

    private static RecipeSpec tierAssemblyLine(String tier, String predecessor, String frameMaterial,
                                               String denseMaterial, String gearMaterial, String smallGearMaterial,
                                               String cableMaterial, String materialFluid, int solder,
                                               int materialFluidAmount, int lubricant, int eutTier) {
        return recipe("assembly_line/component_assembly_line_casing_" + tier, RecipeType.ASSEMBLY_LINE,
                casing(tier), 1,
                inputs(
                        input(casing(predecessor), 1),
                        input(ore("frame_gt", frameMaterial), 1),
                        input(ore("plate_dense", denseMaterial), 1),
                        input(ore("gear", gearMaterial), 4),
                        input(ore("gear_small", smallGearMaterial), 16),
                        input(ore("cable_gt_single", cableMaterial), 8),
                        input(component("robot_arm", tier), 8),
                        input(component("electric_piston", tier), 10),
                        input(component("electric_motor", tier), 16),
                        input(circuit(tier), 8),
                        input(circuit(predecessor), 16)),
                fluids(
                        fluid("soldering_alloy", solder),
                        fluid(materialFluid, materialFluidAmount),
                        fluid("lubricant", lubricant)),
                600, EutSpec.va(eutTier), ResearchSpec.scanner(casing(predecessor), 1800, eutTier));
    }

    private static RecipeSpec endgameCasing(String tier, String predecessor, String frameMaterial, int frames,
                                            int densePlates, String gearMaterial, int gears,
                                            String smallGearMaterial, int smallGears, int cables, int arms,
                                            int pistons, int motors, int circuits, int solder,
                                            String materialFluid, int materialFluidAmount, int lubricant,
                                            int duration, int eutTier, int researchDuration) {
        return recipe("assembly_line/component_assembly_line_casing_" + tier, RecipeType.ASSEMBLY_LINE,
                casing(tier), 1,
                inputs(
                        input(casing(predecessor), 1),
                        input(ore("frame_gt", frameMaterial), frames),
                        input(ore("plate_dense", "darmstadtium"), densePlates),
                        input(ore("gear", gearMaterial), gears),
                        input(ore("gear_small", smallGearMaterial), smallGears),
                        input(ore("cable_gt_single", "tritanium"), cables),
                        input(component("robot_arm", "uhv"), arms),
                        input(component("electric_piston", "uhv"), pistons),
                        input(component("electric_motor", "uhv"), motors),
                        input(circuit("uhv"), circuits)),
                fluids(
                        fluid("soldering_alloy", solder),
                        fluid(materialFluid, materialFluidAmount),
                        fluid("lubricant", lubricant)),
                duration, EutSpec.va(eutTier), ResearchSpec.station(casing(predecessor), researchDuration, eutTier));
    }

    private static RecipeSpec recipe(String id, RecipeType type, ItemRef output, int outputCount,
                                     List<ItemInputSpec> itemInputs, List<FluidInputSpec> fluidInputs,
                                     int duration, EutSpec eut, ResearchSpec research) {
        return new RecipeSpec(id, type, output, outputCount, itemInputs, fluidInputs, duration, eut, research);
    }

    private static void register(RecipeSpec spec, Consumer<FinishedRecipe> provider) {
        GTRecipeBuilder builder = switch (spec.type()) {
            case ASSEMBLER -> GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id(spec.id()));
            case ASSEMBLY_LINE -> GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id(spec.id()));
        };
        for (ItemInputSpec itemInput : spec.itemInputs()) {
            addItemInput(builder, itemInput);
        }
        for (FluidInputSpec fluidInput : spec.fluidInputs()) {
            builder.inputFluids(resolveMaterial(fluidInput.material()).getFluid(fluidInput.amount()));
        }
        builder.outputItems(resolveStack(spec.output(), spec.outputCount()))
                .duration(spec.duration())
                .EUt(spec.eut().value());
        applyResearch(builder, spec.research());
        builder.save(provider);
    }

    private static void addItemInput(GTRecipeBuilder builder, ItemInputSpec input) {
        if (input.item().key().startsWith("ore/")) {
            String[] parts = input.item().key().split("/", 3);
            TagPrefix prefix = resolvePrefix(parts[1]);
            Material material = resolveMaterial(parts[2]);
            input.chunks().forEach(chunk -> builder.inputItems(prefix, material, chunk));
        } else if (input.item().key().startsWith("circuit/")) {
            TagKey<Item> circuit = resolveCircuit(input.item().key().substring("circuit/".length()));
            input.chunks().forEach(chunk -> builder.inputItems(circuit, chunk));
        } else {
            input.chunks().forEach(chunk -> builder.inputItems(resolveStack(input.item(), chunk)));
        }
    }

    private static void applyResearch(GTRecipeBuilder builder, ResearchSpec research) {
        if (research.kind() == ResearchKind.NONE) {
            return;
        }
        ItemStack target = resolveStack(research.target(), 1);
        if (research.kind() == ResearchKind.SCANNER) {
            builder.scannerResearch(scan -> scan
                    .researchStack(target)
                    .duration(research.durationTicks())
                    .EUt(research.eut().value()));
        } else {
            builder.stationResearch(station -> {
                station.researchStack(target)
                        .CWUt(research.cwuPerTick(), research.durationTicks())
                        .EUt(research.eut().value());
                if (research.dataStack() != null) {
                    station.dataStack(resolveStack(research.dataStack(), 1));
                }
                return station;
            });
        }
    }

    private static ItemStack resolveStack(ItemRef item, int count) {
        return switch (item.key()) {
            case "block/component_assembly_line_casing_lv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_LV.get(), count);
            case "block/component_assembly_line_casing_mv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_MV.get(), count);
            case "block/component_assembly_line_casing_hv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_HV.get(), count);
            case "block/component_assembly_line_casing_ev" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_EV.get(), count);
            case "block/component_assembly_line_casing_iv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_IV.get(), count);
            case "block/component_assembly_line_casing_luv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_LUV.get(), count);
            case "block/component_assembly_line_casing_zpm" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_ZPM.get(), count);
            case "block/component_assembly_line_casing_uv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UV.get(), count);
            case "block/component_assembly_line_casing_uhv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UHV.get(), count);
            case "block/component_assembly_line_casing_uev" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UEV.get(), count);
            case "block/component_assembly_line_casing_uiv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UIV.get(), count);
            case "block/component_assembly_line_casing_umv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UMV.get(), count);
            case "block/component_assembly_line_casing_uxv" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UXV.get(), count);
            case "block/component_assembly_line_casing_max" -> new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_MAX.get(), count);
            case "block/molecular_casing" -> new ItemStack(TSTBlocks.MOLECULAR_CASING.get(), count);
            case "block/hollow_casing" -> new ItemStack(TSTBlocks.HOLLOW_CASING.get(), count);
            case "machine/disassembler" -> DisassemblerDefinition.MACHINE.asStack(count);
            case "machine/assembler/lv" -> GTMachines.ASSEMBLER[LV].asStack(count);
            case "machine/assembler/uhv" -> GTMachines.ASSEMBLER[UHV].asStack(count);
            case "machine/energy_input_hatch/uhv" -> GTMachines.ENERGY_INPUT_HATCH[UHV].asStack(count);
            case "component/robot_arm/lv" -> GTItems.ROBOT_ARM_LV.asStack(count);
            case "component/robot_arm/mv" -> GTItems.ROBOT_ARM_MV.asStack(count);
            case "component/robot_arm/hv" -> GTItems.ROBOT_ARM_HV.asStack(count);
            case "component/robot_arm/ev" -> GTItems.ROBOT_ARM_EV.asStack(count);
            case "component/robot_arm/iv" -> GTItems.ROBOT_ARM_IV.asStack(count);
            case "component/robot_arm/luv" -> GTItems.ROBOT_ARM_LuV.asStack(count);
            case "component/robot_arm/zpm" -> GTItems.ROBOT_ARM_ZPM.asStack(count);
            case "component/robot_arm/uv" -> GTItems.ROBOT_ARM_UV.asStack(count);
            case "component/robot_arm/uhv" -> GTItems.ROBOT_ARM_UHV.asStack(count);
            case "component/electric_piston/lv" -> GTItems.ELECTRIC_PISTON_LV.asStack(count);
            case "component/electric_piston/mv" -> GTItems.ELECTRIC_PISTON_MV.asStack(count);
            case "component/electric_piston/hv" -> GTItems.ELECTRIC_PISTON_HV.asStack(count);
            case "component/electric_piston/ev" -> GTItems.ELECTRIC_PISTON_EV.asStack(count);
            case "component/electric_piston/iv" -> GTItems.ELECTRIC_PISTON_IV.asStack(count);
            case "component/electric_piston/luv" -> GTItems.ELECTRIC_PISTON_LuV.asStack(count);
            case "component/electric_piston/zpm" -> GTItems.ELECTRIC_PISTON_ZPM.asStack(count);
            case "component/electric_piston/uv" -> GTItems.ELECTRIC_PISTON_UV.asStack(count);
            case "component/electric_piston/uhv" -> GTItems.ELECTRIC_PISTON_UHV.asStack(count);
            case "component/electric_motor/lv" -> GTItems.ELECTRIC_MOTOR_LV.asStack(count);
            case "component/electric_motor/mv" -> GTItems.ELECTRIC_MOTOR_MV.asStack(count);
            case "component/electric_motor/hv" -> GTItems.ELECTRIC_MOTOR_HV.asStack(count);
            case "component/electric_motor/ev" -> GTItems.ELECTRIC_MOTOR_EV.asStack(count);
            case "component/electric_motor/iv" -> GTItems.ELECTRIC_MOTOR_IV.asStack(count);
            case "component/electric_motor/luv" -> GTItems.ELECTRIC_MOTOR_LuV.asStack(count);
            case "component/electric_motor/zpm" -> GTItems.ELECTRIC_MOTOR_ZPM.asStack(count);
            case "component/electric_motor/uv" -> GTItems.ELECTRIC_MOTOR_UV.asStack(count);
            case "component/electric_motor/uhv" -> GTItems.ELECTRIC_MOTOR_UHV.asStack(count);
            case "component/field_generator/iv" -> GTItems.FIELD_GENERATOR_IV.asStack(count);
            case "component/field_generator/uhv" -> GTItems.FIELD_GENERATOR_UHV.asStack(count);
            case "component/electric_pump/uhv" -> GTItems.ELECTRIC_PUMP_UHV.asStack(count);
            case "component/conveyor_module/uhv" -> GTItems.CONVEYOR_MODULE_UHV.asStack(count);
            case "tool/data_orb" -> GTItems.TOOL_DATA_ORB.asStack(count);
            case "tool/data_stick" -> GTItems.TOOL_DATA_STICK.asStack(count);
            default -> throw new IllegalArgumentException("Unknown item reference: " + item.key());
        };
    }

    private static TagPrefix resolvePrefix(String prefix) {
        return switch (prefix) {
            case "frame_gt" -> TagPrefix.frameGt;
            case "plate_dense" -> TagPrefix.plateDense;
            case "plate" -> TagPrefix.plate;
            case "gear" -> TagPrefix.gear;
            case "gear_small" -> TagPrefix.gearSmall;
            case "cable_gt_single" -> TagPrefix.cableGtSingle;
            case "foil" -> TagPrefix.foil;
            case "screw" -> TagPrefix.screw;
            case "ring" -> TagPrefix.ring;
            default -> throw new IllegalArgumentException("Unknown tag prefix: " + prefix);
        };
    }

    private static Material resolveMaterial(String material) {
        return switch (material) {
            case "steel" -> GTMaterials.Steel;
            case "tin" -> GTMaterials.Tin;
            case "aluminium" -> GTMaterials.Aluminium;
            case "annealed_copper" -> GTMaterials.AnnealedCopper;
            case "stainless_steel" -> GTMaterials.StainlessSteel;
            case "gold" -> GTMaterials.Gold;
            case "titanium" -> GTMaterials.Titanium;
            case "tungsten_steel" -> GTMaterials.TungstenSteel;
            case "tungsten" -> GTMaterials.Tungsten;
            case "europium" -> GTMaterials.Europium;
            case "rhodium_plated_palladium" -> GTMaterials.RhodiumPlatedPalladium;
            case "ruridit" -> GTMaterials.Ruridit;
            case "vanadium_gallium" -> GTMaterials.VanadiumGallium;
            case "iridium" -> GTMaterials.Iridium;
            case "naquadah" -> GTMaterials.Naquadah;
            case "tritanium" -> GTMaterials.Tritanium;
            case "naquadah_alloy" -> GTMaterials.NaquadahAlloy;
            case "osmium" -> GTMaterials.Osmium;
            case "neutronium" -> GTMaterials.Neutronium;
            case "darmstadtium" -> GTMaterials.Darmstadtium;
            case "naquadria" -> GTMaterials.Naquadria;
            case "osmiridium" -> GTMaterials.Osmiridium;
            case "plutonium" -> GTMaterials.Plutonium241;
            case "lead" -> GTMaterials.Lead;
            case "uranium" -> GTMaterials.Uranium238;
            case "soldering_alloy" -> GTMaterials.SolderingAlloy;
            case "lubricant" -> GTMaterials.Lubricant;
            case "pcb_coolant" -> GTMaterials.PCBCoolant;
            case "argon" -> GTMaterials.Argon;
            case "uu_matter" -> GTMaterials.UUMatter;
            default -> throw new IllegalArgumentException("Unknown material: " + material);
        };
    }

    private static TagKey<Item> resolveCircuit(String tier) {
        return switch (tier) {
            case "lv" -> CustomTags.LV_CIRCUITS;
            case "mv" -> CustomTags.MV_CIRCUITS;
            case "hv" -> CustomTags.HV_CIRCUITS;
            case "ev" -> CustomTags.EV_CIRCUITS;
            case "iv" -> CustomTags.IV_CIRCUITS;
            case "luv" -> CustomTags.LuV_CIRCUITS;
            case "zpm" -> CustomTags.ZPM_CIRCUITS;
            case "uv" -> CustomTags.UV_CIRCUITS;
            case "uhv" -> CustomTags.UHV_CIRCUITS;
            default -> throw new IllegalArgumentException("Unknown circuit tier: " + tier);
        };
    }

    private static int tierIndex(String tier) {
        return switch (tier) {
            case "lv" -> LV;
            case "mv" -> MV;
            case "hv" -> HV;
            case "ev" -> EV;
            case "iv" -> IV;
            case "luv" -> LuV;
            case "zpm" -> ZPM;
            case "uv" -> UV;
            case "uhv" -> UHV;
            case "uev" -> UEV;
            case "uiv" -> UIV;
            // GTCEu calls the numeric tiers used by TST's UMV and UXV casings UXV and OpV.
            case "umv" -> UXV;
            case "uxv" -> OpV;
            case "max" -> MAX;
            default -> throw new IllegalArgumentException("Unknown voltage tier: " + tier);
        };
    }

    private static ItemRef casing(String tier) {
        return block("component_assembly_line_casing_" + tier);
    }

    private static ItemRef block(String name) {
        return new ItemRef("block/" + name);
    }

    private static ItemRef component(String component, String tier) {
        return new ItemRef("component/" + component + "/" + tier);
    }

    private static ItemRef machine(String machine, String tier) {
        return new ItemRef("machine/" + machine + (tier == null ? "" : "/" + tier));
    }

    private static ItemRef ore(String prefix, String material) {
        return new ItemRef("ore/" + prefix + "/" + material);
    }

    private static ItemRef circuit(String tier) {
        return new ItemRef("circuit/" + tier);
    }

    private static ItemRef tool(String name) {
        return new ItemRef("tool/" + name);
    }

    private static ItemInputSpec input(ItemRef item, int count) {
        List<Integer> chunks = new ArrayList<>();
        int remaining = count;
        while (remaining > 0) {
            int chunk = Math.min(MAX_STACK_SIZE, remaining);
            chunks.add(chunk);
            remaining -= chunk;
        }
        return new ItemInputSpec(item, count, chunks);
    }

    private static FluidInputSpec fluid(String material, int amount) {
        return new FluidInputSpec(material, amount);
    }

    private static List<ItemInputSpec> inputs(ItemInputSpec... inputs) {
        return List.copyOf(Arrays.asList(inputs));
    }

    private static List<FluidInputSpec> fluids(FluidInputSpec... fluids) {
        return List.copyOf(Arrays.asList(fluids));
    }

    enum RecipeType {
        ASSEMBLER("assembler"),
        ASSEMBLY_LINE("assembly_line");

        private final String key;

        RecipeType(String key) {
            this.key = key;
        }

        String key() {
            return key;
        }

    }

    enum ResearchKind {
        NONE,
        SCANNER,
        STATION
    }

    record ItemRef(String key) {}

    record ItemInputSpec(ItemRef item, int totalCount, List<Integer> chunks) {
        ItemInputSpec {
            chunks = List.copyOf(chunks);
        }
    }

    record FluidInputSpec(String material, int amount) {}

    record EutSpec(String kind, int tier, long literal) {
        static EutSpec va(int tier) {
            return new EutSpec("va", tier, 0);
        }

        static EutSpec literal(long eut) {
            return new EutSpec("literal", -1, eut);
        }

        long value() {
            return kind.equals("va") ? VA[tier] : literal;
        }

        String key() {
            return kind.equals("va") ? "va/" + tierName(tier) : "literal/" + literal;
        }
    }

    record ResearchSpec(ResearchKind kind, ItemRef target, int durationTicks, EutSpec eut, int cwuPerTick, ItemRef dataStack) {
        private static final ResearchSpec NONE = new ResearchSpec(ResearchKind.NONE, null, 0, null, 0, null);

        static ResearchSpec scanner(ItemRef target, int durationTicks, int eutTier) {
            return new ResearchSpec(ResearchKind.SCANNER, target, durationTicks, EutSpec.va(eutTier), 0, null);
        }

        static ResearchSpec station(ItemRef target, int durationTicks, int eutTier) {
            return new ResearchSpec(ResearchKind.STATION, target, durationTicks, EutSpec.va(eutTier), 1, null);
        }

        static ResearchSpec station(ItemRef target, int durationTicks, int eutTier, int cwuPerTick, ItemRef dataStack) {
            return new ResearchSpec(ResearchKind.STATION, target, durationTicks, EutSpec.va(eutTier), cwuPerTick, dataStack);
        }

        String key() {
            if (kind == ResearchKind.NONE) {
                return "none";
            }
            return kind.name().toLowerCase() + ":" + target.key() + ":" + durationTicks + ":" + eut.key() + ":" + cwuPerTick + ":" + (dataStack == null ? "default" : dataStack.key());
        }
    }

    record RecipeSpec(String id, RecipeType type, ItemRef output, int outputCount,
                      List<ItemInputSpec> itemInputs, List<FluidInputSpec> fluidInputs,
                      int duration, EutSpec eut, ResearchSpec research) {
        RecipeSpec {
            itemInputs = List.copyOf(itemInputs);
            fluidInputs = List.copyOf(fluidInputs);
        }
    }

    private static String tierName(int tier) {
        return switch (tier) {
            case LV -> "lv";
            case MV -> "mv";
            case HV -> "hv";
            case EV -> "ev";
            case IV -> "iv";
            case LuV -> "luv";
            case ZPM -> "zpm";
            case UV -> "uv";
            case UHV -> "uhv";
            case UEV -> "uev";
            case UIV -> "uiv";
            case UXV -> "umv";
            case OpV -> "uxv";
            case MAX -> "max";
            default -> throw new IllegalArgumentException("Unknown voltage tier index: " + tier);
        };
    }
}
