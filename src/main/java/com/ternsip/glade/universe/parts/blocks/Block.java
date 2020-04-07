package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ternsip.glade.graphics.visual.impl.basis.EffigyLightMass.MAX_LIGHT_LEVEL;
import static com.ternsip.glade.universe.parts.blocks.BlockMaterial.*;

@RequiredArgsConstructor
@Getter
public enum Block {

    AIR(false, true, true, true, (byte) 1, (byte) 0, GAS),
    DIRT(true, false, false, false, MAX_LIGHT_LEVEL, (byte) 0, SOIL),
    STONE(true, false, false, false, MAX_LIGHT_LEVEL, (byte) 0, SOIL),
    LAWN(true, false, false, false, MAX_LIGHT_LEVEL, (byte) 0, SOIL),
    WOOD(true, false, false, false, MAX_LIGHT_LEVEL, (byte) 0, DECORATIVE),
    LEAVES(true, false, true, false, (byte) 4, (byte) 0, DECORATIVE),
    WATER(false, true, true, true, (byte) 1, (byte) 0, LIQUID),
    LAVA(false, true, false, false, MAX_LIGHT_LEVEL, (byte) 12, LIQUID),
    SAND(true, false, false, false, MAX_LIGHT_LEVEL, (byte) 0, SOIL);

    private final boolean obstacle;
    private final boolean translucent;
    private final boolean semiTransparent;
    private final boolean combineSides;
    private final byte lightOpacity;
    private final byte emitLight;
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
