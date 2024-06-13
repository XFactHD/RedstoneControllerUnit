package io.github.xfacthd.rsctrlunit.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class ClientUtils
{
    private static final WidgetSprites BTN_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );

    public static void drawStringInBatch(GuiGraphics graphics, Font font, String text, int x, int y, int color)
    {
        MultiBufferSource buffer = graphics.bufferSource();
        Matrix4f pose = graphics.pose().last().pose();
        font.drawInBatch(text, x, y, color, false, pose, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT, font.isBidirectional());
    }

    public static void drawButton(
            GuiGraphics graphics,
            Font font,
            int x,
            int y,
            int width,
            int height,
            String text,
            boolean enabled,
            boolean shadow,
            boolean centered,
            int xTextOff,
            int mouseX,
            int mouseY
    )
    {
        drawButton(graphics, font, x, y, width, height, Component.literal(text), enabled, shadow, centered, xTextOff, mouseX, mouseY);
    }

    public static void drawButton(
            GuiGraphics graphics,
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component text,
            boolean enabled,
            boolean shadow,
            boolean centered,
            int xTextOff,
            int mouseX,
            int mouseY
    )
    {
        boolean hovered = enabled && mouseY >= y && mouseY < y + height && mouseX >= x && mouseX < x + width;
        ResourceLocation sprite = BTN_SPRITES.get(enabled, hovered);
        graphics.blitSprite(sprite, x, y, width, height);

        int color = enabled ? 0xFFFFFFFF : 0xFFA0A0A0;
        if (centered)
        {
            drawCenteredString(graphics, font, text, x + (width % 2) + (width / 2), y + 4, color, shadow);
        }
        else
        {
            graphics.drawString(font, text, x + 4 + xTextOff, y + 4, color, shadow);
        }
    }

    public static void drawCenteredString(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadow)
    {
        FormattedCharSequence charSeq = text.getVisualOrderText();
        graphics.drawString(font, charSeq, x - font.width(charSeq) / 2, y, color, shadow);
    }



    private ClientUtils() { }
}
