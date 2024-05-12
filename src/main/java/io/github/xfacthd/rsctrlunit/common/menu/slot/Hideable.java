package io.github.xfacthd.rsctrlunit.common.menu.slot;

public sealed interface Hideable permits HideableSlot, HideableSlotItemHandler
{
    void setActive(boolean active);
}
