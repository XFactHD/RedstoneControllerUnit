package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.ChanneledPowerSupplier;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortMapping;
import io.github.xfacthd.rsctrlunit.common.util.property.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ControllerChanneledPowerSupplier implements ChanneledPowerSupplier
{
    private static final int BUNDLED_MAX_POWER = 31;

    private final ControllerBlockEntity blockEntity;
    private final Direction side;

    private ControllerChanneledPowerSupplier(ControllerBlockEntity blockEntity, Direction side)
    {
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    public int getPowerOnChannel(@NotNull Level level, @NotNull BlockPos wirePos, @NotNull BlockState wireState, @Nullable Direction wireFace, int channel)
    {
        return blockEntity.getBundledOutput(side, channel) * BUNDLED_MAX_POWER;
    }



    @Nullable
    public static ControllerChanneledPowerSupplier get(ControllerBlockEntity be, Direction side)
    {
        BlockState ctrlState = be.getBlockState();

        Direction facing = ctrlState.getValue(BlockStateProperties.FACING);
        if (side.getAxis() == facing.getAxis())
        {
            return null;
        }

        int portIdx = PortMapping.getPortIndex(facing, side);
        RedstoneTypeProperty property = PropertyHolder.RS_CON_PROPS[portIdx];
        if (ctrlState.getValue(property) != RedstoneType.BUNDLED)
        {
            return null;
        }

        return new ControllerChanneledPowerSupplier(be, side);
    }
}
