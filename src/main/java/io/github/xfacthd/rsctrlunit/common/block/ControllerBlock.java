package io.github.xfacthd.rsctrlunit.common.block;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortMapping;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;

public final class ControllerBlock extends Block implements EntityBlock
{
    private static final VoxelShape[] SHAPES = Util.make(() ->
    {
        VoxelShape[] shapes = new VoxelShape[6];
        shapes[Direction.UP.ordinal()] = box(0, 14, 0, 16, 16, 16);
        shapes[Direction.DOWN.ordinal()] = box(0, 0, 0, 16, 2, 16);
        shapes[Direction.NORTH.ordinal()] = box(0, 0, 0, 16, 16, 2);
        shapes[Direction.SOUTH.ordinal()] = box(0, 0, 14, 16, 16, 16);
        shapes[Direction.WEST.ordinal()] = box(0, 0, 0, 2, 16, 16);
        shapes[Direction.EAST.ordinal()] = box(14, 0, 0, 16, 16, 16);
        return shapes;
    });

    public ControllerBlock()
    {
        super(Properties.of().strength(1.5F, 6.0F));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING);
        builder.add(PropertyHolder.RS_CON_PROPS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPES[state.getValue(BlockStateProperties.FACING).ordinal()];
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ControllerBlockEntity be)
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            player.openMenu(new MenuProvider()
            {
                @Override
                public Component getDisplayName()
                {
                    return ControllerBlockEntity.TITLE;
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
                {
                    return ControllerMenu.createServer(windowId, be, (ServerPlayer) player, facing);
                }
            }, buf ->
            {
                BlockPos.STREAM_CODEC.encode(buf, pos);
                Code.STREAM_CODEC.encode(buf, be.getInterpreter().getCode());
                Direction.STREAM_CODEC.encode(buf, facing);
                RedstoneType.PORT_ARRAY_STREAM_CODEC.encode(buf, be.getRedstoneInterface().getPortConfigs());
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction dir)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (dir != null && dir.getAxis() != facing.getAxis())
        {
            int port = PortMapping.getPortIndex(facing, dir.getOpposite());
            return state.getValue(PropertyHolder.RS_CON_PROPS[port]) == RedstoneType.SINGLE;
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
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction side = dir.getOpposite(); // The given direction is from the wire's view
        int port = PortMapping.getPortIndex(facing, side);
        if (port != -1 && state.getValue(PropertyHolder.RS_CON_PROPS[port]) == RedstoneType.SINGLE)
        {
            if (level.getBlockEntity(pos) instanceof ControllerBlockEntity be)
            {
                return be.getRedstoneOutput(side);
            }
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block adjBlock, BlockPos adjPos, boolean moved)
    {
        if (level.isClientSide()) return;

        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction side = Utils.getDirection(pos, adjPos);
        if (side.getAxis() != facing.getAxis() && level.getBlockEntity(pos) instanceof ControllerBlockEntity be)
        {
            be.handleNeighborUpdate(adjPos, side);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new ControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (!level.isClientSide())
        {
            return Utils.createBlockEntityTicker(type, RCUContent.BE_TYPE_CONTROLLER.get(), ControllerBlockEntity::tick);
        }
        return null;
    }
}
