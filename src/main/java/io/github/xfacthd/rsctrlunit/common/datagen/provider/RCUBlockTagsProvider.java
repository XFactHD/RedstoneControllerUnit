package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public final class RCUBlockTagsProvider extends BlockTagsProvider
{
    public RCUBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, RedstoneControllerUnit.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(
                RCUContent.BLOCK_CONTROLLER.getKey(),
                RCUContent.BLOCK_ADC.getKey(),
                RCUContent.BLOCK_DAC.getKey()
        );
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                RCUContent.BLOCK_CONTROLLER.getKey(),
                RCUContent.BLOCK_ADC.getKey(),
                RCUContent.BLOCK_DAC.getKey()
        );
    }
}
