package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public final class RCUItemModelProvider extends ModelProvider
{
    public RCUItemModelProvider(PackOutput output)
    {
        super(output, RedstoneControllerUnit.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
    {
        itemModels.generateFlatItem(RCUContent.ITEM_MEMORY_CARD.value(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RCUContent.ITEM_PROGRAMMER.value(), ModelTemplates.FLAT_ITEM);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks()
    {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems()
    {
        return Stream.of(RCUContent.ITEM_MEMORY_CARD, RCUContent.ITEM_PROGRAMMER);
    }

    @Override
    public String getName()
    {
        return "Item Models - RedstoneControllerUnit";
    }
}
