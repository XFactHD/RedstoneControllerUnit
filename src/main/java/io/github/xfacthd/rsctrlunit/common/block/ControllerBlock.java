package io.github.xfacthd.rsctrlunit.common.block;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.item.ProgrammerItem;
import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortMapping;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class ControllerBlock extends PlateBlock
{
    public ControllerBlock()
    {
        super(Properties.of().strength(1.5F, 6.0F));
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.SHOW_PORT_MAPPING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.SHOW_PORT_MAPPING);
        builder.add(PropertyHolder.RS_CON_PROPS);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA) && level.getBlockEntity(pos) instanceof ControllerBlockEntity be)
        {
            BlockState newState = be.getRedstoneInterface().updateStateFromConfigs(state);
            if (newState != state)
            {
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (stack.is(RCUContent.ITEM_PROGRAMMER) && level.getBlockEntity(pos) instanceof ControllerBlockEntity controller)
        {
            if (!level.isClientSide())
            {
                ProgrammerItem.openMenu(player, stack, controller);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
                RedstoneInterface.PORT_MAPPING_STREAM_CODEC.encode(buf, be.getRedstoneInterface().getPortMapping());
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public RedstoneType getRedstoneTypeOnSide(BlockState state, Direction facing, Direction side)
    {
        int port = PortMapping.getPortIndex(facing, side);
        return port != -1 ? state.getValue(PropertyHolder.RS_CON_PROPS[port]) : RedstoneType.NONE;
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
