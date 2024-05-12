package io.github.xfacthd.rsctrlunit.client.model;

import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.*;
import org.joml.Vector3f;

import java.util.function.Function;

public final class ControllerGeometry implements IUnbakedGeometry<ControllerGeometry>
{
    private final BlockModel baseModel;
    private final BlockModel singleModel;
    private final BlockModel bundledModel;

    ControllerGeometry(BlockModel baseModel, BlockModel singleModel, BlockModel bundledModel)
    {
        this.baseModel = baseModel;
        this.singleModel = singleModel;
        this.bundledModel = bundledModel;
    }

    @Override
    public BakedModel bake(
            IGeometryBakingContext ctx,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            ItemOverrides overrides,
            ResourceLocation modelLocation
    )
    {
        BakedModel[] singleModels = new BakedModel[4];
        BakedModel[] bundledModels = new BakedModel[4];

        for (int i = 0; i < 4; i++)
        {
            int yRot = ((i + 2) % 4) * 90;
            ModelState rotState = UnbakedGeometryHelper.composeRootTransformIntoModelState(
                    modelState, new Transformation(null, Axis.YN.rotationDegrees(yRot), null, null).applyOrigin(new Vector3f(.5F, .5F, .5F))
            );
            singleModels[i] = singleModel.bake(baker, singleModel, spriteGetter, rotState, modelLocation, true);
            bundledModels[i] = bundledModel.bake(baker, bundledModel, spriteGetter, rotState, modelLocation, true);
        }

        return new ControllerModel(
                baseModel.bake(baker, baseModel, spriteGetter, modelState, modelLocation, true),
                singleModels,
                bundledModels
        );
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveParents(modelGetter);
        singleModel.resolveParents(modelGetter);
        bundledModel.resolveParents(modelGetter);
    }
}
