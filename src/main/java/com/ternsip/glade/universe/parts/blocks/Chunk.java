package com.ternsip.glade.universe.parts.blocks;

import lombok.Getter;
import org.joml.Random;
import org.joml.Vector3ic;

@Getter
public class Chunk {

    public static final int SIZE = 16;
    public static final int VOLUME = SIZE * SIZE * SIZE;

    private final Block[][][] blocks;
    private final Vector3ic chunkPosition;

    public Chunk(Vector3ic chunkPosition) {
        this(new Block[SIZE][SIZE][SIZE], chunkPosition);
    }

    public Chunk(Block[][][] blocks, Vector3ic chunkPosition) {
        this.blocks = blocks;
        this.chunkPosition = chunkPosition;
        if (blocks.length != SIZE || blocks[0].length != SIZE || blocks[0][0].length != SIZE) {
            throw new IllegalArgumentException("Invalid Chunk size");
        }
    }

    public boolean isInside(int x, int y, int z) {
        return x >= 0 && x < blocks.length && y >= 0 && y < blocks[x].length && z >= 0 && z < blocks[x][y].length;
    }

    public Block getBlock(int x, int y, int z) {
        return getBlocks()[x][y][z];
    }

    public void randomize() {
        Random random = new Random(System.currentTimeMillis());
        for (int x = 0, idx = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                for (int z = 0; z < SIZE; ++z, ++idx) {
                    blocks[x][y][z] = Block.AIR;
                    if (random.nextFloat() < 0.05) blocks[x][y][z] = Block.SAND;
                    if (random.nextFloat() < 0.05) blocks[x][y][z] = Block.DIRT;
                    if (random.nextFloat() < 0.05) blocks[x][y][z] = Block.STONE;
                    if (random.nextFloat() < 0.05) blocks[x][y][z] = Block.WATER;
                    if (random.nextFloat() < 0.05) blocks[x][y][z] = Block.LEAVES;
                }
            }
        }
    }

}
