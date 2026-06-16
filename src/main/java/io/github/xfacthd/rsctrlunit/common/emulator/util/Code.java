package io.github.xfacthd.rsctrlunit.common.emulator.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.util.RCUCodecs;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public record Code(String name, byte[] rom, Int2ObjectMap<String> labels) implements TooltipProvider {
    public static final Codec<Code> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(Code::name),
            RCUCodecs.BYTE_ARRAY.fieldOf("rom").forGetter(Code::rom),
            RCUCodecs.int2ObjectMap(Codec.STRING).fieldOf("labels").forGetter(Code::labels)
    ).apply(inst, Code::new));
    public static final StreamCodec<ByteBuf, Code> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            Code::name,
            ByteBufCodecs.byteArray(Constants.ROM_SIZE),
            Code::rom,
            ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.VAR_INT, ByteBufCodecs.STRING_UTF8),
            Code::labels,
            Code::new
    );
    public static final Code EMPTY = new Code("", new byte[0], Int2ObjectMaps.emptyMap());
    public static final Component EMPTY_NAME = Component.translatable("rsctrlunit.code.name.empty").withStyle(ChatFormatting.ITALIC);

    public Component displayName() {
        return EMPTY.equals(this) ? EMPTY_NAME : Component.literal(name);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Code other) {
            return Arrays.equals(rom, other.rom) && Objects.equals(labels, other.labels);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(rom), labels);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Code[name=%s, rom=%s, labels=%s]", name, Utils.toHexString(rom), labels);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> adder, TooltipFlag flag, DataComponentGetter componentGetter) {
        adder.accept(Component.translatable("rsctrlunit.code.name", displayName()));
    }
}
