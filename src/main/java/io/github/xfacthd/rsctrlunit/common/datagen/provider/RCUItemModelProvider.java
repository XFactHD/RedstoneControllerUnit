package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class RCUItemModelProvider extends ItemModelProvider
{
    public RCUItemModelProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, RedstoneControllerUnit.MOD_ID, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        singleTexture("memory_card", mcLoc("item/generated"), "layer0", modLoc("item/memory_card"));
        singleTexture("programmer", mcLoc("item/generated"), "layer0", modLoc("item/programmer"));
    }
}
