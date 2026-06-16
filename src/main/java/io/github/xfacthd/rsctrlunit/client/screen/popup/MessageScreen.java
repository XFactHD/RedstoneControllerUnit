package io.github.xfacthd.rsctrlunit.client.screen.popup;

import io.github.xfacthd.rsctrlunit.client.util.ClientUtils;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public sealed class MessageScreen extends Screen permits ConfirmationScreen {
    private static final Identifier BACKGROUND = Utils.rl("background");
    public static final Component INFO_TITLE = Component.translatable("title.rsctrlunit.message.info");
    public static final Component ERROR_TITLE = Component.translatable("title.rsctrlunit.message.error");
    public static final Component CONFIRM_TITLE = Component.translatable("title.rsctrlunit.message.confirm");
    protected static final int WIDTH = 176;
    private static final int BASE_HEIGHT = 64;
    protected static final int TEXT_WIDTH = WIDTH - 12;
    private static final int BTN_WIDTH = 60;
    protected static final int BTN_HEIGHT = 20;
    protected static final int BTN_BOTTOM_OFFSET = 6 + BTN_HEIGHT;
    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 6;

    private final List<Component> messages;
    private final List<List<FormattedCharSequence>> textBlocks = new ArrayList<>();
    protected int leftPos;
    protected int topPos;
    protected int imageHeight;

    public static MessageScreen info(List<Component> message) {
        return new MessageScreen(INFO_TITLE, message);
    }

    public static MessageScreen error(List<Component> message) {
        return new MessageScreen(ERROR_TITLE, message);
    }

    public static MessageScreen confirm(List<Component> message, Runnable action) {
        return new ConfirmationScreen(message, action);
    }

    public MessageScreen(Component title, List<Component> messages) {
        super(title);
        this.messages = messages;
    }

    @Override
    protected void init() {
        textBlocks.clear();

        imageHeight = BASE_HEIGHT;
        for (Component msg : messages) {
            imageHeight += ClientUtils.getWrappedHeight(font, msg, TEXT_WIDTH);
            imageHeight += font.lineHeight;

            textBlocks.add(font.split(msg, TEXT_WIDTH));
        }
        imageHeight -= font.lineHeight;

        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - imageHeight) / 2;

        addButtons();
    }

    protected void addButtons() {
        addRenderableWidget(Button.builder(CommonComponents.GUI_OK, _ -> onClose())
                .pos(leftPos + (WIDTH / 2) - (BTN_WIDTH / 2), topPos + imageHeight - BTN_BOTTOM_OFFSET)
                .size(BTN_WIDTH, BTN_HEIGHT)
                .build()
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, WIDTH, imageHeight);
        graphics.text(font, title, leftPos + TITLE_X, topPos + TITLE_Y, 0x404040, false);

        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<FormattedCharSequence> block : textBlocks) {
            for (FormattedCharSequence line : block) {
                graphics.text(font, line, leftPos + TITLE_X, y, 0, false);
                y += font.lineHeight;
            }
            y += font.lineHeight;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        Style style = findTextLine(mouseX, mouseY);
        if (style != null) {
            graphics.componentHoverEffect(font, style, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        Style style = findTextLine((int) event.x(), (int) event.y());
        if (style != null && style.getClickEvent() != null) {
            defaultHandleClickEvent(style.getClickEvent(), minecraft, this);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private @Nullable Style findTextLine(int mouseX, int mouseY) {
        int x = leftPos - TITLE_X;
        if (mouseX < x) {
            return null;
        }

        ActiveTextCollector.ClickableStyleFinder styleFinder = new ActiveTextCollector.ClickableStyleFinder(font, mouseX, mouseY);
        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<FormattedCharSequence> block : textBlocks) {
            for (FormattedCharSequence line : block) {
                styleFinder.accept(x, y, line);
                y += font.lineHeight;
            }
            y += font.lineHeight;
        }
        return styleFinder.result();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
