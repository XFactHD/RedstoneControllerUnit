package io.github.xfacthd.rsctrlunit.common.block;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.*;
import io.github.xfacthd.rsctrlunit.common.util.registration.DeferredBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public final class SignalConverterBlock extends PlateBlock
{
    private final DeferredBlockEntity<? extends BlockEntity> blockEntityType;

    private SignalConverterBlock(DeferredBlockEntity<? extends BlockEntity> blockEntityType, Properties props)
    {
        super(props.strength(1.5F, 6.0F));
        this.blockEntityType = blockEntityType;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        Direction direction = ctx.getClickedFace().getOpposite();
        Direction orientation = Utils.getDirFromCross(ctx.getClickLocation(), ctx.getClickedFace());
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(direction, orientation));
    }

    @Override
    public Direction getFacing(BlockState state)
    {
        return state.getValue(PropertyHolder.FACING_DIR).direction();
    }

    @Override
    public RedstoneType getRedstoneTypeOnSide(BlockState state, Direction facing, Direction side)
    {
        CompoundDirection dir = state.getValue(PropertyHolder.FACING_DIR);
        if (side == dir.orientation())
        {
            return RedstoneType.SINGLE;
        }
        if (side == dir.orientation().getOpposite())
        {
            return RedstoneType.BUNDLED;
        }
        return RedstoneType.NONE;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return blockEntityType.value().create(pos, state);
    }



    public static SignalConverterBlock analogToDigital(Properties props)
    {
        return new SignalConverterBlock(RCUContent.BE_TYPE_ADC, props);
    }

    public static SignalConverterBlock digitalToAnalog(Properties props)
    {
        return new SignalConverterBlock(RCUContent.BE_TYPE_DAC, props);
    }
}
