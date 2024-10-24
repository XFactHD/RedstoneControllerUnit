package io.github.xfacthd.rsctrlunit.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public final class ClientUtils
{
    private static final WidgetSprites BTN_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );

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
        drawButton(graphics, font, x, y, width, height, Component.literal(text), enabled, shadow, centered, false, xTextOff, mouseX, mouseY);
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
            boolean hoverOverride,
            int xTextOff,
            int mouseX,
            int mouseY
    )
    {
        boolean hovered = enabled && (hoverOverride || (mouseY >= y && mouseY < y + height && mouseX >= x && mouseX < x + width));
        ResourceLocation sprite = BTN_SPRITES.get(enabled, hovered);
        graphics.blitSprite(RenderType::guiTextured, sprite, x, y, width, height);

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

    public static int getWrappedHeight(Font font, FormattedText text, int width)
    {
        return font.split(text, width).size() * font.lineHeight;
    }

    public static int getMaxWidth(Font font, Component... lines)
    {
        int maxWidth = 0;
        for (Component line : lines)
        {
            maxWidth = Math.max(maxWidth, font.width(line));
        }
        return maxWidth;
    }



    private ClientUtils() { }
}
