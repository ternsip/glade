package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Blocks;
import lombok.Getter;

@Getter
public class AirGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void populate(Blocks blocks) {
        for (int x = 0; x < Blocks.SIZE_X; ++x) {
            for (int z = 0; z < Blocks.SIZE_Z; ++z) {
                for (int y = 0; y < Blocks.SIZE_Y; ++y) {
                    blocks.setBlockInternal(x, y, z, Block.AIR);
                }
            }
        }
    }

}
