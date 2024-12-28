package io.github.xfacthd.rsctrlunit.common.datagen;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.datagen.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = RedstoneControllerUnit.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    private GeneratorHandler() { }

    @SubscribeEvent
    static void onGatherData(final GatherDataEvent.Client event)
    {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(true, new RCUSpriteSourceProvider(output, lookupProvider));
        generator.addProvider(true, new RCUBlockStateProvider(output));
        generator.addProvider(true, new RCUItemModelProvider(output));
        generator.addProvider(true, new RCULanguageProvider(output));

        generator.addProvider(true, new RCUBlockTagsProvider(output, lookupProvider));
        generator.addProvider(true, new RCURecipeProvider.Runner(output, lookupProvider));
        generator.addProvider(true, new RCULootTableProvider(output, lookupProvider));
    }
}
