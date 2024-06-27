package io.github.xfacthd.rsctrlunit.common.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CustomSlot extends Slot implements Hideable, Lockable
{
    private final boolean locked;
    private boolean active = true;

    public CustomSlot(Container inv, int slot, int x, int y, boolean locked)
    {
        super(inv, slot, x, y);
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
