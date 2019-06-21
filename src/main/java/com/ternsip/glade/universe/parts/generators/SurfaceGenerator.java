package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import org.joml.SimplexNoise;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int height = 60;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(Chunk chunk) {
        int[][] heightMapStone = generateHeightMap(chunk, 11, 5, 50, 0.01f);
        int[][] heightMapDirt = generateHeightMap(chunk, 60, 5, 20, 0.01f);
        chunk.forEach((Vector3ic pos, Block block) -> {
            Vector3ic wPos = chunk.toWorldPos(pos);
            int stoneHeight = height + heightMapStone[pos.x()][pos.z()];
            int dirtHeight = stoneHeight + heightMapDirt[pos.x()][pos.z()];
            if (wPos.y() < stoneHeight) {
                chunk.setBlock(pos, Block.STONE);
            } else if (wPos.y() <= dirtHeight) {
                chunk.setBlock(pos, wPos.y() == dirtHeight ? Block.LAWN : Block.DIRT);
            }
        });
    }

    private int[][] generateHeightMap(Chunk chunk, float seed, long terraces, float deviation, float compress) {
        int[][] heightMap = new int[Chunk.SIZE][Chunk.SIZE];
        for (int x = 0; x < Chunk.SIZE; ++x) {
            for (int z = 0; z < Chunk.SIZE; ++z) {
                Vector3ic wPos = chunk.toWorldPos(new Vector3i(x, 0, z));
                float noiseX = wPos.x() * compress;
                float noiseZ = wPos.z() * compress;
                double noise1 = SimplexNoise.noise(noiseX, noiseZ, seed);
                double noise2 = SimplexNoise.noise(noiseZ, noiseX, seed);
                long diff = Math.round(Math.pow(noise1, Math.E) * terraces * deviation) / terraces;
                long diff2 = -Math.round(Math.pow(noise2, Math.E) * terraces * deviation) / terraces;
                heightMap[x][z] = (int) (diff + diff2);
            }
        }
        return heightMap;
    }

}
