package io.github.xfacthd.rsctrlunit.common.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class CustomSlotItemHandler extends SlotItemHandler implements Hideable, Lockable
{
    private final boolean locked;
    private boolean active = true;

    public CustomSlotItemHandler(IItemHandler handler, int slot, int x, int y, boolean locked)
    {
        super(handler, slot, x, y);
        this.locked = locked;
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return !locked && super.mayPickup(player);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return !locked && super.mayPlace(stack);
    }

    @Override
    public void setActive(boolean active)
    {
        this.active = active;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    @Override
    public boolean isLocked()
    {
        return locked;
    }
}
