package io.github.xfacthd.rsctrlunit.client.model;

import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.util.ArrayList;
import java.util.List;

public final class ConverterModelLoader implements IGeometryLoader<ConverterGeometry>
{
    @Override
    public ConverterGeometry read(JsonObject obj, JsonDeserializationContext ctx) throws JsonParseException
    {
        List<BlockElement> elements = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(obj, "elements"))
        {
            elements.add(ctx.deserialize(element, BlockElement.class));
        }
        return new ConverterGeometry(elements);
    }
}
