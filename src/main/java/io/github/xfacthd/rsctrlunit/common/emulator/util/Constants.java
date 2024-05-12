package io.github.xfacthd.rsctrlunit.common.emulator.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class Constants
{
    public static final int ROM_SIZE = 65536;
    public static final int RAM_SIZE = 256;
    public static final int INITIAL_PROGRAM_COUNTER = 0;
    public static final int INITIAL_STACK_POINTER = 0x07;

    public static final Code TEST_CODE = new Code(
            Component.literal("RedstoneBlinker"),
            new byte[] { 0x63, (byte) 0x80, (byte) 0xFF, 0x74, 0x14, (byte) 0xD5, (byte) 0xE0, (byte) 0xFD, 0x01, 0x00 },
            new Int2ObjectOpenHashMap<>(Map.of(0, "start", 5, "loop"))
    );
    public static final Code TEST_CODE_TWO = new Code(
            Component.literal("RedstoneForward"),
            new byte[] { (byte) 0x85, (byte) 0xA0, (byte) 0x80, 0x01, 0x00 },
            new Int2ObjectOpenHashMap<>(Map.of(0, "start"))
    );

    public static final int[] REGISTER_BASE_ADDRESS = new int[] { 0x00, 0x08, 0x10, 0x18 };

    public static final int ADDRESS_IO_PORT0 = 0x80;
    public static final int ADDRESS_STACK_POINTER = 0x81;
    public static final int ADDRESS_DATA_POINTER_LOWER = 0x82;
    public static final int ADDRESS_DATA_POINTER_UPPER = 0x83;
    public static final int ADDRESS_PCON = 0x87;
    public static final int ADDRESS_TCON = 0x88;
    public static final int ADDRESS_TMOD = 0x89;
    public static final int ADDRESS_TL0 = 0x8A;
    public static final int ADDRESS_TL1 = 0x8B;
    public static final int ADDRESS_TH0 = 0x8C;
    public static final int ADDRESS_TH1 = 0x8D;
    public static final int ADDRESS_IO_PORT1 = 0x90;
    public static final int ADDRESS_SCON = 0x98;
    public static final int ADDRESS_SBUF = 0x99;
    public static final int ADDRESS_IO_PORT2 = 0xA0;
    public static final int ADDRESS_IE = 0xA8;
    public static final int ADDRESS_IO_PORT3 = 0xB0;
    public static final int ADDRESS_IP = 0xB8;
    public static final int ADDRESS_STATUS_WORD = 0xD0;
    public static final int ADDRESS_ACCUMULATOR = 0xE0;
    public static final int ADDRESS_REGISTER_B = 0xF0;
    public static final int[] IO_PORTS = new int[] {
            ADDRESS_IO_PORT0, ADDRESS_IO_PORT1, ADDRESS_IO_PORT2, ADDRESS_IO_PORT3
    };

    public static final int BIT_ADDRESS_PARITY = ADDRESS_STATUS_WORD;
    public static final int BIT_ADDRESS_OVERFLOW = ADDRESS_STATUS_WORD + 2;
    public static final int BIT_ADDRESS_AUX_CARRY = ADDRESS_STATUS_WORD + 6;
    public static final int BIT_ADDRESS_CARRY = ADDRESS_STATUS_WORD + 7;

    private Constants() { }
}
