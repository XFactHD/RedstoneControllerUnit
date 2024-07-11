package io.github.xfacthd.rsctrlunit.common.redstone.port;

import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public sealed interface PortConfig permits NonePortConfig, SinglePortConfig, BundledPortConfig
{
    int getRedstoneOutput(byte portState);

    int getBundledOutput(byte portState, int channel);

    byte updateInput(Level level, BlockState state, Direction facing, BlockPos adjPos, Direction side);

    boolean hasInputs();

    boolean hasOutputs();

    RedstoneType getType();

    default PortConfig cycleType()
    {
        return switch (getType())
        {
            case NONE -> new SinglePortConfig(0, false);
            case SINGLE -> new BundledPortConfig(false, (byte) 0);
            case BUNDLED -> NonePortConfig.INSTANCE;
        };
    }
}
