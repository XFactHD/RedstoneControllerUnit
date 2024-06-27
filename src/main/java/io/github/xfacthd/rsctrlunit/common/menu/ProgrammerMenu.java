package io.github.xfacthd.rsctrlunit.common.menu;

import com.google.common.base.Preconditions;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class ProgrammerMenu extends CardInventoryContainerMenu
{
    public static final Component TITLE = Component.translatable("menu.rsctrlunit.programmer");

    private final ItemStack progStack;
    private final int slot;
    private final boolean forBlock;
    private final DataSlot targetBlockValidSlot;
    private final DataSlot interpreterCodeLoadedSlot;
    @Nullable
    private ControllerBlockEntity targetController;

    public static ProgrammerMenu createClient(int windowId, Inventory inventory, RegistryFriendlyByteBuf buf)
    {
        int slot = buf.readVarInt();
        boolean forBlock = buf.readBoolean();
        return new ProgrammerMenu(windowId, inventory, ItemStack.EMPTY, slot, forBlock, null);
    }

    public ProgrammerMenu(int windowId, Inventory inventory, ItemStack stack, int slot, boolean forBlock, @Nullable ControllerBlockEntity targetController)
    {
        super(RCUContent.MENU_TYPE_PROGRAMMER.get(), windowId, inventory, inventory.player.blockPosition(), new SlotConfig(
                !forBlock, 9, 129, 9, 93, idx -> idx == slot
        ));
        this.progStack = stack;
        this.slot = slot;
        this.forBlock = forBlock;
        this.targetController = targetController;
        if (forBlock)
        {
            DataSlot dataSlot = addDataSlot(DataSlot.standalone());
            dataSlot.set(1);
            this.targetBlockValidSlot = dataSlot;
            this.interpreterCodeLoadedSlot = addDataSlot(DataSlot.standalone());
        }
        else
        {
            this.targetBlockValidSlot = null;
            this.interpreterCodeLoadedSlot = null;
        }
    }

    @Override
    public void broadcastChanges()
    {
        if (forBlock && targetController != null)
        {
            if (targetController.isRemoved())
            {
                targetController = null;
                targetBlockValidSlot.set(0);
            }
            else
            {
                boolean loaded = !targetController.getInterpreter().getCode().equals(Code.EMPTY);
                interpreterCodeLoadedSlot.set(loaded ? 1 : 0);
            }
        }
        super.broadcastChanges();
    }

    public void writeToTarget(Code code)
    {
        if (forBlock)
        {
            if (targetController != null)
            {
                targetController.loadCode(code);
            }
        }
        else
        {
            ItemStack stack = slots.getFirst().getItem();
            if (stack.is(RCUContent.ITEM_MEMORY_CARD))
            {
                stack.set(RCUContent.COMPONENT_TYPE_CODE, code);
            }
        }
    }

    public boolean isForBlock()
    {
        return forBlock;
    }

    public boolean isTargetValid()
    {
        if (forBlock)
        {
            return targetBlockValidSlot.get() != 0;
        }
        return slots.getFirst().getItem().is(RCUContent.ITEM_MEMORY_CARD);
    }

    public boolean isInterpreterEmpty()
    {
        Preconditions.checkState(forBlock, "Cannot check interpreter code state with non-block target");
        return interpreterCodeLoadedSlot.get() == 0;
    }

    public Code getBlockTargetCode()
    {
        Preconditions.checkState(forBlock, "Cannot get interpreter code with non-block target");
        if (targetController != null)
        {
            return targetController.getInterpreter().getCode();
        }
        return Code.EMPTY;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return player.getInventory().getItem(slot) == progStack;
    }
}
