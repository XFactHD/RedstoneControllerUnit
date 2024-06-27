package io.github.xfacthd.rsctrlunit.common.util;

public interface ThrowingSupplier<R, T extends Throwable>
{
    R get() throws T;
}
