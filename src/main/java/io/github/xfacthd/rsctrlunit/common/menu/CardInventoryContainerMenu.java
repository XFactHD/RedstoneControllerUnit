package io.github.xfacthd.rsctrlunit.common.menu;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.menu.slot.CustomSlot;
import io.github.xfacthd.rsctrlunit.common.menu.util.SafeContainerLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntPredicate;

public abstract class CardInventoryContainerMenu extends AbstractContainerMenu
{
    protected static final int SLOT_CARD = 0;
    protected static final int SLOT_INV_FIRST = SLOT_CARD + 1;
    protected static final int INV_SLOT_COUNT = 9 * 4;

    protected final ContainerLevelAccess levelAccess;
    @Nullable
    protected final Container cardContainer;
    @Nullable
    protected final Slot cardSlot;

    protected CardInventoryContainerMenu(MenuType<?> menuType, int windowId, Inventory inventory, BlockPos pos, SlotConfig slotCfg)
    {
        super(menuType, windowId);
        this.levelAccess = new SafeContainerLevelAccess(inventory.player.level(), pos);

        if (slotCfg.hasCardSlot)
        {
            this.cardContainer = new SimpleContainer(1);
            this.cardSlot = addSlot(new CustomSlot(cardContainer, 0, slotCfg.cardX, slotCfg.cardY, false));
        }
        else
        {
            this.cardContainer = null;
            this.cardSlot = null;
        }

        int x = slotCfg.invX;
        int y = slotCfg.invY;
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                addSlot(new CustomSlot(inventory, col + row * 9 + 9, x + col * 18, y, false));
            }
            y += 18;
        }
        for (int col = 0; col < 9; ++col)
        {
            addSlot(new CustomSlot(inventory, col, x + col * 18, y + 4, slotCfg.hotbarSlotLocked.test(col)));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        if (cardContainer == null) return ItemStack.EMPTY;

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
    public void removed(Player player)
    {
        super.removed(player);
        if (cardContainer != null)
        {
            levelAccess.execute((level, pos) -> clearContainer(player, cardContainer));
        }
    }



    protected record SlotConfig(boolean hasCardSlot, int invX, int invY, int cardX, int cardY, IntPredicate hotbarSlotLocked) { }
}
