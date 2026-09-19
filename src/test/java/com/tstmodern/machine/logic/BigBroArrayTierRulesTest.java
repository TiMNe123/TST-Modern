package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BigBroArrayTierRulesTest {
    @BeforeAll
    static void bootstrapMinecraftRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void frameTiersUnlockTheOriginalMachineBands() {
        assertEquals(GTValues.IV, BigBroArrayTierRules.maxEmbeddedTier(1));
        assertEquals(GTValues.LuV, BigBroArrayTierRules.maxEmbeddedTier(2));
        assertEquals(GTValues.ZPM, BigBroArrayTierRules.maxEmbeddedTier(3));
        assertEquals(GTValues.UV, BigBroArrayTierRules.maxEmbeddedTier(4));
        assertEquals(GTValues.UHV, BigBroArrayTierRules.maxEmbeddedTier(5));
        assertEquals(GTValues.MAX, BigBroArrayTierRules.maxEmbeddedTier(6));
        assertEquals(-1, BigBroArrayTierRules.maxEmbeddedTier(0));
        assertEquals(-1, BigBroArrayTierRules.maxEmbeddedTier(7));
    }

    @Test
    void frameCapEligibilityIsReevaluatedAgainstTheCurrentCore() {
        assertTrue(BigBroArrayTierRules.isEmbeddedTierEligible(1, GTValues.IV));
        assertFalse(BigBroArrayTierRules.isEmbeddedTierEligible(1, GTValues.LuV));
        assertTrue(BigBroArrayTierRules.isEmbeddedTierEligible(6, GTValues.MAX));
        assertFalse(BigBroArrayTierRules.isEmbeddedTierEligible(0, GTValues.ULV));
        assertFalse(BigBroArrayTierRules.isEmbeddedTierEligible(6, -1));
    }

    @Test
    void unknownBlocksAndStatesDoNotAcquireATier() {
        Block unknown = Blocks.DIRT;

        assertEquals(-1, BigBroArrayTierRules.frameTier(unknown));
        assertEquals(-1, BigBroArrayTierRules.glassTier(unknown));
        assertEquals(-1, BigBroArrayTierRules.machineCasingTier(unknown));
        assertEquals(-1, BigBroArrayTierRules.parallelCasingTier(unknown));
        assertEquals(-1, BigBroArrayTierRules.coilTier(unknown.defaultBlockState()));
    }

    @Test
    void uniformChannelsRememberAndEnforceTheFirstTier() {
        PatternMatchContext context = new PatternMatchContext();

        assertFalse(BigBroArrayTierRules.matchUniformTier(null, "test-tier", 3));
        assertTrue(BigBroArrayTierRules.matchUniformTier(context, "test-tier", 3));
        assertTrue(BigBroArrayTierRules.matchUniformTier(context, "test-tier", 3));
        assertFalse(BigBroArrayTierRules.matchUniformTier(context, "test-tier", 4));
        assertFalse(BigBroArrayTierRules.matchUniformTier(context, "test-tier", -1));
        assertEquals(3, context.getInt("test-tier"));
    }

    @Test
    void missingContextCannotPublishCoreTiers() {
        assertEquals(null, BigBroArrayTierRules.validatedCoreTiers(null));
    }
}
