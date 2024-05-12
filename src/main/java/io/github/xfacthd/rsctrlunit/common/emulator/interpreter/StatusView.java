package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

public final class StatusView
{
    private final Interpreter interpreter;
    private final RAM ram;

    StatusView(Interpreter interpreter, RAM ram)
    {
        this.interpreter = interpreter;
        this.ram = ram;
    }

    public int getProgramCounter()
    {
        return interpreter.getProgramCounter();
    }

    public byte[] getRamView()
    {
        return ram.getBackingArray();
    }
}
