package io.github.xfacthd.rsctrlunit.common.item;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.List;

public final class MemoryCardItem extends Item
{
    public MemoryCardItem(Properties props)
    {
        super(props.component(RCUContent.COMPONENT_TYPE_CODE.value(), Code.EMPTY));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        stack.addToTooltip(RCUContent.COMPONENT_TYPE_CODE, ctx, lines::add, flag);
    }
}
