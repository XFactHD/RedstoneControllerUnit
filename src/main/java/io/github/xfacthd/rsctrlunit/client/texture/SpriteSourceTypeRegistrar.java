package io.github.xfacthd.rsctrlunit.client.texture;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface SpriteSourceTypeRegistrar
{
    SpriteSourceType register(ResourceLocation id, MapCodec<? extends SpriteSource> codec);
}
