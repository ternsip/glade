package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.chunks.BlocksServerRepository;

public interface ChunkGenerator {

    int getPriority();

    void populate(BlocksServerRepository blocksServerRepository, int startX, int startZ, int endX, int endZ);

}
