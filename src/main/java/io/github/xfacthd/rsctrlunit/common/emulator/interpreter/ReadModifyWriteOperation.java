package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

public interface ReadModifyWriteOperation
{
    int compute(RAM ram, int value);
}
