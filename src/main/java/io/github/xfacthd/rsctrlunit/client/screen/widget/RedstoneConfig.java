package io.github.xfacthd.rsctrlunit.client.screen.widget;

import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.util.ClientUtils;
import io.github.xfacthd.rsctrlunit.common.redstone.port.*;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class RedstoneConfig
{
    public static final int HEIGHT = 16;
    public static final int FRAME_PADDING = 2;
    public static final int HEIGHT_PADDED = HEIGHT + FRAME_PADDING * 2;
    private static final int PADDING = 5;
    public static final int WIDTH_PORT = 40;
    public static final int WIDTH_SIDE = 40;
    public static final int WIDTH_TYPE = 40;
    private static final int WIDTH_BUNDLE_BIT = 11;
    public static final int WIDTH_DIR = WIDTH_BUNDLE_BIT * 8;
    public static final int WIDTH_PIN_BTN = 11;
    private static final int WIDTH_PIN_FIELD = 23;
    public static final int WIDTH_PIN = WIDTH_PIN_BTN * 2 + WIDTH_PIN_FIELD + 2;
    public static final int X_SIDE = WIDTH_PORT + PADDING;
    public static final int X_TYPE = X_SIDE + WIDTH_SIDE + PADDING;
    public static final int X_DIR = X_TYPE + WIDTH_TYPE + PADDING;
    public static final int X_PIN_BTN_LEFT = X_DIR + WIDTH_DIR + PADDING;
    private static final int X_PIN_FIELD = X_PIN_BTN_LEFT + 1 + WIDTH_PIN_BTN;
    private static final int X_PIN_BTN_RIGHT = X_PIN_FIELD + 1 + WIDTH_PIN_FIELD;
    public static final int WIDTH = X_PIN_BTN_LEFT + WIDTH_PIN;
    public static final int WIDTH_PADDED = WIDTH + FRAME_PADDING * 2;
    public static final Component TEXT_INPUT = Component.translatable("desc.rsctrlunit.redstone.direction.input").withColor(0xFF00CC00);
    public static final Component TEXT_OUTPUT = Component.translatable("desc.rsctrlunit.redstone.direction.output").withColor(0xFFEE0000);
    private static final Component TEXT_INPUT_BIT = Component.literal("I").withColor(0xFF00CC00);
    private static final Component TEXT_OUTPUT_BIT = Component.literal("O").withColor(0xFFEE0000);
    public static final Component TOOLTIP_TYPE_NONE = Component.translatable("tooltip.rsctrlunit.redstone_type.none");
    public static final Component TOOLTIP_TYPE_SINGLE = Component.translatable("tooltip.rsctrlunit.redstone_type.single");
    public static final Component TOOLTIP_TYPE_BUNDLED = Component.translatable("tooltip.rsctrlunit.redstone_type.bundled");
    public static final String TOOLTIP_PORT_BIT_KEY = "tooltip.rsctrlunit.port_config.port_bit";
    private static final Component[] TOOLTIP_PORT_BIT = Util.make(new Component[8], arr ->
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = Component.translatable(TOOLTIP_PORT_BIT_KEY, i);
        }
    });
    public static final String TOOLTIP_WIRE_COLOR_KEY = "tooltip.rsctrlunit.port_config.bundled.wire_color";
    private static final Component[] TOOLTIP_WIRE_COLOR = Util.make(new Component[DyeColor.values().length], arr ->
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = Component.translatable(TOOLTIP_WIRE_COLOR_KEY, Utils.COLOR_NAMES[i]);
        }
    });
    public static final Component TOOLTIP_BUNDLED_MAPPING_LOWER = Component.translatable("tooltip.rsctrlunit.port_config.bundled.mapping_lower");
    public static final Component TOOLTIP_BUNDLED_MAPPING_UPPER = Component.translatable("tooltip.rsctrlunit.port_config.bundled.mapping_upper");
    public static final String TEXT_PORT_IDX_KEY = "desc.rsctrlunit.port_config.port_index";
    public static final Component[] TEXT_PORT_IDX = Util.make(new Component[4], arr ->
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = Component.translatable(TEXT_PORT_IDX_KEY, i);
        }
    });
    public static final ResourceLocation BACKGROUND = Utils.rl("port_config_background");
    public static final ResourceLocation TEXT_FIELD = ResourceLocation.withDefaultNamespace("widget/text_field");

    private final ControllerScreen screen;
    private final int port;
    private final int x;
    private final int y;

    public RedstoneConfig(ControllerScreen screen, int port, int x, int y)
    {
        this.screen = screen;
        this.port = port;
        this.x = x;
        this.y = y;
    }

    public void draw(GuiGraphics graphics, Font font, Direction facing, PortConfig[] configs, int mouseX, int mouseY)
    {
        PortConfig cfg = configs[port];

        graphics.blitSprite(BACKGROUND, x - FRAME_PADDING, y - FRAME_PADDING, 0, WIDTH_PADDED, HEIGHT_PADDED);

        graphics.drawString(font, TEXT_PORT_IDX[port], x + 4, y + 4, 0xFF000000, false);

        int mapped = screen.getMenu().getPortMapping()[port];
        Direction side = PortMapping.getPortSide(facing, mapped);
        graphics.drawString(font, Utils.DIRECTION_NAMES[side.ordinal()], x + X_SIDE + 4, y + 4, 0xFF000000, false);

        ClientUtils.drawButton(graphics, font, x + X_TYPE, y, WIDTH_TYPE, HEIGHT, cfg.getType().getTranslatedName(), true, true, true, false, 0, mouseX, mouseY);

        switch (cfg)
        {
            case NonePortConfig ignored ->
            {
                ClientUtils.drawButton(graphics, font, x + X_DIR, y, WIDTH_DIR, HEIGHT, "-", false, true, true, 0, mouseX, mouseY);
                ClientUtils.drawButton(graphics, font, x + X_PIN_BTN_LEFT, y, WIDTH_PIN, HEIGHT, "-", false, true, true, 0, mouseX, mouseY);
            }
            case SinglePortConfig single ->
            {
                ClientUtils.drawButton(graphics, font, x + X_DIR, y, WIDTH_DIR, HEIGHT, single.input() ? TEXT_INPUT : TEXT_OUTPUT, true, false, true, false, 0, mouseX, mouseY);
                ClientUtils.drawButton(graphics, font, x + X_PIN_BTN_LEFT, y, WIDTH_PIN_BTN, HEIGHT, "<", single.pin() > 0, true, false, -1, mouseX, mouseY);
                ClientUtils.drawButton(graphics, font, x + X_PIN_BTN_RIGHT, y, WIDTH_PIN_BTN, HEIGHT, ">", single.pin() < 7, true, false, 0, mouseX, mouseY);

                graphics.blitSprite(TEXT_FIELD, x + X_PIN_FIELD, y, WIDTH_PIN_FIELD, HEIGHT);
                graphics.drawCenteredString(font, Integer.toString(single.pin()), x + X_PIN_FIELD + WIDTH_PIN_FIELD / 2, y + 4, 0xFFFFFFFF);
            }
            case BundledPortConfig bundle ->
            {
                boolean hoverOverride = isHoveringBundledButtons(x, y, mouseX, mouseY);
                for (int i = 0; i < 8; i++)
                {
                    int px = x + X_DIR + i * WIDTH_BUNDLE_BIT;
                    boolean in = (bundle.inputMask() & (1 << i)) != 0;
                    ClientUtils.drawButton(graphics, font, px, y, WIDTH_BUNDLE_BIT, HEIGHT, in ? TEXT_INPUT_BIT : TEXT_OUTPUT_BIT, true, false, true, hoverOverride, 0, mouseX, mouseY);
                }
                ClientUtils.drawButton(graphics, font, x + X_PIN_BTN_LEFT, y, WIDTH_PIN, HEIGHT, bundle.upper() ? "9-16" : "1-8", true, true, true, 0, mouseX, mouseY);
            }
        }
    }

    public void drawTooltip(GuiGraphics graphics, Font font, int mouseX, int mouseY, PortConfig[] configs)
    {
        if (mouseX >= x + X_TYPE && mouseX < x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT)
        {
            switch (configs[port])
            {
                case NonePortConfig ignored ->
                {
                    if (mouseX < x + X_TYPE + WIDTH_TYPE)
                    {
                        graphics.renderTooltip(font, TOOLTIP_TYPE_NONE, mouseX, mouseY);
                    }
                }
                case SinglePortConfig single ->
                {
                    if (mouseX < x + X_TYPE + WIDTH_TYPE)
                    {
                        graphics.renderTooltip(font, TOOLTIP_TYPE_SINGLE, mouseX, mouseY);
                    }
                    else if (mouseX >= x + X_PIN_BTN_LEFT && mouseX < x + X_PIN_BTN_LEFT + WIDTH_PIN)
                    {
                        graphics.renderTooltip(font, TOOLTIP_PORT_BIT[single.pin()], mouseX, mouseY);
                    }
                }
                case BundledPortConfig bundle ->
                {
                    if (mouseX < x + X_TYPE + WIDTH_TYPE)
                    {
                        graphics.renderTooltip(font, TOOLTIP_TYPE_BUNDLED, mouseX, mouseY);
                        return;
                    }
                    for (int i = 0; i < 8; i++)
                    {
                        int px = x + X_DIR + i * WIDTH_BUNDLE_BIT;
                        if (mouseX >= px && mouseX < px + WIDTH_BUNDLE_BIT)
                        {
                            int colorIdx = i + (bundle.upper() ? 8 : 0);
                            var lines = List.of(TOOLTIP_PORT_BIT[i].getVisualOrderText(), TOOLTIP_WIRE_COLOR[colorIdx].getVisualOrderText());
                            graphics.renderTooltip(font, lines, mouseX, mouseY);
                            return;
                        }
                    }
                    if (mouseX >= x + X_PIN_BTN_LEFT && mouseX < x + X_PIN_BTN_LEFT + WIDTH_PIN)
                    {
                        Component line = bundle.upper() ? TOOLTIP_BUNDLED_MAPPING_UPPER : TOOLTIP_BUNDLED_MAPPING_LOWER;
                        graphics.renderTooltip(font, line, mouseX, mouseY);
                    }
                }
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int btn, PortConfig[] configs)
    {
        if (btn == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= x + X_TYPE && mouseX < x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT)
        {
            PortConfig config = configs[port];
            if (mouseX < x + X_TYPE + WIDTH_TYPE)
            {
                screen.setPortConfig(port, config.cycleType());
                return true;
            }

            switch (config)
            {
                case NonePortConfig ignored -> { }
                case SinglePortConfig single ->
                {
                    if (mouseX >= x + X_DIR && mouseX < x + X_DIR + WIDTH_DIR)
                    {
                        screen.setPortConfig(port, new SinglePortConfig(single.pin(), !single.input()));
                        return true;
                    }
                    if (single.pin() > 0 && mouseX >= x + X_PIN_BTN_LEFT && mouseX < x + X_PIN_BTN_LEFT + WIDTH_PIN_BTN)
                    {
                        screen.setPortConfig(port, new SinglePortConfig(single.pin() - 1, single.input()));
                        return true;
                    }
                    if (single.pin() < 7 && mouseX >= x + X_PIN_BTN_RIGHT && mouseX < x + X_PIN_BTN_RIGHT + WIDTH_PIN_BTN)
                    {
                        screen.setPortConfig(port, new SinglePortConfig(single.pin() + 1, single.input()));
                        return true;
                    }
                }
                case BundledPortConfig bundle ->
                {
                    if (isHoveringBundledButtons(x, y, (int) mouseX, (int) mouseY))
                    {
                        byte mask = (byte) (~bundle.inputMask() & 0xFF);
                        screen.setPortConfig(port, new BundledPortConfig(bundle.upper(), mask));
                        return true;
                    }
                    for (int i = 0; i < 8; i++)
                    {
                        int px = x + X_DIR + i * WIDTH_BUNDLE_BIT;
                        if (mouseX >= px && mouseX < px + WIDTH_BUNDLE_BIT)
                        {
                            byte mask = bundle.inputMask();
                            mask = (byte) (mask ^ (1 << i));
                            screen.setPortConfig(port, new BundledPortConfig(bundle.upper(), mask));
                            return true;
                        }
                    }
                    if (mouseX >= x + X_PIN_BTN_LEFT && mouseX < x + X_PIN_BTN_LEFT + WIDTH_PIN)
                    {
                        screen.setPortConfig(port, new BundledPortConfig(!bundle.upper(), bundle.inputMask()));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isHoveringBundledButtons(int x, int y, int mouseX, int mouseY)
    {
        if (Screen.hasShiftDown())
        {
            int minX = x + X_DIR;
            return mouseX >= minX && mouseX < minX + WIDTH_DIR && mouseY >= y && mouseY < y + HEIGHT;
        }
        return false;
    }
}
