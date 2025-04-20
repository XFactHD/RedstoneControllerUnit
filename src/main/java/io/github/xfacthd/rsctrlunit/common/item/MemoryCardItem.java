package io.github.xfacthd.rsctrlunit.common.item;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public final class MemoryCardItem extends Item
{
    public MemoryCardItem(Properties props)
    {
        super(props.component(RCUContent.COMPONENT_TYPE_CODE.value(), Code.EMPTY));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag)
    {
        stack.addToTooltip(RCUContent.COMPONENT_TYPE_CODE, ctx, tooltipAdder, flag);
    }
}
