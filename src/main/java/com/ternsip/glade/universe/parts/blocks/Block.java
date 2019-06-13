package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Block {

    AIR(true),
    DIRT(false),
    STONE(false),
    LAWN(false),
    WOOD(false),
    LEAVES(true),
    WATER(true),
    LAVA(false),
    SAND(false);

    private final boolean semiTransparent;

    public static Block getBlockByIndex(int index) {
        if (index < 0 || index > getSize()) {
            throw new ArrayIndexOutOfBoundsException(String.format("Block index out of bounds: %s", index));
        }
        return values()[index];
    }

    public static int getSize() {
        return values().length;
    }

    public int getIndex() {
        return ordinal();
    }

    public String getName() {
        return name();
    }

}
