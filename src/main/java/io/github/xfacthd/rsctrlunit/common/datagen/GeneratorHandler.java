package io.github.xfacthd.rsctrlunit.common.datagen;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.datagen.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = RedstoneControllerUnit.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorHandler
{
    private GeneratorHandler() { }

    @SubscribeEvent
    static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new RCUBlockStateProvider(output, fileHelper));
        generator.addProvider(event.includeClient(), new RCUItemModelProvider(output, fileHelper));
        generator.addProvider(event.includeClient(), new RCULanguageProvider(output));
        generator.addProvider(event.includeClient(), new RCUSpriteSourceProvider(output, lookupProvider, fileHelper));

        generator.addProvider(event.includeServer(), new RCUBlockTagsProvider(output, lookupProvider, fileHelper));
        generator.addProvider(event.includeServer(), new RCURecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new RCULootTableProvider(output, lookupProvider));
    }
}
