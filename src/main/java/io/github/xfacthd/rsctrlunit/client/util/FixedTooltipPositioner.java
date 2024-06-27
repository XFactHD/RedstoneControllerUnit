package io.github.xfacthd.rsctrlunit.client.util;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public final class FixedTooltipPositioner implements ClientTooltipPositioner
{
    public static final FixedTooltipPositioner INSTANCE = new FixedTooltipPositioner();

    private FixedTooltipPositioner() { }

    @Override
    public Vector2ic positionTooltip(int screenWidth, int screenHeight, int x, int y, int w, int h)
    {
        return new Vector2i(x, y);
    }
}