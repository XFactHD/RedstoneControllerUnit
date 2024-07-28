package io.github.xfacthd.rsctrlunit;

import io.github.xfacthd.rsctrlunit.common.emulator.disassembler.Disassembler;
import io.github.xfacthd.rsctrlunit.common.emulator.disassembler.Disassembly;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class DisassemblerTests
{
    @Test
    void testOne()
    {
        Code code = makeCode(
                "RedstoneBlinker",
                new int[] { 0x63, 0x80, 0xFF, 0x74, 0x14, 0xD5, 0xE0, 0xFD, 0x01, 0x00 },
                Map.of(0, "start", 5, "loop")
        );
        List<String> expectedLines = List.of(
                String.format(Locale.ROOT, Disassembler.LABEL_LINE_TEMPLATE, "start"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 0, "XRL 80h,#0FFh"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 3, "MOV A,#14h"),
                String.format(Locale.ROOT, Disassembler.LABEL_LINE_TEMPLATE, "loop"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 5, "DJNZ 0E0h,loop"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 8, "AJMP start")
        );
        test(code, expectedLines);
    }

    @Test
    void testTwo()
    {
        Code code = makeCode(
                "RedstoneForward",
                new int[] { 0x85, 0xA0, 0x80, 0x01, 0x00 },
                Map.of(0, "start")
        );
        List<String> expectedLines = List.of(
                String.format(Locale.ROOT, Disassembler.LABEL_LINE_TEMPLATE, "start"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 0, "MOV 0A0h,80h"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 3, "AJMP start")
        );
        test(code, expectedLines);
    }

    @Test
    void testThree()
    {
        Code code = makeCode(
                "RedstoneTimerBlinker",
                new int[] { 0x75, 0x8C, 0xEB, 0x75, 0x8A, 0xEB, 0x43, 0x89, 0x02, 0x43, 0x88, 0x10, 0x63, 0x80, 0xFF, 0x30, 0x8D, 0xFD, 0xC2, 0x8D, 0x01, 0x0C },
                Map.of(0x0C, "loop", 0x0F, "wait")
        );
        List<String> expectedLines = List.of(
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 0, "MOV 8Ch,#0EBh"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 3, "MOV 8Ah,#0EBh"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 6, "ORL 89h,#02h"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 9, "ORL 88h,#10h"),
                String.format(Locale.ROOT, Disassembler.LABEL_LINE_TEMPLATE, "loop"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 12, "XRL 80h,#0FFh"),
                String.format(Locale.ROOT, Disassembler.LABEL_LINE_TEMPLATE, "wait"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 15, "JNB TCON.5,wait"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 18, "CLR TCON.5"),
                String.format(Locale.ROOT, Disassembler.CODE_LINE_TEMPLATE, 20, "AJMP loop")
        );
        test(code, expectedLines);
    }

    private static Code makeCode(String name, int[] rom, Map<Integer, String> labels)
    {
        byte[] romBytes = new byte[rom.length];
        for (int i = 0; i < rom.length; i++)
        {
            romBytes[i] = (byte) (rom[i] & 0xFF);
        }
        return new Code(name, romBytes, new Int2ObjectOpenHashMap<>(labels));
    }

    private static void test(Code code, List<String> expectedLines)
    {
        Disassembly disassembly = Assertions.assertDoesNotThrow(() -> Disassembler.disassemble(code));
        List<String> lines = disassembly.getLines();
        Assertions.assertEquals(lines.size(), expectedLines.size());
        for (int i = 0; i < expectedLines.size(); i++)
        {
            Assertions.assertEquals(expectedLines.get(i), lines.get(i), "Disassembly mismatch on line " + (i + 1));
        }
    }
}
