package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import lombok.AccessLevel;
import lombok.Getter;
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

    private HashMap<Vector3ic, Chunk> positionToChunk = new HashMap<>();

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    public Chunk getChunk(Vector3ic position) {
        if (!isChunkInMemory(position)) {
            if (!isChunkGenerated(position)) {
                Chunk chunk = generate(position);
                saveChunk(chunk);
            }
            Chunk chunk = loadChunk(position);
            getPositionToChunk().put(chunk.getChunkPosition(), chunk);
        }
        return getPositionToChunk().get(position);
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

    private Chunk generate(Vector3ic position) {
        Chunk chunk = new Chunk(position);
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(chunk);
        }
        return chunk;
    }

    private boolean isChunkInMemory(Vector3ic position) {
        return getPositionToChunk().containsKey(position);
    }

    private boolean isChunkGenerated(Vector3ic position) {
        return getUniverse().getUniverseStorage().isExists(position);
    }

    private void saveChunk(Chunk chunk) {
        getUniverse().getUniverseStorage().save(chunk.getChunkPosition(), chunk);
    }

    private Chunk loadChunk(Vector3ic position) {
        return getUniverse().getUniverseStorage().load(position);
    }

}
