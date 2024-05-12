package io.github.xfacthd.rsctrlunit.common.util;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Map;

public final class RCUCodecs
{
    public static final Codec<byte[]> BYTE_ARRAY = Codec.BYTE.listOf().xmap(
            bytes -> new ByteArrayList(bytes).toByteArray(),
            ByteList::of
    );

    public static <V> Codec<Int2ObjectMap<V>> int2ObjectMap(Codec<V> valueCodec)
    {
        return Codec.unboundedMap(
                Codec.STRING.xmap(Integer::parseInt, i -> Integer.toString(i)),
                valueCodec
        ).xmap(Int2ObjectOpenHashMap::new, Map::copyOf);
    }



    private RCUCodecs() { }
}
