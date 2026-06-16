package io.github.xfacthd.rsctrlunit.common.blockentity;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.redstone.BundledConnectionHelper;
import io.github.xfacthd.rsctrlunit.common.util.property.CompoundDirection;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class DigitalToAnalogConverterBlockEntity extends RedstoneHandlerBlockEntity {
    private int lastOutput = 0;

    public DigitalToAnalogConverterBlockEntity(BlockPos pos, BlockState blockState) {
        super(RCUContent.BE_TYPE_DAC.get(), pos, blockState);
    }

    @Override
    public int getRedstoneOutput(Direction side) {
        if (side == getBlockState().getValue(PropertyHolder.FACING_DIR).orientation()) {
            return lastOutput;
        }
        return 0;
    }

    @Override
    public int getBundledOutput(Direction side, int channel) {
        return 0;
    }

    @Override
    public void handleNeighborUpdate(BlockPos adjPos, Direction side) {
        CompoundDirection cmpDir = getBlockState().getValue(PropertyHolder.FACING_DIR);
        Direction orientation = cmpDir.orientation();
        if (side == orientation.getOpposite()) {
            lastOutput = BundledConnectionHelper.readBundledInput(
                    level(), getBlockState(), worldPosition, cmpDir.direction(), adjPos, side.getOpposite(), 0, 4, 0x0F
            );
            setChangedWithoutSignalUpdate();
            BlockPos updPos = worldPosition.relative(orientation);
            // FIXME: the whole Orientation thing makes zero sense...
            level().neighborChanged(updPos, getBlockState().getBlock(), null);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("last_output", lastOutput);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        lastOutput = valueInput.getIntOr("last_output", 0);
    }
}
