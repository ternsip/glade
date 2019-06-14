package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Block {

    AIR(true, true, 0),
    DIRT(false, false, 16),
    STONE(false, false, 16),
    LAWN(false, false, 16),
    WOOD(false, false, 16),
    LEAVES(true, false, 4),
    WATER(true, true, 1),
    LAVA(false, false, 1),
    SAND(false, false, 16);

    private final boolean semiTransparent;
    private final boolean combineSides;
    private final int lightOpacity;

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
