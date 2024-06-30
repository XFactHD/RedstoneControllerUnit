package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.ProgrammerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.popup.MessageScreen;
import io.github.xfacthd.rsctrlunit.client.screen.widget.RedstoneConfig;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.compat.atlasviewer.AtlasViewerCompat;
import io.github.xfacthd.rsctrlunit.common.menu.ProgrammerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class RCULanguageProvider extends LanguageProvider
{
    public RCULanguageProvider(PackOutput output)
    {
        super(output, RedstoneControllerUnit.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        addBlockTranslations();
        addItemTranslations();
        addGenericTranslations();
        addScreenTranslations();
        addSpecialTranslations();
    }

    private void addBlockTranslations()
    {
        add(RCUContent.BLOCK_CONTROLLER.value(), "Controller");
    }

    private void addItemTranslations()
    {
        add(RCUContent.ITEM_MEMORY_CARD.value(), "Memory Card");
        add(RCUContent.ITEM_PROGRAMMER.value(), "Programmer");
    }

    private void addGenericTranslations()
    {
        add("rsctrlunit.code.name", "Program: %s");
        add("rsctrlunit.code.name.empty", "[None]");

        add(RedstoneType.NONE.getTranslatedName(), "None");
        add(RedstoneType.SINGLE.getTranslatedName(), "Single");
        add(RedstoneType.BUNDLED.getTranslatedName(), "Bundle");

        add(Utils.DIRECTION_NAMES[Direction.DOWN.ordinal()], "Down");
        add(Utils.DIRECTION_NAMES[Direction.UP.ordinal()], "Up");
        add(Utils.DIRECTION_NAMES[Direction.NORTH.ordinal()], "North");
        add(Utils.DIRECTION_NAMES[Direction.SOUTH.ordinal()], "South");
        add(Utils.DIRECTION_NAMES[Direction.WEST.ordinal()], "West");
        add(Utils.DIRECTION_NAMES[Direction.EAST.ordinal()], "East");

        add(Utils.COLOR_NAMES[DyeColor.WHITE.ordinal()], "White");
        add(Utils.COLOR_NAMES[DyeColor.ORANGE.ordinal()], "Orange");
        add(Utils.COLOR_NAMES[DyeColor.MAGENTA.ordinal()], "Magenta");
        add(Utils.COLOR_NAMES[DyeColor.LIGHT_BLUE.ordinal()], "Light Blue");
        add(Utils.COLOR_NAMES[DyeColor.YELLOW.ordinal()], "Yellow");
        add(Utils.COLOR_NAMES[DyeColor.LIME.ordinal()], "Lime");
        add(Utils.COLOR_NAMES[DyeColor.PINK.ordinal()], "Pink");
        add(Utils.COLOR_NAMES[DyeColor.GRAY.ordinal()], "Gray");
        add(Utils.COLOR_NAMES[DyeColor.LIGHT_GRAY.ordinal()], "Light Gray");
        add(Utils.COLOR_NAMES[DyeColor.CYAN.ordinal()], "Cyan");
        add(Utils.COLOR_NAMES[DyeColor.PURPLE.ordinal()], "Purple");
        add(Utils.COLOR_NAMES[DyeColor.BLUE.ordinal()], "Blue");
        add(Utils.COLOR_NAMES[DyeColor.BROWN.ordinal()], "Brown");
        add(Utils.COLOR_NAMES[DyeColor.GREEN.ordinal()], "Green");
        add(Utils.COLOR_NAMES[DyeColor.RED.ordinal()], "Red");
        add(Utils.COLOR_NAMES[DyeColor.BLACK.ordinal()], "Black");
    }

    private void addScreenTranslations()
    {
        add(ControllerBlockEntity.TITLE, "Controller");
        add(ProgrammerMenu.TITLE, "Programmer");

        add(ControllerScreen.TAB_TITLE_STATUS, "Status");
        add(ControllerScreen.TAB_TITLE_CODE, "Code");
        add(ControllerScreen.TAB_TITLE_REDSTONE, "Redstone");
        add(ControllerScreen.TABLE_HEADER_PORT, "Port");
        add(ControllerScreen.TABLE_HEADER_SIDE, "Side");
        add(ControllerScreen.TABLE_HEADER_TYPE, "Type");
        add(ControllerScreen.TABLE_HEADER_DIRECTION, "Direction");
        add(ControllerScreen.TABLE_HEADER_MAPPING, "Mapping");
        add(ControllerScreen.TITLE_REGISTERS, "Registers");
        add(ControllerScreen.TITLE_DISASSEMBLY, "Disassembly");
        add(ControllerScreen.BUTTON_LOAD_ROM, "Load ROM from card");
        add(ControllerScreen.BUTTON_SAVE_ROM, "Save ROM to card");
        add(ControllerScreen.BUTTON_CLEAR_ROM, "Clear ROM");
        add(ControllerScreen.LABEL_PROGRAM_KEY, "Program: %s");
        add(ControllerScreen.LABEL_PORT_REG_OUT, "Out");
        add(ControllerScreen.LABEL_PORT_REG_IN, "In");

        add(RedstoneConfig.TEXT_PORT_IDX_KEY, "Port %s");
        add(RedstoneConfig.TEXT_INPUT, "Input");
        add(RedstoneConfig.TEXT_OUTPUT, "Output");
        add(RedstoneConfig.TOOLTIP_TYPE_NONE, "No redstone output");
        add(RedstoneConfig.TOOLTIP_TYPE_SINGLE, "1-bit redstone output to vanilla redstone");
        add(RedstoneConfig.TOOLTIP_TYPE_BUNDLED, "8-bit redstone output to bundled redstone wires");
        add(RedstoneConfig.TOOLTIP_PORT_BIT_KEY, "Port bit: %s");
        add(RedstoneConfig.TOOLTIP_WIRE_COLOR_KEY, "Wire color: %s");
        add(RedstoneConfig.TOOLTIP_BUNDLED_MAPPING_LOWER, "Bits 0-7 mapped to bundled wire colors 1-8 (White-Gray)");
        add(RedstoneConfig.TOOLTIP_BUNDLED_MAPPING_UPPER, "Bits 0-7 mapped to bundled wire colors 9-16 (Light Gray-Black)");

        add(ProgrammerScreen.BUTTON_OPEN_SOURCE, "Open source file");
        add(ProgrammerScreen.BUTTON_REVEAL_IN_EXPLORER, "Reveal in Explorer");
        add(ProgrammerScreen.BUTTON_ASSEMBLE, "Assemble");
        add(ProgrammerScreen.BUTTON_OPEN_BINARY, "Open binary file");
        add(ProgrammerScreen.BUTTON_SAVE_BINARY, "Save binary file");
        add(ProgrammerScreen.BUTTON_READ_BINARY_CARD, "Read ROM from card");
        add(ProgrammerScreen.BUTTON_READ_BINARY_BLOCK, "Read ROM from controller");
        add(ProgrammerScreen.BUTTON_WRITE_BINARY_CARD, "Write ROM to card");
        add(ProgrammerScreen.BUTTON_WRITE_BINARY_BLOCK, "Write ROM to controller");
        add(ProgrammerScreen.LABEL_FILE_PATH, "File Path:");
        add(ProgrammerScreen.LABEL_FILE_TYPE, "File Type:");
        add(ProgrammerScreen.LABEL_CODE_INFO, "Assembly:");
        add(ProgrammerScreen.LABEL_ERROR, "Error:");
        add(ProgrammerScreen.LABEL_INFO, "Info:");
        add(ProgrammerScreen.DESC_PATH_NONE, "-");
        add(ProgrammerScreen.DESC_TYPE_NONE, "-");
        add(ProgrammerScreen.DESC_TYPE_SOURCE, "Source");
        add(ProgrammerScreen.DESC_TYPE_BINARY, "Binary");
        add(ProgrammerScreen.DESC_CODE_INFO, "%s (%s bytes)");
        add(ProgrammerScreen.DESC_CODE_INFO_NONE, "-");
        add(ProgrammerScreen.MSG_ERROR_READ_SOURCE, "Unknown error reading source file '%s'");
        add(ProgrammerScreen.MSG_ERROR_REVEAL_IN_EXPLORER, "Unknown error revealing file '%s' in file explorer");
        add(ProgrammerScreen.MSG_ERROR_ASSEMBLE, "Unknown error assembling source file '%s'");
        add(ProgrammerScreen.MSG_ERROR_READ_BINARY, "Unknown error reading binary file '%s'");
        add(ProgrammerScreen.MSG_ERROR_WRITE_BINARY, "Unknown error writing binary file '%s'");
        add(ProgrammerScreen.MSG_ERROR_ASSEMBLY_FAILED, "Failed to assemble, see below for errors");
        add(ProgrammerScreen.MSG_INFO_ASSEMBLY_SUCCESS, "Assembly successful");
        add(ProgrammerScreen.MSG_INFO_ROM_READ, "ROM loaded from target");
        add(ProgrammerScreen.MSG_INFO_WAITING_FOR_RESPONSE, "Waiting for server response");
        add(ProgrammerScreen.MSG_INFO_ROM_WRITTEN, "ROM written to target");
        add(ProgrammerScreen.MSG_CONFIRM_NOT_EMPTY_BLOCK, "The target Controller already has code loaded. Are you sure you want to overwrite it?");
        add(ProgrammerScreen.MSG_CONFIRM_NOT_EMPTY_CARD, "The Memory Card already contains code. Are you sure you want to overwrite it?");
        add(ProgrammerScreen.TOOLTIP_NO_SOURCE, "No source file loaded");
        add(ProgrammerScreen.TOOLTIP_NO_ASSEMBLY, "No assembly available");
        add(ProgrammerScreen.TOOLTIP_BLOCK_REMOVED, "Target controller block was removed");
        add(ProgrammerScreen.TOOLTIP_NO_CARD_ITEM, "No memory card present in card slot");
        add(ProgrammerScreen.TOOLTIP_NO_CODE_BLOCK, "Target controller has no code loaded");
        add(ProgrammerScreen.TOOLTIP_NO_CODE_CARD, "Memory card is empty");

        add(MessageScreen.INFO_TITLE, "Info");
        add(MessageScreen.ERROR_TITLE, "Error");
        add(MessageScreen.CONFIRM_TITLE, "Confirm");
    }

    private void addSpecialTranslations()
    {
        add(AtlasViewerCompat.LABEL_TEXTURE, "Texture");
        add(AtlasViewerCompat.LABEL_SPRITE, "Sprite");
        add(AtlasViewerCompat.LABEL_AREA, "Area");
        add(AtlasViewerCompat.VALUE_AREA, "X: %s Y: %s Width: %s Height: %s");
    }

    private void add(Component key, String value)
    {
        ComponentContents contents = key.getContents();
        if (contents instanceof TranslatableContents translatable)
        {
            add(translatable.getKey(), value);
        }
        else
        {
            add(key.getString(), value);
        }
    }
}
