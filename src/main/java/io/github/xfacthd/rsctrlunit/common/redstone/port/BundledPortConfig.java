package io.github.xfacthd.rsctrlunit.common.redstone.port;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.redstone.BundledConnectionHelper;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record BundledPortConfig(boolean upper, byte inputMask) implements PortConfig
{
    public static final MapCodec<BundledPortConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.BOOL.fieldOf("upper").forGetter(BundledPortConfig::upper),
            Codec.BYTE.fieldOf("input_mask").forGetter(BundledPortConfig::inputMask)
    ).apply(inst, BundledPortConfig::new));
    public static final StreamCodec<ByteBuf, BundledPortConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            BundledPortConfig::upper,
            ByteBufCodecs.BYTE,
            BundledPortConfig::inputMask,
            BundledPortConfig::new
    );

    @Override
    public int getRedstoneOutput(byte portState)
    {
        return 0;
    }

    @Override
    public int getBundledOutput(byte portState, int channel)
    {
        if (upper)
        {
            channel -= 8;
        }
        if (channel >= 0 && channel < 8)
        {
            boolean output = (inputMask & (1 << channel)) == 0;
            return output ? ((portState >> channel) & 0x01) : 0;
        }
        return 0;
    }

    @Override
    public byte updateInput(Level level, BlockState state, BlockPos pos, Direction facing, BlockPos adjPos, Direction side)
    {
        return (byte) BundledConnectionHelper.readBundledInput(level, state, pos, facing, adjPos, side, upper ? 8 : 0, 8, inputMask);
    }

    @Override
    public boolean hasInputs()
    {
        return inputMask != 0;
    }

    @Override
    public boolean hasOutputs()
    {
        return (~inputMask & 0xFF) != 0;
    }

    @Override
    public RedstoneType getType()
    {
        return RedstoneType.BUNDLED;
    }
}
