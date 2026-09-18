package com.tstmodern.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TSTConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue NEPTUNIUM_238_DECAY_TICKS;
    public static final ForgeConfigSpec.IntValue MEGA_NAQUADAH_REACTOR_MAX_PARALLEL;
    public static final ForgeConfigSpec.IntValue GALACTIC_ARMILLARY_STARTUP_SECONDS;
    public static final ForgeConfigSpec.EnumValue<RecipeMode> DRACONIC_CRUCIBLE_RECIPE_MODE;
    public static final ForgeConfigSpec.EnumValue<RecipeMode> GALACTIC_ARMILLARY_RECIPE_MODE;

    public enum RecipeMode {
        CUSTOM,
        MOD_ORIGINAL
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        NEPTUNIUM_238_DECAY_TICKS = builder
                .comment("Neptunium-238 decay time in ticks. 20 ticks are approximately one second at 20 TPS.")
                .defineInRange("neptunium238DecayTicks", 50_000, 20, 1_200_000);
        MEGA_NAQUADAH_REACTOR_MAX_PARALLEL = builder
                .comment("Maximum parallel fuel recipes for the Mega Naquadah Reactor.")
                .defineInRange("megaNaquadahReactorMaxParallel", 1_000, 1, Integer.MAX_VALUE);
        GALACTIC_ARMILLARY_STARTUP_SECONDS = builder
                .comment("Galactic Armillary core warm-up time in seconds.")
                .defineInRange("galacticArmillaryStartupSeconds", 120, 20, 3_600);

        builder.push("recipes");
        DRACONIC_CRUCIBLE_RECIPE_MODE = builder
                .comment("Recipe mode for the Draconic Crucible.",
                        "Only takes effect when the 'draconicevolution' mod is loaded.",
                        "When Draconic Evolution is not installed, this machine always falls back to CUSTOM.",
                        "Options: CUSTOM (TST-Modern standalone progression), MOD_ORIGINAL (Draconic Evolution original block-scale fusion recipe).")
                .defineEnum("draconicCrucibleRecipeMode", RecipeMode.CUSTOM);
        GALACTIC_ARMILLARY_RECIPE_MODE = builder
                .comment("Recipe mode for the Galactic Armillary.",
                        "Only takes effect when the 'avaritia' mod is loaded.",
                        "When Re-Avaritia is not installed, this machine always falls back to CUSTOM.",
                        "Options: CUSTOM (TST-Modern standalone progression: energy synthesis for catalyst nuggets, custom ingot recipe), MOD_ORIGINAL (Re-Avaritia original recipe requirements and items).")
                .defineEnum("galacticArmillaryRecipeMode", RecipeMode.CUSTOM);
        builder.pop();

        SPEC = builder.build();
    }

    private TSTConfig() {}
}
