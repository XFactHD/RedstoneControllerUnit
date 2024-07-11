package io.github.xfacthd.rsctrlunit.client.screen;

import io.github.xfacthd.rsctrlunit.client.screen.popup.EditPortMappingScreen;
import io.github.xfacthd.rsctrlunit.client.screen.widget.*;
import io.github.xfacthd.rsctrlunit.client.util.ClientUtils;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.disassembler.Disassembler;
import io.github.xfacthd.rsctrlunit.common.emulator.disassembler.Disassembly;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.menu.slot.Hideable;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.*;
import io.github.xfacthd.rsctrlunit.common.redstone.port.*;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class ControllerScreen extends CardInventoryContainerScreen<ControllerMenu>
{
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
    private static final ResourceLocation INVENTORY = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final ResourceLocation REGISTERS = Utils.rl("textures/gui/controller_registers.png");
    private static final ResourceLocation CODE_BACKGROUND = Utils.rl("code_background");
    private static final ResourceLocation CODE_SCROLLER_VERT = Utils.rl("code_scroller_vert");
    private static final ResourceLocation CODE_SCROLLER_HOR = Utils.rl("code_scroller_hor");
    private static final ResourceLocation SLOT_BACKGROUND = ResourceLocation.withDefaultNamespace("container/slot");
    private static final ResourceLocation INDICATOR_RUNNING = Utils.rl("indicator_green");
    private static final ResourceLocation INDICATOR_STOPPED = Utils.rl("indicator_red");
    private static final int IMAGE_WIDTH = 360;
    private static final int IMAGE_HEIGHT = 221;
    private static final int TAB_HEIGHT = 22;
    private static final int TAB_EDGE_HEIGHT = 4;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SCROLL_BAR_WIDTH = 6;
    private static final int SCROLL_BAR_HEIGHT = 17;
    private static final int DISASSEMBLY_X = IMAGE_WIDTH / 2 + 7;
    private static final int DISASSEMBLY_Y = TAB_HEIGHT + 13;
    private static final int DISASSEMBLY_WIDTH = IMAGE_WIDTH - 5 - 4 - DISASSEMBLY_X;
    private static final int DISASSEMBLY_HEIGHT = IMAGE_HEIGHT - 5 - 4 - DISASSEMBLY_Y;
    private static final int DISASSEMBLY_WIDTH_VERT_SCROLL = DISASSEMBLY_WIDTH - SCROLL_BAR_WIDTH - 2;
    private static final int DISASSEMBLY_HEIGHT_HOR_SCROLL = DISASSEMBLY_HEIGHT - SCROLL_BAR_WIDTH - 2;
    private static final int REGISTER_WIDTH = 172;
    private static final int REGISTER_HEIGHT = 154;
    private static final int REGISTER_ENTRY_WIDTH = 27;
    private static final int INVENTORY_WIDTH = 162;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INDICATOR_SIZE = 12;
    private static final int BACKGROUND_Y = TAB_HEIGHT - TAB_EDGE_HEIGHT;
    private static final int REGISTER_X = 5;
    private static final int REGISTER_LEFT_X = 20;
    private static final int REGISTER_CENTER_X = 86;
    private static final int REGISTER_RIGHT_X = 150;
    private static final int REGISTER_Y = TAB_HEIGHT + 13;
    private static final int REGISTER_PC_PSW_X = 105;
    private static final int REDSTONE_CFG_X = (IMAGE_WIDTH / 2) - (RedstoneConfig.WIDTH / 2);
    private static final int REDSTONE_CFG_Y = TAB_HEIGHT + ((IMAGE_HEIGHT - TAB_HEIGHT) / 2) - (RedstoneConfig.HEIGHT_PADDED * 3 / 2) - BUTTON_HEIGHT - 5;
    private static final int REDSTONE_HEADER_Y = REDSTONE_CFG_Y - RedstoneConfig.HEIGHT_PADDED;
    private static final int INVENTORY_X = DISASSEMBLY_X / 2 - INVENTORY_WIDTH / 2 + 1;
    private static final int INVENTORY_Y = IMAGE_HEIGHT - 76 - 8;
    private static final int TITLE_Y = TAB_HEIGHT + 1;
    private static final int LABEL_PROGRAM_Y = TAB_HEIGHT + 15;
    private static final int CARD_SLOT_Y = TAB_HEIGHT + 55;
    private static final int BUTTON_X = INVENTORY_X + INVENTORY_WIDTH - BUTTON_WIDTH;
    private static final int LOAD_BUTTON_Y = TAB_HEIGHT + 30;
    private static final int SAVE_BUTTON_Y = TAB_HEIGHT + 54;
    private static final int CLEAR_BUTTON_Y = TAB_HEIGHT + 78;
    private static final int INDICATOR_X = REGISTER_X + 4;
    private static final int INDICATOR_Y = IMAGE_HEIGHT - 11 - INDICATOR_SIZE;
    private static final int CTRL_BUTTON_Y = INDICATOR_Y - 2;
    private static final int CTRL_BUTTON_WIDTH = (REGISTER_WIDTH - (INDICATOR_X - REGISTER_X) - INDICATOR_SIZE - 15) / 3;
    private static final int PORT_MAP_BUTTON_WIDTH = 140;
    private static final int PAUSE_BUTTON_X = INDICATOR_X + INDICATOR_SIZE + 5;
    private static final int STEP_BUTTON_X = PAUSE_BUTTON_X + CTRL_BUTTON_WIDTH + 5;
    private static final int RESET_BUTTON_X = STEP_BUTTON_X + CTRL_BUTTON_WIDTH + 5;
    private static final int PORT_MAP_BUTTON_X = IMAGE_WIDTH / 2 - PORT_MAP_BUTTON_WIDTH / 2;
    private static final int EDIT_PORT_MAP_BUTTON_Y = REDSTONE_CFG_Y + RedstoneConfig.HEIGHT_PADDED * 4 + 10;
    private static final int TOGGLE_PORT_MAP_BUTTON_Y = EDIT_PORT_MAP_BUTTON_Y + BUTTON_HEIGHT + 5;
    private static final int TAB_STATUS = 0;
    private static final int TAB_CODE = 1;
    private static final int TAB_REDSTONE = 2;
    public static final Component TAB_TITLE_STATUS = Component.translatable("tab.rsctrlunit.controller.status");
    public static final Component TAB_TITLE_CODE = Component.translatable("tab.rsctrlunit.controller.code");
    public static final Component TAB_TITLE_REDSTONE = Component.translatable("tab.rsctrlunit.controller.redstone");
    private static final Component[] TAB_TITLES = new Component[] {
            TAB_TITLE_STATUS, TAB_TITLE_CODE, TAB_TITLE_REDSTONE
    };
    public static final Component TABLE_HEADER_PORT = Component.translatable("table.rsctrlunit.port_config.header.port");
    public static final Component TABLE_HEADER_SIDE = Component.translatable("table.rsctrlunit.port_config.header.side");
    public static final Component TABLE_HEADER_TYPE = Component.translatable("table.rsctrlunit.port_config.header.type");
    public static final Component TABLE_HEADER_DIRECTION = Component.translatable("table.rsctrlunit.port_config.header.direction");
    public static final Component TABLE_HEADER_MAPPING = Component.translatable("table.rsctrlunit.port_config.header.mapping");
    public static final Component TITLE_REGISTERS = Component.translatable("title.rsctrlunit.controller.registries");
    public static final Component TITLE_DISASSEMBLY = Component.translatable("title.rsctrlunit.controller.disassembly");
    public static final Component BUTTON_LOAD_ROM = Component.translatable("button.rsctrlunit.controller.load_rom");
    public static final Component BUTTON_SAVE_ROM = Component.translatable("button.rsctrlunit.controller.save_rom");
    public static final Component BUTTON_CLEAR_ROM = Component.translatable("button.rsctrlunit.controller.clear_rom");
    public static final Component BUTTON_PAUSE = Component.translatable("button.rsctrlunit.controller.pause");
    public static final Component BUTTON_RESUME = Component.translatable("button.rsctrlunit.controller.resume");
    public static final Component BUTTON_STEP = Component.translatable("button.rsctrlunit.controller.step");
    public static final Component BUTTON_RESET = Component.translatable("button.rsctrlunit.controller.reset");
    public static final Component BUTTON_EDIT_PORT_MAP = Component.translatable("button.rsctrlunit.controller.edit_port_map");
    public static final Component BUTTON_SHOW_PORT_MAP = Component.translatable("button.rsctrlunit.controller.show_port_map");
    public static final Component BUTTON_HIDE_PORT_MAP = Component.translatable("button.rsctrlunit.controller.hide_port_map");
    public static final String LABEL_PROGRAM_KEY = "label.rsctrlunit.controller.program";
    public static final Component LABEL_PORT_REG_OUT = Component.translatable("label.rsctrlunit.controller.port.out");
    public static final Component LABEL_PORT_REG_IN = Component.translatable("label.rsctrlunit.controller.port.in");
    public static final Component TOOLTIP_RUNNING = Component.translatable("tooltip.rsctrlunit.controller.running");
    public static final Component TOOLTIP_PAUSED = Component.translatable("tooltip.rsctrlunit.controller.paused");

    private final List<Register> registers = new ArrayList<>();
    private final List<RedstoneConfig> redstoneConfigs = new ArrayList<>();
    private final byte[] ramView = new byte[Constants.RAM_SIZE];
    private final byte[] outputs = new byte[4];
    private final byte[] inputs = new byte[4];
    private Button buttonLoad;
    private Button buttonSave;
    private Button buttonClear;
    private Button buttonPauseResume;
    private Button buttonStep;
    private Button buttonReset;
    private Button buttonEditPortMap;
    private Button buttonTogglePortMap;
    private int lineHeight = 0;
    private int programCounter = 0;
    private Disassembly disassembly = Disassembly.EMPTY;
    private int tab = 0;
    private boolean horScrollBar = false;
    private boolean vertScrollBar = false;
    private int codeWidth = 0;
    private int codeHeight = 0;
    private int codeHorOffset = 0;
    private int codeVertOffset = 0;
    private boolean dragScrollingHor = false;
    private boolean dragScrollingVert = false;

    public ControllerScreen(ControllerMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        imageWidth = IMAGE_WIDTH;
        imageHeight = IMAGE_HEIGHT;
        inventoryLabelX = INVENTORY_X;
        inventoryLabelY = INVENTORY_Y - 10;
    }

    @Override
    protected void init()
    {
        super.init();

        new TabGroup(leftPos, topPos, IMAGE_WIDTH, TAB_HEIGHT, this::setTab)
                .addButton(TAB_TITLES[TAB_STATUS])
                .addButton(TAB_TITLES[TAB_CODE])
                .addButton(TAB_TITLES[TAB_REDSTONE])
                .build(this::addRenderableWidget, tab);

        buttonLoad = addRenderableWidget(ActionButton.builder(BUTTON_LOAD_ROM)
                .pos(leftPos + BUTTON_X, topPos + LOAD_BUTTON_Y)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build(this, ServerboundControllerActionPayload.Action.LOAD_ROM)
        );
        buttonSave = addRenderableWidget(ActionButton.builder(BUTTON_SAVE_ROM)
                .pos(leftPos + BUTTON_X, topPos + SAVE_BUTTON_Y)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build(this, ServerboundControllerActionPayload.Action.SAVE_ROM)
        );
        buttonClear = addRenderableWidget(ActionButton.builder(BUTTON_CLEAR_ROM)
                .pos(leftPos + BUTTON_X, topPos + CLEAR_BUTTON_Y)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build(this, ServerboundControllerActionPayload.Action.CLEAR_ROM)
        );
        buttonPauseResume = addRenderableWidget(ActionButton.builder(BUTTON_PAUSE)
                .pos(leftPos + PAUSE_BUTTON_X, topPos + CTRL_BUTTON_Y)
                .size(CTRL_BUTTON_WIDTH, BUTTON_HEIGHT - 4)
                .build(this, ServerboundControllerActionPayload.Action.PAUSE_RESUME)
        );
        buttonStep = addRenderableWidget(ActionButton.builder(BUTTON_STEP)
                .pos(leftPos + STEP_BUTTON_X, topPos + CTRL_BUTTON_Y)
                .size(CTRL_BUTTON_WIDTH, BUTTON_HEIGHT - 4)
                .build(this, ServerboundControllerActionPayload.Action.STEP)
        );
        buttonReset = addRenderableWidget(ActionButton.builder(BUTTON_RESET)
                .pos(leftPos + RESET_BUTTON_X, topPos + CTRL_BUTTON_Y)
                .size(CTRL_BUTTON_WIDTH, BUTTON_HEIGHT - 4)
                .build(this, ServerboundControllerActionPayload.Action.RESET)
        );
        buttonEditPortMap = addRenderableWidget(Button.builder(BUTTON_EDIT_PORT_MAP, btn -> editPortMap())
                .pos(leftPos + PORT_MAP_BUTTON_X, topPos + EDIT_PORT_MAP_BUTTON_Y)
                .size(PORT_MAP_BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        );
        buttonTogglePortMap = addRenderableWidget(ActionButton.builder(BUTTON_SHOW_PORT_MAP)
                .pos(leftPos + PORT_MAP_BUTTON_X, topPos + TOGGLE_PORT_MAP_BUTTON_Y)
                .size(PORT_MAP_BUTTON_WIDTH, BUTTON_HEIGHT)
                .build(this, ServerboundControllerActionPayload.Action.TOGGLE_PORT_MAP)
        );

        buttonStep.active = false;

        lineHeight = font.lineHeight + 1;

        registers.clear();
        int yTop = topPos + REGISTER_Y;
        int xLeft = leftPos + REGISTER_LEFT_X;
        for (int i = 0; i < 8; i++)
        {
            registers.add(new Register(xLeft, yTop + i * Register.HEIGHT, i));
        }
        registers.add(new Register.Port(xLeft, yTop + Register.HEIGHT * 10, 0, outputs, inputs));
        registers.add(new Register.Port(xLeft, yTop + Register.HEIGHT * 11, 1, outputs, inputs));
        registers.add(new Register.Port(xLeft, yTop + Register.HEIGHT * 12, 2, outputs, inputs));
        registers.add(new Register.Port(xLeft, yTop + Register.HEIGHT * 13, 3, outputs, inputs));

        int xCenter = leftPos + REGISTER_CENTER_X;
        registers.add(new Register(xCenter, yTop, "TMOD", 2, Constants.ADDRESS_TMOD));
        registers.add(new Register(xCenter, yTop + Register.HEIGHT, "TCON", 2, Constants.ADDRESS_TCON));
        registers.add(new Register(xCenter, yTop + Register.HEIGHT * 2, "TH0", 2, Constants.ADDRESS_TH0));
        registers.add(new Register(xCenter, yTop + Register.HEIGHT * 3, "TL0", 2, Constants.ADDRESS_TL0));
        registers.add(new Register(xCenter, yTop + Register.HEIGHT * 4, "TH1", 2, Constants.ADDRESS_TH1));
        registers.add(new Register(xCenter, yTop + Register.HEIGHT * 5, "TL1", 2, Constants.ADDRESS_TL1));

        int xRight = leftPos + REGISTER_RIGHT_X;
        registers.add(new Register(xRight, yTop, "B", 2, Constants.ADDRESS_REGISTER_B));
        registers.add(new Register(xRight, yTop + Register.HEIGHT, "ACC", 2, Constants.ADDRESS_ACCUMULATOR));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 2, "PSW", 2, Constants.ADDRESS_STATUS_WORD));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 3, "IP", 2, Constants.ADDRESS_IP));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 4, "IE", 2, Constants.ADDRESS_IE));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 5, "PCON", 2, Constants.ADDRESS_PCON));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 6, "DPH", 2, Constants.ADDRESS_DATA_POINTER_UPPER));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 7, "DPL", 2, Constants.ADDRESS_DATA_POINTER_LOWER));
        registers.add(new Register(xRight, yTop + Register.HEIGHT * 8, "SP", 2, Constants.ADDRESS_STACK_POINTER));

        registers.add(new Register.ProgramCounter(leftPos + REGISTER_PC_PSW_X, yTop + Register.HEIGHT * 10, () -> programCounter));
        registers.add(new Register.StatusWord(leftPos + REGISTER_PC_PSW_X, yTop + Register.HEIGHT * 13));

        registers.forEach(reg -> reg.updateTooltipRect(font));

        redstoneConfigs.clear();
        int rsCfgX = leftPos + REDSTONE_CFG_X;
        int rsCfgY = topPos + REDSTONE_CFG_Y;
        for (int i = 0; i < 4; i++)
        {
            redstoneConfigs.add(new RedstoneConfig(this, i, rsCfgX, rsCfgY + i * RedstoneConfig.HEIGHT_PADDED));
        }

        setTab(tab);
        updateDisassembly();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        graphics.blitWithBorder(BACKGROUND, leftPos, topPos + BACKGROUND_Y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT - BACKGROUND_Y, 248, 166, 4, 4, 4, 4);
        switch (tab)
        {
            case TAB_STATUS -> renderStatusTab(graphics, mouseX, mouseY);
            case TAB_CODE -> renderCodeTab(graphics, mouseX, mouseY);
            case TAB_REDSTONE -> renderRedstoneTab(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (tab == TAB_CODE)
        {
            graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFF404040, false);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        switch (tab)
        {
            case TAB_STATUS ->
            {
                for (Register reg : registers)
                {
                    reg.drawTooltip(graphics, font, ramView, mouseX, mouseY);
                }
                if (mouseX >= leftPos + INDICATOR_X && mouseX < leftPos + INDICATOR_X + INDICATOR_SIZE && mouseY >= topPos + INDICATOR_Y && mouseY < topPos + INDICATOR_Y + INDICATOR_SIZE)
                {
                    Component tooltip = menu.isRunning() ? TOOLTIP_RUNNING : TOOLTIP_PAUSED;
                    graphics.renderTooltip(font, tooltip, mouseX, mouseY);
                }
            }
            case TAB_CODE -> { }
            case TAB_REDSTONE ->
            {
                PortConfig[] configs = menu.getPortConfigs();
                for (RedstoneConfig cfg : redstoneConfigs)
                {
                    cfg.drawTooltip(graphics, font, mouseX, mouseY, configs);
                }
            }
        }
    }

    @Override
    protected void containerTick()
    {
        boolean running = menu.isRunning();
        buttonPauseResume.setMessage(running ? BUTTON_PAUSE : BUTTON_RESUME);
        buttonStep.active = !running;
        boolean showPortMap = menu.isPortMapShown();
        buttonTogglePortMap.setMessage(showPortMap ? BUTTON_HIDE_PORT_MAP : BUTTON_SHOW_PORT_MAP);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn)
    {
        if (tab == TAB_REDSTONE)
        {
            PortConfig[] configs = menu.getPortConfigs();
            for (RedstoneConfig config : redstoneConfigs)
            {
                if (config.mouseClicked(mouseX, mouseY, btn, configs))
                {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
                    return true;
                }
            }
        }
        else if (horScrollBar && isHoveringHorBar(mouseX, mouseY))
        {
            dragScrollingHor = true;
        }
        else if (vertScrollBar && isHoveringVertBar(mouseX, mouseY))
        {
            dragScrollingVert = true;
        }
        return super.mouseClicked(mouseX, mouseY, btn);
    }

    private boolean isHoveringHorBar(double mouseX, double mouseY)
    {
        int x = leftPos + DISASSEMBLY_X + 1;
        int y = topPos + DISASSEMBLY_Y + DISASSEMBLY_HEIGHT_HOR_SCROLL + 1;
        int width = (vertScrollBar ? DISASSEMBLY_WIDTH_VERT_SCROLL : DISASSEMBLY_WIDTH) - 2;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + SCROLL_BAR_WIDTH;
    }

    private boolean isHoveringVertBar(double mouseX, double mouseY)
    {
        int x = leftPos + DISASSEMBLY_X + DISASSEMBLY_WIDTH_VERT_SCROLL + 1;
        int y = topPos + DISASSEMBLY_Y + 1;
        int height = (horScrollBar ? DISASSEMBLY_HEIGHT_HOR_SCROLL : DISASSEMBLY_HEIGHT) - 2;
        return mouseX >= x && mouseX < x + SCROLL_BAR_WIDTH && mouseY >= y && mouseY < y + height;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (button == 0)
        {
            dragScrollingHor = false;
            dragScrollingVert = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        int x = leftPos + DISASSEMBLY_X;
        int y = topPos + DISASSEMBLY_Y;
        int width = vertScrollBar ? DISASSEMBLY_WIDTH_VERT_SCROLL : DISASSEMBLY_WIDTH;
        int height = horScrollBar ? DISASSEMBLY_HEIGHT_HOR_SCROLL : DISASSEMBLY_HEIGHT;
        if (tab != TAB_REDSTONE && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height)
        {
            if (scrollX != 0D)
            {
                codeHorOffset = scroll(codeHorOffset, scrollX, codeWidth, width);
            }
            else if (hasShiftDown())
            {
                codeHorOffset = scroll(codeHorOffset, scrollY, codeWidth, width);
            }
            else
            {
                codeVertOffset = scroll(codeVertOffset, scrollY, codeHeight, height);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private static int scroll(int offset, double scrollOffset, int codeSize, int frameSize)
    {
        return (int) Mth.clamp(offset - scrollOffset * 2, 0, Math.max(codeSize - (frameSize - 4), 0));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (dragScrollingHor)
        {
            int width = vertScrollBar ? DISASSEMBLY_WIDTH_VERT_SCROLL : DISASSEMBLY_WIDTH;
            codeHorOffset = drag(leftPos + DISASSEMBLY_X, width, mouseX, codeWidth);
        }
        else if (dragScrollingVert)
        {
            int height = horScrollBar ? DISASSEMBLY_HEIGHT_HOR_SCROLL : DISASSEMBLY_HEIGHT;
            codeVertOffset = drag(topPos + DISASSEMBLY_Y, height, mouseY, codeHeight);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private static int drag(int origin, int barSize, double mousePos, int codeSize)
    {
        float freeScrollWidth = barSize - SCROLL_BAR_HEIGHT;
        float factor = ((float) mousePos - origin - (SCROLL_BAR_HEIGHT / 2F)) / freeScrollWidth;
        float size = codeSize - (barSize - 4);
        return (int) Mth.clamp(factor * size, 0, Math.max(size, 0));
    }

    private void renderDisassembly(GuiGraphics graphics, boolean renderCursor)
    {
        graphics.drawString(font, TITLE_DISASSEMBLY, leftPos + DISASSEMBLY_X, topPos + TITLE_Y, 0xFF404040, false);

        int x = leftPos + DISASSEMBLY_X;
        int y = topPos + DISASSEMBLY_Y;
        int width = vertScrollBar ? DISASSEMBLY_WIDTH_VERT_SCROLL : DISASSEMBLY_WIDTH;
        int height = horScrollBar ? DISASSEMBLY_HEIGHT_HOR_SCROLL : DISASSEMBLY_HEIGHT;
        if (vertScrollBar)
        {
            graphics.blitSprite(CODE_BACKGROUND, x + width, y, SCROLL_BAR_WIDTH + 2, height);
            int off = (int)((float)codeVertOffset / (codeHeight - height + 4) * (height - 2 - SCROLL_BAR_HEIGHT));
            graphics.blitSprite(CODE_SCROLLER_VERT, x + width + 1, y + 1 + off, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
        }
        if (horScrollBar)
        {
            graphics.blitSprite(CODE_BACKGROUND, x, y + height, width, SCROLL_BAR_WIDTH + 2);
            int off = (int)((float)codeHorOffset / (codeWidth - width + 4) * (width - 2 - SCROLL_BAR_HEIGHT));
            //noinspection SuspiciousNameCombination
            graphics.blitSprite(CODE_SCROLLER_HOR, x + 1 + off, y + height + 1, SCROLL_BAR_HEIGHT, SCROLL_BAR_WIDTH);
        }
        graphics.blitSprite(CODE_BACKGROUND, x, y, width, height);

        graphics.enableScissor(x + 2, y + 2, x + width - 2, y + height - 2);
        x += 2;
        y += 2 - (codeVertOffset % 10);
        int minLine = codeVertOffset / lineHeight;
        int maxLine = Math.min((height - 4) / lineHeight + minLine + 2, disassembly.getLines().size());
        int pcLine = disassembly.getLineIndexForProgramCounter(programCounter);
        for (int i = minLine; i < maxLine; i++)
        {
            if (renderCursor && i == pcLine)
            {
                graphics.drawString(font, ">", x - codeHorOffset, y, 0xFFFFFFFF, false);
            }
            graphics.drawString(font, disassembly.getLines().get(i), x + 7 - codeHorOffset, y, 0xFFFFFFFF, false);
            y += lineHeight;
        }
        graphics.disableScissor();
    }

    private void renderStatusTab(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(font, TITLE_REGISTERS, leftPos + REGISTER_X, topPos + TITLE_Y, 0xFF404040, false);
        graphics.blit(REGISTERS, leftPos + REGISTER_X, topPos + REGISTER_Y, 0, 0, REGISTER_WIDTH, REGISTER_HEIGHT);
        for (Register reg : registers)
        {
            reg.draw(graphics, font, ramView);
        }

        int x = leftPos + REGISTER_LEFT_X + REGISTER_ENTRY_WIDTH / 2 + 1;
        int y = topPos + REGISTER_Y + Register.HEIGHT * 9 + 2;
        ClientUtils.drawCenteredString(graphics, font, LABEL_PORT_REG_OUT, x, y, 0xFF404040, false);
        x += REGISTER_ENTRY_WIDTH;
        ClientUtils.drawCenteredString(graphics, font, LABEL_PORT_REG_IN, x, y, 0xFF404040, false);

        boolean running = menu.isRunning();
        ResourceLocation indicator = running ? INDICATOR_RUNNING : INDICATOR_STOPPED;
        graphics.blitSprite(indicator, leftPos + INDICATOR_X, topPos + INDICATOR_Y, 0, INDICATOR_SIZE, INDICATOR_SIZE);

        renderDisassembly(graphics, true);
    }

    private void renderCodeTab(GuiGraphics graphics, int mouseX, int mouseY)
    {
        Component program = Component.translatable(LABEL_PROGRAM_KEY, menu.getCode().displayName());
        graphics.drawString(font, program, leftPos + INVENTORY_X, topPos + LABEL_PROGRAM_Y, 0xFF404040, false);

        graphics.blitSprite(SLOT_BACKGROUND, leftPos + INVENTORY_X, topPos + CARD_SLOT_Y, SLOT_SIZE, SLOT_SIZE);
        Slot slot = menu.slots.getFirst();
        if (slot.hasItem())
        {
            Code code = slot.getItem().get(RCUContent.COMPONENT_TYPE_CODE);
            boolean hasCode = code != null && !code.equals(Code.EMPTY);
            buttonLoad.active = hasCode;
            buttonSave.active = !hasCode;
        }
        else
        {
            buttonLoad.active = buttonSave.active = false;
        }
        drawGhostCard(graphics, leftPos + INVENTORY_X + 1, topPos + CARD_SLOT_Y + 1);

        graphics.blit(INVENTORY, leftPos + INVENTORY_X, topPos + INVENTORY_Y, 7, 139, INVENTORY_WIDTH, INVENTORY_HEIGHT);

        renderDisassembly(graphics, false);
    }

    private void renderRedstoneTab(GuiGraphics graphics, int mouseX, int mouseY)
    {
        int x = leftPos + REDSTONE_CFG_X;
        int y = topPos + REDSTONE_HEADER_Y;
        int xBg = x - RedstoneConfig.FRAME_PADDING;
        int yBg = y - RedstoneConfig.FRAME_PADDING;
        graphics.blitSprite(RedstoneConfig.BACKGROUND, xBg, yBg, 0, RedstoneConfig.WIDTH_PADDED, RedstoneConfig.HEIGHT_PADDED);

        graphics.drawString(font, TABLE_HEADER_PORT, x + 4, y + 4, 0xFF000000, false);
        graphics.drawString(font, TABLE_HEADER_SIDE, x + RedstoneConfig.X_SIDE + 4, y + 4, 0xFF000000, false);
        graphics.drawString(font, TABLE_HEADER_TYPE, x + RedstoneConfig.X_TYPE + 4, y + 4, 0xFF000000, false);
        graphics.drawString(font, TABLE_HEADER_DIRECTION, x + RedstoneConfig.X_DIR + 4, y + 4, 0xFF000000, false);
        graphics.drawString(font, TABLE_HEADER_MAPPING, x + RedstoneConfig.X_PIN_BTN_LEFT + 4, y + 4, 0xFF000000, false);

        Direction facing = menu.getFacing();
        PortConfig[] configs = menu.getPortConfigs();
        for (RedstoneConfig config : redstoneConfigs)
        {
            config.draw(graphics, font, facing, configs, mouseX, mouseY);
        }
    }

    private void setTab(int tab)
    {
        this.tab = tab;
        menu.slots.stream()
                .filter(Hideable.class::isInstance)
                .map(Hideable.class::cast)
                .forEach(slot -> slot.setActive(tab == TAB_CODE));
        buttonLoad.visible = tab == TAB_CODE;
        buttonSave.visible = tab == TAB_CODE;
        buttonClear.visible = tab == TAB_CODE;
        buttonPauseResume.visible = tab == TAB_STATUS;
        buttonStep.visible = tab == TAB_STATUS;
        buttonReset.visible = tab == TAB_STATUS;
        buttonEditPortMap.visible = tab == TAB_REDSTONE;
        buttonTogglePortMap.visible = tab == TAB_REDSTONE;
    }

    private void editPortMap()
    {
        Minecraft.getInstance().pushGuiLayer(new EditPortMappingScreen(this));
    }

    public void updateStatus(byte[] ram, byte[] output, byte[] input, int programCounter)
    {
        Utils.copyByteArray(ram, this.ramView);
        Utils.copyByteArray(output, this.outputs);
        Utils.copyByteArray(input, this.inputs);
        this.programCounter = programCounter;
    }

    public void updateDisassembly()
    {
        disassembly = Disassembler.disassemble(menu.getCode());
        codeHorOffset = 0;
        codeVertOffset = 0;

        int width = DISASSEMBLY_WIDTH;
        int height = DISASSEMBLY_HEIGHT;
        codeHeight = disassembly.getLines().size() * lineHeight;
        vertScrollBar = codeHeight > height - 4;
        if (vertScrollBar)
        {
            width = DISASSEMBLY_WIDTH_VERT_SCROLL;
        }
        codeWidth = 0;
        for (String line : disassembly.getLines())
        {
            codeWidth = Math.max(codeWidth, font.width(line) + 7);
        }
        horScrollBar = codeWidth > width - 4;
        if (horScrollBar)
        {
            height = DISASSEMBLY_HEIGHT_HOR_SCROLL;
        }
        if (!vertScrollBar && disassembly.getLines().size() * lineHeight > height - 4)
        {
            vertScrollBar = true;
        }
    }

    public void setPortConfig(int port, PortConfig config)
    {
        PacketDistributor.sendToServer(new ServerboundSetPortConfigPayload(menu.containerId, port, config));
    }
}
