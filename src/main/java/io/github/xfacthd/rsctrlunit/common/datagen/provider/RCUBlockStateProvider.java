package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.model.ControllerModelLoader;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
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
        getVariantBuilder(RCUContent.BLOCK_CONTROLLER.value()).forAllStatesExcept(state ->
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
        }, Utils.appendArray(PropertyHolder.RS_CON_PROPS, PropertyHolder.SHOW_PORT_MAPPING, Property[].class));

        itemModels().withExistingParent("controller", controller.getLocation());

        for (int edge = 0; edge < 4; edge++)
        {
            plateOverlay(ControllerModelLoader.LOCATIONS_SINGLE[edge], modLoc("block/overlay_single"), edge, true, true);
            plateOverlay(ControllerModelLoader.LOCATIONS_BUNDLED[edge], modLoc("block/overlay_bundled"), edge, true, true);

            for (int port = 0; port < 4; port++)
            {
                plateOverlay(ControllerModelLoader.LOCATIONS_PORT[edge][port], modLoc("block/port_" + port), edge, false, false);
            }
        }
    }

    private void plateOverlay(ResourceLocation name, ResourceLocation texture, int edge, boolean withSide, boolean mirrorTopX)
    {
        ModelBuilder<BlockModelBuilder>.ElementBuilder element = models().getBuilder(name.toString())
                .texture("0", texture)
                .texture("particle", texture)
                .element()
                .from(0, 0, 0)
                .to(16, 2, 16);

        element.face(Direction.UP)
                .uvs(0, mirrorTopX ? 16 : 0, 16, mirrorTopX ? 0 : 16)
                .rotation(ModelBuilder.FaceRotation.values()[edge])
                .texture("#0");

        if (withSide)
        {
            Direction edgeDir = Direction.from2DDataValue(edge);
            element.face(edgeDir)
                    .cullface(edgeDir)
                    .uvs(0, 0, 16, 2)
                    .texture("#0");
        }
    }
}
