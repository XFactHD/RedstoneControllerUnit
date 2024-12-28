package io.github.xfacthd.rsctrlunit.client.model;

import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.DelegateBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ControllerModel extends DelegateBakedModel
{
    private final BakedModel[] singleModels;
    private final BakedModel[] bundledModels;
    private final BakedModel[][] portIndexModels;

    ControllerModel(BakedModel baseModel, BakedModel[] singleModels, BakedModel[] bundledModels, BakedModel[][] portIndexModels)
    {
        super(baseModel);
        this.singleModels = singleModels;
        this.bundledModels = bundledModels;
        this.portIndexModels = portIndexModels;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType)
    {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (state != null)
        {
            boolean copied = false;
            for (int i = 0; i < 4; i++)
            {
                RedstoneType type = state.getValue(PropertyHolder.RS_CON_PROPS[i]);
                if (type == RedstoneType.NONE) continue;

                if (!copied)
                {
                    quads = new ArrayList<>(quads);
                    copied = true;
                }

                BakedModel model = type == RedstoneType.SINGLE ? singleModels[i] : bundledModels[i];
                quads.addAll(model.getQuads(state, side, rand, extraData, renderType));
            }
            int[] portMapping = extraData.get(ControllerBlockEntity.PORT_MAPPING_PROPERTY);
            if (portMapping != null && state.getValue(PropertyHolder.SHOW_PORT_MAPPING))
            {
                if (!copied)
                {
                    quads = new ArrayList<>(quads);
                }

                for (int port = 0; port < 4; port++)
                {
                    int extPort = portMapping[port];
                    BakedModel model = portIndexModels[extPort][port];
                    quads.addAll(model.getQuads(state, side, rand, extraData, renderType));
                }
            }
        }
        return quads;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
    {
        return getQuads(state, side, rand, ModelData.EMPTY, null);
    }
}
