package io.github.xfacthd.rsctrlunit.client.screen.widget;

import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class TabButton extends Button
{
    private Position pos = Position.CENTER;
    private boolean selected = false;

    TabButton(int x, int y, int w, int h, Component title, OnPress onPress)
    {
        super(x, y, w, h, title, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.blitSprite(RenderType::guiTextured, pos.getSprite(selected), getX(), getY(), getWidth(), getHeight(), ARGB.white(alpha));
        renderString(graphics, Minecraft.getInstance().font, getFGColor() | Mth.ceil(alpha * 255F) << 24);
    }

    @Override
    protected void renderScrollingString(GuiGraphics graphics, Font font, int border, int color)
    {
        int minX = getX() + border;
        int maxX = getX() + getWidth() - border;
        renderScrollingString(graphics, font, getMessage(), minX, getY(), maxX, getY() + getHeight() - 1, color);
    }

    public void setPos(Position pos)
    {
        this.pos = pos;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public enum Position
    {
        LEFT(Utils.rl("tab/tab_left"), Utils.rl("tab/tab_left_selected")),
        CENTER(Utils.rl("tab/tab_middle"), Utils.rl("tab/tab_middle_selected")),
        RIGHT(Utils.rl("tab/tab_right"), Utils.rl("tab/tab_right_selected"));

        private final ResourceLocation sprite;
        private final ResourceLocation spriteSelected;

        Position(ResourceLocation sprite, ResourceLocation spriteSelected)
        {
            this.sprite = sprite;
            this.spriteSelected = spriteSelected;
        }

        public ResourceLocation getSprite(boolean selected)
        {
            return selected ? spriteSelected : sprite;
        }
    }
}
