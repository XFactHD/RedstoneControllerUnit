package io.github.xfacthd.rsctrlunit.client;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.model.ControllerModelLoader;
import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.texture.AreaMaskSource;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@SuppressWarnings("UtilityClassWithPublicConstructor")
@Mod(value = RedstoneControllerUnit.MOD_ID, dist = Dist.CLIENT)
public final class RCUClient
{
    public RCUClient(IEventBus modBus)
    {
        modBus.addListener(RCUClient::onClientSetup);
        modBus.addListener(RCUClient::onRegisterMenuScreens);
        modBus.addListener(RCUClient::onRegisterGeometryLoaders);
        modBus.addListener(RCUClient::onRegisterSpriteSourceTypes);
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event)
    {

    }

    @SubscribeEvent
    static void onRegisterMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(RCUContent.MENU_TYPE_CONTROLLER.get(), ControllerScreen::new);
    }

    @SubscribeEvent
    static void onRegisterGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(Utils.rl("controller"), new ControllerModelLoader());
    }

    @SubscribeEvent
    static void onRegisterSpriteSourceTypes(final RegisterSpriteSourceTypesEvent event)
    {
        AreaMaskSource.register(event::register);
    }
}
