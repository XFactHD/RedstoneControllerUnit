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
    private static final ResourceLocation FILE_PORT0 = ModelBakery.MODEL_LISTER.idToFile(Utils.rl("block/port_0"));
    private static final ResourceLocation FILE_PORT1 = ModelBakery.MODEL_LISTER.idToFile(Utils.rl("block/port_1"));
    private static final ResourceLocation FILE_PORT2 = ModelBakery.MODEL_LISTER.idToFile(Utils.rl("block/port_2"));
    private static final ResourceLocation FILE_PORT3 = ModelBakery.MODEL_LISTER.idToFile(Utils.rl("block/port_3"));

    @Override
    public ControllerGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        BlockModel singleModel = loadModel(FILE_SINGLE);
        BlockModel bundledModel = loadModel(FILE_BUNDLED);
        BlockModel[] portModels = new BlockModel[] {
                loadModel(FILE_PORT0),
                loadModel(FILE_PORT1),
                loadModel(FILE_PORT2),
                loadModel(FILE_PORT3),
        };

        json.remove("loader");
        return new ControllerGeometry(ctx.deserialize(json, BlockModel.class), singleModel, bundledModel, portModels);
    }

    private static BlockModel loadModel(ResourceLocation file)
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        try (InputStream stream = manager.getResourceOrThrow(file).open())
        {
            return BlockModel.fromStream(new InputStreamReader(stream));
        }
        catch (IOException e)
        {
            throw new JsonParseException("Failed to load part model '" + file + "'", e);
        }
    }
}
