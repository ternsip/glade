package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.chunks.Chunk;

public interface ChunkGenerator {

    int getPriority();

    void populate(Chunk chunk);

}
