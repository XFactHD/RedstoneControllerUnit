package io.github.xfacthd.rsctrlunit.common.redstone.port;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class NonePortConfig implements PortConfig
{
    public static final NonePortConfig INSTANCE = new NonePortConfig();
    public static final MapCodec<NonePortConfig> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, NonePortConfig> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private NonePortConfig() { }

    @Override
    public int getRedstoneOutput(byte portState)
    {
        return 0;
    }

    @Override
    public int getBundledOutput(byte portState, int channel)
    {
        return 0;
    }

    @Override
    public byte updateInput(Level level, BlockState state, Direction facing, BlockPos adjPos, Direction side, byte portState)
    {
        return portState;
    }

    @Override
    public boolean hasInputs()
    {
        return false;
    }

    @Override
    public boolean hasOutputs()
    {
        return false;
    }

    @Override
    public RedstoneType getType()
    {
        return RedstoneType.NONE;
    }
}
