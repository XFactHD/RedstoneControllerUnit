package io.github.xfacthd.rsctrlunit.common.menu.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.BiFunction;

public final class SafeContainerLevelAccess implements ContainerLevelAccess
{
    private final Level level;
    private final BlockPos pos;

    public SafeContainerLevelAccess(Level level, BlockPos pos)
    {
        this.level = level;
        this.pos = pos;
    }

    @Override
    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> action)
    {
        return Optional.ofNullable(action.apply(level, pos));
    }
}
