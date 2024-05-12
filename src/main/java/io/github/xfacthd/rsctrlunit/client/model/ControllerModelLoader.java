package io.github.xfacthd.rsctrlunit.client.model;

import com.google.gson.*;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.io.*;

public final class ControllerModelLoader implements IGeometryLoader<ControllerGeometry>
{
    private static final ResourceLocation FILE_SINGLE = ModelBakery.MODEL_LISTER.idToFile(Utils.rl("block/type_single"));
    private static final ResourceLocation FILE_BUNDLED = ModelBakery.MODEL_LISTER.idToFile(Utils.rl("block/type_bundled"));

    @Override
    public ControllerGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        BlockModel singleModel;
        BlockModel bundledModel;
        try
        {
            try (InputStream single = manager.getResourceOrThrow(FILE_SINGLE).open())
            {
                singleModel = BlockModel.fromStream(new InputStreamReader(single));
            }
            try (InputStream bundled = manager.getResourceOrThrow(FILE_BUNDLED).open())
            {
                bundledModel = BlockModel.fromStream(new InputStreamReader(bundled));
            }
        }
        catch (IOException e)
        {
            throw new JsonParseException("Failed to load part models", e);
        }

        json.remove("loader");
        return new ControllerGeometry(ctx.deserialize(json, BlockModel.class), singleModel, bundledModel);
    }
}
