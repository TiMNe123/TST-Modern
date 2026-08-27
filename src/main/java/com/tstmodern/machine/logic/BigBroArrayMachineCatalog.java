package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Authoritative whitelist catalog for machines embeddable in the Big Bro Array.
 */
public final class BigBroArrayMachineCatalog {

    public record Entry(
            ResourceLocation definitionId,
            Supplier<GTRecipeType> recipeTypeSupplier,
            int tier,
            BigBroArrayMode mode) {
        public Entry {
            Objects.requireNonNull(definitionId, "definitionId");
            Objects.requireNonNull(recipeTypeSupplier, "recipeTypeSupplier");
            Objects.requireNonNull(mode, "mode");
        }

        public GTRecipeType recipeType() {
            return recipeTypeSupplier.get();
        }
    }

    private static final Map<ResourceLocation, Entry> CATALOG = buildCatalog();

    private BigBroArrayMachineCatalog() {}

    public static Optional<Entry> find(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MetaMachineItem machineItem)) {
            return Optional.empty();
        }
        MachineDefinition def = machineItem.getDefinition();
        return def == null ? Optional.empty() : find(def.getId());
    }

    public static Optional<Entry> find(ResourceLocation id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CATALOG.get(id));
    }

    public static Map<ResourceLocation, Entry> allEntries() {
        return CATALOG;
    }

    private static Map<ResourceLocation, Entry> buildCatalog() {
        Map<ResourceLocation, Entry> map = new HashMap<>();

        // Standard electric tiers LV..OpV (and UHV..MAX where available)
        int[] allElectricTiers = new int[] {
                GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV,
                GTValues.IV, GTValues.LuV, GTValues.ZPM, GTValues.UV,
                GTValues.UHV, GTValues.UEV, GTValues.UIV, GTValues.UXV,
                GTValues.OpV, GTValues.MAX
        };

        // 27 Standard GTCEu native processor families
        registerFamily(map, "gtceu", "macerator", () -> GTRecipeTypes.MACERATOR_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "ore_washer", () -> GTRecipeTypes.ORE_WASHER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "chemical_bath", () -> GTRecipeTypes.CHEMICAL_BATH_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "thermal_centrifuge", () -> GTRecipeTypes.THERMAL_CENTRIFUGE_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "electric_furnace", () -> GTRecipeTypes.FURNACE_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "arc_furnace", () -> GTRecipeTypes.ARC_FURNACE_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "bender", () -> GTRecipeTypes.BENDER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "wiremill", () -> GTRecipeTypes.WIREMILL_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "lathe", () -> GTRecipeTypes.LATHE_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "forge_hammer", () -> GTRecipeTypes.FORGE_HAMMER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "extruder", () -> GTRecipeTypes.EXTRUDER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "compressor", () -> GTRecipeTypes.COMPRESSOR_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "forming_press", () -> GTRecipeTypes.FORMING_PRESS_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "fluid_solidifier", () -> GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "extractor", () -> GTRecipeTypes.EXTRACTOR_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "laser_engraver", () -> GTRecipeTypes.LASER_ENGRAVER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "autoclave", () -> GTRecipeTypes.AUTOCLAVE_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "mixer", () -> GTRecipeTypes.MIXER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "alloy_smelter", () -> GTRecipeTypes.ALLOY_SMELTER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "electrolyzer", () -> GTRecipeTypes.ELECTROLYZER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "sifter", () -> GTRecipeTypes.SIFTER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "chemical_reactor", () -> GTRecipeTypes.CHEMICAL_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "electromagnetic_separator", () -> GTRecipeTypes.ELECTROMAGNETIC_SEPARATOR_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "centrifuge", () -> GTRecipeTypes.CENTRIFUGE_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "cutter", () -> GTRecipeTypes.CUTTER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "assembler", () -> GTRecipeTypes.ASSEMBLER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);
        registerFamily(map, "gtceu", "circuit_assembler", () -> GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES, BigBroArrayMode.PROCESSOR, allElectricTiers);

        // TSTModern Mass Fabricator family (UHV..MAX)
        int[] massFabTiers = new int[] {
                GTValues.UHV, GTValues.UEV, GTValues.UIV, GTValues.UXV, GTValues.OpV, GTValues.MAX
        };
        registerFamily(map, "tstmodern", "mass_fabricator", () -> TSTRecipeTypes.MASS_FABRICATOR, BigBroArrayMode.PROCESSOR, massFabTiers);

        // 3 Supported Generator families (LV, MV, HV)
        int[] generatorTiers = new int[] { GTValues.LV, GTValues.MV, GTValues.HV };
        registerFamily(map, "gtceu", "combustion", () -> GTRecipeTypes.COMBUSTION_GENERATOR_FUELS, BigBroArrayMode.GENERATOR, generatorTiers);
        registerFamily(map, "gtceu", "steam_turbine", () -> GTRecipeTypes.STEAM_TURBINE_FUELS, BigBroArrayMode.GENERATOR, generatorTiers);
        registerFamily(map, "gtceu", "gas_turbine", () -> GTRecipeTypes.GAS_TURBINE_FUELS, BigBroArrayMode.GENERATOR, generatorTiers);

        return Collections.unmodifiableMap(map);
    }

    private static void registerFamily(Map<ResourceLocation, Entry> map, String namespace, String baseName,
                                       Supplier<GTRecipeType> recipeType, BigBroArrayMode mode, int[] tiers) {
        for (int tier : tiers) {
            String tierPrefix = GTValues.VN[tier].toLowerCase(Locale.ROOT);
            ResourceLocation id = new ResourceLocation(namespace, tierPrefix + "_" + baseName);
            if (map.containsKey(id)) {
                throw new IllegalStateException("Duplicate entry in BigBroArray catalog: " + id);
            }
            map.put(id, new Entry(id, recipeType, tier, mode));
        }
    }
}
