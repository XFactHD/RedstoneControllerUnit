package io.github.xfacthd.rsctrlunit.client.screen.widget;

import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundControllerActionPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ActionButton extends Button
{
    private final ControllerScreen owner;
    private final ServerboundControllerActionPayload.Action action;

    public ActionButton(Button.Builder builder, ControllerScreen owner, ServerboundControllerActionPayload.Action action)
    {
        super(builder);
        this.owner = owner;
        this.action = action;
    }

    @Override
    public void onPress()
    {
        PacketDistributor.sendToServer(new ServerboundControllerActionPayload(owner.getMenu().containerId, action));
    }



    public static Builder builder(Component message)
    {
        return new Builder(message);
    }

    public static final class Builder extends Button.Builder
    {
        private Builder(Component message)
        {
            super(message, btn -> {});
        }

        @Override
        public Builder pos(int x, int y)
        {
            return (Builder) super.pos(x, y);
        }

        @Override
        public Builder width(int width)
        {
            return (Builder) super.width(width);
        }

        @Override
        public Builder size(int width, int height)
        {
            return (Builder) super.size(width, height);
        }

        @Override
        public Builder bounds(int x, int y, int width, int height)
        {
            return (Builder) super.bounds(x, y, width, height);
        }

        @Override
        public Builder tooltip(@Nullable Tooltip tooltip)
        {
            return (Builder) super.tooltip(tooltip);
        }

        @Override
        public Builder createNarration(CreateNarration createNarration)
        {
            return (Builder) super.createNarration(createNarration);
        }

        @Override
        public Button build()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Button build(Function<Button.Builder, Button> builder)
        {
            throw new UnsupportedOperationException();
        }

        public ActionButton build(ControllerScreen owner, ServerboundControllerActionPayload.Action action)
        {
            return new ActionButton(this, owner, action);
        }
    }
}
