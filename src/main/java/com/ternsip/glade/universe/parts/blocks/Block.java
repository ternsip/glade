package com.ternsip.glade.universe.parts.blocks;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Block {

    AIR,
    DIRT,
    STONE,
    LAWN,
    WOOD,
    LEAVES,
    WATER,
    LAVA,
    SAND;

    public int getIndex() {
        return ordinal();
    }

    public String getName() {
        return name();
    }

    public static int getSize() {
        return values().length;
    }

    public static Block getBlockByIndex(int index) {
        if (index < 0 || index > getSize()) {
            throw new ArrayIndexOutOfBoundsException(String.format("Block index out of bounds: %s", index));
        }
        return values()[index];
    }

}
