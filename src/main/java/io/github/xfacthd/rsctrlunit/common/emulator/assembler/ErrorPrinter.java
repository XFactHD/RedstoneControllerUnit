package io.github.xfacthd.rsctrlunit.common.emulator.assembler;

public interface ErrorPrinter
{
    void warning(String msg);

    void warning(String msg, Object... args);

    void error(String msg);

    void error(String msg, Object... args);
}
