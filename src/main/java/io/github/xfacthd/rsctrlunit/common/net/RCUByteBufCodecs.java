package io.github.xfacthd.rsctrlunit.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
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
                    throw new EncoderException("IntArray with size " + value.length + " is bigger than allowed " + maxSize);
                }
                buffer.writeVarIntArray(value);
            }
        };
    }

    public static <B extends ByteBuf> StreamCodec<B, Integer> intRange(int minInclusive, int maxInclusive)
    {
        return new StreamCodec<>()
        {
            @Override
            public Integer decode(B buffer)
            {
                int value = VarInt.read(buffer);
                if (value < minInclusive || value > maxInclusive)
                {
                    throw new DecoderException("Value " + value + "outside of range [" + minInclusive + "," + maxInclusive + "]");
                }
                return value;
            }

            @Override
            public void encode(B buffer, Integer value)
            {
                if (value < minInclusive || value > maxInclusive)
                {
                    throw new EncoderException("Value " + value + "outside of range [" + minInclusive + "," + maxInclusive + "]");
                }
                VarInt.write(buffer, value);
            }
        };
    }



    private RCUByteBufCodecs() { }
}
