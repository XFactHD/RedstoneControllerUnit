package io.github.xfacthd.rsctrlunit.common.compat.morered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class MoreRedCompat
{
    public static boolean isBundledCable(Level level, BlockState state, BlockPos pos, Direction side)
    {
        return false;
    }
}
