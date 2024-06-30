package io.github.xfacthd.rsctrlunit.common.compat;

import io.github.xfacthd.rsctrlunit.common.compat.atlasviewer.AtlasViewerCompat;
import net.neoforged.bus.api.IEventBus;

public final class CompatHandler
{
    public static void init(IEventBus modBus)
    {
        AtlasViewerCompat.init(modBus);
    }



    private CompatHandler() { }
}
