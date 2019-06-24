package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Blocks;
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
    public void populate(Blocks blocks) {
        int[][] heightMapStone = generateHeightMap(11, 5, 50, 0.01f);
        int[][] heightMapDirt = generateHeightMap(60, 5, 20, 0.01f);
        for (int x = 0; x < Blocks.SIZE_X; ++x) {
            for (int z = 0; z < Blocks.SIZE_Z; ++z) {
                for (int y = 0; y < Blocks.SIZE_Y; ++y) {
                    Vector3ic wPos = new Vector3i(x, y, z);
                    int stoneHeight = height + heightMapStone[x][z];
                    int dirtHeight = stoneHeight + heightMapDirt[x][z];
                    if (wPos.y() < stoneHeight) {
                        blocks.setBlock(x, y, z, Block.STONE);
                    } else if (wPos.y() <= dirtHeight) {
                        blocks.setBlock(x, y, z, wPos.y() == dirtHeight ? Block.LAWN : Block.DIRT);
                    }
                }
            }
        }
    }

    private int[][] generateHeightMap(float seed, long terraces, float deviation, float compress) {
        int[][] heightMap = new int[Blocks.SIZE_X][Blocks.SIZE_Z];
        for (int x = 0; x < Blocks.SIZE_X; ++x) {
            for (int z = 0; z < Blocks.SIZE_Z; ++z) {
                float noiseX = x * compress;
                float noiseZ = z * compress;
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
