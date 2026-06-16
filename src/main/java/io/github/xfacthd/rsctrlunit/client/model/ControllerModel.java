package io.github.xfacthd.rsctrlunit.client.model;

import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

import java.util.List;

public final class ControllerModel extends DelegateBlockStateModel {
    private final BlockStateModel[] singleModels;
    private final BlockStateModel[] bundledModels;
    private final BlockStateModel[][] portIndexModels;

    ControllerModel(BlockStateModel baseModel, BlockStateModel[] singleModels, BlockStateModel[] bundledModels, BlockStateModel[][] portIndexModels) {
        super(baseModel);
        this.singleModels = singleModels;
        this.bundledModels = bundledModels;
        this.portIndexModels = portIndexModels;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        super.collectParts(level, pos, state, random, parts);

        for (int i = 0; i < 4; i++) {
            RedstoneType type = state.getValue(PropertyHolder.RS_CON_PROPS[i]);
            if (type == RedstoneType.NONE) {
                continue;
            }

            BlockStateModel model = type == RedstoneType.SINGLE ? singleModels[i] : bundledModels[i];
            model.collectParts(level, pos, state, random, parts);
        }
        int[] portMapping = level.getModelData(pos).get(ControllerBlockEntity.PORT_MAPPING_PROPERTY);
        if (portMapping != null && state.getValue(PropertyHolder.SHOW_PORT_MAPPING)) {
            for (int port = 0; port < 4; port++) {
                int extPort = portMapping[port];
                BlockStateModel model = portIndexModels[extPort][port];
                model.collectParts(level, pos, state, random, parts);
            }
        }
    }
}
