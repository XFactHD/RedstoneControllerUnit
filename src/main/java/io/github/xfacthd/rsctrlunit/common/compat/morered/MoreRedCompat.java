package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.MoreRedAPI;
import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class MoreRedCompat
{
    private static boolean loaded = false;

    public static void init(IEventBus modBus)
    {
        try
        {
            if (ModList.get().isLoaded("morered"))
            {
                GuardedAccess.init(modBus);
                loaded = true;
            }
        }
        catch (Throwable t)
        {
            RedstoneControllerUnit.LOGGER.error("Encountered an error while initializing MoreRed compat");
        }
    }

    public static boolean isBundledCable(Level level, BlockState ctrlState, BlockPos ctrlPos, BlockState cableState, BlockPos cablePos, Direction facing, Direction side)
    {
        if (loaded)
        {
            return BundledCableReader.isBundledCable(level, ctrlState, ctrlPos, cableState, cablePos, facing, side);
        }
        return false;
    }

    public static byte getBundledPower(Level level, BlockState ctrlState, BlockPos ctrlPos, BlockPos cablePos, Direction facing, Direction side, boolean upper, byte inputMask)
    {
        if (loaded)
        {
            return BundledCableReader.getBundledPower(level, ctrlState, ctrlPos, cablePos, facing, side, upper, inputMask);
        }
        return 0;
    }

    private static final class GuardedAccess
    {
        public static void init(IEventBus modBus)
        {
            modBus.addListener(GuardedAccess::onCommonSetup);
            modBus.addListener(GuardedAccess::onRegisterCapabilities);
        }

        private static void onCommonSetup(FMLCommonSetupEvent event)
        {
            MoreRedAPI.getCableConnectabilityRegistry().put(
                    RCUContent.BLOCK_CONTROLLER.value(),
                    new ControllerCableConnector()
            );
        }

        private static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
        {
            event.registerBlockEntity(
                    MoreRedAPI.CHANNELED_POWER_CAPABILITY,
                    RCUContent.BE_TYPE_CONTROLLER.get(),
                    ControllerChanneledPowerSupplier::get
            );
        }
    }
}
