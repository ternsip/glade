package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.BlocksServerRepository;
import lombok.Getter;
import org.joml.SimplexNoise;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int height = 30;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(BlocksServerRepository blocksServerRepository, int startX, int startZ, int endX, int endZ) {
        int[][] heightMapStone = generateHeightMap(11, 5, 25, 0.01f, startX, startZ, endX, endZ);
        int[][] heightMapDirt = generateHeightMap(60, 5, 10, 0.01f, startX, startZ, endX, endZ);
        for (int dx = 0, x = startX; x <= endX; ++x, ++dx) {
            for (int dz = 0, z = startZ; z <= endZ; ++z, ++dz) {
                for (int y = 0; y < BlocksServerRepository.SIZE_Y; ++y) {
                    Vector3ic wPos = new Vector3i(x, y, z);
                    int stoneHeight = height + heightMapStone[dx][dz];
                    int dirtHeight = stoneHeight + heightMapDirt[dx][dz];
                    if (wPos.y() < stoneHeight) {
                        blocksServerRepository.setBlock(x, y, z, Block.STONE);
                    } else if (wPos.y() <= dirtHeight) {
                        blocksServerRepository.setBlock(x, y, z, wPos.y() == dirtHeight ? Block.LAWN : Block.DIRT);
                    }
                }
            }
        }
    }

    private int[][] generateHeightMap(float seed, long terraces, float deviation, float compress, int startX, int startZ, int endX, int endZ) {
        int[][] heightMap = new int[endX - startX + 1][endZ - startZ + 1];
        for (int dx = 0, x = startX; x <= endX; ++x, ++dx) {
            for (int dz = 0, z = startZ; z <= endZ; ++z, ++dz) {
                float noiseX = x * compress;
                float noiseZ = z * compress;
                double noise1 = SimplexNoise.noise(noiseX, noiseZ, seed);
                double noise2 = SimplexNoise.noise(noiseZ, noiseX, seed);
                long diff = Math.round(Math.pow(noise1, Math.E) * terraces * deviation) / terraces;
                long diff2 = -Math.round(Math.pow(noise2, Math.E) * terraces * deviation) / terraces;
                heightMap[dx][dz] = (int) (diff + diff2);
            }
        }
        return heightMap;
    }

}
