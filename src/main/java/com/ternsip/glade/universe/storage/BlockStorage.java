package com.ternsip.glade.universe.storage;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.Blocks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BlockStorage extends Storage {

    private final Map<Vector2ic, Chunk> chunks;

    @SneakyThrows
    public BlockStorage(String name) {
        super(name);
        this.chunks = new HashMap<>();
    }

    public void relaxMemory(boolean force) {
        if (force) {
            save();
            getChunks().clear();
        }
        // TODO in case of non-force timeout cleaning
    }

    @SneakyThrows
    public void setBlock(int x, int y, int z, Block block) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        chunk.getBlocks()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = block;
    }

    @SneakyThrows
    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        return chunk.getBlocks()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    @SneakyThrows
    public void setSkyLight(int x, int y, int z, byte light) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        chunk.getSkyLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = light;
    }

    @SneakyThrows
    public byte getSkyLight(int x, int y, int z) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        return chunk.getSkyLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    @SneakyThrows
    public void setEmitLight(int x, int y, int z, byte light) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        chunk.getEmitLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = light;
    }

    @SneakyThrows
    public byte getEmitLight(int x, int y, int z) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        return chunk.getEmitLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    @SneakyThrows
    public void setHeight(int x, int z, int height) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        chunk.getHeights()[x % Chunk.SIZE_X][z % Chunk.SIZE_Z] = height;
    }

    @SneakyThrows
    public int getHeight(int x, int z) {
        Chunk chunk = getChunk(new Vector2i(x / Chunk.SIZE_X, z / Chunk.SIZE_Z));
        return chunk.getHeights()[x % Chunk.SIZE_X][z % Chunk.SIZE_Z];
    }

    private Chunk getChunk(Vector2ic pos) {
        return getChunks().computeIfAbsent(pos, e -> {
            if (isChunkExists(pos)) {
                return loadChunk(pos);
            }
            return new Chunk();
        });
    }

    private Chunk loadChunk(Vector2ic pos) {
        Chunk chunk = load(pos);
        getChunks().put(pos, chunk);
        return chunk;
    }

    private void saveChunk(Vector2ic pos, Chunk chunk) {
        save(pos, chunk);
    }

    private boolean isChunkExists(Vector2ic pos) {
        return isExists(pos);
    }

    public void save() {
        getChunks().forEach(this::saveChunk);
    }

    @RequiredArgsConstructor
    @Getter
    private static class Chunk implements Serializable {

        private final static int SIZE_X = 32;
        private final static int SIZE_Z = 32;

        private Block[][][] blocks = new Block[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
        private byte[][][] skyLights = new byte[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
        private byte[][][] emitLights = new byte[SIZE_X][Blocks.SIZE_Y][SIZE_Z];
        private int[][] heights = new int[SIZE_X][SIZE_Z];

    }

}
