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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Chunk implements Serializable, Universal {

    public static final int SIZE = 16;
    public static final int VOLUME = SIZE * SIZE * SIZE;

    private final Block[][][] blocks;
    private final Vector3ic position;

    private transient int[][][] light = new int[SIZE][SIZE][SIZE];
    private transient boolean logicReloadRequired = true;
    private transient boolean visualReloadRequired = false;
    private transient List<Vector3ic> changedBlocks = new ArrayList<>();

    public Chunk(Vector3ic position) {
        this(createEmptyBlockArray(), position);
    }

    public Chunk(Block[][][] blocks, Vector3ic position) {
        this.blocks = blocks;
        this.position = position;
        if (blocks.length != SIZE || blocks[0].length != SIZE || blocks[0][0].length != SIZE) {
            throw new IllegalArgumentException("Invalid Chunk size");
        }
    }

    private static Block[][][] createEmptyBlockArray() {
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
        if (isLogicReloadRequired()) {
            //getUniverse().getChunks().recalculateBlockRegion(toWorldPos(new Vector3i(0)), new Vector3i(SIZE));
            setLogicReloadRequired(false);
        }
    }

    public boolean isInside(Vector3ic pos) {
        return pos.x() >= 0 && pos.x() < SIZE && pos.y() >= 0 && pos.y() < SIZE && pos.z() >= 0 && pos.z() < SIZE;
    }

    public int getLight(Vector3ic pos) {
        return getLight()[pos.x()][pos.y()][pos.z()];
    }

    public void setLight(Vector3ic pos, int light) {
        getLight()[pos.x()][pos.y()][pos.z()] = light;
    }

    public Block getBlock(Vector3ic pos) {
        return getBlocks()[pos.x()][pos.y()][pos.z()];
    }

    public void setBlock(Vector3ic pos, Block block) {
        getBlocks()[pos.x()][pos.y()][pos.z()] = block;
    }

    public void clean() {
        forEach((Vector3ic pos, Block block, int light) -> {
            setBlock(pos, Block.AIR);
        });
    }

    public void randomize() {
        Random random = new Random(System.currentTimeMillis());
        forEach((Vector3ic pos, Block block, int light) -> {
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
                    processEachBlock.apply(pos, getBlocks()[x][y][z], getLight()[x][y][z]);
                }
            }
        }
    }

    public void forEachStartingFromOpaque(ProcessEachBlock processEachBlock) {
        forEach((Vector3ic pos, Block block, int light) -> {
            if (!block.isTranslucent()) {
                processEachBlock.apply(pos, block, light);
            }
        });
        forEach((Vector3ic pos, Block block, int light) -> {
            if (block.isTranslucent()) {
                processEachBlock.apply(pos, block, light);
            }
        });
    }

    public Vector3ic toWorldPos(Vector3ic position) {
        return new Vector3i(
                getPosition().x() * SIZE,
                getPosition().y() * SIZE,
                getPosition().z() * SIZE
        ).add(position);
    }

    public void registerBlockChange(Vector3ic pos) {
        getChangedBlocks().add(pos);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.light = new int[SIZE][SIZE][SIZE];
        this.logicReloadRequired = false;
        this.changedBlocks = new ArrayList<>();
    }


    @FunctionalInterface
    public interface ProcessEachBlock {
        void apply(Vector3ic pos, Block block, int light);
    }

}
