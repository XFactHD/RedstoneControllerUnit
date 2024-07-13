package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.WireConnector;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortMapping;
import io.github.xfacthd.rsctrlunit.common.util.property.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

final class ControllerCableConnector implements WireConnector
{
    @Override
    public boolean canConnectToAdjacentWire(
            @NotNull BlockGetter level,
            @NotNull BlockPos thisPos,
            @NotNull BlockState thisState,
            @NotNull BlockPos wirePos,
            @NotNull BlockState wireState,
            @NotNull Direction wireFace,
            @NotNull Direction directionToWire
    )
    {
        Direction facing = thisState.getValue(BlockStateProperties.FACING);
        if (facing != wireFace)
        {
            return false;
        }

        int portIdx = PortMapping.getPortIndex(facing, directionToWire);
        RedstoneTypeProperty property = PropertyHolder.RS_CON_PROPS[portIdx];
        return thisState.getValue(property) == RedstoneType.BUNDLED;
    }
}
