package io.github.xfacthd.rsctrlunit.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.ExtendedUnbakedModel;

public final class ControllerGeometry implements ExtendedUnbakedModel
{
    private final UnbakedModel baseModel;
    private final UnbakedModel[] singleModels;
    private final UnbakedModel[] bundledModels;
    private final UnbakedModel[][] portIndexModels;

    ControllerGeometry(UnbakedModel baseModel, UnbakedModel[] singleModels, UnbakedModel[] bundledModels, UnbakedModel[][] portIndexModels)
    {
        this.baseModel = baseModel;
        this.singleModels = singleModels;
        this.bundledModels = bundledModels;
        this.portIndexModels = portIndexModels;
    }

    @Override
    public BakedModel bake(
            TextureSlots textures,
            ModelBaker baker,
            ModelState modelState,
            boolean useAmbientOcclusion,
            boolean usesBlockLight,
            ItemTransforms itemTransforms,
            ContextMap additionalProperties
    )
    {
        BakedModel[] singleModelsBaked = new BakedModel[4];
        BakedModel[] bundledModelsBaked = new BakedModel[4];
        BakedModel[][] portIndexModelsBaked = new BakedModel[4][4];

        for (int edge = 0; edge < 4; edge++)
        {
            singleModelsBaked[edge] = UnbakedModel.bakeWithTopModelValues(singleModels[edge], baker, modelState);
            bundledModelsBaked[edge] = UnbakedModel.bakeWithTopModelValues(bundledModels[edge], baker, modelState);
            for (int port = 0; port < 4; port++)
            {
                portIndexModelsBaked[edge][port] = UnbakedModel.bakeWithTopModelValues(portIndexModels[edge][port], baker, modelState);
            }
        }

        return new ControllerModel(UnbakedModel.bakeWithTopModelValues(baseModel, baker, modelState), singleModelsBaked, bundledModelsBaked, portIndexModelsBaked);
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver resolver)
    {
        baseModel.resolveDependencies(resolver);
        for (int edge = 0; edge < 4; edge++)
        {
            singleModels[edge].resolveDependencies(resolver);
            bundledModels[edge].resolveDependencies(resolver);
            for (int port = 0; port < 4; port++)
            {
                portIndexModels[edge][port].resolveDependencies(resolver);
            }
        }
    }
}
