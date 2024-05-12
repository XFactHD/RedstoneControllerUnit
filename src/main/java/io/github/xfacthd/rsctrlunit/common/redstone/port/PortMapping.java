package io.github.xfacthd.rsctrlunit.common.redstone.port;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;

public final class PortMapping
{
    private static final int[][] PORT_MATRIX = buildPortMatrix();
    private static final Direction[][] INV_PORT_MATRIX = buildInversePortMatrix();

    public static int getPortIndex(Direction facing, Direction side)
    {
        return PORT_MATRIX[facing.ordinal()][side.ordinal()];
    }

    public static Direction getPortSide(Direction facing, int port)
    {
        return INV_PORT_MATRIX[facing.ordinal()][port];
    }

    private static int[][] buildPortMatrix()
    {
        int[][] matrix = new int[6][6];
        for (Direction facing : Direction.values())
        {
            int[] inner = matrix[facing.ordinal()];
            for (Direction side : Direction.values())
            {
                inner[side.ordinal()] = switch (facing)
                {
                    case DOWN -> side.get2DDataValue();
                    case UP -> Mirror.LEFT_RIGHT.mirror(side).get2DDataValue();
                    default -> switch (side)
                    {
                        case UP -> Direction.SOUTH.get2DDataValue();
                        case DOWN -> Direction.NORTH.get2DDataValue();
                        default ->
                        {
                            if (side == facing.getCounterClockWise())
                            {
                                yield Direction.EAST.get2DDataValue();
                            }
                            if (side == facing.getClockWise())
                            {
                                yield Direction.WEST.get2DDataValue();
                            }
                            yield -1;
                        }
                    };
                };
            }
        }
        return matrix;
    }

    private static Direction[][] buildInversePortMatrix()
    {
        Direction[][] matrix = new Direction[6][4];
        for (Direction facing : Direction.values())
        {
            Direction[] inner = matrix[facing.ordinal()];
            for (Direction side : Direction.values())
            {
                if (side.getAxis() == facing.getAxis()) continue;

                int port = PORT_MATRIX[facing.ordinal()][side.ordinal()];
                inner[port] = side;
            }
        }
        return matrix;
    }

    private PortMapping() { }
}
