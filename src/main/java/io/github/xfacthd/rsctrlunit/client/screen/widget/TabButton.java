package io.github.xfacthd.rsctrlunit.client.screen.widget;

import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public final class TabButton extends Button.Plain {
    private Position pos = Position.CENTER;
    private boolean selected = false;

    TabButton(int x, int y, int w, int h, Component title, OnPress onPress) {
        super(x, y, w, h, title, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, pos.getSprite(selected), getX(), getY(), getWidth(), getHeight(), ARGB.white(alpha));
        extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }

    @Override
    public void extractScrollingStringOverContents(ActiveTextCollector textCollector, Component text, int border) {
        int minX = getX() + border;
        int maxX = getX() + getWidth() - border;
        textCollector.acceptScrollingWithDefaultCenter(text, minX, maxX, getY(), getY() + getHeight() - 1);
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public enum Position {
        LEFT(Utils.rl("tab/tab_left"), Utils.rl("tab/tab_left_selected")),
        CENTER(Utils.rl("tab/tab_middle"), Utils.rl("tab/tab_middle_selected")),
        RIGHT(Utils.rl("tab/tab_right"), Utils.rl("tab/tab_right_selected"));

        private final Identifier sprite;
        private final Identifier spriteSelected;

        Position(Identifier sprite, Identifier spriteSelected) {
            this.sprite = sprite;
            this.spriteSelected = spriteSelected;
        }

        public Identifier getSprite(boolean selected) {
            return selected ? spriteSelected : sprite;
        }
    }
}
