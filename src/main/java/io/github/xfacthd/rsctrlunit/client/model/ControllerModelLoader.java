package io.github.xfacthd.rsctrlunit.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ControllerModelLoader implements IGeometryLoader<ControllerGeometry>
{
    private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
    private static final String[] EDGE_SUFFIXES = new String[] { "n", "e", "s", "w" };
    public static final ResourceLocation[] LOCATIONS_SINGLE = Utils.makeArray(new ResourceLocation[4], edge ->
            Utils.rl("block/type_single_" + EDGE_SUFFIXES[edge])
    );
    public static final ResourceLocation[] LOCATIONS_BUNDLED = Utils.makeArray(new ResourceLocation[4], edge ->
            Utils.rl("block/type_bundled_" + EDGE_SUFFIXES[edge])
    );
    public static final ResourceLocation[][] LOCATIONS_PORT = Utils.makeArray(new ResourceLocation[4][4], edge ->
            Utils.makeArray(new ResourceLocation[4], port -> Utils.rl("block/port_" + port + "_" + EDGE_SUFFIXES[edge]))
    );

    @Override
    public ControllerGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        BlockModel[] singleModels = new BlockModel[4];
        BlockModel[] bundledModels = new BlockModel[4];
        BlockModel[][] portIndexModels = new BlockModel[4][4];

        for (int edge = 0; edge < 4; edge++)
        {
            singleModels[edge] = loadModel(LOCATIONS_SINGLE[edge]);
            bundledModels[edge] = loadModel(LOCATIONS_BUNDLED[edge]);
            for (int port = 0; port < 4; port++)
            {
                portIndexModels[edge][port] = loadModel(LOCATIONS_PORT[edge][port]);
            }
        }

        json.remove("loader");
        return new ControllerGeometry(ctx.deserialize(json, BlockModel.class), singleModels, bundledModels, portIndexModels);
    }

    private static BlockModel loadModel(ResourceLocation location)
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        ResourceLocation file = MODEL_LISTER.idToFile(location);
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
