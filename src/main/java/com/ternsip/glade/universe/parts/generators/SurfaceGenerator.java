package com.ternsip.glade.universe.parts.generators;

import com.flowpowered.noise.Noise;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int height = 64;
    private final int deviation = 10;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(Chunk chunk) {
        int[][] heightMap = generateHeightMap(chunk);
        chunk.forEach((Vector3ic pos, Block block) -> {
            Vector3ic wPos = chunk.toWorldPos(pos);
            int curHeight = heightMap[pos.x()][pos.z()];
            if (wPos.y() <= curHeight) {
                chunk.setBlock(pos, Block.STONE);
            }
        });
    }

    private int[][] generateHeightMap(Chunk chunk) {
        int[][] heightMap = new int[Chunk.SIZE][Chunk.SIZE];
        int seed = new Vector2i(chunk.getChunkPosition().x(), chunk.getChunkPosition().z()).hashCode();
        for (int x = 0; x < Chunk.SIZE; ++x) {
            for (int z = 0; z < Chunk.SIZE; ++z) {
                Vector3ic wPos = chunk.toWorldPos(new Vector3i(x, 0, z));
                heightMap[x][z] = (int) (getHeight() + (Noise.valueNoise3D(wPos.x(), 0, wPos.z(), seed) - 0.5) * getDeviation());
            }
        }
        return heightMap;
    }

}
