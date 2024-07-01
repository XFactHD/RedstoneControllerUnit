package io.github.xfacthd.rsctrlunit;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.Assembler;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.ErrorPrinter;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class AssemblerTests
{
    @Test
    void testOne()
    {
        String source = """
                start:
                xrl 80h,#0ffh
                mov a,#20
                loop:
                djnz 0E0h,loop
                ajmp start
                """;
        Code expected = makeCode(
                "RedstoneBlinker",
                new int[] { 0x63, 0x80, 0xFF, 0x74, 0x14, 0xD5, 0xE0, 0xFD, 0x01, 0x00 },
                Map.of(0, "start", 5, "loop")
        );
        test(source, expected);
    }

    @Test
    void testTwo()
    {
        String source = """
                start:
                mov 0a0h,80h
                ajmp start
                """;
        Code expected = makeCode(
                "RedstoneForward",
                new int[] { 0x85, 0xA0, 0x80, 0x01, 0x00 },
                Map.of(0, "start")
        );
        test(source, expected);
    }

    @Test
    void testThree()
    {
        String source = """
                mov th0,#0ebh
                mov tl0,#0ebh
                orl tmod,#02h
                orl tcon,#10h
                loop:
                xrl 80h,#0ffh
                wait:
                jnb tcon.5,wait
                clr tcon.5
                ajmp loop
                """;
        Code expected = makeCode(
                "RedstoneTimerBlinker",
                new int[] { 0x75, 0x8C, 0xEB, 0x75, 0x8A, 0xEB, 0x43, 0x89, 0x02, 0x43, 0x88, 0x10, 0x63, 0x80, 0xFF, 0x30, 0x8D, 0xFD, 0xC2, 0x8D, 0x01, 0x0C },
                Map.of(0x0C, "loop", 0x0F, "wait")
        );
        test(source, expected);
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

    private static void test(String source, Code expected)
    {
        List<Component> messages = new ArrayList<>();
        Code code = Assertions.assertDoesNotThrow(
                () -> Assembler.assemble(expected.name(), source, new ErrorPrinter.Collecting(messages))
        );
        Assertions.assertTrue(messages.isEmpty(), "Received error messages");
        Assertions.assertNotNull(code, "Assembled code is null");
        Assertions.assertNotEquals(Code.EMPTY, code, "Assembled code is empty");
        Assertions.assertEquals(expected, code);
    }
}
