package io.github.xfacthd.rsctrlunit.client.model;

import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;

public final class UnbakedControllerModelBuilder extends CustomBlockStateModelBuilder
{
    private final Variant variant;

    public UnbakedControllerModelBuilder(Identifier baseModel, Variant.SimpleModelState modelState)
    {
        this(new Variant(baseModel, modelState));
    }

    private UnbakedControllerModelBuilder(Variant variant)
    {
        this.variant = variant;
    }

    @Override
    public CustomBlockStateModelBuilder with(VariantMutator variantMutator)
    {
        return new UnbakedControllerModelBuilder(variantMutator.apply(variant));
    }

    @Override
    public CustomBlockStateModelBuilder with(UnbakedMutator variantMutator)
    {
        return this;
    }

    @Override
    public CustomUnbakedBlockStateModel toUnbaked()
    {
        return new UnbakedControllerModel(variant.modelLocation(), variant.modelState());
    }
}
