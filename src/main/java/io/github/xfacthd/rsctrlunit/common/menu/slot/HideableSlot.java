package io.github.xfacthd.rsctrlunit.common.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public final class HideableSlot extends Slot implements Hideable
{
    private boolean active = true;

    public HideableSlot(Container inv, int slot, int x, int y)
    {
        super(inv, slot, x, y);
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
}
