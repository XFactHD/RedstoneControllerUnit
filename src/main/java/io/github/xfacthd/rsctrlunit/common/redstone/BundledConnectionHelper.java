package io.github.xfacthd.rsctrlunit.common.redstone;

import io.github.xfacthd.rsctrlunit.common.block.PlateBlock;
import io.github.xfacthd.rsctrlunit.common.blockentity.RedstoneHandler;
import io.github.xfacthd.rsctrlunit.common.compat.morered.MoreRedCompat;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BundledConnectionHelper
{
    public static int readBundledInput(Level level, BlockState state, BlockPos pos, Direction facing, BlockPos adjPos, Direction side, int offset, int count, int inputMask)
    {
        BlockState adjState = level.getBlockState(adjPos);
        if (adjState.getBlock() instanceof PlateBlock adjBlock && level.getBlockEntity(adjPos) instanceof RedstoneHandler adjBe)
        {
            Direction adjSide = side.getOpposite();
            Direction adjFacing = adjBlock.getFacing(adjState);
            if (adjFacing == facing && adjBlock.getRedstoneTypeOnSide(adjState, adjFacing, adjSide) == RedstoneType.BUNDLED)
            {
                int newPortState = 0;
                for (int i = 0; i < count; i++)
                {
                    if ((inputMask & (1 << i)) != 0 && adjBe.getBundledOutput(adjSide, i + offset) > 0)
                    {
                        newPortState |= 1 << i;
                    }
                }
                return newPortState;
            }
        }
        else if (MoreRedCompat.isBundledCable(level, state, pos, adjState, adjPos, facing, side.getOpposite()))
        {
            return MoreRedCompat.getBundledPower(level, state, pos, adjPos, facing, side.getOpposite(), offset, count, inputMask);
        }
        return 0;
    }



    private BundledConnectionHelper() { }
}
