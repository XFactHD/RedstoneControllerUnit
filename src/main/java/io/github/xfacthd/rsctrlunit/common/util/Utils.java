package io.github.xfacthd.rsctrlunit.common.util;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.*;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Utils
{
    private static final ResourceLocation RL_TEMPLATE = new ResourceLocation(RedstoneControllerUnit.MOD_ID, "");
    private static final Long2ObjectMap<Direction> DIRECTION_BY_NORMAL = Arrays.stream(Direction.values())
            .collect(Collectors.toMap(
                    side -> new BlockPos(side.getNormal()).asLong(),
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

    public static void copyByteArray(byte[] src, byte[] dest)
    {
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, dest.length));
    }

    public static <T> void copyArray(T[] src, T[] dest)
    {
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, dest.length));
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



    private Utils() { }
}
