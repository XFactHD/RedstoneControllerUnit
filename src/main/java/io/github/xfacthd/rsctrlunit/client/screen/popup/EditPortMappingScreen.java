package io.github.xfacthd.rsctrlunit.client.screen.popup;

import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.widget.RedstoneConfig;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundSetPortMappingPayload;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortMapping;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public final class EditPortMappingScreen extends Screen
{
    public static final Component TITLE = Component.translatable("screen.rsctrlunit.edit_port_mapping");
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
    private static final int EDGE_PADDING_X = 8;
    private static final int EDGE_PADDING_Y = 6;
    private static final int PADDING = 5;
    private static final int SMALL_PADDING = 2;
    private static final int ENTRY_TEXT_PADDING = 6;
    private static final int LINE_HEIGHT = 10;
    private static final int PORT_WIDTH = 40;
    private static final int DIR_FIELD_WIDTH = 50;
    private static final int ENTRY_WIDTH = ENTRY_TEXT_PADDING + PORT_WIDTH + (RedstoneConfig.WIDTH_PIN_BTN * 2) + DIR_FIELD_WIDTH + (SMALL_PADDING * 4);
    private static final int WIDTH = (EDGE_PADDING_X * 2) + ENTRY_WIDTH;
    private static final int HEIGHT = RedstoneConfig.HEIGHT_PADDED * 4 + (EDGE_PADDING_Y * 2) + Button.DEFAULT_HEIGHT + PADDING + LINE_HEIGHT + PADDING;
    private static final int DONE_BUTTON_WIDTH = WIDTH - (EDGE_PADDING_X * 2);
    private static final int TITLE_X = EDGE_PADDING_X;
    private static final int TITLE_Y = EDGE_PADDING_Y;
    private static final int ENTRY_X = EDGE_PADDING_X;
    private static final int PORT_X = ENTRY_X + ENTRY_TEXT_PADDING;
    private static final int CYCLE_BUTTON_LEFT_X = PORT_X + PORT_WIDTH + SMALL_PADDING;
    private static final int DIR_FIELD_X = CYCLE_BUTTON_LEFT_X + RedstoneConfig.WIDTH_PIN_BTN + SMALL_PADDING;
    private static final int DIR_TEXT_X = DIR_FIELD_X + SMALL_PADDING * 2;
    private static final int CYCLE_BUTTON_RIGHT_X = DIR_FIELD_X + DIR_FIELD_WIDTH + SMALL_PADDING;
    private static final int ENTRY_TOP_Y = TITLE_Y + LINE_HEIGHT + PADDING;
    private static final int CYCLE_BUTTON_TOP_Y = ENTRY_TOP_Y + SMALL_PADDING;

    private final ControllerScreen screen;
    private final int[] mapping;
    private int leftPos;
    private int topPos;
    private Button buttonDone;

    public EditPortMappingScreen(ControllerScreen screen)
    {
        super(TITLE);
        this.screen = screen;
        this.mapping = screen.getMenu().getPortMapping().clone();
    }

    @Override
    protected void init()
    {
        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - HEIGHT) / 2;

        int btnY = topPos + CYCLE_BUTTON_TOP_Y;
        for (int i = 0; i < 4; i++)
        {
            int port = i;
            addRenderableWidget(Button.builder(Component.literal("<"), btn -> cycle(port, -1))
                    .pos(leftPos + CYCLE_BUTTON_LEFT_X, btnY)
                    .size(RedstoneConfig.WIDTH_PIN_BTN, RedstoneConfig.HEIGHT)
                    .build()
            );
            addRenderableWidget(Button.builder(Component.literal(">"), btn -> cycle(port, 1))
                    .pos(leftPos + CYCLE_BUTTON_RIGHT_X, btnY)
                    .size(RedstoneConfig.WIDTH_PIN_BTN, RedstoneConfig.HEIGHT)
                    .build()
            );
            btnY += RedstoneConfig.HEIGHT_PADDED;
        }

        buttonDone = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> save())
                .pos(leftPos + EDGE_PADDING_X, topPos + HEIGHT - Button.DEFAULT_HEIGHT - EDGE_PADDING_Y)
                .size(DONE_BUTTON_WIDTH, Button.DEFAULT_HEIGHT)
                .build()
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderTransparentBackground(graphics);

        graphics.blitWithBorder(BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT, 248, 166, 4, 4, 4, 4);
        graphics.drawString(font, title, leftPos + TITLE_X, topPos + TITLE_Y, 0x404040, false);

        int x = leftPos + ENTRY_X;
        for (int i = 0; i < 4; i++)
        {
            int y = topPos + ENTRY_TOP_Y + RedstoneConfig.HEIGHT_PADDED * i;
            graphics.blitSprite(RedstoneConfig.BACKGROUND, x, y, 0, ENTRY_WIDTH, RedstoneConfig.HEIGHT_PADDED);
            graphics.blitSprite(RedstoneConfig.TEXT_FIELD, leftPos + DIR_FIELD_X, y + SMALL_PADDING, 0, DIR_FIELD_WIDTH, RedstoneConfig.HEIGHT);

            graphics.drawString(font, RedstoneConfig.TEXT_PORT_IDX[i], x + ENTRY_TEXT_PADDING, y + ENTRY_TEXT_PADDING, 0xFF000000, false);
            Direction dir = PortMapping.getPortSide(screen.getMenu().getFacing(), mapping[i]);
            graphics.drawString(font, Utils.DIRECTION_NAMES[dir.ordinal()], leftPos + DIR_TEXT_X, y + ENTRY_TEXT_PADDING, 0xFFFFFFFF, false);
        }
    }

    @Override
    public void tick()
    {
        buttonDone.active = RedstoneInterface.validatePortMapping(mapping);
    }

    private void cycle(int port, int dir)
    {
        mapping[port] = Mth.positiveModulo(mapping[port] + dir, 4);
    }

    private void save()
    {
        PacketDistributor.sendToServer(new ServerboundSetPortMappingPayload(screen.getMenu().containerId, mapping));
        onClose();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
