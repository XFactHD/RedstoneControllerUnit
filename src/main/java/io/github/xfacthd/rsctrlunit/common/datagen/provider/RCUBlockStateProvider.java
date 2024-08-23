package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.model.ControllerModelLoader;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.CompoundDirection;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.joml.Vector3f;

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

        ModelFile converter = models().getExistingFile(Utils.rl("block/converter"));

        makeConverterBlockStateAndItemModel(RCUContent.BLOCK_ADC, converter);
        makeConverterBlockStateAndItemModel(RCUContent.BLOCK_DAC, converter);

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

    private void makeConverterBlockStateAndItemModel(Holder<Block> block, ModelFile converterBase)
    {
        String name = Utils.getKeyOrThrow(block).location().getPath();

        ModelFile converter = models().withExistingParent(name, converterBase.getLocation())
                .texture("dir_overlay", modLoc("block/dir_overlay_" + name));

        ModelFile[] converters = new ModelFile[] {
                converter,
                makeConverterRotation(converter, name + "_cw90", 90),
                makeConverterRotation(converter, name + "_cw180", 180),
                makeConverterRotation(converter, name + "_ccw90", -90)
        };

        getVariantBuilder(block.value()).forAllStates(state ->
        {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            Direction dir = cmpDir.direction();
            int rotX = switch (dir)
            {
                case UP -> 180;
                case DOWN -> 0;
                default -> 90;
            };
            int rotY = dir.getAxis() == Direction.Axis.Y ? 0 : (int) dir.toYRot();
            ModelFile model = converters[cmpDir.rotation().ordinal()];
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        });

        itemModels().withExistingParent(name, converters[0].getLocation());
    }

    private ModelFile makeConverterRotation(ModelFile converter, String name, int rot)
    {
        return models().withExistingParent(name, converter.getLocation())
                .rootTransforms()
                .origin(new Vector3f(.5F, 0, .5F))
                .rotation(0, rot, 0, true)
                .end();
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
