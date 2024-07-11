package io.github.xfacthd.rsctrlunit;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.compat.CompatHandler;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.InterpreterThreadPool;
import io.github.xfacthd.rsctrlunit.common.net.NetworkSetup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
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

        modBus.addListener(NetworkSetup::onRegisterPayloadHandlers);

        InterpreterThreadPool.init();
        CompatHandler.init(modBus);
    }
}
