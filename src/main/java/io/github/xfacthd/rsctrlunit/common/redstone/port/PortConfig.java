package io.github.xfacthd.rsctrlunit.common.redstone.port;

import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public sealed interface PortConfig permits NonePortConfig, SinglePortConfig, BundledPortConfig
{
    /**
     * Returns the vanilla redstone output (0-15) for the given port state on this port config
     */
    int getRedstoneOutput(byte portState);

    /**
     * Returns the output state of the given channel for the given port state on this port config.
     * The returned values is either 0 or 1 and the called must multiply it by the target power value of the
     * respective bundled cable implementation
     */
    int getBundledOutput(byte portState, int channel);

    byte updateInput(Level level, BlockState state, BlockPos pos, Direction facing, BlockPos adjPos, Direction side);

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
