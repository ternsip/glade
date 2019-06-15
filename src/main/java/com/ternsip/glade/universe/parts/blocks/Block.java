package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ternsip.glade.universe.parts.blocks.BlockMaterial.*;

@RequiredArgsConstructor
@Getter
public enum Block {

    AIR(true, true, 0, GAS),
    DIRT(false, false, 16, SOIL),
    STONE(false, false, 16, SOIL),
    LAWN(false, false, 16, SOIL),
    WOOD(false, false, 16, DECORATIVE),
    LEAVES(true, false, 4, DECORATIVE),
    WATER(true, true, 1, LIQUID),
    LAVA(false, false, 1, LIQUID),
    SAND(false, false, 16, SOIL);

    private final boolean semiTransparent;
    private final boolean combineSides;
    private final int lightOpacity;
    private final BlockMaterial blockMaterial;

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
