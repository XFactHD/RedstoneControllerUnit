package io.github.xfacthd.rsctrlunit.common.emulator.disassembler;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public final class Disassembly
{
    public static final Disassembly EMPTY = new Disassembly();

    private final List<String> lines = new ArrayList<>();
    private final Int2IntMap lineIndexByProgramCounter = new Int2IntOpenHashMap();

    Disassembly() { }

    void addCodeLine(int programCounter, String line)
    {
        lines.add(line);
        lineIndexByProgramCounter.put(programCounter, lines.size() - 1);
    }

    void addLabelLine(String line)
    {
        lines.add(line);
    }

    public List<String> getLines()
    {
        return lines;
    }

    public int getLineIndexForProgramCounter(int programCounter)
    {
        return lineIndexByProgramCounter.getOrDefault(programCounter, -1);
    }
}
