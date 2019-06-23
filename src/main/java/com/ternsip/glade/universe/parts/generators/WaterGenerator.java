package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Blocks;
import com.ternsip.glade.universe.storage.BlockStorage;

public class WaterGenerator implements ChunkGenerator {

    private final int waterHeight = 60;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void populate(BlockStorage blockStorage) {
        for (int x = 0; x < Blocks.SIZE_X; ++x) {
            for (int y = 0; y < Blocks.SIZE_Y; ++y) {
                for (int z = 0; z < Blocks.SIZE_Z; ++z) {
                    if (y <= waterHeight && blockStorage.getBlock(x, y, z) == Block.AIR) {
                        blockStorage.setBlock(x, y, z, Block.WATER);
                    }
                }
            }
        }
    }
}
