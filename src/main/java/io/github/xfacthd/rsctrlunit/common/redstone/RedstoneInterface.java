package io.github.xfacthd.rsctrlunit.common.redstone;

import com.mojang.datafixers.util.Pair;
import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.IOPorts;
import io.github.xfacthd.rsctrlunit.common.redstone.port.*;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public final class RedstoneInterface
{
    private final ControllerBlockEntity be;
    private final IOPorts ports;
    private final PortConfig[] portConfigs = new PortConfig[4];
    private final byte[] portStatesOut = new byte[4];
    private final byte[] portStatesIn = new byte[4];
    private Direction facing = Direction.DOWN;

    public RedstoneInterface(ControllerBlockEntity be)
    {
        this.be = be;
        this.ports = be.getInterpreter().getIoPorts();
        Arrays.fill(portConfigs, NonePortConfig.INSTANCE);
    }

    public void tick()
    {
        for (int port = 0; port < portStatesOut.length; port++)
        {
            if (portConfigs[port] == NonePortConfig.INSTANCE) continue;

            byte portState = ports.readOutputPort(port);
            if (portState != portStatesOut[port])
            {
                portStatesOut[port] = portState;
                updateNeighborOnSide(PortMapping.getPortSide(facing, port));
            }
        }
    }

    public void handleNeighborUpdate(BlockState state, BlockPos adjPos, Direction side)
    {
        int port = PortMapping.getPortIndex(facing, side);
        byte portState = portStatesIn[port];
        byte newState = portConfigs[port].updateInput(be.level(), state, facing, adjPos, side, portState);
        if (portState != newState)
        {
            portStatesIn[port] = newState;
            ports.writeInputPort(port, newState);
            be.setChangedWithoutSignalUpdate();
        }
    }

    public int getRedstoneOutput(Direction side)
    {
        int port = PortMapping.getPortIndex(facing, side);
        return port == -1 ? 0 : portConfigs[port].getRedstoneOutput(portStatesOut[port]);
    }

    public int getBundledOutput(Direction side, int channel)
    {
        int port = PortMapping.getPortIndex(facing, side);
        return port == -1 ? 0 : portConfigs[port].getBundledOutput(portStatesOut[port], channel);
    }

    public void setFacing(Direction facing)
    {
        this.facing = facing;
    }

    public void setPortConfig(int port, PortConfig config)
    {
        PortConfig oldConfig = portConfigs[port];
        portConfigs[port] = config;
        if (oldConfig.getType() != config.getType())
        {
            RedstoneTypeProperty prop = PropertyHolder.RS_CON_PROPS[port];
            be.level().setBlockAndUpdate(be.getBlockPos(), be.getBlockState().setValue(prop, config.getType()));
        }
        else
        {
            updateNeighborOnSide(PortMapping.getPortSide(facing, port));
        }
        be.setChangedWithoutSignalUpdate();
    }

    public Direction getFacing()
    {
        return facing;
    }

    public PortConfig[] getPortConfigs()
    {
        return portConfigs;
    }

    private void updateNeighborOnSide(Direction side)
    {
        be.level().neighborChanged(be.getBlockPos().relative(side), be.getBlockState().getBlock(), be.getBlockPos());
    }

    public void load(CompoundTag tag)
    {
        RedstoneType.PORT_LIST_CODEC.decode(NbtOps.INSTANCE, tag.get("config"))
                .result()
                .map(Pair::getFirst)
                .map(list -> list.toArray(PortConfig[]::new))
                .ifPresent(arr -> System.arraycopy(arr, 0, portConfigs, 0, Math.min(arr.length, portConfigs.length)));
        Utils.copyByteArray(tag.getByteArray("states_out"), portStatesOut);
        Utils.copyByteArray(tag.getByteArray("states_in"), portStatesIn);
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        List<PortConfig> list = Arrays.asList(portConfigs);
        tag.put("config", RedstoneType.PORT_LIST_CODEC.encodeStart(NbtOps.INSTANCE, list).result().orElseGet(CompoundTag::new));
        tag.putByteArray("states_out", Arrays.copyOf(portStatesOut, portStatesOut.length));
        tag.putByteArray("states_in", Arrays.copyOf(portStatesIn, portStatesIn.length));
        return tag;
    }
}
