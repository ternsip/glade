package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Block {

    AIR(true, true),
    DIRT(false, false),
    STONE(false, false),
    LAWN(false, false),
    WOOD(false, false),
    LEAVES(true, false),
    WATER(true, true),
    LAVA(false, false),
    SAND(false, false);

    private final boolean semiTransparent;
    private final boolean combineSides;

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
