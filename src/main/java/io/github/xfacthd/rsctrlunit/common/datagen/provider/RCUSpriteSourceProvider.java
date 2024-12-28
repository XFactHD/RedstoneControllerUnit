package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.texture.AreaMaskSource;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class RCUSpriteSourceProvider extends SpriteSourceProvider
{
    public RCUSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, RedstoneControllerUnit.MOD_ID);
    }

    @Override
    protected void gather()
    {
        atlas(BLOCKS_ATLAS)
                .addSource(new AreaMaskSource(
                        ResourceLocation.withDefaultNamespace("block/moss_block"),
                        Optional.empty(),
                        Utils.rl("block/pcb"),
                        2, 2, 12, 12
                ))
                .addSource(new AreaMaskSource(
                        ResourceLocation.fromNamespaceAndPath("morered", "block/redwire_post_plate_overlay"),
                        Optional.of(Utils.rl("block/type_single")),
                        Utils.rl("block/overlay_single"),
                        0, 0, 16, 2
                ))
                .addSource(new AreaMaskSource(
                        ResourceLocation.fromNamespaceAndPath("morered", "block/bundled_cable_plate_overlay"),
                        Optional.of(Utils.rl("block/type_bundled")),
                        Utils.rl("block/overlay_bundled"),
                        0, 0, 16, 2
                ));
    }
}
