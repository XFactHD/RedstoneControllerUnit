package io.github.xfacthd.rsctrlunit.client.net;

import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.ProgrammerScreen;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundReplyCodePayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdateCodePayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdatePortConfigsPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdatePortMappingPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdateStatusPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandler
{
    public static void onRegisterPayloadHandlers(RegisterClientPayloadHandlersEvent event)
    {
        event.register(ClientboundUpdateStatusPayload.TYPE, ClientNetworkHandler::handleUpdateStatus);
        event.register(ClientboundUpdateCodePayload.TYPE, ClientNetworkHandler::handleUpdateCode);
        event.register(ClientboundUpdatePortConfigsPayload.TYPE, ClientNetworkHandler::handleUpdatePortConfigs);
        event.register(ClientboundReplyCodePayload.TYPE, ClientNetworkHandler::handleReplyCode);
        event.register(ClientboundUpdatePortMappingPayload.TYPE, ClientNetworkHandler::handleUpdatePortMapping);
    }

    private static void handleUpdateStatus(ClientboundUpdateStatusPayload payload, IPayloadContext ctx)
    {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == payload.windowId())
        {
            ClientboundUpdateStatusPayload.InterpreterState state = payload.state();
            ctrlScreen.updateStatus(state.ram(), state.sfr(), state.output(), state.input(), state.programCounter());
        }
    }

    private static void handleUpdateCode(ClientboundUpdateCodePayload payload, IPayloadContext ctx)
    {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == payload.windowId())
        {
            ctrlScreen.getMenu().updateCode(payload.code());
            ctrlScreen.updateDisassembly();
        }
    }

    private static void handleUpdatePortConfigs(ClientboundUpdatePortConfigsPayload payload, IPayloadContext ctx)
    {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == payload.windowId())
        {
            ctrlScreen.getMenu().updatePortConfigs(payload.facing(), payload.configs());
        }
    }

    private static void handleReplyCode(ClientboundReplyCodePayload payload, IPayloadContext ctx)
    {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen instanceof ProgrammerScreen progScreen && progScreen.getMenu().containerId == payload.windowId())
        {
            progScreen.receiveBlockCodeFromServer(payload.code());
        }
    }

    private static void handleUpdatePortMapping(ClientboundUpdatePortMappingPayload payload, IPayloadContext ctx)
    {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen instanceof ControllerScreen ctrlScreen && ctrlScreen.getMenu().containerId == payload.windowId())
        {
            ctrlScreen.getMenu().updatePortMapping(payload.portMapping());
        }
    }

    private ClientNetworkHandler() { }
}
