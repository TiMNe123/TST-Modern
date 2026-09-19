
    private static void addMegaTreeFarmControllerRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/mega_tree_farm"))
                .inputItems(HULL[UHV], 2)
                .inputItems(CustomTags.UHV_CIRCUITS, 16)
                .inputItems(ELECTRIC_PUMP_ZPM, 16)
                .inputItems(ROBOT_ARM_ZPM, 16)
                .inputItems(CONVEYOR_MODULE_ZPM, 16)
                .inputItems(FIELD_GENERATOR_ZPM, 8)
                .inputItems(plateDense, NaquadahAlloy, 16)
                .inputItems(TSTBlocks.STERILE_CASING.get().asItem(), 16)
                .inputItems(TSTBlocks.CULTIVATION_SOIL_CASING.get().asItem(), 16)
                .inputFluids(SolderingAlloy.getFluid(9_216))
                .inputFluids(DistilledWater.getFluid(64_000))
                .outputItems(TSTMachines.MEGA_TREE_FARM)
                .stationResearch(b -> b
                        .researchStack(HULL[UHV].asStack())
                        .CWUt(64, 128_000)
                        .EUt(VA[UHV]))
                .duration(20 * 60)
                .EUt(VA[UHV])
                .save(provider);
    }

    private static void addMegaTreeFarmCasingRecipes(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/sterile_casing"))
                .inputItems(CASING_STAINLESS_CLEAN.asStack())
                .inputItems(plate, StainlessSteel, 6)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.STERILE_CASING, 2)
                .duration(200)
                .EUt(VA[IV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/arcane_translucent_casing"))
                .inputItems(CASING_TEMPERED_GLASS.asStack())
                .inputItems(plate, NaquadahAlloy, 4)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.ARCANE_TRANSLUCENT_CASING, 2)
                .duration(200)
                .EUt(VA[LuV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/air_crystal_casing"))
                .inputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().asItem())
                .inputItems(dust, NetherQuartz, 4)
                .inputFluids(LiquidNetherAir.getFluid(1000))
                .outputItems(TSTBlocks.AIR_CRYSTAL_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/water_crystal_casing"))
                .inputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().asItem())
                .inputItems(dust, NetherQuartz, 4)
                .inputFluids(DistilledWater.getFluid(1000))
                .outputItems(TSTBlocks.WATER_CRYSTAL_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/earth_crystal_casing"))
                .inputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().asItem())
                .inputItems(dust, NetherQuartz, 4)
                .inputItems(GTItems.FERTILIZER.asStack(4))
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.EARTH_CRYSTAL_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/cultivation_soil_casing"))
                .inputItems(Items.DIRT, 4)
                .inputItems(GTItems.FERTILIZER.asStack(4))
                .inputItems(plate, StainlessSteel, 4)
                .inputFluids(DistilledWater.getFluid(1000))
                .outputItems(TSTBlocks.CULTIVATION_SOIL_CASING, 2)
                .duration(200)
                .EUt(VA[EV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/advanced_radiation_proof_casing"))
                .inputItems(CASING_TUNGSTENSTEEL_ROBUST.asStack())
                .inputItems(plate, Lead, 6)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.ADVANCED_RADIATION_PROOF_CASING, 2)
                .duration(200)
                .EUt(VA[IV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/extreme_heat_resistant_casing"))
                .inputItems(CASING_INVAR_HEATPROOF.asStack())
                .inputItems(plate, HSSG, 4)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.EXTREME_HEAT_RESISTANT_CASING, 2)
                .duration(200)
                .EUt(VA[LuV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/superconducting_magnetic_casing"))
                .inputItems(TSTBlocks.EXTREME_DENSITY_CASING.get().asItem())
                .inputItems(plate, Naquadah, 4)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.SUPERCONDUCTING_MAGNETIC_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/integral_framework_uv_casing"))
                .inputItems(HULL[UV].asStack())
                .inputItems(frameGt, NaquadahAlloy)
                .inputFluids(SolderingAlloy.getFluid(576))
                .outputItems(TSTBlocks.INTEGRAL_FRAMEWORK_UV_CASING, 2)
                .duration(200)
                .EUt(VA[UV])
                .save(provider);
    }

    public static void addMegaTreeFarmRecipes(Consumer<FinishedRecipe> provider) {
        // === 1. Tree Growth Simulator Recipes (UHV Tier) ===
        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/oak"))
                .inputItems(Items.OAK_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.OAK_LOG, 64)
                .outputItems(Items.OAK_SAPLING, 8)
                .outputItems(Items.OAK_LEAVES, 16)
                .outputItems(Items.APPLE, 4)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/birch"))
                .inputItems(Items.BIRCH_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.BIRCH_LOG, 64)
                .outputItems(Items.BIRCH_SAPLING, 8)
                .outputItems(Items.BIRCH_LEAVES, 16)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/spruce"))
                .inputItems(Items.SPRUCE_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.SPRUCE_LOG, 64)
                .outputItems(Items.SPRUCE_SAPLING, 8)
                .outputItems(Items.SPRUCE_LEAVES, 16)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/jungle"))
                .inputItems(Items.JUNGLE_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.JUNGLE_LOG, 64)
                .outputItems(Items.JUNGLE_SAPLING, 8)
                .outputItems(Items.JUNGLE_LEAVES, 16)
                .outputItems(Items.COCOA_BEANS, 8)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/acacia"))
                .inputItems(Items.ACACIA_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.ACACIA_LOG, 64)
                .outputItems(Items.ACACIA_SAPLING, 8)
                .outputItems(Items.ACACIA_LEAVES, 16)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/dark_oak"))
                .inputItems(Items.DARK_OAK_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.DARK_OAK_LOG, 64)
                .outputItems(Items.DARK_OAK_SAPLING, 8)
                .outputItems(Items.DARK_OAK_LEAVES, 16)
                .outputItems(Items.APPLE, 8)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/mangrove"))
                .inputItems(Items.MANGROVE_PROPAGULE, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.MANGROVE_LOG, 64)
                .outputItems(Items.MANGROVE_PROPAGULE, 8)
                .outputItems(Items.MANGROVE_LEAVES, 16)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/cherry"))
                .inputItems(Items.CHERRY_SAPLING, 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.CHERRY_LOG, 64)
                .outputItems(Items.CHERRY_SAPLING, 8)
                .outputItems(Items.CHERRY_LEAVES, 16)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/rubber"))
                .inputItems(GTBlocks.RUBBER_SAPLING.get().asItem(), 1)
                .inputItems(GTItems.FERTILIZER.asStack(1))
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(GTBlocks.RUBBER_LOG.get().asItem(), 64)
                .outputItems(GTBlocks.RUBBER_SAPLING.get().asItem(), 8)
                .outputItems(GTBlocks.RUBBER_LEAVES.get().asItem(), 16)
                .outputItems(GTItems.STICKY_RESIN.asStack(16))
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);

        // === 2. Aquatic Zone Simulator Recipes (UHV Tier) ===
        TSTRecipeTypes.AQUATIC_ZONE_SIMULATOR.recipeBuilder(TSTModern.id("aquatic_zone_simulator/marine_resources"))
                .inputFluids(new FluidStack(Fluids.WATER, 10_000))
                .outputItems(Items.COD, 16)
                .outputItems(Items.SALMON, 16)
                .outputItems(Items.PUFFERFISH, 8)
                .outputItems(Items.TROPICAL_FISH, 8)
                .outputItems(Items.INK_SAC, 16)
                .outputItems(Items.PRISMARINE_SHARD, 8)
                .outputItems(Items.PRISMARINE_CRYSTALS, 8)
                .outputItems(Items.KELP, 32)
                .outputItems(Items.NAUTILUS_SHELL, 2)
                .duration(20)
                .EUt(VA[UHV])
                .save(provider);
    }
