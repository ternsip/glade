package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.chunks.Blocks;

public interface ChunkGenerator {

    int getPriority();

    void populate(Blocks blocks);

}
