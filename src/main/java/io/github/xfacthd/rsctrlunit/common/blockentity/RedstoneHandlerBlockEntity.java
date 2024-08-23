package io.github.xfacthd.rsctrlunit.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

abstract class RedstoneHandlerBlockEntity extends BlockEntity implements RedstoneHandler
{
    // Keep around the chunk holding this BE to avoid having to look it up every tick to mark it as unsaved
    @Nullable
    private LevelChunk owningChunk = null;

    protected RedstoneHandlerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public final void setChangedWithoutSignalUpdate()
    {
        if (owningChunk != null)
        {
            owningChunk.setUnsaved(true);
        }
    }

    public final Level level()
    {
        return Objects.requireNonNull(level);
    }

    @Override
    public void clearRemoved()
    {
        super.clearRemoved();
        owningChunk = level().getChunkAt(worldPosition);
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        owningChunk = null;
    }
}
