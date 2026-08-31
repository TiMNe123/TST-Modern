package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/** Authoritative whitelist of concrete registered machines embeddable in the Big Bro Array. */
public final class BigBroArrayMachineCatalog {

    public record Entry(
            MachineDefinition definition,
            Supplier<GTRecipeType> recipeTypeSupplier,
            int tier,
            BigBroArrayMode mode) {
        public Entry {
            Objects.requireNonNull(definition, "definition");
            Objects.requireNonNull(recipeTypeSupplier, "recipeTypeSupplier");
            Objects.requireNonNull(mode, "mode");
        }

        public ResourceLocation definitionId() {
            return definition.getId();
        }

        public GTRecipeType recipeType() {
            return recipeTypeSupplier.get();
        }
    }

    private record Family(Supplier<GTRecipeType> recipeType, BigBroArrayMode mode) {}

    private static final Map<String, Family> PROCESSOR_FAMILIES = Map.ofEntries(
            family("macerator", () -> GTRecipeTypes.MACERATOR_RECIPES),
            family("ore_washer", () -> GTRecipeTypes.ORE_WASHER_RECIPES),
            family("chemical_bath", () -> GTRecipeTypes.CHEMICAL_BATH_RECIPES),
            family("thermal_centrifuge", () -> GTRecipeTypes.THERMAL_CENTRIFUGE_RECIPES),
            family("electric_furnace", () -> GTRecipeTypes.FURNACE_RECIPES),
            family("arc_furnace", () -> GTRecipeTypes.ARC_FURNACE_RECIPES),
            family("bender", () -> GTRecipeTypes.BENDER_RECIPES),
            family("wiremill", () -> GTRecipeTypes.WIREMILL_RECIPES),
            family("lathe", () -> GTRecipeTypes.LATHE_RECIPES),
            family("forge_hammer", () -> GTRecipeTypes.FORGE_HAMMER_RECIPES),
            family("extruder", () -> GTRecipeTypes.EXTRUDER_RECIPES),
            family("compressor", () -> GTRecipeTypes.COMPRESSOR_RECIPES),
            family("forming_press", () -> GTRecipeTypes.FORMING_PRESS_RECIPES),
            family("fluid_solidifier", () -> GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES),
            family("extractor", () -> GTRecipeTypes.EXTRACTOR_RECIPES),
            family("laser_engraver", () -> GTRecipeTypes.LASER_ENGRAVER_RECIPES),
            family("autoclave", () -> GTRecipeTypes.AUTOCLAVE_RECIPES),
            family("mixer", () -> GTRecipeTypes.MIXER_RECIPES),
            family("alloy_smelter", () -> GTRecipeTypes.ALLOY_SMELTER_RECIPES),
            family("electrolyzer", () -> GTRecipeTypes.ELECTROLYZER_RECIPES),
            family("sifter", () -> GTRecipeTypes.SIFTER_RECIPES),
            family("chemical_reactor", () -> GTRecipeTypes.CHEMICAL_RECIPES),
            family("electromagnetic_separator", () -> GTRecipeTypes.ELECTROMAGNETIC_SEPARATOR_RECIPES),
            family("centrifuge", () -> GTRecipeTypes.CENTRIFUGE_RECIPES),
            family("cutter", () -> GTRecipeTypes.CUTTER_RECIPES),
            family("assembler", () -> GTRecipeTypes.ASSEMBLER_RECIPES),
            family("circuit_assembler", () -> GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES));

    private static final Map<String, Family> GENERATOR_FAMILIES = Map.of(
            "combustion", new Family(() -> GTRecipeTypes.COMBUSTION_GENERATOR_FUELS, BigBroArrayMode.GENERATOR),
            "steam_turbine", new Family(() -> GTRecipeTypes.STEAM_TURBINE_FUELS, BigBroArrayMode.GENERATOR),
            "gas_turbine", new Family(() -> GTRecipeTypes.GAS_TURBINE_FUELS, BigBroArrayMode.GENERATOR));

    private static final Set<Integer> GENERATOR_TIERS = Set.of(GTValues.LV, GTValues.MV, GTValues.HV);
    private static Map<ResourceLocation, Entry> catalog;

    private BigBroArrayMachineCatalog() {}

    public static Optional<Entry> find(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MetaMachineItem machineItem)) {
            return Optional.empty();
        }
        MachineDefinition definition = machineItem.getDefinition();
        return definition == null ? Optional.empty() : find(definition.getId());
    }

    public static Optional<Entry> find(ResourceLocation id) {
        return id == null ? Optional.empty() : Optional.ofNullable(allEntries().get(id));
    }

    public static Map<ResourceLocation, Entry> allEntries() {
        if (catalog == null) {
            Map<ResourceLocation, Entry> built = buildCatalogForDefinitions(GTRegistries.MACHINES.values());
            for (Entry entry : built.values()) {
                if (GTRegistries.MACHINES.get(entry.definitionId()) != entry.definition()) {
                    throw new IllegalStateException("BigBroArray catalog definition is not registry-owned: "
                            + entry.definitionId());
                }
            }
            catalog = Collections.unmodifiableMap(built);
        }
        return catalog;
    }

    static Map<ResourceLocation, Entry> buildCatalogForDefinitions(Iterable<MachineDefinition> definitions) {
        Map<ResourceLocation, Entry> result = new HashMap<>();
        for (MachineDefinition definition : definitions) {
            if (definition == null) continue;
            Entry entry = classify(definition);
            if (entry == null) continue;
            if (result.putIfAbsent(entry.definitionId(), entry) != null) {
                throw new IllegalStateException("Duplicate BigBroArray definition: " + entry.definitionId());
            }
        }
        return result;
    }

    private static Entry classify(MachineDefinition definition) {
        ResourceLocation id = definition.getId();
        int tier = definition.getTier();
        String familyName = familyName(id.getPath(), tier);
        if (familyName == null) return null;

        if ("tstmodern".equals(id.getNamespace()) && "mass_fabricator".equals(familyName)
                && tier >= GTValues.UHV && tier <= GTValues.MAX) {
            return new Entry(definition, () -> TSTRecipeTypes.MASS_FABRICATOR, tier, BigBroArrayMode.PROCESSOR);
        }
        if (!"gtceu".equals(id.getNamespace())) return null;

        Family generator = GENERATOR_FAMILIES.get(familyName);
        if (generator != null) {
            return GENERATOR_TIERS.contains(tier)
                    ? new Entry(definition, generator.recipeType(), tier, generator.mode()) : null;
        }
        Family processor = PROCESSOR_FAMILIES.get(familyName);
        if (processor != null && tier >= GTValues.LV && tier <= GTValues.OpV) {
            return new Entry(definition, processor.recipeType(), tier, processor.mode());
        }
        return null;
    }

    private static String familyName(String path, int tier) {
        if (tier < 0 || tier >= GTValues.VN.length) return null;
        String prefix = GTValues.VN[tier].toLowerCase(java.util.Locale.ROOT) + "_";
        return path.startsWith(prefix) ? path.substring(prefix.length()) : null;
    }

    private static Map.Entry<String, Family> family(String name, Supplier<GTRecipeType> recipeType) {
        return Map.entry(name, new Family(recipeType, BigBroArrayMode.PROCESSOR));
    }
}
