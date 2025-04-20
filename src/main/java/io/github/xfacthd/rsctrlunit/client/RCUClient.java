package io.github.xfacthd.rsctrlunit.client;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.model.UnbakedControllerModel;
import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.ProgrammerScreen;
import io.github.xfacthd.rsctrlunit.client.texture.AreaMaskSource;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourcesEvent;

@Mod(value = RedstoneControllerUnit.MOD_ID, dist = Dist.CLIENT)
public final class RCUClient
{
    public RCUClient(IEventBus modBus)
    {
        modBus.addListener(RCUClient::onRegisterMenuScreens);
        modBus.addListener(RCUClient::onRegisterBlockStateModels);
        modBus.addListener(RCUClient::onRegisterSpriteSourceTypes);
    }

    private static void onRegisterMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(RCUContent.MENU_TYPE_CONTROLLER.get(), ControllerScreen::new);
        event.register(RCUContent.MENU_TYPE_PROGRAMMER.get(), ProgrammerScreen::new);
    }

    private static void onRegisterBlockStateModels(final RegisterBlockStateModels event)
    {
        event.registerModel(Utils.rl("controller"), UnbakedControllerModel.CODEC);
    }

    private static void onRegisterSpriteSourceTypes(final RegisterSpriteSourcesEvent event)
    {
        event.register(AreaMaskSource.ID, AreaMaskSource.CODEC);
    }
}
