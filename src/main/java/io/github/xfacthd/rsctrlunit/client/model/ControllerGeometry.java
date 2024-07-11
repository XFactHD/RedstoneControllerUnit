package io.github.xfacthd.rsctrlunit.client.model;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.*;

import java.util.function.Function;

public final class ControllerGeometry implements IUnbakedGeometry<ControllerGeometry>
{
    private final BlockModel baseModel;
    private final BlockModel[] singleModels;
    private final BlockModel[] bundledModels;
    private final BlockModel[][] portIndexModels;

    ControllerGeometry(BlockModel baseModel, BlockModel[] singleModels, BlockModel[] bundledModels, BlockModel[][] portIndexModels)
    {
        this.baseModel = baseModel;
        this.singleModels = singleModels;
        this.bundledModels = bundledModels;
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
        BakedModel[] singleModelsBaked = new BakedModel[4];
        BakedModel[] bundledModelsBaked = new BakedModel[4];
        BakedModel[][] portIndexModelsBaked = new BakedModel[4][4];

        for (int edge = 0; edge < 4; edge++)
        {
            singleModelsBaked[edge] = bakePart(singleModels[edge], baker, spriteGetter, modelState);
            bundledModelsBaked[edge] = bakePart(bundledModels[edge], baker, spriteGetter, modelState);
            for (int port = 0; port < 4; port++)
            {
                portIndexModelsBaked[edge][port] = bakePart(portIndexModels[edge][port], baker, spriteGetter, modelState);
            }
        }

        return new ControllerModel(bakePart(baseModel, baker, spriteGetter, modelState), singleModelsBaked, bundledModelsBaked, portIndexModelsBaked);
    }

    private static BakedModel bakePart(BlockModel model, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
    {
        return model.bake(baker, model, spriteGetter, modelState, true);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveParents(modelGetter);
        for (int edge = 0; edge < 4; edge++)
        {
            singleModels[edge].resolveParents(modelGetter);
            bundledModels[edge].resolveParents(modelGetter);
            for (int port = 0; port < 4; port++)
            {
                portIndexModels[edge][port].resolveParents(modelGetter);
            }
        }
    }
}
