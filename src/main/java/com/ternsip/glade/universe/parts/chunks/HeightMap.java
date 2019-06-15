package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2ic;
import org.joml.Vector3i;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class HeightMap implements Serializable, Universal {

    public static final int SKY_BLOCK_HEIGHT = 127;

    private final Vector2ic position;

    private final int[][] throughAir = new int[Chunk.SIZE][Chunk.SIZE];
    private final int[][] throughGas = new int[Chunk.SIZE][Chunk.SIZE];
    private final int[][] throughGasAndLiquid = new int[Chunk.SIZE][Chunk.SIZE];
    private final int[][] untilSoil = new int[Chunk.SIZE][Chunk.SIZE];

    public void recalculate() {

        Chunks chunks = getUniverse().getChunks();

        for (int x = 0; x < Chunk.SIZE; ++x) {
            for (int z = 0; z < Chunk.SIZE; ++z) {

                int yAir = SKY_BLOCK_HEIGHT;
                for (; yAir >= 0; --yAir) {
                    if (chunks.getBlock(new Vector3i(x, yAir, z)) != Block.AIR) {
                        break;
                    }
                }
                throughAir[x][z] = yAir;

                int yGas = SKY_BLOCK_HEIGHT;
                for (; yGas >= 0; --yGas) {
                    if (chunks.getBlock(new Vector3i(x, yGas, z)).getBlockMaterial() != BlockMaterial.GAS) {
                        break;
                    }
                }
                throughGas[x][z] = yGas;

                int yGasAndLiquid = SKY_BLOCK_HEIGHT;
                for (; yGasAndLiquid >= 0; --yGasAndLiquid) {
                    BlockMaterial material = chunks.getBlock(new Vector3i(x, yGasAndLiquid, z)).getBlockMaterial();
                    if (material != BlockMaterial.GAS && material != BlockMaterial.LIQUID) {
                        break;
                    }
                }
                throughGasAndLiquid[x][z] = yGasAndLiquid;

                int ySoil = SKY_BLOCK_HEIGHT;
                for (; ySoil >= 0; --ySoil) {
                    BlockMaterial material = chunks.getBlock(new Vector3i(x, ySoil, z)).getBlockMaterial();
                    if (material == BlockMaterial.SOIL) {
                        break;
                    }
                }
                untilSoil[x][z] = ySoil;

            }
        }

    }

}
