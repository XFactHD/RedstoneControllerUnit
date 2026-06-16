package io.github.xfacthd.rsctrlunit.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

public final class UnbakedControllerModel implements CustomUnbakedBlockStateModel {
    private static final String[] EDGE_SUFFIXES = new String[] { "n", "e", "s", "w" };
    public static final Identifier[] LOCATIONS_SINGLE = Utils.makeArray(new Identifier[4], edge ->
            Utils.rl("block/type_single_" + EDGE_SUFFIXES[edge])
    );
    public static final Identifier[] LOCATIONS_BUNDLED = Utils.makeArray(new Identifier[4], edge ->
            Utils.rl("block/type_bundled_" + EDGE_SUFFIXES[edge])
    );
    public static final Identifier[][] LOCATIONS_PORT = Utils.makeArray(new Identifier[4][4], edge ->
            Utils.makeArray(new Identifier[4], port -> Utils.rl("block/port_" + port + "_" + EDGE_SUFFIXES[edge]))
    );
    public static final MapCodec<UnbakedControllerModel> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("base").forGetter(model -> model.baseModel),
            Variant.SimpleModelState.MAP_CODEC.forGetter(model -> model.simpleModelState)
    ).apply(inst, UnbakedControllerModel::new));

    private final Identifier baseModel;
    private final Variant.SimpleModelState simpleModelState;
    private final ModelState modelState;

    UnbakedControllerModel(Identifier baseModel, Variant.SimpleModelState modelState) {
        this.baseModel = baseModel;
        this.simpleModelState = modelState;
        this.modelState = modelState.asModelState();
    }

    @Override
    public BlockStateModel bake(ModelBaker baker) {
        BlockStateModel[] singleModelsBaked = new BlockStateModel[4];
        BlockStateModel[] bundledModelsBaked = new BlockStateModel[4];
        BlockStateModel[][] portIndexModelsBaked = new BlockStateModel[4][4];

        for (int edge = 0; edge < 4; edge++) {
            singleModelsBaked[edge] = bakePart(baker, LOCATIONS_SINGLE[edge]);
            bundledModelsBaked[edge] = bakePart(baker, LOCATIONS_BUNDLED[edge]);
            for (int port = 0; port < 4; port++) {
                portIndexModelsBaked[edge][port] = bakePart(baker, LOCATIONS_PORT[edge][port]);
            }
        }

        return new ControllerModel(bakePart(baker, baseModel), singleModelsBaked, bundledModelsBaked, portIndexModelsBaked);
    }

    private BlockStateModel bakePart(ModelBaker baker, Identifier model) {
        return new SingleVariant(SimpleModelWrapper.bake(baker, model, modelState));
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver resolver) {
        resolver.markDependency(baseModel);
        for (int edge = 0; edge < 4; edge++) {
            resolver.markDependency(LOCATIONS_SINGLE[edge]);
            resolver.markDependency(LOCATIONS_BUNDLED[edge]);
            for (int port = 0; port < 4; port++) {
                resolver.markDependency(LOCATIONS_PORT[edge][port]);
            }
        }
    }

    @Override
    public MapCodec<UnbakedControllerModel> codec() {
        return CODEC;
    }
}
