package com.tstmodern.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TSTConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue NEPTUNIUM_238_DECAY_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        NEPTUNIUM_238_DECAY_TICKS = builder
                .comment("Neptunium-238 decay time in ticks. 20 ticks are approximately one second at 20 TPS.")
                .defineInRange("neptunium238DecayTicks", 50_000, 20, 1_200_000);
        SPEC = builder.build();
    }

    private TSTConfig() {}
}
