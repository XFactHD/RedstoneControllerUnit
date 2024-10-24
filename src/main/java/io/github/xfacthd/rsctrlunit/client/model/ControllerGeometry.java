package io.github.xfacthd.rsctrlunit.client.model;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.List;
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
            List<ItemOverride> overrides
    )
    {
        BakedModel[] singleModelsBaked = new BakedModel[4];
        BakedModel[] bundledModelsBaked = new BakedModel[4];
        BakedModel[][] portIndexModelsBaked = new BakedModel[4][4];

        for (int edge = 0; edge < 4; edge++)
        {
            singleModelsBaked[edge] = bakePart(singleModels[edge], spriteGetter, modelState);
            bundledModelsBaked[edge] = bakePart(bundledModels[edge], spriteGetter, modelState);
            for (int port = 0; port < 4; port++)
            {
                portIndexModelsBaked[edge][port] = bakePart(portIndexModels[edge][port], spriteGetter, modelState);
            }
        }

        return new ControllerModel(bakePart(baseModel, spriteGetter, modelState), singleModelsBaked, bundledModelsBaked, portIndexModelsBaked);
    }

    private static BakedModel bakePart(BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
    {
        return model.bake(spriteGetter, modelState, true);
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveDependencies(modelGetter);
        for (int edge = 0; edge < 4; edge++)
        {
            singleModels[edge].resolveDependencies(modelGetter);
            bundledModels[edge].resolveDependencies(modelGetter);
            for (int port = 0; port < 4; port++)
            {
                portIndexModels[edge][port].resolveDependencies(modelGetter);
            }
        }
    }
}
