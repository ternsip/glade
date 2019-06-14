package com.ternsip.glade.universe.parts.blocks;

import com.ternsip.glade.universe.common.Universal;
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Vector3ic;

import java.util.HashMap;

@Getter(AccessLevel.PRIVATE)
public class Chunks implements Universal {

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
        chunk.randomize();
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
