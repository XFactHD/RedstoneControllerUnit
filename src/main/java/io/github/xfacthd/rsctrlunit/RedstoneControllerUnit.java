package io.github.xfacthd.rsctrlunit;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.InterpreterThreadPool;
import io.github.xfacthd.rsctrlunit.common.net.NetworkSetup;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(RedstoneControllerUnit.MOD_ID)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class RedstoneControllerUnit
{
    public static final String MOD_ID = "rsctrlunit";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RedstoneControllerUnit(IEventBus modBus)
    {
        RCUContent.init(modBus);

        modBus.addListener(RedstoneControllerUnit::onBuildCreativeModeTabs);
        modBus.addListener(NetworkSetup::onRegisterPayloadHandlers);

        InterpreterThreadPool.init();
    }

    private static void onBuildCreativeModeTabs(final BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS)
        {
            event.accept(RCUContent.BLOCK_CONTROLLER.value());
            event.accept(RCUContent.ITEM_MEMORY_CARD.value());
            event.accept(RCUContent.ITEM_PROGRAMMER.value());
        }
    }
}
