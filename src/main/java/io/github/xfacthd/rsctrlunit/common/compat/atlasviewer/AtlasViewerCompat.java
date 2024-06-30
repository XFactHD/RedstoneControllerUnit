package io.github.xfacthd.rsctrlunit.common.compat.atlasviewer;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.texture.AreaMaskSource;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;

public final class AtlasViewerCompat
{
    public static final Component LABEL_TEXTURE = Component.translatable("label.rsctrlunit.source_tooltip.area_mask.texture");
    public static final Component LABEL_SPRITE = Component.translatable("label.rsctrlunit.source_tooltip.area_mask.sprite");
    public static final Component LABEL_AREA = Component.translatable("label.rsctrlunit.source_tooltip.area_mask.area");
    public static final String VALUE_AREA = "value.rsctrlunit.source_tooltip.area_mask.area";

    public static void init(IEventBus modBus)
    {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("atlasviewer"))
        {
            try
            {
                GuardedAccess.init(modBus);
            }
            catch (Throwable t)
            {
                RedstoneControllerUnit.LOGGER.error("Encountered an error while initializing AtlasViewer compat");
            }
        }
    }



    private static final class GuardedAccess
    {
        public static void init(IEventBus modBus)
        {
            modBus.addListener(GuardedAccess::onRegisterSpriteSourceDetails);
        }

        private static void onRegisterSpriteSourceDetails(RegisterSpriteSourceDetailsEvent event)
        {
            event.registerPrimaryResourceGetter(
                    AreaMaskSource.AreaMaskInstance.class,
                    AreaMaskSource.AreaMaskInstance::srcRes
            );
            event.registerSourceTooltipAppender(AreaMaskSource.class, (src, consumer) ->
            {
                consumer.accept(LABEL_TEXTURE, Component.literal(src.src().toString()));
                consumer.accept(LABEL_SPRITE, Component.literal(src.sprite().toString()));
                consumer.accept(LABEL_AREA, Component.translatable(VALUE_AREA, src.x(), src.y(), src.w(), src.h()));
            });
        }
    }



    private AtlasViewerCompat() { }
}
