package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class RCUBlockTagsProvider extends BlockTagsProvider
{
    public RCUBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper fileHelper)
    {
        super(output, lookupProvider, RedstoneControllerUnit.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(
                RCUContent.BLOCK_CONTROLLER.value(),
                RCUContent.BLOCK_ADC.value(),
                RCUContent.BLOCK_DAC.value()
        );
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                RCUContent.BLOCK_CONTROLLER.value(),
                RCUContent.BLOCK_ADC.value(),
                RCUContent.BLOCK_DAC.value()
        );
    }
}
