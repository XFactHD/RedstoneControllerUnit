package io.github.xfacthd.rsctrlunit.client.screen;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.menu.CardInventoryContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

abstract class CardInventoryContainerScreen<T extends CardInventoryContainerMenu> extends AbstractContainerScreen<T>
{
    protected static final int SLOT_SIZE = 18;
    protected static final int SLOT_SIZE_INNER = 16;

    private final ItemStack cardStack = new ItemStack(RCUContent.ITEM_MEMORY_CARD);

    protected CardInventoryContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight)
    {
        super(menu, inventory, title, imageWidth, imageHeight);
    }

    protected void drawGhostCard(GuiGraphicsExtractor graphics, int x, int y)
    {
        if (!menu.slots.getFirst().hasItem())
        {
            graphics.fakeItem(cardStack, x, y, 0);
            graphics.fill(x, y, x + SLOT_SIZE_INNER, y + SLOT_SIZE_INNER, 0x80888888);
        }
    }
}
