package io.github.xfacthd.rsctrlunit.common.menu.slot;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class HideableSlotItemHandler extends SlotItemHandler implements Hideable
{
    private boolean active = true;

    public HideableSlotItemHandler(IItemHandler handler, int slot, int x, int y)
    {
        super(handler, slot, x, y);
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
