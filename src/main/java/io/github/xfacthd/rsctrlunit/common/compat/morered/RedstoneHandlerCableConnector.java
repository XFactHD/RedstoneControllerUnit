package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.WireConnector;
import io.github.xfacthd.rsctrlunit.common.block.PlateBlock;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

final class RedstoneHandlerCableConnector implements WireConnector
{
    static final RedstoneHandlerCableConnector INSTANCE = new RedstoneHandlerCableConnector();

    private RedstoneHandlerCableConnector() { }

    @Override
    public boolean canConnectToAdjacentWire(
            BlockGetter level,
            BlockPos thisPos,
            BlockState thisState,
            BlockPos wirePos,
            BlockState wireState,
            Direction wireFace,
            Direction directionToWire
    )
    {
        if (!(thisState.getBlock() instanceof PlateBlock block))
        {
            return false;
        }

        Direction facing = block.getFacing(thisState);
        return facing == wireFace && block.getRedstoneTypeOnSide(thisState, facing, directionToWire) == RedstoneType.BUNDLED;
    }
}
