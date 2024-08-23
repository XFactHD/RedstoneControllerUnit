package io.github.xfacthd.rsctrlunit.common.compat.morered;

import commoble.morered.api.MoreRedAPI;
import commoble.morered.api.WireConnector;
import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.RedstoneHandler;
import io.github.xfacthd.rsctrlunit.common.util.registration.DeferredBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Map;

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

    public static int getBundledPower(Level level, BlockState ctrlState, BlockPos ctrlPos, BlockPos cablePos, Direction facing, Direction side, int offset, int count, int inputMask)
    {
        if (loaded)
        {
            return BundledCableReader.getBundledPower(level, ctrlState, ctrlPos, cablePos, facing, side, offset, count, inputMask);
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
            Map<Block, WireConnector> registry = MoreRedAPI.getCableConnectabilityRegistry();
            registry.put(RCUContent.BLOCK_CONTROLLER.value(), RedstoneHandlerCableConnector.INSTANCE);
            registry.put(RCUContent.BLOCK_ADC.value(), RedstoneHandlerCableConnector.INSTANCE);
            registry.put(RCUContent.BLOCK_DAC.value(), RedstoneHandlerCableConnector.INSTANCE);
        }

        private static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
        {
            registerChanneledPower(event, RCUContent.BE_TYPE_CONTROLLER);
            registerChanneledPower(event, RCUContent.BE_TYPE_ADC);
            registerChanneledPower(event, RCUContent.BE_TYPE_DAC);
        }

        private static void registerChanneledPower(RegisterCapabilitiesEvent event, DeferredBlockEntity<? extends RedstoneHandler> blockEntity)
        {
            event.registerBlockEntity(MoreRedAPI.CHANNELED_POWER_CAPABILITY, blockEntity.get(), RedstoneHandlerChanneledPowerSupplier::get);
        }
    }
}
