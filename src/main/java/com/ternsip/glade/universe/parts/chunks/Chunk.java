package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.Random;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@Getter
@Setter
public class Chunk implements Serializable, Universal {

    public static final int SIZE = 16;
    public static final int VOLUME = SIZE * SIZE * SIZE;

    private final Block[][][] blocks;
    private final Vector3ic position;

    public Chunk(Vector3ic position) {
        this(getAirBlocksArray(), position);
    }

    public Chunk(Block[][][] blocks, Vector3ic position) {
        this.blocks = blocks;
        this.position = position;
        if (blocks.length != SIZE || blocks[0].length != SIZE || blocks[0][0].length != SIZE) {
            throw new IllegalArgumentException("Invalid Chunk size");
        }
    }

    private static Block[][][] getAirBlocksArray() {
        Block[][][] blocks = new Block[SIZE][SIZE][SIZE];
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                for (int z = 0; z < SIZE; ++z) {
                    blocks[x][y][z] = Block.AIR;
                }
            }
        }
        return blocks;
    }

    public void update() {
    }

    public boolean isInside(Vector3ic pos) {
        return pos.x() >= 0 && pos.x() < SIZE && pos.y() >= 0 && pos.y() < SIZE && pos.z() >= 0 && pos.z() < SIZE;
    }

    public Block getBlock(Vector3ic pos) {
        return getBlocks()[pos.x()][pos.y()][pos.z()];
    }

    public void setBlock(Vector3ic pos, Block block) {
        getBlocks()[pos.x()][pos.y()][pos.z()] = block;
    }

    public void clean() {
        forEach((Vector3ic pos, Block block) -> {
            setBlock(pos, Block.AIR);
        });
    }

    public void randomize() {
        Random random = new Random(System.currentTimeMillis());
        forEach((Vector3ic pos, Block block) -> {
            setBlock(pos, Block.AIR);
            if (random.nextFloat() < 0.05) setBlock(pos, Block.SAND);
            if (random.nextFloat() < 0.05) setBlock(pos, Block.DIRT);
            if (random.nextFloat() < 0.05) setBlock(pos, Block.STONE);
            if (random.nextFloat() < 0.05) setBlock(pos, Block.WATER);
            if (random.nextFloat() < 0.05) setBlock(pos, Block.LEAVES);
        });
    }

    public void forEach(ProcessEachBlock processEachBlock) {
        Vector3i pos = new Vector3i();
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                for (int z = 0; z < SIZE; ++z) {
                    pos.set(x, y, z);
                    processEachBlock.apply(pos, getBlocks()[x][y][z]);
                }
            }
        }
    }

    public Vector3ic toWorldPos(Vector3ic position) {
        return new Vector3i(
                getPosition().x() * SIZE,
                getPosition().y() * SIZE,
                getPosition().z() * SIZE
        ).add(position);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @FunctionalInterface
    public interface ProcessEachBlock {
        void apply(Vector3ic pos, Block block);
    }

}
