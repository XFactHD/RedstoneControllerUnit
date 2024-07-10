package io.github.xfacthd.rsctrlunit.common.blockentity;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.*;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ControllerBlockEntity extends BlockEntity
{
    public static final Component TITLE = Component.translatable("menu.rsctrlunit.controller");
    public static final ModelProperty<int[]> PORT_MAPPING_PROPERTY = new ModelProperty<>();

    private final Interpreter interpreter = new Interpreter();
    private final Timers timers = interpreter.getTimers();
    private final RedstoneInterface redstone = new RedstoneInterface(this);
    // Keep around the chunk holding this BE to avoid having to look it up every tick to mark it as unsaved
    @Nullable
    private LevelChunk owningChunk = null;

    public ControllerBlockEntity(BlockPos pos, BlockState state)
    {
        super(RCUContent.BE_TYPE_CONTROLLER.value(), pos, state);
        redstone.setFacing(state.getValue(BlockStateProperties.FACING));
    }

    public void tick()
    {
        timers.tickClock();
        redstone.tick();
        setChangedWithoutSignalUpdate();
    }

    public void loadCode(@Nullable Code code)
    {
        code = Objects.requireNonNullElse(code, Code.EMPTY);
        interpreter.writeLockGuarded(code, Interpreter::loadCode);
        setChangedWithoutSignalUpdate();
    }

    public Interpreter getInterpreter()
    {
        return interpreter;
    }

    public RedstoneInterface getRedstoneInterface()
    {
        return redstone;
    }

    public int getRedstoneOutput(Direction side)
    {
        return redstone.getRedstoneOutput(side);
    }

    public int getBundledOutput(Direction side, int channel)
    {
        return redstone.getBundledOutput(side, channel);
    }

    public void handleNeighborUpdate(BlockPos adjPos, Direction side)
    {
        redstone.handleNeighborUpdate(getBlockState(), adjPos, side);
    }

    public Level level()
    {
        return Objects.requireNonNull(level);
    }

    public void setChangedWithoutSignalUpdate()
    {
        if (owningChunk != null)
        {
            owningChunk.setUnsaved(true);
        }
    }

    public void markForSyncAndSave()
    {
        level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        setChangedWithoutSignalUpdate();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag tag = new CompoundTag();
        tag.put("redstone", redstone.writeToNetwork());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries)
    {
        redstone.readFromNetwork(tag.getCompound("redstone"));
        requestModelDataUpdate();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries)
    {
        CompoundTag tag = pkt.getTag();
        if (!tag.isEmpty() && redstone.readFromNetwork(tag.getCompound("redstone")))
        {
            requestModelDataUpdate();
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public ModelData getModelData()
    {
        return ModelData.builder().with(PORT_MAPPING_PROPERTY, redstone.getPortMapping().clone()).build();
    }

    @Override
    public void clearRemoved()
    {
        super.clearRemoved();
        owningChunk = level().getChunkAt(worldPosition);
        if (!level().isClientSide())
        {
            interpreter.startup();
            InterpreterThreadPool.addInterpreter(interpreter);
        }
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        owningChunk = null;
        if (!level().isClientSide())
        {
            interpreter.shutdown();
            InterpreterThreadPool.removeInterpreter(interpreter);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state)
    {
        super.setBlockState(state);
        redstone.setFacing(state.getValue(BlockStateProperties.FACING));
        setChangedWithoutSignalUpdate();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup)
    {
        super.loadAdditional(tag, lookup);
        interpreter.writeLockGuarded(tag.getCompound("interpreter"), Interpreter::load);
        redstone.load(tag.getCompound("redstone"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup)
    {
        super.saveAdditional(tag, lookup);
        tag.put("interpreter", interpreter.readLockGuarded(Interpreter::save));
        tag.put("redstone", redstone.save());
    }
}
