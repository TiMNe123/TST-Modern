package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.tstmodern.config.TSTConfig;
import org.junit.jupiter.api.Test;

class IsotopeDecayConfigTest {

    @Test
    void decayLifetimeUsesTheCommonConfigWithTheTstDefault() {
        TSTConfig.SPEC.setConfig(CommentedConfig.inMemory());
        try {
            assertEquals(50_000, IsotopeDecayHandler.decayLifetimeTicks());
            TSTConfig.NEPTUNIUM_238_DECAY_TICKS.set(6_000);
            assertEquals(6_000, IsotopeDecayHandler.decayLifetimeTicks());
        } finally {
            TSTConfig.SPEC.setConfig(null);
        }
    }
}
