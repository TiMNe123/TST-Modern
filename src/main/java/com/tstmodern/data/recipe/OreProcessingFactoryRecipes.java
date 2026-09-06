package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gem;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gemExquisite;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gemFlawless;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeHugeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rawOre;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rotor;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtOctal;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GCYMMachines;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.OreProcessingFactoryDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** Recipe port for TST's direct ore-to-dust/byproduct factory. */
public final class OreProcessingFactoryRecipes {
    private OreProcessingFactoryRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            OreProperty ore = material.getProperty(PropertyKey.ORE);
            if (ore == null) {
                continue;
            }
            for (TagPrefix prefix : TagPrefix.ORES.keySet()) {
                registerOre(provider, material, ore, prefix, TagPrefix.ORES.get(prefix).isDoubleDrops());
            }
            registerOre(provider, material, ore, rawOre, false);
        }
        registerController(provider);
    }

    private static void registerOre(Consumer<FinishedRecipe> provider, Material material, OreProperty ore,
                                    TagPrefix prefix, boolean rich) {
        if (!material.shouldGenerateRecipesFor(prefix)) {
            return;
        }
        ItemStack input = ChemicalHelper.get(prefix, material);
        if (input.isEmpty()) {
            return;
        }
        List<ItemStack> outputs = outputs(material, ore, rich);
        if (outputs.isEmpty()) {
            return;
        }
        GTRecipeBuilder builder = TSTRecipeTypes.ORE_PROCESSING_FACTORY.recipeBuilder(
                TSTModern.id(recipePath(prefix.name, material.getResourceLocation().toString())))
                .inputItems(input)
                .duration(128)
                .EUt(30);
        outputs.forEach(builder::outputItems);
        builder.save(provider);
    }

    static String recipePath(String prefixName, String materialId) {
        return "ore_processing_factory/" + prefixName.toLowerCase(Locale.ROOT) + "_" + materialId.replace(':', '_');
    }

    static List<ItemStack> outputs(Material material, OreProperty ore, boolean rich) {
        int multiplier = rich ? 2 : 1;
        List<ItemStack> outputs = new ArrayList<>();
        List<Material> byproducts = ore.getOreByProducts();
        addOutput(outputs, ChemicalHelper.get(dust, material, (byproducts.isEmpty() ? 8 : 4) * multiplier));
        if (byproducts.size() == 1) {
            addOutput(outputs, ChemicalHelper.get(dust, byproducts.get(0), 3 * multiplier));
        } else {
            for (Material byproduct : byproducts) {
                if (byproduct == GTMaterials.Stone || byproduct == GTMaterials.Netherrack ||
                        byproduct == GTMaterials.Endstone) {
                    continue;
                }
                addOutput(outputs, ChemicalHelper.get(dust, byproduct, 2 * multiplier));
            }
        }
        if (!ChemicalHelper.get(gem, material).isEmpty()) {
            if (!ChemicalHelper.get(gemExquisite, material).isEmpty()) {
                addOutput(outputs, ChemicalHelper.get(gemExquisite, material, multiplier));
                addOutput(outputs, ChemicalHelper.get(gemFlawless, material, 2 * multiplier));
                addOutput(outputs, ChemicalHelper.get(gem, material, 2 * multiplier));
            } else {
                addOutput(outputs, ChemicalHelper.get(gem, material, 4 * multiplier));
            }
        }
        return outputs;
    }

    static void addOutput(List<ItemStack> outputs, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        for (ItemStack output : outputs) {
            if (ItemStack.isSameItemSameTags(output, stack)) {
                output.grow(stack.getCount());
                return;
            }
        }
        outputs.add(stack);
    }

    private static void registerController(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("ore_processing_factory"))
                .inputItems(new ItemStack(TSTBlocks.COSMIC_NEUTRONIUM_FRAME.get(), 64))
                .inputItems(GCYMMachines.LARGE_MACERATION_TOWER.asStack(64))
                .inputItems(TSTCircuitTags.get(UIV), 64)
                .inputItems(ChemicalHelper.get(rotor, GTMaterials.Neutronium, 64))
                .inputItems(GTItems.ELECTRIC_MOTOR_UEV.asStack(64))
                .inputItems(GTItems.ELECTRIC_PUMP_UEV.asStack(16))
                .inputItems(GTItems.CONVEYOR_MODULE_UEV.asStack(16))
                .inputItems(GTItems.ROBOT_ARM_UEV.asStack(16))
                .inputItems(GTItems.ROBOT_ARM_UEV.asStack(16))
                .inputItems(ChemicalHelper.get(pipeHugeFluid, GTMaterials.Neutronium, 16))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Neutronium, 64))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Iridium, 64))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.StainlessSteel, 64))
                .inputItems(new ItemStack(TSTBlocks.HIGH_POWER_CASING.get(), 64))
                .inputItems(ChemicalHelper.get(wireGtOctal, GTMaterials.UraniumRhodiumDinaquadide, 64))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(73_728))
                .inputFluids(GTMaterials.TungstenSteel.getFluid(147_456))
                .inputFluids(GTMaterials.Neutronium.getFluid(147_456))
                .inputFluids(GTMaterials.Osmiridium.getFluid(147_456))
                .outputItems(OreProcessingFactoryDefinition.MACHINE.asStack())
                .duration(36_000)
                .EUt(VA[UEV])
                .stationResearch(b -> b
                        .researchStack(GCYMMachines.LARGE_MACERATION_TOWER.asStack())
                        .researchId("ore_processing_factory")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 144_000)
                        .EUt(VA[UEV]))
                .save(provider);
    }
}
