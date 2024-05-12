package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

public interface ReadModifyWriteWithArgOperation
{
    int compute(RAM ram, int value, byte argument);
}
