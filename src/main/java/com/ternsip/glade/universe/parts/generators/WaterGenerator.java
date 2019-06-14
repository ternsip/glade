package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import org.joml.Vector3ic;

public class WaterGenerator implements ChunkGenerator {

    private final int waterHeight = 60;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void populate(Chunk chunk) {
        chunk.forEach((Vector3ic pos, Block block) -> {
            Vector3ic wPos = chunk.toWorldPos(pos);
            if (wPos.y() <= waterHeight && block == Block.AIR) {
                chunk.setBlock(pos, Block.WATER);
            }
        });
    }
}
