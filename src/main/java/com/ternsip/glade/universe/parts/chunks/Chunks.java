package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.storage.Storage;
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class and all folded data should be thread safe
 */
@Getter(AccessLevel.PRIVATE)
public class Chunks implements Universal {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();

    @Getter(lazy = true)
    private final Storage storage = new Storage("chunks");
    private final HashMap<Vector3ic, Chunk> positionToChunk = new HashMap<>();
    private final HashMap<Vector2ic, HeightMap> positionToHeightMap = new HashMap<>();

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    public Chunk getChunk(Vector3ic position) {
        if (!isChunkInMemory(position)) {
            if (!isChunkGenerated(position)) {
                Chunk chunk = generateChunk(position);
                saveChunk(chunk);
            }
            Chunk chunk = loadChunk(position);
            getPositionToChunk().put(chunk.getPosition(), chunk);
        }
        return getPositionToChunk().get(position);
    }

    public void unloadChunk(Vector3ic position) {
        if (isChunkInMemory(position)) {
            saveChunk(getPositionToChunk().get(position));
            getPositionToChunk().remove(position);
            Vector2ic slicePos = new Vector2i(position.x(), position.z());
            if (isSliceUnloaded(slicePos)) {
                unloadHeightMap(slicePos);
            }
        }
    }

    public HeightMap getHeightMap(Vector2ic position) {
        if (!isHeightMapInMemory(position)) {
            if (!isHeightMapGenerated(position)) {
                HeightMap heightMap = generateHeightMap(position);
                saveHeightMap(heightMap);
            }
            HeightMap heightMap = loadHeightMap(position);
            getPositionToHeightMap().put(heightMap.getPosition(), heightMap);
        }
        return getPositionToHeightMap().get(position);
    }

    public void unloadHeightMap(Vector2ic position) {
        if (isHeightMapInMemory(position)) {
            saveHeightMap(getPositionToHeightMap().get(position));
            getPositionToHeightMap().remove(position);
        }
    }

    private boolean isSliceUnloaded(Vector2ic slicePos) {
        return getPositionToChunk().keySet().stream().noneMatch(e -> slicePos.equals(e.x(), e.z()));
    }

    public boolean isBlockLoaded(Vector3ic pos) {
        return isChunkInMemory(getChunkPosition(pos));
    }

    public Block getBlock(Vector3ic pos) {
        return getChunk(getChunkPosition(pos)).getBlock(getBlockPositionInsideChunk(pos));
    }

    public void setBlock(Vector3ic pos, Block block) {
        getChunk(getChunkPosition(pos)).setBlock(getBlockPositionInsideChunk(pos), block);
    }

    public void finish() {
        getStorage().commit();
        getStorage().finish();
    }

    private Vector3ic getChunkPosition(Vector3ic pos) {
        return new Vector3i(
                (pos.x() >= 0 ? pos.x() : (pos.x() - Chunk.SIZE + 1)) / Chunk.SIZE,
                (pos.y() >= 0 ? pos.y() : (pos.y() - Chunk.SIZE + 1)) / Chunk.SIZE,
                (pos.z() >= 0 ? pos.z() : (pos.z() - Chunk.SIZE + 1)) / Chunk.SIZE
        );
    }

    private Vector3ic getBlockPositionInsideChunk(Vector3ic pos) {
        return getChunkPosition(pos).mul(Chunk.SIZE, new Vector3i()).negate().add(pos);
    }

    private Chunk generateChunk(Vector3ic position) {
        Chunk chunk = new Chunk(position);
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(chunk);
        }
        return chunk;
    }

    private HeightMap generateHeightMap(Vector2ic position) {
        HeightMap heightMap = new HeightMap(position);
        heightMap.recalculate();
        return heightMap;
    }

    private boolean isHeightMapInMemory(Vector2ic position) {
        return getPositionToHeightMap().containsKey(position);
    }

    private boolean isHeightMapGenerated(Vector2ic position) {
        return getStorage().isExists(position);
    }

    private void saveHeightMap(HeightMap heightMap) {
        getStorage().save(heightMap.getPosition(), heightMap);
    }

    private HeightMap loadHeightMap(Vector2ic position) {
        return getStorage().load(position);
    }

    private boolean isChunkInMemory(Vector3ic position) {
        return getPositionToChunk().containsKey(position);
    }

    private boolean isChunkGenerated(Vector3ic position) {
        return getStorage().isExists(position);
    }

    private void saveChunk(Chunk chunk) {
        getStorage().save(chunk.getPosition(), chunk);
    }

    private Chunk loadChunk(Vector3ic position) {
        return getStorage().load(position);
    }

}
