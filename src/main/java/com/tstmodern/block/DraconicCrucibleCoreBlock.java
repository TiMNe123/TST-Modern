package com.tstmodern.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Hides the static core model while the formed controller renders the animated Fire Dragon Egg. */
public final class DraconicCrucibleCoreBlock extends Block {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public DraconicCrucibleCoreBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORMED);
    }
}
