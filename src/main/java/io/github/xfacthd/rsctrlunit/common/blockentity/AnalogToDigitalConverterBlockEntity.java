package io.github.xfacthd.rsctrlunit.common.blockentity;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public final class AnalogToDigitalConverterBlockEntity extends RedstoneHandlerBlockEntity
{
    private int lastInput = 0;

    public AnalogToDigitalConverterBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(RCUContent.BE_TYPE_ADC.get(), pos, blockState);
    }

    @Override
    public int getRedstoneOutput(Direction side)
    {
        return 0;
    }

    @Override
    public int getBundledOutput(Direction side, int channel)
    {
        if (side == getBlockState().getValue(PropertyHolder.FACING_DIR).orientation().getOpposite())
        {
            return (lastInput >> channel) & 0x1;
        }
        return 0;
    }

    @Override
    public void handleNeighborUpdate(BlockPos adjPos, Direction side)
    {
        Direction orientation = getBlockState().getValue(PropertyHolder.FACING_DIR).orientation();
        if (side == orientation)
        {
            lastInput = level().getSignal(adjPos, side.getOpposite());
            setChangedWithoutSignalUpdate();
            BlockPos updPos = worldPosition.relative(orientation.getOpposite());
            level().getBlockState(updPos).onNeighborChange(level(), updPos, worldPosition);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putInt("last_input", lastInput);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        lastInput = tag.getInt("last_input");
    }
}
