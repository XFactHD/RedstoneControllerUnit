package io.github.xfacthd.rsctrlunit.common.datagen.provider;

import com.mojang.math.Quadrant;
import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import io.github.xfacthd.rsctrlunit.client.model.UnbakedControllerModel;
import io.github.xfacthd.rsctrlunit.client.model.UnbakedControllerModelBuilder;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.stream.Stream;

public final class RCUBlockStateProvider extends ModelProvider {
    private static final Identifier CONTROLLER = Utils.rl("block/controller");
    private static final Identifier CONVERTER_BASE = Utils.rl("block/converter");
    private static final TextureSlot DIR_OVERLAY = TextureSlot.create("dir_overlay");
    private static final TextureSlot OVERLAY = TextureSlot.create("overlay");
    private static final ModelTemplate CONVERTER = new ModelTemplate(Optional.of(CONVERTER_BASE), Optional.empty(), DIR_OVERLAY);

    public RCUBlockStateProvider(PackOutput output) {
        super(output, RedstoneControllerUnit.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        MultiVariantGenerator generator = MultiVariantGenerator.dispatch(
                RCUContent.BLOCK_CONTROLLER.value(),
                MultiVariant.of(new UnbakedControllerModelBuilder(CONTROLLER, Variant.SimpleModelState.DEFAULT))
        ).with(PropertyDispatch.modify(BlockStateProperties.FACING).generate(dir -> {
            Quadrant rotX = switch (dir) {
                case UP -> Quadrant.R180;
                case DOWN -> Quadrant.R0;
                default -> Quadrant.R90;
            };
            Quadrant rotY = Quadrant.R0;
            if (dir.getAxis() != Direction.Axis.Y) {
                rotY = Quadrant.values()[(int) dir.toYRot() / 90];
            }
            return VariantMutator.X_ROT.withValue(rotX).then(VariantMutator.Y_ROT.withValue(rotY));
        }));
        blockModels.blockStateOutput.accept(generator);

        blockModels.registerSimpleItemModel(RCUContent.BLOCK_CONTROLLER.value(), CONTROLLER);

        makeConverterBlockStateAndItemModel(blockModels, RCUContent.BLOCK_ADC);
        makeConverterBlockStateAndItemModel(blockModels, RCUContent.BLOCK_DAC);

        for (int edge = 0; edge < 4; edge++) {
            plateOverlay(blockModels, UnbakedControllerModel.LOCATIONS_SINGLE[edge], new Material(Utils.rl("block/overlay_single")), edge, true, true);
            plateOverlay(blockModels, UnbakedControllerModel.LOCATIONS_BUNDLED[edge], new Material(Utils.rl("block/overlay_bundled")), edge, true, true);

            for (int port = 0; port < 4; port++) {
                plateOverlay(blockModels, UnbakedControllerModel.LOCATIONS_PORT[edge][port], new Material(Utils.rl("block/port_" + port)), edge, false, false);
            }
        }
    }

    private static void makeConverterBlockStateAndItemModel(BlockModelGenerators blockModels, Holder<Block> block) {
        Identifier name = Utils.getKeyOrThrow(block).identifier();
        Identifier baseLoc = name.withPrefix("block/");

        TextureMapping textures = TextureMapping.singleSlot(DIR_OVERLAY, new Material(name.withPrefix("block/dir_overlay_")));
        Identifier converter = CONVERTER.create(baseLoc, textures, blockModels.modelOutput);

        Identifier[] converters = new Identifier[] {
                converter,
                makeConverterRotation(blockModels, converter, baseLoc.withSuffix("_cw90"), 90),
                makeConverterRotation(blockModels, converter, baseLoc.withSuffix("_cw180"), 180),
                makeConverterRotation(blockModels, converter, baseLoc.withSuffix("_ccw90"), -90)
        };

        MultiVariantGenerator generator = MultiVariantGenerator.dispatch(block.value())
                .with(PropertyDispatch.initial(PropertyHolder.FACING_DIR).generate(cmpDir -> {
                    Direction dir = cmpDir.direction();
                    Quadrant rotX = switch (dir) {
                        case UP -> Quadrant.R180;
                        case DOWN -> Quadrant.R0;
                        default -> Quadrant.R90;
                    };
                    Quadrant rotY = Quadrant.R0;
                    if (dir.getAxis() != Direction.Axis.Y) {
                        rotY = Quadrant.values()[(int) dir.toYRot() / 90];
                    }
                    return BlockModelGenerators.plainVariant(converters[cmpDir.rotation().ordinal()])
                            .with(VariantMutator.X_ROT.withValue(rotX))
                            .with(VariantMutator.Y_ROT.withValue(rotY));
                }));
        blockModels.blockStateOutput.accept(generator);

        blockModels.registerSimpleItemModel(block.value(), converter);
    }

    private static Identifier makeConverterRotation(BlockModelGenerators blockModels, Identifier converter, Identifier name, int rot) {
        ModelTemplate template = ExtendedModelTemplateBuilder.builder()
                .parent(converter)
                .rootTransforms(xforms ->
                        xforms.origin(new Vector3f(.5F, 0, .5F))
                                .rotation(0, rot, 0, true)
                )
                .build();

        return template.create(name, new TextureMapping(), blockModels.modelOutput);
    }

    private static void plateOverlay(BlockModelGenerators blockModels, Identifier name, Material texture, int edge, boolean withSide, boolean mirrorTopX) {
        ExtendedModelTemplate template = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(OVERLAY)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .element(element -> {
                    element.from(0, 0, 0)
                            .to(16, 2, 16)
                            .face(Direction.UP, face ->
                                    face.uvs(0, mirrorTopX ? 16 : 0, 16, mirrorTopX ? 0 : 16)
                                            .rotation(Quadrant.values()[edge])
                                            .texture(OVERLAY)
                            );

                    if (withSide) {
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
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(item -> item.value() instanceof BlockItem);
    }

    @Override
    public String getName() {
        return "Block Models - RedstoneControllerUnit";
    }
}
