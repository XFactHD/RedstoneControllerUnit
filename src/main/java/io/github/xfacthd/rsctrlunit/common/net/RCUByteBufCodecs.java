package io.github.xfacthd.rsctrlunit.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.IntFunction;

public final class RCUByteBufCodecs
{
    public static <B extends ByteBuf, V> StreamCodec<B, V[]> array(StreamCodec<? super B, V> wrapped, IntFunction<V[]> arrayFactory, int maxSize)
    {
        return new StreamCodec<>()
        {
            @Override
            public void encode(B buf, V[] arr)
            {
                ByteBufCodecs.writeCount(buf, arr.length, maxSize);
                for (V v : arr)
                {
                    wrapped.encode(buf, v);
                }
            }

            @Override
            public V[] decode(B buf)
            {
                int size = ByteBufCodecs.readCount(buf, maxSize);
                V[] arr = arrayFactory.apply(size);
                for (int i = 0; i < size; i++)
                {
                    arr[i] = wrapped.decode(buf);
                }
                return arr;
            }
        };
    }

    public static <B extends FriendlyByteBuf> StreamCodec<B, int[]> intArray(int maxSize)
    {
        return new StreamCodec<>()
        {
            @Override
            public int[] decode(B buffer)
            {
                return buffer.readVarIntArray(maxSize);
            }

            @Override
            public void encode(B buffer, int[] value)
            {
                if (value.length > maxSize)
                {
                    throw new EncoderException("ByteArray with size " + value.length + " is bigger than allowed " + maxSize);
                }
                buffer.writeVarIntArray(value);
            }
        };
    }



    private RCUByteBufCodecs() { }
}
