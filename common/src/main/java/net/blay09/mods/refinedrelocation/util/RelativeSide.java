package net.blay09.mods.refinedrelocation.util;


import net.minecraft.core.Direction;

public enum RelativeSide {
    BACK,
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    FRONT;

    private static final RelativeSide[] values = values();

    public static RelativeSide fromIndex(int i) {
        return values[Math.abs(i % values.length)];
    }

    public Direction toFacing(Direction baseFacing) {
        switch (this) {
            case FRONT:
                return baseFacing;
            case BACK:
                return baseFacing.getOpposite();
            case LEFT:
                if (baseFacing.getAxis() == Direction.Axis.Y) {
                    return Direction.EAST;
                } else {
                    return baseFacing.getClockWise();
                }
            case RIGHT:
                if (baseFacing.getAxis() == Direction.Axis.Y) {
                    return Direction.WEST;
                } else {
                    return baseFacing.getCounterClockWise();
                }
            case TOP:
                if (baseFacing == Direction.UP) {
                    return Direction.SOUTH;
                } else if (baseFacing == Direction.DOWN) {
                    return Direction.NORTH;
                } else {
                    return Direction.UP;
                }
            case BOTTOM:
                if (baseFacing == Direction.UP) {
                    return Direction.NORTH;
                } else if (baseFacing == Direction.DOWN) {
                    return Direction.SOUTH;
                } else {
                    return Direction.DOWN;
                }
        }
        return null;
    }

}
