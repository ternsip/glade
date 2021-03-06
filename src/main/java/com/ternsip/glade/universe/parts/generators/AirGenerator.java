package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;
import lombok.Getter;

@Getter
public class AirGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void populate(BlocksRepository blocksRepository, int startX, int startZ, int endX, int endZ) {
        for (int x = startX; x <= endX; ++x) {
            for (int z = startZ; z <= endZ; ++z) {
                for (int y = 0; y < BlocksRepository.SIZE_Y; ++y) {
                    blocksRepository.setBlockInternal(x, y, z, Block.AIR);
                }
            }
        }
    }

}
