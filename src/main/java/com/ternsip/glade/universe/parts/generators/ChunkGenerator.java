package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.storage.BlockStorage;

public interface ChunkGenerator {

    int getPriority();

    void populate(BlockStorage blockStorage);

}
