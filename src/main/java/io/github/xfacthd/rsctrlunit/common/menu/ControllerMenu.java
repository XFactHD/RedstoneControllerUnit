package io.github.xfacthd.rsctrlunit.common.menu;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.Interpreter;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.*;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortConfig;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class ControllerMenu extends CardInventoryContainerMenu
{
    private static final SlotConfig SLOT_CONFIG = new SlotConfig(true, 14, 138, 14, 78, idx -> false);

    @Nullable
    private final ServerPlayer player;
    @Nullable
    private final Interpreter interpreter;
    @Nullable
    private final RedstoneInterface redstone;
    private final PortConfig[] portConfigs;
    private final PortConfig[] lastPortConfigs = new PortConfig[4];
    private final DataSlot runningSlot;
    private Direction facing;
    private Code code;

    public static ControllerMenu createClient(int windowId, Inventory inventory, RegistryFriendlyByteBuf buf)
    {
        BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
        Code code = Code.STREAM_CODEC.decode(buf);
        Direction facing = Direction.STREAM_CODEC.decode(buf);
        PortConfig[] portConfigs = RedstoneType.PORT_ARRAY_STREAM_CODEC.decode(buf);
        return new ControllerMenu(windowId, pos, inventory, null, null, facing, portConfigs, code);
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
        return new ControllerMenu(windowId, be.getBlockPos(), player.getInventory(), interpreter, redstone, facing, portConfigs, code);
    }

    private ControllerMenu(
            int windowId,
            BlockPos pos,
            Inventory inventory,
            @Nullable Interpreter interpreter,
            @Nullable RedstoneInterface redstone,
            Direction facing,
            PortConfig[] portConfigs,
            Code code
    )
    {
        super(RCUContent.MENU_TYPE_CONTROLLER.get(), windowId, inventory, pos, SLOT_CONFIG);
        this.player = inventory.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        this.interpreter = interpreter;
        this.redstone = redstone;
        this.facing = facing;
        this.portConfigs = portConfigs;
        this.runningSlot = addDataSlot(DataSlot.standalone());
        this.code = code;
    }

    @Override
    public void broadcastChanges()
    {
        if (interpreter != null)
        {
            runningSlot.set(interpreter.isPaused() ? 0 : 1);
        }

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

    public boolean isRunning()
    {
        return runningSlot.get() != 0;
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
        if (Objects.requireNonNull(cardSlot).getItem().has(RCUContent.COMPONENT_TYPE_CODE))
        {
            loadCode(cardSlot.getItem().get(RCUContent.COMPONENT_TYPE_CODE));
        }
    }

    public void saveRomToCard()
    {
        if (Objects.requireNonNull(cardSlot).getItem().has(RCUContent.COMPONENT_TYPE_CODE))
        {
            levelAccess.evaluate(Level::getBlockEntity)
                    .filter(ControllerBlockEntity.class::isInstance)
                    .map(ControllerBlockEntity.class::cast)
                    .ifPresent(be ->
                    {
                        ItemStack stack = cardSlot.getItem();
                        Code code = be.getInterpreter().getCode();
                        stack.set(RCUContent.COMPONENT_TYPE_CODE, code);
                    });
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

    public void togglePauseResume()
    {
        if (interpreter == null) return;

        if (interpreter.isPaused())
        {
            interpreter.resume();
        }
        else
        {
            interpreter.pause();
        }
    }

    public void requestStep()
    {
        if (interpreter != null && interpreter.isPaused())
        {
            interpreter.step();
        }
    }

    public void requestReset()
    {
        if (interpreter != null)
        {
            interpreter.reset(false);
        }
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(levelAccess, player, RCUContent.BLOCK_CONTROLLER.value());
    }
}
