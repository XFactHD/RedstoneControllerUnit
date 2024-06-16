package io.github.xfacthd.rsctrlunit.common.menu;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.Interpreter;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.menu.slot.HideableSlot;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.*;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortConfig;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class ControllerMenu extends AbstractContainerMenu
{
    private static final int SLOT_CARD = 0;
    private static final int SLOT_INV_FIRST = SLOT_CARD + 1;
    private static final int INV_SLOT_COUNT = 9 * 4;

    private final ContainerLevelAccess levelAccess;
    @Nullable
    private final ServerPlayer player;
    @Nullable
    private final Interpreter interpreter;
    @Nullable
    private final RedstoneInterface redstone;
    private final PortConfig[] portConfigs;
    private final PortConfig[] lastPortConfigs = new PortConfig[4];
    private final Container cardContainer;
    private final Slot cardSlot;
    private Direction facing;
    private Code code;

    public static ControllerMenu createClient(int windowId, Inventory inventory, RegistryFriendlyByteBuf buf)
    {
        BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
        Code code = Code.STREAM_CODEC.decode(buf);
        Direction facing = Direction.STREAM_CODEC.decode(buf);
        PortConfig[] portConfigs = RedstoneType.PORT_ARRAY_STREAM_CODEC.decode(buf);
        return new ControllerMenu(windowId, inventory.player.level(), pos, inventory, null, null, facing, portConfigs, code);
    }

    public static ControllerMenu createServer(
            int windowId,
            ControllerBlockEntity be,
            ServerPlayer player,
            Direction facing
    )
    {
        Interpreter interpreter = be.getInterpreter();
        RedstoneInterface redstone = be.getRedstoneInterface();
        PortConfig[] portConfigs = redstone.getPortConfigs();
        Code code = interpreter.getCode();
        return new ControllerMenu(windowId, be.getLevel(), be.getBlockPos(), player.getInventory(), interpreter, redstone, facing, portConfigs, code);
    }

    private ControllerMenu(
            int windowId,
            Level level,
            BlockPos pos,
            Inventory inventory,
            @Nullable Interpreter interpreter,
            @Nullable RedstoneInterface redstone,
            Direction facing,
            PortConfig[] portConfigs,
            Code code
    )
    {
        super(RCUContent.MENU_TYPE_CONTROLLER.get(), windowId);
        this.levelAccess = ContainerLevelAccess.create(level, pos);
        this.player = inventory.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        this.interpreter = interpreter;
        this.redstone = redstone;
        this.facing = facing;
        this.portConfigs = portConfigs;
        this.code = code;
        this.cardContainer = new SimpleContainer(1);
        this.cardSlot = addSlot(new Slot(cardContainer, 0, 14, 66));

        int x = 14;
        int y = 111;
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                addSlot(new HideableSlot(inventory, col + row * 9 + 9, x + col * 18, y));
            }
            y += 18;
        }
        for (int col = 0; col < 9; ++col)
        {
            addSlot(new HideableSlot(inventory, col, x + col * 18, y + 4));
        }
    }

    @Override
    public void broadcastChanges()
    {
        super.broadcastChanges();
        if (player == null) return;

        Objects.requireNonNull(interpreter);
        Objects.requireNonNull(redstone);

        if (!interpreter.getCode().equals(code))
        {
            code = interpreter.getCode();
            PacketDistributor.sendToPlayer(player, new ClientboundUpdateCodePayload(containerId, code));
        }
        Direction newFacing = redstone.getFacing();
        if (!Arrays.equals(lastPortConfigs, portConfigs))
        {
            facing = newFacing;
            Utils.copyArray(portConfigs, lastPortConfigs);
            PacketDistributor.sendToPlayer(player, new ClientboundUpdatePortConfigsPayload(containerId, facing, portConfigs));
        }
        PacketDistributor.sendToPlayer(player, ClientboundUpdateStatusPayload.of(containerId, interpreter));
    }

    public Direction getFacing()
    {
        return facing;
    }

    public PortConfig[] getPortConfigs()
    {
        return portConfigs;
    }

    public Code getCode()
    {
        return code;
    }

    public void updatePortConfigs(Direction facing, PortConfig[] configs)
    {
        this.facing = facing;
        Utils.copyArray(configs, this.portConfigs);
    }

    public void updateCode(Code code)
    {
        this.code = code;
    }

    public void setPortConfig(int port, PortConfig config)
    {
        Objects.requireNonNull(redstone).setPortConfig(port, config);
    }

    public void loadRomFromCard()
    {
        if (cardSlot.getItem().has(RCUContent.COMPONENT_TYPE_CODE))
        {
            loadCode(cardSlot.getItem().get(RCUContent.COMPONENT_TYPE_CODE));
        }
    }

    public void clearRom()
    {
        loadCode(null);
    }

    private void loadCode(@Nullable Code code)
    {
        levelAccess.evaluate(Level::getBlockEntity)
                .filter(ControllerBlockEntity.class::isInstance)
                .map(ControllerBlockEntity.class::cast)
                .ifPresent(be -> be.loadCode(code));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            remainder = stack.copy();

            if (index == SLOT_CARD)
            {
                if (!moveItemStackTo(stack, SLOT_INV_FIRST, SLOT_INV_FIRST + INV_SLOT_COUNT, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (stack.is(RCUContent.ITEM_MEMORY_CARD))
            {
                if (!moveItemStackTo(stack, SLOT_CARD, SLOT_CARD + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (stack.getCount() == remainder.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            broadcastChanges();
        }
        return remainder;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(levelAccess, player, RCUContent.BLOCK_CONTROLLER.value());
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        levelAccess.execute((level, pos) -> clearContainer(player, cardContainer));
    }
}
