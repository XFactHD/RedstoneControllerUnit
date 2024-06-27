package io.github.xfacthd.rsctrlunit.client.util;

import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.ProgrammerScreen;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;

public final class ClientAccess
{
    public static void handleStatusViewUpdate(int windowId, byte[] ram, byte[] output, byte[] input, int programCounter)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == windowId)
        {
            ctrlScreen.updateStatus(ram, output, input, programCounter);
        }
    }

    public static void handleCodeUpdate(int windowId, Code code)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == windowId)
        {
            ctrlScreen.getMenu().updateCode(code);
            ctrlScreen.updateDisassembly();
        }
    }

    public static void handlePortConfigUpdate(int windowId, Direction facing, PortConfig[] configs)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == windowId)
        {
            ctrlScreen.getMenu().updatePortConfigs(facing, configs);
        }
    }

    public static void handleCodeReply(int windowId, Code code)
    {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ProgrammerScreen progScreen && progScreen.getMenu().containerId == windowId)
        {
            progScreen.receiveBlockCodeFromServer(code);
        }
    }



    private ClientAccess() { }
}
