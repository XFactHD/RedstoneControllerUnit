package io.github.xfacthd.rsctrlunit.common.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.Vec3;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.*;
import java.util.stream.Collectors;

public final class Utils
{
    private static final ResourceLocation RL_TEMPLATE = ResourceLocation.fromNamespaceAndPath(RedstoneControllerUnit.MOD_ID, "");
    private static final Long2ObjectMap<Direction> DIRECTION_BY_NORMAL = Arrays.stream(Direction.values())
            .collect(Collectors.toMap(
                    side -> new BlockPos(side.getUnitVec3i()).asLong(),
                    Function.identity(),
                    (sideA, sideB) -> { throw new IllegalArgumentException("Duplicate keys"); },
                    Long2ObjectOpenHashMap::new
            ));
    public static final Component[] DIRECTION_NAMES = Util.make(new Component[Direction.values().length], arr ->
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = Component.translatable("desc.rsctrlunit.direction." + Direction.from3DDataValue(i).getName());
        }
    });
    public static final Component[] COLOR_NAMES = Util.make(new Component[DyeColor.values().length], arr ->
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = Component.translatable("desc.rsctrlunit.color." + DyeColor.byId(i).getName());
        }
    });

    public static ResourceLocation rl(String path)
    {
        return RL_TEMPLATE.withPath(path);
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> payloadType(String path)
    {
        return new CustomPacketPayload.Type<>(Utils.rl(path));
    }

    public static Direction getDirection(BlockPos srcPos, BlockPos destPos)
    {
        return dirByNormal(destPos.getX() - srcPos.getX(), destPos.getY() - srcPos.getY(), destPos.getZ() - srcPos.getZ());
    }

    public static Direction dirByNormal(int x, int y, int z)
    {
        return DIRECTION_BY_NORMAL.get(BlockPos.asLong(x, y, z));
    }

    public static boolean isX(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.X;
    }

    public static boolean isY(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isZ(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Z;
    }

    public static Direction getDirFromCross(Vec3 hitVec, Direction hitFace)
    {
        hitVec = fraction(hitVec).subtract(.5, .5, .5);

        return switch (hitFace.getAxis())
        {
            case X -> Direction.getApproximateNearest(0, hitVec.y, hitVec.z);
            case Y -> Direction.getApproximateNearest(hitVec.x, 0, hitVec.z);
            case Z -> Direction.getApproximateNearest(hitVec.x, hitVec.y, 0);
        };
    }

    public static Vec3 fraction(Vec3 vec)
    {
        return new Vec3(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    public static void copyByteArray(byte[] src, byte[] dest)
    {
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, dest.length));
    }

    public static void copyIntArray(int[] src, int[] dest)
    {
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, dest.length));
    }

    public static <T> void copyArray(T[] src, T[] dest)
    {
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, dest.length));
    }

    public static <T, A extends T, B extends T> T[] appendArray(A[] arr, B toAdd, Class<T[]> type)
    {
        T[] newArr = Arrays.copyOf(arr, arr.length + 1, type);
        newArr[newArr.length - 1] = toAdd;
        return newArr;
    }

    public static <T> T[] makeArray(T[] arr, IntFunction<T> initializer)
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = initializer.apply(i);
        }
        return arr;
    }

    public static String toHexString(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < bytes.length; i++)
        {
            if (i > 0)
            {
                builder.append(", ");
            }
            int value = bytes[i] & 0xFF;
            builder.append(String.format(Locale.ROOT, "0x%02X", value));
        }
        return builder.append("]").toString();
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>)ticker : null;
    }

    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, Consumer<E> ticker
    )
    {
        return createBlockEntityTicker(type, actualType, (level, pos, state, be) -> ticker.accept(be));
    }

    public static String getFileNameNoExt(Path path)
    {
        String fileName = path.getFileName().toString();
        int period = fileName.lastIndexOf('.');
        if (period > -1)
        {
            fileName = fileName.substring(0, period);
        }
        return fileName;
    }

    public static <T> T fromNbt(Codec<T> codec, Tag tag, T defaultValue)
    {
        return codec.decode(NbtOps.INSTANCE, tag).result().map(Pair::getFirst).orElse(defaultValue);
    }

    public static <T> Tag toNbt(Codec<T> codec, T value)
    {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().orElseGet(CompoundTag::new);
    }

    public static <T> ResourceKey<T> getKeyOrThrow(Holder<T> holder)
    {
        return holder.unwrapKey().orElseThrow(
                () -> new IllegalArgumentException("Direct holders and unbound reference holders are not supported")
        );
    }



    private Utils() { }
}
