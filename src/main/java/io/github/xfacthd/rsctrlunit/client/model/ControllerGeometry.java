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
    private final BlockModel[] portIndexModels;

    ControllerGeometry(BlockModel baseModel, BlockModel singleModel, BlockModel bundledModel, BlockModel[] portIndexModels)
    {
        this.baseModel = baseModel;
        this.singleModel = singleModel;
        this.bundledModel = bundledModel;
        this.portIndexModels = portIndexModels;
    }

    @Override
    public BakedModel bake(
            IGeometryBakingContext ctx,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            ItemOverrides overrides
    )
    {
        BakedModel[] singleModels = new BakedModel[4];
        BakedModel[] bundledModels = new BakedModel[4];
        BakedModel[][] portModels = new BakedModel[4][4];

        for (int i = 0; i < 4; i++)
        {
            int yRot = ((i + 2) % 4) * 90;
            ModelState rotState = UnbakedGeometryHelper.composeRootTransformIntoModelState(
                    modelState, new Transformation(null, Axis.YN.rotationDegrees(yRot), null, null).applyOrigin(new Vector3f(.5F, .5F, .5F))
            );
            singleModels[i] = singleModel.bake(baker, singleModel, spriteGetter, rotState, true);
            bundledModels[i] = bundledModel.bake(baker, bundledModel, spriteGetter, rotState, true);
            for (int j = 0; j < 4; j++)
            {
                BlockModel portModel = portIndexModels[j];
                portModels[i][j] = portModel.bake(baker, portModel, spriteGetter, rotState, true);
            }
        }

        return new ControllerModel(baseModel.bake(baker, baseModel, spriteGetter, modelState, true), singleModels, bundledModels, portModels);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveParents(modelGetter);
        singleModel.resolveParents(modelGetter);
        bundledModel.resolveParents(modelGetter);
    }
}
