package io.github.xfacthd.rsctrlunit.common.emulator.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.util.RCUCodecs;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public record Code(Component name, byte[] rom, Int2ObjectMap<String> labels) implements TooltipProvider
{
    public static final Codec<Code> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(Code::name),
            RCUCodecs.BYTE_ARRAY.fieldOf("rom").forGetter(Code::rom),
            RCUCodecs.int2ObjectMap(Codec.STRING).fieldOf("labels").forGetter(Code::labels)
    ).apply(inst, Code::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Code> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            Code::name,
            ByteBufCodecs.byteArray(Constants.ROM_SIZE),
            Code::rom,
            ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.VAR_INT, ByteBufCodecs.STRING_UTF8),
            Code::labels,
            Code::new
    );
    public static final Code EMPTY = new Code(
            Component.translatable("rsctrlunit.code.name.empty").withStyle(ChatFormatting.ITALIC),
            new byte[0],
            Int2ObjectMaps.emptyMap()
    );

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code = (Code) o;
        return Arrays.equals(rom, code.rom) && Objects.equals(labels, code.labels);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(Arrays.hashCode(rom), labels);
    }

    @Override
    public void addToTooltip(Item.TooltipContext ctx, Consumer<Component> adder, TooltipFlag flag)
    {
        adder.accept(Component.translatable("rsctrlunit.code.name", name));
    }
}
