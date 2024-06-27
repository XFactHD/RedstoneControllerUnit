package io.github.xfacthd.rsctrlunit.client.screen.popup;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

final class ConfirmationScreen extends MessageScreen
{
    private static final int PADDING = 4;
    private static final int BTN_WIDTH = TEXT_WIDTH / 2 - PADDING;
    private static final int BTN_OK_X = WIDTH / 2 - PADDING - BTN_WIDTH;
    private static final int BTN_CANCEL_X = WIDTH / 2 + PADDING;

    private final Runnable confirmAction;

    ConfirmationScreen(List<Component> messages, Runnable confirmAction)
    {
        super(MessageScreen.CONFIRM_TITLE, messages);
        this.confirmAction = confirmAction;
    }

    @Override
    protected void addButtons()
    {
        addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, btn -> onConfirm())
                .pos(leftPos + BTN_OK_X, topPos + imageHeight - BTN_BOTTOM_OFFSET)
                .size(BTN_WIDTH, BTN_HEIGHT)
                .build()
        );
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose())
                .pos(leftPos + BTN_CANCEL_X, topPos + imageHeight - BTN_BOTTOM_OFFSET)
                .size(BTN_WIDTH, BTN_HEIGHT)
                .build()
        );
    }

    private void onConfirm()
    {
        confirmAction.run();
        onClose();
    }
}
