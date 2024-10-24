package io.github.xfacthd.rsctrlunit.common.block;

import io.github.xfacthd.rsctrlunit.common.blockentity.RedstoneHandler;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class PlateBlock extends Block implements EntityBlock
{
    private static final VoxelShape[] SHAPES = makeShapes(2D);
    // Make the collision shape slightly higher to avoid playing step sound and particles of the block below
    private static final VoxelShape[] COLLISION_SHAPES = makeShapes(3.3D);

    protected PlateBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace().getOpposite());
    }

    public Direction getFacing(BlockState state)
    {
        return state.getValue(BlockStateProperties.FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPES[getFacing(state).ordinal()];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return COLLISION_SHAPES[getFacing(state).ordinal()];
    }

    public abstract RedstoneType getRedstoneTypeOnSide(BlockState state, Direction facing, Direction side);

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction dir)
    {
        Direction facing = getFacing(state);
        if (dir != null && dir.getAxis() != facing.getAxis())
        {
            Direction side = dir.getOpposite(); // The given direction is from the wire's view
            return getRedstoneTypeOnSide(state, facing, side) == RedstoneType.SINGLE;
        }
        return false;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir)
    {
        return getSignal(state, level, pos, dir);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir)
    {
        Direction side = dir.getOpposite(); // The given direction is from the wire's view
        RedstoneType type = getRedstoneTypeOnSide(state, getFacing(state), side);
        if (type == RedstoneType.SINGLE && level.getBlockEntity(pos) instanceof RedstoneHandler be)
        {
            return be.getRedstoneOutput(side);
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block adjBlock, @Nullable Orientation orientation, boolean moved)
    {
        // FIXME: the whole Orientation thing makes zero sense...
        //onNeighborChange(state, level, pos, adjPos);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos adjPos)
    {
        if (level.isClientSide()) return;

        Direction side = Utils.getDirection(pos, adjPos);
        if (side.getAxis() != getFacing(state).getAxis() && level.getBlockEntity(pos) instanceof RedstoneHandler be)
        {
            be.handleNeighborUpdate(adjPos, side);
        }
    }



    @SuppressWarnings("SuspiciousNameCombination")
    private static VoxelShape[] makeShapes(double height)
    {
        double inv = 16 - height;
        VoxelShape[] shapes = new VoxelShape[6];
        shapes[Direction.UP.ordinal()] =    box(  0, inv,   0,     16,     16,     16);
        shapes[Direction.DOWN.ordinal()] =  box(  0,   0,   0,     16, height,     16);
        shapes[Direction.NORTH.ordinal()] = box(  0,   0,   0,     16,     16, height);
        shapes[Direction.SOUTH.ordinal()] = box(  0,   0, inv,     16,     16,     16);
        shapes[Direction.WEST.ordinal()] =  box(  0,   0,   0, height,     16,     16);
        shapes[Direction.EAST.ordinal()] =  box(inv,   0,   0,     16,     16,     16);
        return shapes;
    }
}
