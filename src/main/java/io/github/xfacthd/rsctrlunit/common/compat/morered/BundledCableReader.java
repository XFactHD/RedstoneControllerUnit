package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

final class BundledCableReader
{
    public static boolean isBundledCable(Level level, BlockState ctrlState, BlockPos ctrlPos, BlockState cableState, BlockPos cablePos, Direction facing, Direction side)
    {
        Map<Block, WireConnector> registry = MoreRedAPI.getCableConnectabilityRegistry();
        WireConnector connector = registry.getOrDefault(cableState.getBlock(), MoreRedAPI.getDefaultCableConnector());
        return connector.canConnectToAdjacentWire(level, cablePos, cableState, ctrlPos, ctrlState, facing, side);
    }

    public static byte getBundledPower(Level level, BlockState ctrlState, BlockPos ctrlPos, BlockPos cablePos, Direction facing, Direction side, boolean upper, byte inputMask)
    {
        ChanneledPowerSupplier supplier = level.getCapability(MoreRedAPI.CHANNELED_POWER_CAPABILITY, cablePos, side);
        if (supplier != null)
        {
            int input = 0;
            int offset = upper ? 8 : 0;
            for (int i = 0; i < 8; i++)
            {
                if (((inputMask >> i) & 0x1) == 0) continue;
                int power = supplier.getPowerOnChannel(level, ctrlPos, ctrlState, facing, i + offset);
                if (power > 0)
                {
                    input |= (1 << i);
                }
            }
            return (byte) (input & 0xFF);
        }
        return 0;
    }
}
