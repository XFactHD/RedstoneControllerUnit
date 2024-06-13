package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.texture.AreaMaskSource;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public final class RCUSpriteSourceProvider extends SpriteSourceProvider
{
    public RCUSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(output, lookupProvider, RedstoneControllerUnit.MOD_ID, helper);
    }

    @Override
    protected void gather()
    {
        atlas(BLOCKS_ATLAS)
                .addSource(new AreaMaskSource(
                        ResourceLocation.withDefaultNamespace("block/moss_block"),
                        Utils.rl("block/pcb"),
                        2, 2, 12, 12
                ));
    }
}
