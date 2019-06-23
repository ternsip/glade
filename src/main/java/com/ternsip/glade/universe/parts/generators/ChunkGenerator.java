package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;

public interface ChunkGenerator {

    int getPriority();

    void populate(Block[][][] blocks);

}
