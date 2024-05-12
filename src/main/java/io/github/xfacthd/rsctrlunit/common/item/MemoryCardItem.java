package io.github.xfacthd.rsctrlunit.common.item;

import io.github.xfacthd.rsctrlunit.common.RCUContent;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public final class MemoryCardItem extends Item
{
    public MemoryCardItem(Properties props)
    {
        super(props.component(RCUContent.COMPONENT_TYPE_CODE.value(), Code.EMPTY));
    }

    @Override // TODO: remove when programmer is done
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown())
        {
            Code code = stack.getOrDefault(RCUContent.COMPONENT_TYPE_CODE, Code.EMPTY);
            if (code.equals(Code.EMPTY))
            {
                stack.set(RCUContent.COMPONENT_TYPE_CODE, Constants.TEST_CODE);
            }
            else if (code.equals(Constants.TEST_CODE))
            {
                stack.set(RCUContent.COMPONENT_TYPE_CODE, Constants.TEST_CODE_TWO);
            }
            else
            {
                stack.set(RCUContent.COMPONENT_TYPE_CODE, Code.EMPTY);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        stack.addToTooltip(RCUContent.COMPONENT_TYPE_CODE, ctx, lines::add, flag);
    }
}
