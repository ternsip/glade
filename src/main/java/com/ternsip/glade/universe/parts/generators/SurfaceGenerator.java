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
    private final float compress = 0.01f;
    private final int deviation = 5;
    private final int terraces = 15;

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
        for (int x = 0; x < Chunk.SIZE; ++x) {
            for (int z = 0; z < Chunk.SIZE; ++z) {
                Vector3ic wPos = chunk.toWorldPos(new Vector3i(x, 0, z));
                float noiseX = wPos.x() * getCompress();
                float noiseZ = wPos.z() * getCompress();
                double noise1 = SimplexNoise.noise(noiseX, noiseZ) * getDeviation();
                double noise2 = SimplexNoise.noise(noiseZ, noiseX) * getDeviation();
                long diff = Math.round(Math.pow(noise1, Math.E) * getTerraces()) / getTerraces();
                long diff2 = -Math.round(Math.pow(noise2, Math.E) * getTerraces()) / getTerraces();
                heightMap[x][z] = (int) (getHeight() + diff + diff2);
            }
        }
        return heightMap;
    }

}
