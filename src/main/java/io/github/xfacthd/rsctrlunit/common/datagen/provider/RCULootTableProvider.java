package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class RCULootTableProvider extends LootTableProvider
{
    public RCULootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, Set.of(), entries(), registries);
    }

    private static List<SubProviderEntry> entries()
    {
        return List.of(
                new SubProviderEntry(RCUBlockLootSubProvider::new, LootContextParamSets.BLOCK)
        );
    }

    private static final class RCUBlockLootSubProvider extends BlockLootSubProvider
    {
        private RCUBlockLootSubProvider(HolderLookup.Provider registries)
        {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return List.of(
                    RCUContent.BLOCK_CONTROLLER.value(),
                    RCUContent.BLOCK_ADC.value(),
                    RCUContent.BLOCK_DAC.value()
            );
        }

        @Override
        protected void generate()
        {
            dropSelf(RCUContent.BLOCK_CONTROLLER.value());
            dropSelf(RCUContent.BLOCK_ADC.value());
            dropSelf(RCUContent.BLOCK_DAC.value());
        }
    }
}
