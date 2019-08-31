package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;

public class WaterGenerator implements ChunkGenerator {

    private final int waterHeight = 60;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        for (int x = 0; x < BlocksRepository.SIZE_X; ++x) {
            for (int z = 0; z < BlocksRepository.SIZE_Z; ++z) {
                for (int y = 0; y < BlocksRepository.SIZE_Y; ++y) {
                    if (y <= waterHeight && blocksRepository.getBlockInternal(x, y, z) == Block.AIR) {
                        blocksRepository.setBlockInternal(x, y, z, Block.WATER);
                    }
                }
            }
        }
    }
}
