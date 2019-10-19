package com.ternsip.glade.universe.parts.blocks;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum BlockSide {

    TOP("TOP", new Vector3i(0, 1, 0)),
    BOTTOM("BOTTOM", new Vector3i(0, -1, 0)),
    LEFT("WEST", new Vector3i(-1, 0, 0)),
    RIGHT("EAST", new Vector3i(1, 0, 0)),
    FRONT("NORTH", new Vector3i(0, 0, 1)),
    BACK("SOUTH", new Vector3i(0, 0, -1));

    private static final Map<BlockSide, BlockSide> OPPOSITES = ImmutableMap.<BlockSide, BlockSide>builder()
            .put(TOP, BOTTOM)
            .put(BOTTOM, TOP)
            .put(LEFT, RIGHT)
            .put(RIGHT, LEFT)
            .put(FRONT, BACK)
            .put(BACK, FRONT)
            .build();

    private final String logicalName;
    private final Vector3ic adjacentBlockOffset;

    public static BlockSide getSideByIndex(int index) {
        return values()[index];
    }

    public static int getSize() {
        return values().length;
    }

    public int getIndex() {
        return ordinal();
    }

    public BlockSide getOpposite() {
        return OPPOSITES.get(this);
    }

}
