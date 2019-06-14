package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Vector3ic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
public class Chunks implements Universal {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();

    private HashMap<Vector3ic, Chunk> positionToChunk = new HashMap<>();

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

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

}
