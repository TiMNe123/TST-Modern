package com.tstmodern.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TSTConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue NEPTUNIUM_238_DECAY_TICKS;
    public static final ForgeConfigSpec.IntValue MEGA_NAQUADAH_REACTOR_MAX_PARALLEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        NEPTUNIUM_238_DECAY_TICKS = builder
                .comment("Neptunium-238 decay time in ticks. 20 ticks are approximately one second at 20 TPS.")
                .defineInRange("neptunium238DecayTicks", 50_000, 20, 1_200_000);
        MEGA_NAQUADAH_REACTOR_MAX_PARALLEL = builder
                .comment("Maximum parallel fuel recipes for the Mega Naquadah Reactor.")
                .defineInRange("megaNaquadahReactorMaxParallel", 1_000, 1, Integer.MAX_VALUE);
        SPEC = builder.build();
    }

    private TSTConfig() {}
}
