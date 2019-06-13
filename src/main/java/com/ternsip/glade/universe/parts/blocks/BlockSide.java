package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public enum BlockSide {

    TOP("TOP", new Vector3i(0, 1, 0)),
    BOTTOM("BOTTOM", new Vector3i(0, -1, 0)),
    LEFT("WEST", new Vector3i(-1, 0, 0)),
    RIGHT("EAST", new Vector3i(1, 0, 0)),
    FRONT("NORTH", new Vector3i(0, 0, 1)),
    BACK("SOUTH", new Vector3i(0, 0, -1));

    private final String logicalName;
    private final Vector3ic adjacentBlockOffset;

}
