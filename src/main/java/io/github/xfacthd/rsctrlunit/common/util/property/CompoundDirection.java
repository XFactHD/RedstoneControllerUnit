package io.github.xfacthd.rsctrlunit.common.util.property;

import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.Locale;

public enum CompoundDirection implements StringRepresentable
{
    DOWN_NORTH  (Direction.DOWN, Direction.NORTH, Rotation.NONE),
    DOWN_SOUTH  (Direction.DOWN, Direction.SOUTH, Rotation.CLOCKWISE_180),
    DOWN_WEST   (Direction.DOWN, Direction.WEST, Rotation.CLOCKWISE_90),
    DOWN_EAST   (Direction.DOWN, Direction.EAST, Rotation.COUNTERCLOCKWISE_90),

    UP_NORTH    (Direction.UP, Direction.NORTH, Rotation.CLOCKWISE_180),
    UP_SOUTH    (Direction.UP, Direction.SOUTH, Rotation.NONE),
    UP_WEST     (Direction.UP, Direction.WEST, Rotation.CLOCKWISE_90),
    UP_EAST     (Direction.UP, Direction.EAST, Rotation.COUNTERCLOCKWISE_90),

    NORTH_DOWN  (Direction.NORTH, Direction.DOWN, Rotation.NONE),
    NORTH_UP    (Direction.NORTH, Direction.UP, Rotation.CLOCKWISE_180),
    NORTH_WEST  (Direction.NORTH, Direction.WEST, Rotation.COUNTERCLOCKWISE_90),
    NORTH_EAST  (Direction.NORTH, Direction.EAST, Rotation.CLOCKWISE_90),

    SOUTH_DOWN  (Direction.SOUTH, Direction.DOWN, Rotation.NONE),
    SOUTH_UP    (Direction.SOUTH, Direction.UP, Rotation.CLOCKWISE_180),
    SOUTH_WEST  (Direction.SOUTH, Direction.WEST, Rotation.CLOCKWISE_90),
    SOUTH_EAST  (Direction.SOUTH, Direction.EAST, Rotation.COUNTERCLOCKWISE_90),

    WEST_DOWN   (Direction.WEST, Direction.DOWN, Rotation.NONE),
    WEST_UP     (Direction.WEST, Direction.UP, Rotation.CLOCKWISE_180),
    WEST_NORTH  (Direction.WEST, Direction.NORTH, Rotation.CLOCKWISE_90),
    WEST_SOUTH  (Direction.WEST, Direction.SOUTH, Rotation.COUNTERCLOCKWISE_90),

    EAST_DOWN   (Direction.EAST, Direction.DOWN, Rotation.NONE),
    EAST_UP     (Direction.EAST, Direction.UP, Rotation.CLOCKWISE_180),
    EAST_NORTH  (Direction.EAST, Direction.NORTH, Rotation.COUNTERCLOCKWISE_90),
    EAST_SOUTH  (Direction.EAST, Direction.SOUTH, Rotation.CLOCKWISE_90),
    ;

    private static final CompoundDirection[][] FROM_DIRS = makeDirTable();

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Direction direction;
    private final Direction orientation;
    private final Rotation rotation;

    CompoundDirection(Direction direction, Direction orientation, Rotation rotation)
    {
        this.direction = direction;
        this.orientation = orientation;
        this.rotation = rotation;
    }

    public Direction direction()
    {
        return direction;
    }

    public Direction orientation()
    {
        return orientation;
    }

    public Rotation rotation()
    {
        return rotation;
    }

    public CompoundDirection rotate(Rotation rot)
    {
        if (rot == Rotation.NONE)
        {
            return this;
        }

        if (Utils.isY(direction))
        {
            return of(direction, rot.rotate(orientation));
        }
        else
        {
            Direction newOrientation = orientation;
            if (orientation.getAxis() != Direction.Axis.Y)
            {
                newOrientation = rot.rotate(orientation);
            }
            return of(rot.rotate(direction), newOrientation);
        }
    }

    public CompoundDirection mirror(Mirror mirror)
    {
        return switch (mirror)
        {
            case NONE -> this;
            case FRONT_BACK -> Utils.isX(direction) ? of(direction.getOpposite(), orientation) : this;
            case LEFT_RIGHT -> Utils.isZ(direction) ? of(direction.getOpposite(), orientation) : this;
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }



    public static CompoundDirection of(Direction direction, Direction orientation)
    {
        CompoundDirection dirAxis = FROM_DIRS[direction.ordinal()][orientation.ordinal()];
        if (dirAxis == null)
        {
            throw new IllegalArgumentException(
                    "Invalid direction pair! Direction: " + direction + ", Orientation: " + orientation
            );
        }
        return dirAxis;
    }

    private static CompoundDirection[][] makeDirTable()
    {
        CompoundDirection[][] table = new CompoundDirection[6][6];
        for (CompoundDirection cmpDir : values())
        {
            Direction direction = cmpDir.direction;
            Direction orientation = cmpDir.orientation;
            table[direction.ordinal()][orientation.ordinal()] = cmpDir;
        }
        return table;
    }
}
