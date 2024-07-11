package io.github.xfacthd.rsctrlunit.common.redstone.port;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.net.RCUByteBufCodecs;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record SinglePortConfig(int pin, boolean input) implements PortConfig
{
    public static final MapCodec<SinglePortConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.intRange(0, 7).fieldOf("pin").forGetter(SinglePortConfig::pin),
            Codec.BOOL.fieldOf("input").forGetter(SinglePortConfig::input)
    ).apply(inst, SinglePortConfig::new));
    public static final StreamCodec<ByteBuf, SinglePortConfig> STREAM_CODEC = StreamCodec.composite(
            RCUByteBufCodecs.intRange(0, 7),
            SinglePortConfig::pin,
            ByteBufCodecs.BOOL,
            SinglePortConfig::input,
            SinglePortConfig::new
    );

    @Override
    public int getRedstoneOutput(byte portState)
    {
        return input ? 0 : ((portState >> pin) & 0x01) * 15;
    }

    @Override
    public int getBundledOutput(byte portState, int channel)
    {
        return 0;
    }

    @Override
    public byte updateInput(Level level, BlockState state, Direction facing, BlockPos adjPos, Direction side)
    {
        if (input && level.hasSignal(adjPos, side.getOpposite()))
        {
            return (byte) (1 << pin);
        }
        return 0;
    }

    @Override
    public boolean hasInputs()
    {
        return input;
    }

    @Override
    public boolean hasOutputs()
    {
        return !input;
    }

    @Override
    public RedstoneType getType()
    {
        return RedstoneType.SINGLE;
    }
}
