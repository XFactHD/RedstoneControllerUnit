package io.github.xfacthd.rsctrlunit.client.screen;

import io.github.xfacthd.rsctrlunit.client.screen.popup.MessageScreen;
import io.github.xfacthd.rsctrlunit.client.util.*;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.Assembler;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.ErrorPrinter;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Labels;
import io.github.xfacthd.rsctrlunit.common.menu.ProgrammerMenu;
import io.github.xfacthd.rsctrlunit.common.menu.slot.Lockable;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundRequestCodePayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundWriteToTargetPayload;
import io.github.xfacthd.rsctrlunit.common.util.ThrowingSupplier;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class ProgrammerScreen extends CardInventoryContainerScreen<ProgrammerMenu>
{
    private static final ResourceLocation BACKGROUND = Utils.rl("background");
    private static final ResourceLocation INVENTORY = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final ResourceLocation LOCK_ICON = ResourceLocation.withDefaultNamespace("container/cartography_table/locked");
    private static final ResourceLocation SLOT_BACKGROUND = ResourceLocation.withDefaultNamespace("container/slot");
    private static final int IMAGE_WIDTH = 360;
    private static final int IMAGE_HEIGHT = 212;
    private static final int INVENTORY_WIDTH = 162;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_Y_OFF = 24;
    private static final int PADDING = 5;
    private static final int EDGE_PADDING = 8;
    private static final int LINE_HEIGHT = 10;
    private static final int BACKGROUND_Y = 0;
    private static final int INVENTORY_X = EDGE_PADDING;
    private static final int INVENTORY_Y = IMAGE_HEIGHT - INVENTORY_HEIGHT - EDGE_PADDING;
    private static final int CARD_SLOT_Y = INVENTORY_Y - 36;
    private static final int LABEL_X = EDGE_PADDING;
    private static final int BUTTON_COUNT = 7;
    private static final int BUTTON_TOP_Y = IMAGE_HEIGHT - EDGE_PADDING - BUTTON_HEIGHT - BUTTON_Y_OFF * (BUTTON_COUNT - 1);
    private static final int LINE_FILE_PATH = 22;
    private static final int LINE_FILE_TYPE = 32;
    private static final int LINE_CODE_INFO = 42;
    private static final int LINE_MESSAGE = 52;
    private static final int INFO_MSG_TIMEOUT = 10000;
    public static final Component BUTTON_OPEN_SOURCE = Component.translatable("button.rsctrlunit.programmer.open_source");
    public static final Component BUTTON_REVEAL_IN_EXPLORER = Component.translatable("button.rsctrlunit.programmer.reveal_in_explorer");
    public static final Component BUTTON_ASSEMBLE = Component.translatable("button.rsctrlunit.programmer.assemble");
    public static final Component BUTTON_OPEN_BINARY = Component.translatable("button.rsctrlunit.programmer.open_binary");
    public static final Component BUTTON_SAVE_BINARY = Component.translatable("button.rsctrlunit.programmer.save_binary");
    public static final Component BUTTON_READ_BINARY_CARD = Component.translatable("button.rsctrlunit.programmer.read_binary_card");
    public static final Component BUTTON_READ_BINARY_BLOCK = Component.translatable("button.rsctrlunit.programmer.read_binary_block");
    public static final Component BUTTON_WRITE_BINARY_CARD = Component.translatable("button.rsctrlunit.programmer.write_binary_card");
    public static final Component BUTTON_WRITE_BINARY_BLOCK = Component.translatable("button.rsctrlunit.programmer.write_binary_block");
    public static final Component LABEL_FILE_PATH = Component.translatable("label.rsctrlunit.programmer.file_path");
    public static final Component LABEL_FILE_TYPE = Component.translatable("label.rsctrlunit.programmer.file_type");
    public static final Component LABEL_CODE_INFO = Component.translatable("label.rsctrlunit.programmer.code_info");
    public static final Component LABEL_ERROR = Component.translatable("label.rsctrlunit.programmer.error");
    public static final Component LABEL_INFO = Component.translatable("label.rsctrlunit.programmer.info");
    public static final Component DESC_PATH_NONE = Component.translatable("desc.rsctrlunit.programmer.path.empty");
    public static final Component DESC_TYPE_NONE = Component.translatable("desc.rsctrlunit.programmer.type.none");
    public static final Component DESC_TYPE_SOURCE = Component.translatable("desc.rsctrlunit.programmer.type.source");
    public static final Component DESC_TYPE_BINARY = Component.translatable("desc.rsctrlunit.programmer.type.binary");
    public static final String DESC_CODE_INFO = "desc.rsctrlunit.programmer.code_info";
    public static final Component DESC_CODE_INFO_NONE = Component.translatable("desc.rsctrlunit.programmer.code_info.none");
    public static final String MSG_ERROR_READ_SOURCE = "msg.rsctrlunit.programmer.error.read_source";
    public static final String MSG_ERROR_REVEAL_IN_EXPLORER = "msg.rsctrlunit.programmer.error.reveal_in_explorer";
    public static final String MSG_ERROR_ASSEMBLE = "msg.rsctrlunit.programmer.error.assemble";
    public static final String MSG_ERROR_READ_BINARY = "msg.rsctrlunit.programmer.error.read_binary";
    public static final String MSG_ERROR_WRITE_BINARY = "msg.rsctrlunit.programmer.error.write_binary";
    public static final Component MSG_ERROR_ASSEMBLY_FAILED = Component.translatable("msg.rsctrlunit.programmer.error.assembly_failed");
    public static final Component MSG_INFO_ASSEMBLY_SUCCESS = Component.translatable("msg.rsctrlunit.programmer.info.assembly_success");
    public static final Component MSG_INFO_ROM_READ = Component.translatable("msg.rsctrlunit.programmer.info.rom_read");
    public static final Component MSG_INFO_WAITING_FOR_RESPONSE = Component.translatable("msg.rsctrlunit.programmer.info.rom_read.await_response");
    public static final Component MSG_INFO_ROM_WRITTEN = Component.translatable("msg.rsctrlunit.programmer.info.rom_written");
    public static final Component MSG_CONFIRM_NOT_EMPTY_BLOCK = Component.translatable("msg.rsctrlunit.programmer.confirm.overwrite_block");
    public static final Component MSG_CONFIRM_NOT_EMPTY_CARD = Component.translatable("msg.rsctrlunit.programmer.confirm.overwrite_card");
    public static final Component TOOLTIP_NO_SOURCE = Component.translatable("tooltip.rsctrlunit.programmer.no_source");
    public static final Component TOOLTIP_NO_ASSEMBLY = Component.translatable("tooltip.rsctrlunit.programmer.no_assembly");
    public static final Component TOOLTIP_BLOCK_REMOVED = Component.translatable("tooltip.rsctrlunit.programmer.no_block");
    public static final Component TOOLTIP_NO_CARD_ITEM = Component.translatable("tooltip.rsctrlunit.programmer.no_card");
    public static final Component TOOLTIP_NO_CODE_BLOCK = Component.translatable("tooltip.rsctrlunit.programmer.no_code_block");
    public static final Component TOOLTIP_NO_CODE_CARD = Component.translatable("tooltip.rsctrlunit.programmer.no_code_card");
    private static final LastPathStorage LAST_PATH_STORAGE = new LastPathStorage(".last_path", "Projects");
    private static final FileDialog.Filter SOURCE_FILTER = new FileDialog.Filter(new String[] { "*.a", "*.asm" }, "Assembly Source Files");
    private static final FileDialog.Filter BINARY_FILTER = new FileDialog.Filter(new String[] { "*.bin" }, "Binary Files");

    private final boolean forBlock;
    private Button buttonRevealInExplorer;
    private Button buttonAssemble;
    private Button buttonSaveBinary;
    private Button buttonReadBinary;
    private Button buttonWriteBinary;
    private int buttonX;
    private int descX;
    private int descWidth;
    private int maxPathWidth;
    private Path filePath = null;
    private Component pathDisplay = DESC_PATH_NONE;
    private boolean pathCropped = false;
    private Component fileType = DESC_TYPE_NONE;
    private boolean binaryFromFile = false;
    private Code assembledCode = null;
    private Component codeInfo = DESC_CODE_INFO_NONE;
    private Component codeInfoFull = null;
    private List<FormattedCharSequence> lastErrorMsg = null;
    private Throwable lastError = null;
    private List<FormattedCharSequence> lastInfoMsg = null;
    private long lastInfoStamp = 0;

    public ProgrammerScreen(ProgrammerMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.forBlock = menu.isForBlock();
        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
        this.inventoryLabelX = INVENTORY_X;
        this.inventoryLabelY = INVENTORY_Y - 10;
    }

    @Override
    protected void init()
    {
        super.init();

        buttonX = leftPos + IMAGE_WIDTH - EDGE_PADDING - BUTTON_WIDTH;
        Component readTitle = forBlock ? BUTTON_READ_BINARY_BLOCK : BUTTON_READ_BINARY_CARD;
        Component writeTitle = forBlock ? BUTTON_WRITE_BINARY_BLOCK : BUTTON_WRITE_BINARY_CARD;

        addButton(BUTTON_OPEN_SOURCE, 0, this::loadSourceFile);
        buttonRevealInExplorer = addButton(BUTTON_REVEAL_IN_EXPLORER, 1, this::revealInFileExplorer);
        buttonAssemble = addButton(BUTTON_ASSEMBLE, 2, this::assemble);
        addButton(BUTTON_OPEN_BINARY, 3, this::loadBinaryFile);
        buttonSaveBinary = addButton(BUTTON_SAVE_BINARY, 4, this::saveBinaryFile);
        buttonReadBinary = addButton(readTitle, 5, this::readBinaryFromTarget);
        buttonWriteBinary = addButton(writeTitle, 6, () -> writeBinaryToTarget(true));

        buttonRevealInExplorer.active = false;
        buttonAssemble.active = false;
        buttonSaveBinary.active = false;
        buttonReadBinary.active = false;
        buttonWriteBinary.active = false;

        int maxWidth = ClientUtils.getMaxWidth(font, LABEL_FILE_PATH, LABEL_FILE_TYPE, LABEL_CODE_INFO, LABEL_ERROR);
        descX = leftPos + LABEL_X + maxWidth + PADDING;
        descWidth = buttonX - PADDING - descX;
        maxPathWidth = IMAGE_WIDTH - EDGE_PADDING + leftPos - descX;
    }

    private Button addButton(Component title, int line, Runnable action)
    {
        return addRenderableWidget(Button.builder(title, btn -> action.run())
                .pos(buttonX, topPos + BUTTON_TOP_Y + BUTTON_Y_OFF * line)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        );
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
        graphics.blitSprite(RenderType::guiTextured, BACKGROUND, leftPos, topPos + BACKGROUND_Y, IMAGE_WIDTH, IMAGE_HEIGHT - BACKGROUND_Y);
        graphics.blit(RenderType::guiTextured, INVENTORY, leftPos + INVENTORY_X, topPos + INVENTORY_Y, 7, 139, INVENTORY_WIDTH, INVENTORY_HEIGHT, 256, 256);
        if (!forBlock)
        {
            graphics.blitSprite(RenderType::guiTextured, SLOT_BACKGROUND, leftPos + INVENTORY_X, topPos + CARD_SLOT_Y, SLOT_SIZE, SLOT_SIZE);
            drawGhostCard(graphics, leftPos + INVENTORY_X + 1, topPos + CARD_SLOT_Y + 1);
        }

        graphics.drawString(font, LABEL_FILE_PATH, leftPos + LABEL_X, topPos + LINE_FILE_PATH, 0x404040, false);
        graphics.drawString(font, pathDisplay, descX, topPos + LINE_FILE_PATH, 0x404040, false);

        graphics.drawString(font, LABEL_FILE_TYPE, leftPos + LABEL_X, topPos + LINE_FILE_TYPE, 0x404040, false);
        graphics.drawString(font, fileType, descX, topPos + LINE_FILE_TYPE, 0x404040, false);

        graphics.drawString(font, LABEL_CODE_INFO, leftPos + LABEL_X, topPos + LINE_CODE_INFO, 0x404040, false);
        graphics.drawString(font, codeInfo, descX, topPos + LINE_CODE_INFO, 0x404040, false);

        if (lastErrorMsg != null)
        {
            drawMessage(graphics, LABEL_ERROR, lastErrorMsg);
        }
        else if (lastInfoMsg != null)
        {
            drawMessage(graphics, LABEL_INFO, lastInfoMsg);
        }
    }

    private void drawMessage(GuiGraphics graphics, Component label, List<FormattedCharSequence> message)
    {
        graphics.drawString(font, label, leftPos + LABEL_X, topPos + LINE_MESSAGE, 0x404040, false);
        int y = topPos + LINE_MESSAGE;
        for (FormattedCharSequence line : message)
        {
            graphics.drawString(font, line, descX, y, 0x404040, false);
            y += LINE_HEIGHT;
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (pathCropped && mouseX >= descX && mouseX < descX + maxPathWidth && mouseY >= topPos + LINE_FILE_PATH && mouseY < topPos + LINE_FILE_PATH + LINE_HEIGHT)
        {
            renderFixedTooltip(graphics, Component.literal(filePath.toString()), LINE_FILE_PATH);
        }
        else if (codeInfoFull != null && mouseX >= descX && mouseX < buttonX - PADDING && mouseY >= topPos + LINE_CODE_INFO && mouseY < topPos + LINE_CODE_INFO + LINE_HEIGHT)
        {
            renderFixedTooltip(graphics, codeInfoFull, LINE_CODE_INFO);
        }
        else if (isInactiveHovered(buttonRevealInExplorer, mouseX, mouseY) || isInactiveHovered(buttonAssemble, mouseX, mouseY))
        {
            if (binaryFromFile || filePath == null)
            {
                graphics.renderTooltip(font, TOOLTIP_NO_SOURCE, mouseX, mouseY);
            }
        }
        else if (isInactiveHovered(buttonSaveBinary, mouseX, mouseY))
        {
            if (assembledCode == null)
            {
                graphics.renderTooltip(font, TOOLTIP_NO_ASSEMBLY, mouseX, mouseY);
            }
        }
        else if (isInactiveHovered(buttonReadBinary, mouseX, mouseY))
        {
            if (!menu.isTargetValid())
            {
                graphics.renderTooltip(font, forBlock ? TOOLTIP_BLOCK_REMOVED : TOOLTIP_NO_CARD_ITEM, mouseX, mouseY);
            }
            else if (forBlock && menu.isInterpreterEmpty())
            {
                graphics.renderTooltip(font, TOOLTIP_NO_CODE_BLOCK, mouseX, mouseY);
            }
            else if (!forBlock && isMemoryCardEmpty())
            {
                graphics.renderTooltip(font, TOOLTIP_NO_CODE_CARD, mouseX, mouseY);
            }
        }
        else if (isInactiveHovered(buttonWriteBinary, mouseX, mouseY))
        {
            if (assembledCode == null)
            {
                graphics.renderTooltip(font, TOOLTIP_NO_ASSEMBLY, mouseX, mouseY);
            }
            else if (!menu.isTargetValid())
            {
                graphics.renderTooltip(font, forBlock ? TOOLTIP_BLOCK_REMOVED : TOOLTIP_NO_CARD_ITEM, mouseX, mouseY);
            }
        }
        else if (lastError != null && mouseX >= descX && mouseX < descX + descWidth && mouseY >= topPos + LINE_MESSAGE && mouseY < topPos + LINE_MESSAGE + (lastErrorMsg.size() * LINE_HEIGHT))
        {
            graphics.renderTooltip(font, List.of(
                    Component.literal(lastError.getClass().getName()),
                    Component.literal(lastError.getMessage())
            ), Optional.empty(), mouseX, mouseY);
        }
    }

    private static boolean isInactiveHovered(Button button, int mouseX, int mouseY)
    {
        return !button.active &&
               mouseX >= button.getX() &&
               mouseX < button.getX() + button.getWidth() &&
               mouseY >= button.getY() &&
               mouseY < button.getY() + button.getHeight();
    }

    private void renderFixedTooltip(GuiGraphics graphics, Component line, int y)
    {
        int lineWidth = font.width(line);
        int x = descX;
        if (x + lineWidth + TooltipRenderUtil.PADDING_RIGHT > width)
        {
            x = width - lineWidth - 1 - TooltipRenderUtil.PADDING_RIGHT;
        }
        graphics.renderTooltip(font, List.of(line.getVisualOrderText()), FixedTooltipPositioner.INSTANCE, x, topPos + y);
    }

    @Override
    protected void renderSlotContents(GuiGraphics graphics, ItemStack stack, Slot slot, @Nullable String countString)
    {
        super.renderSlotContents(graphics, stack, slot, countString);
        if (slot instanceof Lockable lockable && lockable.isLocked())
        {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);
            graphics.blitSprite(RenderType::guiTextured, LOCK_ICON, slot.x + SLOT_SIZE_INNER - 5, slot.y + SLOT_SIZE_INNER - 7, 5, 7);
            graphics.pose().popPose();
        }
    }

    @Override
    protected void containerTick()
    {
        buttonRevealInExplorer.active = filePath != null && !binaryFromFile;
        buttonAssemble.active = filePath != null && !binaryFromFile;
        buttonSaveBinary.active = assembledCode != null;
        buttonReadBinary.active = forBlock ? !menu.isInterpreterEmpty() : !isMemoryCardEmpty();
        buttonWriteBinary.active = assembledCode != null && menu.isTargetValid();

        if (lastInfoStamp > 0 && System.currentTimeMillis() - lastInfoStamp > INFO_MSG_TIMEOUT)
        {
            lastInfoMsg = null;
            lastInfoStamp = 0;
        }
    }

    private void setFilePath(@Nullable Path path, boolean binary)
    {
        filePath = path;
        binaryFromFile = binary;
        pathCropped = false;

        if (path == null)
        {
            pathDisplay = DESC_PATH_NONE;
            fileType = DESC_TYPE_NONE;
            return;
        }

        String pathString = path.toString();
        if (font.width(pathString) > maxPathWidth)
        {
            String[] pathParts = pathString.split(Pattern.quote(File.separator));
            String newPathString = "";
            for (int i = pathParts.length - 1; i >= 0; i--)
            {
                String newPathTemp = File.separator + pathParts[i] + newPathString;
                if (font.width(newPathTemp) >= maxPathWidth)
                {
                    break;
                }
                newPathString = newPathTemp;
            }
            pathString = "..." + newPathString;
            pathCropped = true;
        }
        pathDisplay = Component.literal(pathString);
        fileType = binary ? DESC_TYPE_BINARY : DESC_TYPE_SOURCE;
    }

    private void setAssembledCode(@Nullable Code code)
    {
        assembledCode = code;
        if (code != null && !code.equals(Code.EMPTY))
        {
            codeInfo = Component.translatable(DESC_CODE_INFO, code.name(), code.rom().length);
            int maxWidth = buttonX - descX - PADDING;
            if (font.width(codeInfo) > maxWidth)
            {
                codeInfoFull = codeInfo;
                String part = font.plainSubstrByWidth(codeInfo.getString(), maxWidth - PADDING);
                codeInfo = Component.literal(part + "...");
            }
        }
        else
        {
            codeInfo = DESC_CODE_INFO_NONE;
            codeInfoFull = null;
        }
    }

    private void setLastError(Component errorMsg, @Nullable Throwable error)
    {
        lastErrorMsg = font.split(errorMsg.copy().withStyle(ChatFormatting.DARK_RED), descWidth);
        lastError = error;
    }

    private void clearLastError()
    {
        lastErrorMsg = null;
        lastError = null;
    }

    private void setLastInfo(Component infoMsg, boolean success)
    {
        if (success)
        {
            infoMsg = infoMsg.copy().withStyle(ChatFormatting.DARK_GREEN);
        }
        lastInfoMsg = font.split(infoMsg, descWidth);
        lastInfoStamp = System.currentTimeMillis();
    }

    private boolean isMemoryCardEmpty()
    {
        ItemStack stack = menu.slots.getFirst().getItem();
        Code code = stack.getOrDefault(RCUContent.COMPONENT_TYPE_CODE, Code.EMPTY);
        return code.equals(Code.EMPTY);
    }

    private void loadSourceFile()
    {
        clearLastError();
        FileDialog.openFileDialog(this, LAST_PATH_STORAGE, "Open source file", SOURCE_FILTER, false, path ->
        {
            setFilePath(path, false);
            setAssembledCode(null);
        });
    }

    private void revealInFileExplorer()
    {
        if (filePath == null || binaryFromFile) return;

        if (!Explorer.revealInFileExplorer(filePath))
        {
            setLastError(Component.translatable(MSG_ERROR_REVEAL_IN_EXPLORER, filePath.toString()), null);
        }
    }

    private void assemble()
    {
        if (filePath == null || binaryFromFile) return;

        String source = guardOperation(
                () -> Files.readString(filePath),
                () -> Component.translatable(MSG_ERROR_READ_SOURCE, filePath.getFileName().toString())
        );
        if (source == null || source.isBlank()) return;

        String name = Utils.getFileNameNoExt(filePath);
        List<Component> lines = new ArrayList<>();
        Code code = guardOperation(
                () -> Assembler.assemble(name, source, new ErrorPrinter.Collecting(lines)),
                () -> Component.translatable(MSG_ERROR_ASSEMBLE, filePath.getFileName().toString())
        );
        if (Code.EMPTY.equals(code))
        {
            code = null;
        }
        setAssembledCode(code);
        if (code != null)
        {
            setLastInfo(MSG_INFO_ASSEMBLY_SUCCESS, true);
        }
        else if (!lines.isEmpty())
        {
            lines.addFirst(MSG_ERROR_ASSEMBLY_FAILED);
            Minecraft.getInstance().pushGuiLayer(MessageScreen.error(lines));
        }
    }

    private void loadBinaryFile()
    {
        FileDialog.openFileDialog(this, LAST_PATH_STORAGE, "Open binary file", BINARY_FILTER, false, this::loadBinaryFile);
    }

    private void loadBinaryFile(Path path)
    {
        if (path == null) return;

        setFilePath(path, true);

        setAssembledCode(guardOperation(() ->
        {
            byte[] bytes = Files.readAllBytes(filePath);
            String fileName = Utils.getFileNameNoExt(filePath);
            Labels labels = Labels.readFromFile(filePath, bytes);
            return new Code(fileName, bytes, labels.labels());
        }, () -> Component.translatable(MSG_ERROR_READ_BINARY, filePath.getFileName().toString())));
    }

    private void saveBinaryFile()
    {
        if (assembledCode == null) return;
        FileDialog.openFileDialog(this, LAST_PATH_STORAGE, "Save binary file", BINARY_FILTER, true, path -> guardOperation(
                () ->
                {
                    Files.write(path, assembledCode.rom(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    Labels.of(assembledCode).writeToFile(path);
                    return null;
                },
                () -> Component.translatable(MSG_ERROR_WRITE_BINARY, filePath.getFileName().toString())
        ));
    }

    private void readBinaryFromTarget()
    {
        clearLastError();

        if (!menu.isTargetValid()) return;
        if (forBlock && menu.isInterpreterEmpty()) return;
        if (!forBlock && isMemoryCardEmpty()) return;

        setFilePath(null, false);
        if (forBlock)
        {
            setLastInfo(MSG_INFO_WAITING_FOR_RESPONSE, false);
            PacketDistributor.sendToServer(new ServerboundRequestCodePayload(menu.containerId));
        }
        else
        {
            ItemStack stack = menu.slots.getFirst().getItem();
            setAssembledCode(stack.get(RCUContent.COMPONENT_TYPE_CODE));
            setLastInfo(MSG_INFO_ROM_READ, true);
        }
    }

    public void receiveBlockCodeFromServer(Code code)
    {
        setAssembledCode(code);
        setLastInfo(MSG_INFO_ROM_READ, true);
    }

    private void writeBinaryToTarget(boolean checkEmpty)
    {
        clearLastError();

        if (binaryFromFile)
        {
            loadBinaryFile(filePath);
        }

        if (assembledCode == null || !menu.isTargetValid()) return;

        if (forBlock)
        {
            if (checkEmpty && !menu.isInterpreterEmpty())
            {
                Minecraft.getInstance().pushGuiLayer(MessageScreen.confirm(
                        List.of(MSG_CONFIRM_NOT_EMPTY_BLOCK),
                        () -> writeBinaryToTarget(false)
                ));
                return;
            }
        }
        else
        {
            if (checkEmpty && !isMemoryCardEmpty())
            {
                Minecraft.getInstance().pushGuiLayer(MessageScreen.confirm(
                        List.of(MSG_CONFIRM_NOT_EMPTY_CARD),
                        () -> writeBinaryToTarget(false)
                ));
                return;
            }
        }

        PacketDistributor.sendToServer(new ServerboundWriteToTargetPayload(menu.containerId, assembledCode));
        setLastInfo(MSG_INFO_ROM_WRITTEN, true);
    }

    private <R, T extends Throwable> R guardOperation(ThrowingSupplier<R, T> operation, Supplier<MutableComponent> errorSupplier)
    {
        clearLastError();
        try
        {
            return operation.get();
        }
        catch (Throwable e)
        {
            Throwable error = e;
            while (error.getCause() != null)
            {
                error = error.getCause();
            }
            setLastError(errorSupplier.get(), error);
            return null;
        }
    }
}
