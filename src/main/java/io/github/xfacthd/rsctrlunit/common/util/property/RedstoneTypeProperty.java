package io.github.xfacthd.rsctrlunit.common.util.property;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Arrays;
import java.util.Collection;

public final class RedstoneTypeProperty extends EnumProperty<RedstoneType>
{
    private RedstoneTypeProperty(String name, Collection<RedstoneType> values)
    {
        super(name, RedstoneType.class, values);
    }

    public static RedstoneTypeProperty create(String name)
    {
        return new RedstoneTypeProperty(name, Arrays.asList(RedstoneType.values()));
    }
}
