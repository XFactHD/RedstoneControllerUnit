package io.github.xfacthd.rsctrlunit.client.screen.widget;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final class TabGroup
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final IntConsumer tabListener;
    private final List<TabButton> buttons = new ArrayList<>();
    private boolean built = false;

    public TabGroup(int x, int y, int width, int height, IntConsumer tabListener)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tabListener = tabListener;
    }

    public TabGroup addButton(Component title)
    {
        Preconditions.checkState(!built, "TabGroup already built!");

        int idx = buttons.size();
        buttons.add(new TabButton(x, y, width, height, title, btn -> tabPressed(idx)));

        return this;
    }

    public void build(Consumer<TabButton> registrar, int selectedTab)
    {
        if (built) return;
        built = true;

        int btnWidth = width / buttons.size();
        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).setRectangle(btnWidth, height, x + btnWidth * i, y);
        }

        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).setSelected(i == selectedTab);
        }
        buttons.getFirst().setPos(TabButton.Position.LEFT);
        buttons.getLast().setPos(TabButton.Position.RIGHT);

        buttons.forEach(registrar);
    }

    private void tabPressed(int idx)
    {
        tabListener.accept(idx);
        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).setSelected(i == idx);
        }
    }
}
