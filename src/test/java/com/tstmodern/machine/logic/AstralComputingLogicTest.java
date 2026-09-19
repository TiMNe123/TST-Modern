package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

final class AstralComputingLogicTest {
    @Test
    void preservesSourceRackAndCoolantMath() {
        var circuit = new AstralComputingLogic.Component(44, 28, -0.4, 6000, true);
        var tick = AstralComputingLogic.tick(List.of(new AstralComputingLogic.Stack(circuit, 2)),
                0, 1, 1, () -> 0);
        assertEquals(88, tick.computation());
        assertEquals(56, tick.heat());

        var cooled = AstralComputingLogic.coolWithFluid(10_000, 1_000, 0.01);
        assertEquals(1_000, cooled.fluidUsed());
        assertEquals(9_990, cooled.heat());
        assertEquals(1_048_576, AstralComputingLogic.requiredEUt(524_288, 1, 1, 1));
    }
}
