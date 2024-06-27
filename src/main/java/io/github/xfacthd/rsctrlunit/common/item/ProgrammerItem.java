package io.github.xfacthd.rsctrlunit.common.item;

import io.github.xfacthd.rsctrlunit.common.blockentity.ControllerBlockEntity;
import io.github.xfacthd.rsctrlunit.common.menu.ProgrammerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ProgrammerItem extends Item
{
    public ProgrammerItem(Properties props)
    {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide())
        {
            openMenu(player, stack, null);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }



    public static void openMenu(Player player, ItemStack stack, @Nullable ControllerBlockEntity controller)
    {
        int slot = player.getInventory().selected;
        boolean forBlock = controller != null;
        player.openMenu(new MenuProvider()
        {
            @Override
            public Component getDisplayName()
            {
                return ProgrammerMenu.TITLE;
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
            {
                return new ProgrammerMenu(windowId, inventory, stack, slot, forBlock, controller);
            }
        }, buf ->
        {
            buf.writeVarInt(slot);
            buf.writeBoolean(forBlock);
        });
    }
}
