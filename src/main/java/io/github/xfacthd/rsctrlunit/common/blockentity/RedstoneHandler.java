package io.github.xfacthd.rsctrlunit.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface RedstoneHandler
{
    int getRedstoneOutput(Direction side);

    int getBundledOutput(Direction side, int channel);

    void handleNeighborUpdate(BlockPos adjPos, Direction side);
}
