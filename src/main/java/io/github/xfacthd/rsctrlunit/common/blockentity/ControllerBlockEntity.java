package io.github.xfacthd.rsctrlunit.common.blockentity;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.Timers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.Interpreter;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ControllerBlockEntity extends BlockEntity
{
    public static final Component TITLE = Component.translatable("menu.rsctrlunit.controller");

    private final Interpreter interpreter = new Interpreter();
    private final Timers timers = interpreter.getTimers();
    private final RedstoneInterface redstone = new RedstoneInterface(this);

    public ControllerBlockEntity(BlockPos pos, BlockState state)
    {
        super(RCUContent.BE_TYPE_CONTROLLER.value(), pos, state);
        redstone.setFacing(state.getValue(BlockStateProperties.FACING));
    }

    public void tick()
    {
        timers.tickClock();
        interpreter.run();
        redstone.tick();
        setChangedWithoutSignalUpdate();
    }

    public void loadCode(@Nullable Code code)
    {
        interpreter.loadCode(Objects.requireNonNullElse(code, Code.EMPTY));
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
        level().blockEntityChanged(worldPosition);
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
        interpreter.load(tag.getCompound("interpreter"));
        redstone.load(tag.getCompound("redstone"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup)
    {
        super.saveAdditional(tag, lookup);
        tag.put("interpreter", interpreter.save());
        tag.put("redstone", redstone.save());
    }
}
