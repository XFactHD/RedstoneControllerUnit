package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public final class RCURecipeProvider extends RecipeProvider
{
    public RCURecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RCUContent.BLOCK_CONTROLLER.value())
                .pattern("DRD")
                .pattern("RCR")
                .pattern("SRS")
                .define('D', Tags.Items.DUSTS_REDSTONE)
                .define('R', Items.REPEATER)
                .define('C', Items.COMPARATOR)
                .define('S', Items.STONE_SLAB)
                .unlockedBy("has_comparator", has(Items.COMPARATOR))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RCUContent.ITEM_MEMORY_CARD.value())
                .pattern(" TD")
                .pattern("TGT")
                .pattern("DTD")
                .define('T', Items.REDSTONE_TORCH)
                .define('D', Tags.Items.DUSTS_REDSTONE)
                .define('G', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RCUContent.ITEM_PROGRAMMER.value())
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', RCUContent.ITEM_MEMORY_CARD.value())
                .unlockedBy("has_memory_card", has(RCUContent.ITEM_MEMORY_CARD.value()))
                .save(output);
    }
}
