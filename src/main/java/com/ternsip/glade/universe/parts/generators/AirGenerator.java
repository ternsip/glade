package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;

@Getter
public class AirGenerator implements ChunkGenerator {


    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void populate(Block[][][] blocks) {
        for (int x = 0; x < blocks.length; ++x) {
            for (int y = 0; y < blocks[x].length; ++y) {
                for (int z = 0; z < blocks[x][y].length; ++z) {
                    blocks[x][y][z] = Block.AIR;
                }
            }
        }
    }

}
