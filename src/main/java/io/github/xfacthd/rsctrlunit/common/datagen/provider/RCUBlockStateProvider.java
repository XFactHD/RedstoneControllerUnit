package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.model.ControllerModelLoader;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceRotation;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.stream.Stream;

public final class RCUBlockStateProvider extends ModelProvider
{
    private static final ResourceLocation CONTROLLER = Utils.rl("block/controller");
    private static final ResourceLocation CONVERTER_BASE = Utils.rl("block/converter");
    private static final TextureSlot DIR_OVERLAY = TextureSlot.create("dir_overlay");
    private static final TextureSlot OVERLAY = TextureSlot.create("overlay");
    private static final ModelTemplate CONVERTER = new ModelTemplate(Optional.of(CONVERTER_BASE), Optional.empty(), DIR_OVERLAY);

    public RCUBlockStateProvider(PackOutput output)
    {
        super(output, RedstoneControllerUnit.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
    {
        MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(
                RCUContent.BLOCK_CONTROLLER.value(),
                Variant.variant().with(VariantProperties.MODEL, CONTROLLER)
        );
        generator.with(PropertyDispatch.property(BlockStateProperties.FACING).generate(dir ->
        {
            VariantProperties.Rotation rotX = switch (dir)
            {
                case UP -> VariantProperties.Rotation.R180;
                case DOWN -> VariantProperties.Rotation.R0;
                default -> VariantProperties.Rotation.R90;
            };
            VariantProperties.Rotation rotY = VariantProperties.Rotation.R0;
            if (dir.getAxis() != Direction.Axis.Y)
            {
                rotY = VariantProperties.Rotation.values()[(int) dir.toYRot() / 90];
            }
            return Variant.variant().with(VariantProperties.X_ROT, rotX).with(VariantProperties.Y_ROT, rotY);
        }));
        blockModels.blockStateOutput.accept(generator);

        blockModels.registerSimpleItemModel(RCUContent.BLOCK_CONTROLLER.value(), CONTROLLER);

        makeConverterBlockStateAndItemModel(blockModels, RCUContent.BLOCK_ADC);
        makeConverterBlockStateAndItemModel(blockModels, RCUContent.BLOCK_DAC);

        for (int edge = 0; edge < 4; edge++)
        {
            plateOverlay(blockModels, ControllerModelLoader.LOCATIONS_SINGLE[edge], Utils.rl("block/overlay_single"), edge, true, true);
            plateOverlay(blockModels, ControllerModelLoader.LOCATIONS_BUNDLED[edge], Utils.rl("block/overlay_bundled"), edge, true, true);

            for (int port = 0; port < 4; port++)
            {
                plateOverlay(blockModels, ControllerModelLoader.LOCATIONS_PORT[edge][port], Utils.rl("block/port_" + port), edge, false, false);
            }
        }
    }

    private static void makeConverterBlockStateAndItemModel(BlockModelGenerators blockModels, Holder<Block> block)
    {
        ResourceLocation name = Utils.getKeyOrThrow(block).location();
        ResourceLocation baseLoc = name.withPrefix("block/");

        TextureMapping textures = TextureMapping.singleSlot(DIR_OVERLAY, name.withPrefix("block/dir_overlay_"));
        ResourceLocation converter = CONVERTER.create(baseLoc, textures, blockModels.modelOutput);

        ResourceLocation[] converters = new ResourceLocation[] {
                converter,
                makeConverterRotation(blockModels, converter, baseLoc.withSuffix("_cw90"), 90),
                makeConverterRotation(blockModels, converter, baseLoc.withSuffix("_cw180"), 180),
                makeConverterRotation(blockModels, converter, baseLoc.withSuffix("_ccw90"), -90)
        };

        MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block.value(), Variant.variant());
        generator.with(PropertyDispatch.property(PropertyHolder.FACING_DIR).generate(cmpDir ->
        {
            Direction dir = cmpDir.direction();
            VariantProperties.Rotation rotX = switch (dir)
            {
                case UP -> VariantProperties.Rotation.R180;
                case DOWN -> VariantProperties.Rotation.R0;
                default -> VariantProperties.Rotation.R90;
            };
            VariantProperties.Rotation rotY = VariantProperties.Rotation.R0;
            if (dir.getAxis() != Direction.Axis.Y)
            {
                rotY = VariantProperties.Rotation.values()[(int) dir.toYRot() / 90];
            }
            return Variant.variant()
                    .with(VariantProperties.MODEL, converters[cmpDir.rotation().ordinal()])
                    .with(VariantProperties.X_ROT, rotX)
                    .with(VariantProperties.Y_ROT, rotY);
        }));
        blockModels.blockStateOutput.accept(generator);

        blockModels.registerSimpleItemModel(block.value(), converter);
    }

    private static ResourceLocation makeConverterRotation(BlockModelGenerators blockModels, ResourceLocation converter, ResourceLocation name, int rot)
    {
        ModelTemplate template = ExtendedModelTemplateBuilder.builder()
                .parent(converter)
                .rootTransforms(xforms ->
                        xforms.origin(new Vector3f(.5F, 0, .5F))
                                .rotation(0, rot, 0, true)
                )
                .build();

        return template.create(name, new TextureMapping(), blockModels.modelOutput);
    }

    private static void plateOverlay(BlockModelGenerators blockModels, ResourceLocation name, ResourceLocation texture, int edge, boolean withSide, boolean mirrorTopX)
    {
        ExtendedModelTemplate template = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(OVERLAY)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .element(element ->
                {
                    element.from(0, 0, 0)
                            .to(16, 2, 16)
                            .face(Direction.UP, face ->
                                    face.uvs(0, mirrorTopX ? 16 : 0, 16, mirrorTopX ? 0 : 16)
                                            .rotation(FaceRotation.values()[edge])
                                            .texture(OVERLAY)
                            );

                    if (withSide)
                    {
                        Direction edgeDir = Direction.from2DDataValue(edge);
                        element.face(edgeDir, face ->
                                face.cullface(edgeDir)
                                        .uvs(0, 0, 16, 2)
                                        .texture(OVERLAY)
                        );
                    }
                })
                .build();

        TextureMapping textures = new TextureMapping().put(OVERLAY, texture).put(TextureSlot.PARTICLE, texture);
        template.create(name, textures, blockModels.modelOutput);
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems()
    {
        return super.getKnownItems().filter(item -> item.value() instanceof BlockItem);
    }

    @Override
    public String getName()
    {
        return "Block Models - RedstoneControllerUnit";
    }
}
