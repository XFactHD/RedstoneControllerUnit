package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.IntFunction;

public record ServerboundControllerActionPayload(int windowId, Action action) implements CustomPacketPayload
{
    public static final Type<ServerboundControllerActionPayload> TYPE = Utils.payloadType("serverbound_controller_action");
    public static final StreamCodec<ByteBuf, ServerboundControllerActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundControllerActionPayload::windowId,
            ByteBufCodecs.idMapper(Action.BY_ID, Action::ordinal),
            ServerboundControllerActionPayload::action,
            ServerboundControllerActionPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            switch (action)
            {
                case UNKNOWN -> ctx.disconnect(Component.literal("Received invalid controller action"));
                case LOAD_ROM -> ctrlMenu.loadRomFromCard();
                case SAVE_ROM -> ctrlMenu.saveRomToCard();
                case CLEAR_ROM -> ctrlMenu.clearRom();
                case PAUSE_RESUME -> ctrlMenu.togglePauseResume();
                case STEP -> ctrlMenu.requestStep();
                case RESET -> ctrlMenu.requestReset();
                case TOGGLE_PORT_MAP -> ctrlMenu.togglePortMapRender();
            }
        }
    }

    @Override
    public Type<ServerboundControllerActionPayload> type()
    {
        return TYPE;
    }



    public enum Action
    {
        UNKNOWN,
        LOAD_ROM,
        SAVE_ROM,
        CLEAR_ROM,
        PAUSE_RESUME,
        STEP,
        RESET,
        TOGGLE_PORT_MAP
        ;

        private static final IntFunction<Action> BY_ID = ByIdMap.continuous(
                Action::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
    }
}
