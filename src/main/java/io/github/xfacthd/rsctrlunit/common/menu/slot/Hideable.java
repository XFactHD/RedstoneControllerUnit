package io.github.xfacthd.rsctrlunit.common.menu.slot;

public sealed interface Hideable permits CustomSlot, CustomSlotItemHandler
{
    void setActive(boolean active);
}
