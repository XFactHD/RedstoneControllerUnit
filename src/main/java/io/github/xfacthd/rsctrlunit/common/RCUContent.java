package io.github.xfacthd.rsctrlunit.common;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.block.SignalConverterBlock;
import io.github.xfacthd.rsctrlunit.common.block.ControllerBlock;
import io.github.xfacthd.rsctrlunit.common.blockentity.AnalogToDigitalConverterBlockEntity;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.blockentity.DigitalToAnalogConverterBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.item.MemoryCardItem;
import io.github.xfacthd.rsctrlunit.common.item.ProgrammerItem;
import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.menu.ProgrammerMenu;
import io.github.xfacthd.rsctrlunit.common.util.registration.DeferredBlockEntity;
import io.github.xfacthd.rsctrlunit.common.util.registration.DeferredBlockEntityRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class RCUContent
{
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RedstoneControllerUnit.MOD_ID);
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RedstoneControllerUnit.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RedstoneControllerUnit.MOD_ID);
    private static final DeferredBlockEntityRegister BLOCK_ENTITIES = DeferredBlockEntityRegister.create(RedstoneControllerUnit.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, RedstoneControllerUnit.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RedstoneControllerUnit.MOD_ID);

    // region Blocks
    public static final Holder<Block> BLOCK_CONTROLLER = registerBlock("controller", ControllerBlock::new);
    public static final Holder<Block> BLOCK_ADC = registerBlock("adc", SignalConverterBlock::analogToDigital);
    public static final Holder<Block> BLOCK_DAC = registerBlock("dac", SignalConverterBlock::digitalToAnalog);
    // endregion

    // region DataComponents
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Code>> COMPONENT_TYPE_CODE = DATA_COMPONENTS.registerComponentType(
            "code", builder -> builder.persistent(Code.CODEC).networkSynchronized(Code.STREAM_CODEC).cacheEncoding()
    );
    // endregion

    // region Items
    public static final Holder<Item> ITEM_MEMORY_CARD = ITEMS.registerItem("memory_card", MemoryCardItem::new);
    public static final Holder<Item> ITEM_PROGRAMMER = ITEMS.registerItem("programmer", ProgrammerItem::new);
    // endregion

    // region BlockEntities
    public static final DeferredBlockEntity<ControllerBlockEntity> BE_TYPE_CONTROLLER = BLOCK_ENTITIES.registerBlockEntity(
            "controller", ControllerBlockEntity::new, BLOCK_CONTROLLER
    );
    public static final DeferredBlockEntity<AnalogToDigitalConverterBlockEntity> BE_TYPE_ADC = BLOCK_ENTITIES.registerBlockEntity(
            "adc", AnalogToDigitalConverterBlockEntity::new, BLOCK_ADC
    );
    public static final DeferredBlockEntity<DigitalToAnalogConverterBlockEntity> BE_TYPE_DAC = BLOCK_ENTITIES.registerBlockEntity(
            "dac", DigitalToAnalogConverterBlockEntity::new, BLOCK_DAC
    );
    // endregion

    // region MenuTypes
    public static final DeferredHolder<MenuType<?>, MenuType<ControllerMenu>> MENU_TYPE_CONTROLLER = MENU_TYPES.register(
            "controller", () -> IMenuTypeExtension.create(ControllerMenu::createClient)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<ProgrammerMenu>> MENU_TYPE_PROGRAMMER = MENU_TYPES.register(
            "programmer", () -> IMenuTypeExtension.create(ProgrammerMenu::createClient)
    );
    // endregion

    // region CreativeModeTabs
    public static final Holder<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.rsctrlunit"))
                    .icon(() -> BLOCK_CONTROLLER.value().asItem().getDefaultInstance())
                    .displayItems((params, output) ->
                    {
                        output.accept(BLOCK_CONTROLLER.value());
                        output.accept(BLOCK_ADC.value());
                        output.accept(BLOCK_DAC.value());
                        output.accept(ITEM_MEMORY_CARD.value());
                        output.accept(ITEM_PROGRAMMER.value());
                    })
                    .build()
    );
    // endregion

    private static Holder<Block> registerBlock(String name, Function<BlockBehaviour.Properties, Block> blockFactory)
    {
        Holder<Block> block = BLOCKS.registerBlock(name, blockFactory, BlockBehaviour.Properties.of());
        ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        DATA_COMPONENTS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENU_TYPES.register(modBus);
        CREATIVE_TABS.register(modBus);
    }

    private RCUContent() { }
}
