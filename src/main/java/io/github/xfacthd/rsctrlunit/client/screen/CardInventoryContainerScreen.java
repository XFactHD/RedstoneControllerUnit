package io.github.xfacthd.rsctrlunit.client.screen;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.menu.CardInventoryContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

abstract class CardInventoryContainerScreen<T extends CardInventoryContainerMenu> extends AbstractContainerScreen<T>
{
    protected static final int SLOT_SIZE = 18;
    protected static final int SLOT_SIZE_INNER = 16;

    private final ItemStack cardStack = new ItemStack(RCUContent.ITEM_MEMORY_CARD);

    protected CardInventoryContainerScreen(T menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    protected void drawGhostCard(GuiGraphics graphics, int x, int y)
    {
        if (!menu.slots.getFirst().hasItem())
        {
            graphics.renderFakeItem(cardStack, x, y, 0);
            graphics.fill(RenderType.guiGhostRecipeOverlay(), x, y, x + SLOT_SIZE_INNER, y + SLOT_SIZE_INNER, 0x80888888);
        }
    }
}
