package io.github.xfacthd.rsctrlunit.common.util.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.xfacthd.rsctrlunit.common.net.RCUByteBufCodecs;
import io.github.xfacthd.rsctrlunit.common.redstone.port.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

public enum RedstoneType implements StringRepresentable
{
    NONE(NonePortConfig.MAP_CODEC, NonePortConfig.STREAM_CODEC),
    SINGLE(SinglePortConfig.MAP_CODEC, SinglePortConfig.STREAM_CODEC),
    BUNDLED(BundledPortConfig.MAP_CODEC, BundledPortConfig.STREAM_CODEC);

    public static final Codec<RedstoneType> CODEC = StringRepresentable.fromEnum(RedstoneType::values);
    public static final IntFunction<RedstoneType> BY_ID = ByIdMap.continuous(RedstoneType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, RedstoneType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RedstoneType::ordinal);

    public static final Codec<PortConfig> PORT_CODEC = CODEC.dispatch(PortConfig::getType, RedstoneType::getPortConfigCodec);
    public static final Codec<List<PortConfig>> PORT_LIST_CODEC = PORT_CODEC.listOf();
    public static final StreamCodec<ByteBuf, PortConfig> PORT_STREAM_CODEC = STREAM_CODEC.dispatch(PortConfig::getType, RedstoneType::getPortConfigStreamCodec);
    public static final StreamCodec<ByteBuf, PortConfig[]> PORT_ARRAY_STREAM_CODEC = RCUByteBufCodecs.array(PORT_STREAM_CODEC, PortConfig[]::new, 4);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Component translatedName = Component.translatable("desc.rsctrlunit.redstone_type." + name);
    private final MapCodec<? extends PortConfig> portConfigCodec;
    private final StreamCodec<ByteBuf, ? extends PortConfig> portConfigStreamCodec;

    RedstoneType(MapCodec<? extends PortConfig> portConfigCodec, StreamCodec<ByteBuf, ? extends PortConfig> portConfigStreamCodec)
    {
        this.portConfigCodec = portConfigCodec;
        this.portConfigStreamCodec = portConfigStreamCodec;
    }

    public MapCodec<? extends PortConfig> getPortConfigCodec()
    {
        return portConfigCodec;
    }

    public StreamCodec<ByteBuf, ? extends PortConfig> getPortConfigStreamCodec()
    {
        return portConfigStreamCodec;
    }

    public Component getTranslatedName()
    {
        return translatedName;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
