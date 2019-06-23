package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.storage.BlockStorage;
import lombok.Getter;

import static com.ternsip.glade.universe.parts.chunks.Blocks.SIZE_X;
import static com.ternsip.glade.universe.parts.chunks.Blocks.SIZE_Y;
import static com.ternsip.glade.universe.parts.chunks.Blocks.SIZE_Z;

@Getter
public class AirGenerator implements ChunkGenerator {


    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void populate(BlockStorage blockStorage) {
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                for (int z = 0; z < SIZE_Z; ++z) {
                    blockStorage.setBlock(x, y, z, Block.AIR);
                }
            }
        }
    }

}
