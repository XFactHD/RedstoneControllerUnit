package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.ChanneledPowerSupplier;
import io.github.xfacthd.rsctrlunit.common.block.PlateBlock;
import io.github.xfacthd.rsctrlunit.common.blockentity.RedstoneHandler;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class RedstoneHandlerChanneledPowerSupplier<T extends BlockEntity & RedstoneHandler> implements ChanneledPowerSupplier
{
    private static final int BUNDLED_MAX_POWER = 31;

    private final T blockEntity;
    private final Direction side;

    private RedstoneHandlerChanneledPowerSupplier(T blockEntity, Direction side)
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
    public static <T extends BlockEntity & RedstoneHandler> RedstoneHandlerChanneledPowerSupplier<T> get(@Nullable T be, Direction side)
    {
        if (be == null) return null;

        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof PlateBlock block)) return null;

        Direction facing = block.getFacing(state);
        if (side.getAxis() != facing.getAxis() && block.getRedstoneTypeOnSide(state, facing, side) == RedstoneType.BUNDLED)
        {
            return new RedstoneHandlerChanneledPowerSupplier<>(be, side);
        }
        return null;
    }
}
