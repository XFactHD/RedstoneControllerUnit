package io.github.xfacthd.rsctrlunit.common.menu.slot;

public sealed interface Lockable permits CustomSlot {
    boolean isLocked();
}
