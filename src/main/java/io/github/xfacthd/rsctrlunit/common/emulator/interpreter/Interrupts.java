package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.nbt.CompoundTag;

final class Interrupts
{
    private static final int NO_ISR = -1;
    private static final int[] ISR_ADDRESSES = new int[] {
            0x0003, // External 0
            0x000B, // Timer 0
            0x0013, // External 1
            0x001B  // Timer 1
    };
    private static final int[] TRIGGER_MASKS = new int[] {
            0b00000001, // External 0
            0b00000100, // Timer 0
            0b00100000, // External 1
            0b10000000  // Timer 1
    };
    private static final int MASK_ENABLE_ALL = 0b10000000;

    private final RAM ram;
    private ISR activeIsrHighPrio = null;
    private ISR activeIsrLowPrio = null;

    Interrupts(RAM ram)
    {
        this.ram = ram;
    }

    int run()
    {
        byte ie = ram.readByte(Constants.ADDRESS_IE);
        if ((ie & MASK_ENABLE_ALL) == 0)
        {
            return NO_ISR;
        }

        byte ip = ram.readByte(Constants.ADDRESS_IP);
        // All interrupt trigger bits except those for the serial port are stored in TCON
        byte tcon = ram.readByte(Constants.ADDRESS_TCON);

        if (activeIsrHighPrio != null)
        {
            return NO_ISR;
        }

        activeIsrHighPrio = findNextInterrupt(true, ie, ip, tcon);
        if (activeIsrHighPrio != null)
        {
            return activeIsrHighPrio.isrAddress;
        }

        if (activeIsrLowPrio != null)
        {
            return NO_ISR;
        }

        activeIsrLowPrio = findNextInterrupt(false, ie, ip, tcon);
        if (activeIsrLowPrio != null)
        {
            return activeIsrLowPrio.isrAddress;
        }

        return NO_ISR;
    }

    private ISR findNextInterrupt(boolean serviceHighPrio, byte ie, byte ip, byte tcon)
    {
        // 8051 technically has 5 interrupts but the serial port is unsupported, so its interrupt is ignored
        for (int i = 0; i < 4; i++)
        {
            // Interrupt may have switched priority while its ISR is running, ignore
            if (activeIsrHighPrio != null && activeIsrHighPrio.index == i) continue;
            if (activeIsrLowPrio != null && activeIsrLowPrio.index == i) continue;

            if ((ie & (1 << i)) == 0)
            {
                continue;
            }

            boolean isHighPrio = (ip & (1 << i)) != 0;
            if (isHighPrio != serviceHighPrio)
            {
                continue;
            }

            int mask = TRIGGER_MASKS[i];
            if ((tcon & mask) != 0)
            {
                ram.write(Constants.ADDRESS_TCON, tcon & ~mask);
                return new ISR(i, ISR_ADDRESSES[i], isHighPrio);
            }
        }
        return null;
    }

    void returnFromIsr()
    {
        if (activeIsrHighPrio != null)
        {
            activeIsrHighPrio = null;
        }
        else if (activeIsrLowPrio != null)
        {
            activeIsrLowPrio = null;
        }
    }

    public void load(CompoundTag tag)
    {
        if (tag.contains("isr_high_prio"))
        {
            activeIsrHighPrio = Utils.fromNbt(ISR.CODEC, tag.getCompound("isr_high_prio"), null);
        }
        if (tag.contains("isr_low_prio"))
        {
            activeIsrLowPrio = Utils.fromNbt(ISR.CODEC, tag.getCompound("isr_low_prio"), null);
        }
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        if (activeIsrHighPrio != null)
        {
            tag.put("isr_high_prio", Utils.toNbt(ISR.CODEC, activeIsrHighPrio));
        }
        if (activeIsrLowPrio != null)
        {
            tag.put("isr_low_prio", Utils.toNbt(ISR.CODEC, activeIsrLowPrio));
        }
        return tag;
    }

    private record ISR(int index, int isrAddress, boolean highPriority)
    {
        private static final Codec<ISR> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("index").forGetter(ISR::index),
                Codec.INT.fieldOf("address").forGetter(ISR::isrAddress),
                Codec.BOOL.fieldOf("high_priority").forGetter(ISR::highPriority)
        ).apply(inst, ISR::new));
    }
}
