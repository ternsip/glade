package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.chunks.BlocksRepository;

public interface ChunkGenerator {

    int getPriority();

    void populate(BlocksRepository blocksRepository);

}
