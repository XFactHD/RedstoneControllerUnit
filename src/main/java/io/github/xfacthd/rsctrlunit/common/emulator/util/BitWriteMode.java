package io.github.xfacthd.rsctrlunit.common.emulator.util;

public enum BitWriteMode
{
    SET,
    CLEAR,
    COMPLEMENT;

    public static BitWriteMode of(boolean set)
    {
        return set ? SET : CLEAR;
    }
}
