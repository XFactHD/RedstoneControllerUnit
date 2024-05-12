package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class RCUBlockStateProvider extends BlockStateProvider
{
    public RCUBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, RedstoneControllerUnit.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile controller = models().getExistingFile(Utils.rl("block/controller"));
        getVariantBuilder(RCUContent.BLOCK_CONTROLLER.value()).forAllStates(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            int rotX = switch (dir)
            {
                case UP -> 180;
                case DOWN -> 0;
                default -> 90;
            };
            int rotY = dir.getAxis() == Direction.Axis.Y ? 0 : (int) dir.toYRot();
            return ConfiguredModel.builder()
                    .modelFile(controller)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        });

        itemModels().withExistingParent("controller", controller.getLocation());
    }
}
