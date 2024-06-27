package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import io.github.xfacthd.rsctrlunit.common.emulator.util.BitWriteMode;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import net.minecraft.nbt.CompoundTag;

/**
 *      |           T1         |           T0         |
 *      +------+-----+----+----+------+-----+----+----+
 * TMOD | Gate | C/T | M1 | M0 | Gate | C/T | M1 | M0 |
 *      +------+-----+----+----+------+-----+----+----+
 *      Gate:  1 => gated by INTx input
 *      C/T:   0 => Timer (internal clock), 1 => Counter (external clock)
 *      M1/M0: 00 => 13bit counter, 01 => 16bit counter, 10 => 8bit counter with auto-reload, 11 => 2 8bit counters (T0 only)
 * <p>
 *      +-----+-----+-----+-----+-----+-----+-----+-----+
 * TCON | TF1 | TR1 | TF0 | TR0 | IE1 | IT1 | IE0 | IT0 |
 *      +-----+-----+-----+-----+-----+-----+-----+-----+
 *      TR: 0 => timer stopped, 1 => timer running
 *      TF: 1 => count register overflow
 */
public final class Timers
{
    private static final int MAX_COUNT_5BIT = (1 << 5) - 1;
    private static final int MAX_COUNT_8BIT = (1 << 8) - 1;
    private static final int MODE_13BIT = 0b00000000;
    private static final int MODE_16BIT = 0b00000001;
    private static final int MODE_8BIT_AUTORELOAD = 0b00000010;
    private static final int MODE_8BIT_SPLIT = 0b00000011;
    private static final int MASK_MODE = MODE_8BIT_SPLIT;
    private static final int MASK_RUN0 = 0b00010000;
    private static final int MASK_RUN1 = 0b01000000;
    private static final int MASK_GATE0 = 0b00001000;
    private static final int MASK_INT0 = 0b00000100;
    private static final int MASK_CT_0 = 0b00000100;

    private final RAM ram;
    private final IOPorts ioPorts;
    private volatile boolean ticked = false;
    private boolean lastTrigger0 = false;
    private boolean lastTrigger1 = false;

    public Timers(RAM ram, IOPorts ioPorts)
    {
        this.ram = ram;
        this.ioPorts = ioPorts;
    }

    public void tickClock()
    {
        ticked = true;
    }

    void run()
    {
        if (!ticked) return;
        ticked = false;

        byte port3 = ioPorts.readInputPort(3);
        boolean trigger0 = (port3 & 0b00010000) != 0;
        boolean trigger1 = (port3 & 0b00100000) != 0;

        // Counter mode triggers on falling edge
        updateTimer(0, port3, lastTrigger0 && !trigger0);
        updateTimer(1, port3, lastTrigger1 && !trigger1);

        lastTrigger0 = trigger0;
        lastTrigger1 = trigger1;
    }

    private void updateTimer(int idx, byte port3, boolean extTrigger)
    {
        byte tmod = ram.readByte(Constants.ADDRESS_TMOD);
        int mode = (tmod >>> (4 * idx)) & MASK_MODE;
        if (idx == 1 && mode == MODE_8BIT_SPLIT) return;

        boolean counter = (tmod & (MASK_CT_0 << (4 * idx))) != 0;
        if (counter && !extTrigger)
        {
            return;
        }

        byte tcon = ram.readByte(Constants.ADDRESS_TCON);
        boolean running = (tcon & (MASK_RUN0 << (2 * idx))) != 0 && isNotGated(port3, tmod, idx);
        if ((idx != 0 || mode != MODE_8BIT_SPLIT) && !running)
        {
            return;
        }

        switch (mode)
        {
            case MODE_13BIT ->
            {
                int countLow = ram.read(Constants.ADDRESS_TL0 + idx) + 1;
                int countHigh = ram.read(Constants.ADDRESS_TH0 + idx);
                if (countLow > MAX_COUNT_5BIT)
                {
                    countLow = 0;
                    countHigh++;
                    if (countHigh > MAX_COUNT_8BIT)
                    {
                        countHigh = 0;
                        setOverflow(idx);
                    }
                }
                // Bits 5-7 of the LSB mirror bits 0-2 of the MSB
                ram.write(Constants.ADDRESS_TL0 + idx, ((countHigh & 0b00000111) << 5) | (countLow & 0x1F));
                ram.write(Constants.ADDRESS_TH0 + idx, countHigh);
            }
            case MODE_16BIT ->
            {
                int countLow = ram.read(Constants.ADDRESS_TL0 + idx) + 1;
                if (countLow > MAX_COUNT_8BIT)
                {
                    countLow = 0;
                    int countHigh = ram.read(Constants.ADDRESS_TH0 + idx) + 1;
                    if (countHigh > MAX_COUNT_8BIT)
                    {
                        countHigh = 0;
                        setOverflow(idx);
                    }
                    ram.write(Constants.ADDRESS_TH0 + idx, countHigh);
                }
                ram.write(Constants.ADDRESS_TL0 + idx, countLow);
            }
            case MODE_8BIT_AUTORELOAD ->
            {
                int count = ram.read(Constants.ADDRESS_TL0 + idx) + 1;
                if (count > MAX_COUNT_8BIT)
                {
                    count = ram.readByte(Constants.ADDRESS_TH0 + idx) & 0xFF;
                    setOverflow(idx);
                }
                ram.write(Constants.ADDRESS_TL0 + idx, count);
            }
            case MODE_8BIT_SPLIT ->
            {
                if (running)
                {
                    int count = ram.read(Constants.ADDRESS_TL0) + 1;
                    if (count > MAX_COUNT_8BIT)
                    {
                        count = 0;
                        setOverflow(idx);
                    }
                    ram.write(Constants.ADDRESS_TL0, count);
                }
                boolean runningUpper = (tcon & MASK_RUN1) != 0;
                if (runningUpper)
                {
                    int count = ram.read(Constants.ADDRESS_TH0) + 1;
                    if (count > MAX_COUNT_8BIT)
                    {
                        count = 0;
                        setOverflow(1);
                    }
                    ram.write(Constants.ADDRESS_TH0, count);
                }
            }
        }
    }

    private static boolean isNotGated(byte port3, byte tmod, int idx)
    {
        if ((tmod & (MASK_GATE0 << (4 * idx))) != 0)
        {
            return (port3 & (MASK_INT0 << idx)) != 0;
        }
        return true;
    }

    private void setOverflow(int idx)
    {
        ram.writeBit(Constants.BIT_ADDRESS_TIMER0_OVERFLOW + (idx * 2), BitWriteMode.SET);
    }

    public void load(CompoundTag tag)
    {
        lastTrigger0 = tag.getBoolean("last_trigger_0");
        lastTrigger1 = tag.getBoolean("last_trigger_1");
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("last_trigger_0", lastTrigger0);
        tag.putBoolean("last_trigger_1", lastTrigger1);
        return tag;
    }
}
