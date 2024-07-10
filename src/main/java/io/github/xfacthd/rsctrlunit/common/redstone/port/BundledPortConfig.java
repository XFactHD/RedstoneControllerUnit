package io.github.xfacthd.rsctrlunit.common.redstone.port;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.util.property.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
            return output ? ((portState >> channel) & 0x01) * 15 : 0;
        }
        return 0;
    }

    @Override
    public byte updateInput(Level level, BlockState state, Direction facing, BlockPos adjPos, Direction side, byte portState)
    {
        BlockState adjState = level.getBlockState(adjPos);
        if (adjState.is(RCUContent.BLOCK_CONTROLLER) && level.getBlockEntity(adjPos) instanceof ControllerBlockEntity be)
        {
            Direction adjSide = side.getOpposite();
            int adjPort = PortMapping.getPortIndex(facing, adjSide);
            RedstoneTypeProperty adjProp = PropertyHolder.RS_CON_PROPS[adjPort];
            if (adjState.getValue(BlockStateProperties.FACING) == facing && adjState.getValue(adjProp) == RedstoneType.BUNDLED)
            {
                int newPortState = 0;
                int offset = upper ? 8 : 0;
                for (int i = 0; i < 8; i++)
                {
                    if (be.getBundledOutput(adjSide, i + offset) > 0)
                    {
                        newPortState |= 1 << i;
                    }
                }
                return (byte) newPortState;
            }
        }
        else
        {
            // TODO: implement More Red bundled wire integration
        }
        return portState;
    }

    @Override
    public boolean hasInputs()
    {
        return inputMask != 0;
    }

    @Override
    public boolean hasOutputs()
    {
        return ~inputMask != 0;
    }

    @Override
    public RedstoneType getType()
    {
        return RedstoneType.BUNDLED;
    }
}
