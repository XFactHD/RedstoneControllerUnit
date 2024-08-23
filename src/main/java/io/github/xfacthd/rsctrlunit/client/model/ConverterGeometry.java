package io.github.xfacthd.rsctrlunit.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.*;
import net.neoforged.neoforge.client.model.geometry.*;

import java.util.List;
import java.util.function.Function;

public final class ConverterGeometry extends SimpleUnbakedGeometry<ConverterGeometry>
{
    private final List<BlockElement> elements;

    public ConverterGeometry(List<BlockElement> elements)
    {
        this.elements = elements;
    }

    @Override
    protected void addQuads(IGeometryBakingContext ctx, IModelBuilder<?> builder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
    {
        Transformation rootTransform = ctx.getRootTransform();
        if (!rootTransform.isIdentity())
        {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        for (BlockElement element : elements)
        {
            for (Direction direction : element.faces.keySet())
            {
                var face = element.faces.get(direction);
                var sprite = spriteGetter.apply(ctx.getMaterial(face.texture()));
                var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);

                if (face.cullForDirection() == null)
                {
                    builder.addUnculledFace(quad);
                }
                else
                {
                    builder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection()), quad);
                }
            }
        }
    }
}
