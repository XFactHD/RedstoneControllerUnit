package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.screen.ControllerScreen;
import io.github.xfacthd.rsctrlunit.client.screen.widget.RedstoneConfig;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
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
    }

    private void addBlockTranslations()
    {
        add(RCUContent.BLOCK_CONTROLLER.value(), "Controller");
    }

    private void addItemTranslations()
    {
        add(RCUContent.ITEM_MEMORY_CARD.value(), "Memory Card");
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
