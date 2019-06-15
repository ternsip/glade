package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ternsip.glade.universe.parts.blocks.BlockMaterial.*;
import static com.ternsip.glade.universe.parts.chunks.Chunks.MAX_LIGHT_LEVEL;

@RequiredArgsConstructor
@Getter
public enum Block {

    AIR(true, true, true, 1, 0, GAS),
    DIRT(false, false, false, MAX_LIGHT_LEVEL, 0, SOIL),
    STONE(false, false, false, MAX_LIGHT_LEVEL, 0, SOIL),
    LAWN(false, false, false, MAX_LIGHT_LEVEL, 0, SOIL),
    WOOD(false, false, false, MAX_LIGHT_LEVEL, 0, DECORATIVE),
    LEAVES(false, true, false, 4, 0, DECORATIVE),
    WATER(true, true, true, 1, 0, LIQUID),
    LAVA(true, false, false, 1, 4, LIQUID),
    SAND(false, false, false, MAX_LIGHT_LEVEL, 0, SOIL);

    private final boolean translucent;
    private final boolean semiTransparent;
    private final boolean combineSides;
    private final int lightOpacity;
    private final int emitLight;
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
