package io.github.xfacthd.rsctrlunit.common.redstone;

import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.IOPorts;
import io.github.xfacthd.rsctrlunit.common.net.RCUByteBufCodecs;
import io.github.xfacthd.rsctrlunit.common.redstone.port.NonePortConfig;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortConfig;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortMapping;
import io.github.xfacthd.rsctrlunit.common.util.RCUCodecs;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.PropertyHolder;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class RedstoneInterface {
    public static final StreamCodec<FriendlyByteBuf, int[]> PORT_MAPPING_STREAM_CODEC = RCUByteBufCodecs.intArray(4);

    private final ControllerBlockEntity be;
    private final IOPorts ports;
    private final PortConfig[] portConfigs = new PortConfig[4];
    private final byte[] portStatesOut = new byte[4];
    private final byte[] portStatesIn = new byte[4];
    // Internal to external port
    private final int[] portMapping = new int[] { 0, 1, 2, 3 };
    // External to internal port
    private final int[] invPortMapping = new int[] { 0, 1, 2, 3 };
    private Direction facing = Direction.DOWN;

    public RedstoneInterface(ControllerBlockEntity be) {
        this.be = be;
        this.ports = be.getInterpreter().getIoPorts();
        Arrays.fill(portConfigs, NonePortConfig.INSTANCE);
    }

    public void tick() {
        for (int port = 0; port < portStatesOut.length; port++) {
            if (portConfigs[port].hasOutputs()) {
                updateOutputOnSide(port, false);
            }
        }
    }

    public void handleNeighborUpdate(BlockState state, BlockPos adjPos, Direction side) {
        updateInputOnSide(state, adjPos, side, false);
    }

    private void updateInputOnSide(BlockState state, BlockPos adjPos, Direction side, boolean force) {
        int extPort = PortMapping.getPortIndex(facing, side);
        if (extPort == -1) {
            return;
        }

        int port = invPortMapping[extPort];
        PortConfig config = portConfigs[port];
        if (!config.hasInputs() && !force) {
            return;
        }

        byte portState = portStatesIn[port];
        byte newState = config.updateInput(be.level(), state, be.getBlockPos(), facing, adjPos, side);
        if (portState != newState) {
            portStatesIn[port] = newState;
            ports.writeInputPort(port, newState);
            be.setChangedWithoutSignalUpdate();
        }
    }

    private void updateOutputOnSide(int port, boolean force) {
        byte portState = ports.readOutputPort(port);
        if (portState != portStatesOut[port] || force) {
            portStatesOut[port] = portState;
            int extPort = portMapping[port];
            RedstoneType type = portConfigs[port].getType();
            updateNeighborOnSide(PortMapping.getPortSide(facing, extPort), type == RedstoneType.BUNDLED);
        }
    }

    public int getRedstoneOutput(Direction side) {
        int extPort = PortMapping.getPortIndex(facing, side);
        if (extPort != -1) {
            int port = invPortMapping[extPort];
            return portConfigs[port].getRedstoneOutput(portStatesOut[port]);
        }
        return 0;
    }

    public int getBundledOutput(Direction side, int channel) {
        int extPort = PortMapping.getPortIndex(facing, side);
        if (extPort != -1) {
            int port = invPortMapping[extPort];
            return portConfigs[port].getBundledOutput(portStatesOut[port], channel);
        }
        return 0;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public void setPortConfig(int port, PortConfig config) {
        PortConfig oldConfig = portConfigs[port];
        portConfigs[port] = config;
        int extPort = portMapping[port];
        if (oldConfig.getType() != config.getType()) {
            EnumProperty<RedstoneType> prop = PropertyHolder.RS_CON_PROPS[extPort];
            be.level().setBlockAndUpdate(be.getBlockPos(), be.getBlockState().setValue(prop, config.getType()));
        } else {
            updateNeighborOnSide(PortMapping.getPortSide(facing, extPort), config.getType() == RedstoneType.BUNDLED);
        }
        Direction side = PortMapping.getPortSide(facing, extPort);
        updateInputOnSide(be.getBlockState(), be.getBlockPos().relative(side), side, true);
        updateOutputOnSide(port, true);
        be.setChangedWithoutSignalUpdate();
    }

    public void setPortMapping(int[] mapping) {
        Utils.copyIntArray(mapping, portMapping);
        setupInvPortMapping();
        be.markForSyncAndSave();

        BlockState state = be.getBlockState();
        for (int port = 0; port < 4; port++) {
            PortConfig config = portConfigs[port];
            int extPort = portMapping[port];
            state = state.setValue(PropertyHolder.RS_CON_PROPS[extPort], config.getType());
            Direction side = PortMapping.getPortSide(facing, extPort);
            updateInputOnSide(state, be.getBlockPos().relative(side), side, true);
            updateOutputOnSide(port, true);
        }
        be.level().setBlockAndUpdate(be.getBlockPos(), state);
    }

    public Direction getFacing() {
        return facing;
    }

    public PortConfig[] getPortConfigs() {
        return portConfigs;
    }

    public int[] getPortMapping() {
        return portMapping;
    }

    private void updateNeighborOnSide(Direction side, boolean bundled) {
        BlockPos adjPos = be.getBlockPos().relative(side);
        if (bundled) {
            be.level().getBlockState(adjPos).onNeighborChange(be.level(), adjPos, be.getBlockPos());
        } else {
            // FIXME: the whole Orientation thing makes zero sense...
            be.level().neighborChanged(adjPos, be.getBlockState().getBlock(), null);
        }
    }

    public BlockState updateStateFromConfigs(BlockState state) {
        for (int port = 0; port < 4; port++) {
            PortConfig config = portConfigs[port];
            int extPort = portMapping[port];
            state = state.setValue(PropertyHolder.RS_CON_PROPS[extPort], config.getType());
        }
        return state;
    }

    public boolean readFromNetwork(ValueInput valueInput) {
        Optional<int[]> mapping = valueInput.getIntArray("mapping");
        if (mapping.isPresent() && !Arrays.equals(mapping.get(), portMapping)) {
            Utils.copyIntArray(mapping.get(), portMapping);
            setupInvPortMapping();
            return true;
        }
        return false;
    }

    public CompoundTag writeToNetwork() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("mapping", Arrays.copyOf(portMapping, portMapping.length));
        return tag;
    }

    public void load(ValueInput valueInput) {
        List<PortConfig> configs = valueInput.read("config", RedstoneType.PORT_LIST_CODEC).orElse(List.of());
        Utils.copyArray(configs.toArray(PortConfig[]::new), portConfigs);
        Utils.copyByteArray(valueInput.read("states_out", RCUCodecs.BYTE_ARRAY), portStatesOut);
        Utils.copyByteArray(valueInput.read("states_in", RCUCodecs.BYTE_ARRAY), portStatesIn);
        Utils.copyIntArray(valueInput.getIntArray("mapping"), portMapping);
        setupInvPortMapping();
    }

    public void save(ValueOutput valueOutput) {
        valueOutput.store("config", RedstoneType.PORT_LIST_CODEC, Arrays.asList(portConfigs));
        valueOutput.store("states_out", RCUCodecs.BYTE_ARRAY, Arrays.copyOf(portStatesOut, portStatesOut.length));
        valueOutput.store("states_in", RCUCodecs.BYTE_ARRAY, Arrays.copyOf(portStatesIn, portStatesIn.length));
        valueOutput.putIntArray("mapping", Arrays.copyOf(portMapping, portMapping.length));
    }

    private void setupInvPortMapping() {
        for (int i = 0; i < 4; i++) {
            int mapping = portMapping[i];
            invPortMapping[mapping] = i;
        }
    }

    public static boolean validatePortMapping(int[] mapping) {
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (mapping[i] == mapping[j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
