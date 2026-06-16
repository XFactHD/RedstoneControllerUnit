package io.github.xfacthd.rsctrlunit.common.menu.slot;

public sealed interface Hideable permits CustomSlot {
    void setActive(boolean active);
}
