package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.storage.Storage;
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * This class and all folded data should be thread safe
 */
@Getter(AccessLevel.PRIVATE)
public class Chunks implements Universal {

    public static final int MAX_LIGHT_LEVEL = 16;
    public static final int SKY_BLOCK_HEIGHT = 127;

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();

    @Getter(lazy = true)
    private final Storage storage = new Storage("chunks");
    private final Map<Vector3ic, Chunk> positionToChunk = new ConcurrentHashMap<>();

    @Getter(AccessLevel.PUBLIC)
    private final Deque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

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
        }
    }

    public boolean isBlockLoaded(Vector3ic pos) {
        return isChunkInMemory(getChunkPositionForBlock(pos));
    }

    public Block getBlock(Vector3ic pos) {
        return getChunk(getChunkPositionForBlock(pos)).getBlock(getBlockPositionInsideChunk(pos));
    }

    public void setBlock(Vector3ic pos, Block block) {
        getChunk(getChunkPositionForBlock(pos)).setBlock(getBlockPositionInsideChunk(pos), block);
    }

    public void finish() {
        getStorage().commit();
        getStorage().finish();
    }

    public Vector3ic getChunkPositionForBlock(Vector3ic pos) {
        return new Vector3i(
                (pos.x() >= 0 ? pos.x() : (pos.x() - Chunk.SIZE + 1)) / Chunk.SIZE,
                (pos.y() >= 0 ? pos.y() : (pos.y() - Chunk.SIZE + 1)) / Chunk.SIZE,
                (pos.z() >= 0 ? pos.z() : (pos.z() - Chunk.SIZE + 1)) / Chunk.SIZE
        );
    }

    public Vector3ic getBlockPositionInsideChunk(Vector3ic pos) {
        return getChunkPositionForBlock(pos).mul(Chunk.SIZE, new Vector3i()).negate().add(pos);
    }

    public void recalculateBlockRegion(Vector3ic pos) {
        recalculateBlockRegion(pos, new Vector3i(1));
    }

    public void recalculateBlockRegion(Vector3ic start, Vector3ic size) {
        if (size.x() <= 0 || size.y() <= 0 || size.z() <= 0) {
            return;
        }
        Block[][][] blocks = new Block[size.x()][size.y()][size.z()];
        for (int x = 0, wx = start.x(); x < size.x(); ++x, ++wx) {
            for (int y = 0, wy = start.y(); y < size.y(); ++y, ++wy) {
                for (int z = 0, wz = start.z(); z < size.z(); ++z, ++wz) {
                    blocks[x][y][z] = getBlock(new Vector3i(wx, wy, wz));
                }
            }
        }
        int[][] heights = new int[size.x()][size.z()];
        for (int x = 0, wx = start.x(); x < size.x(); ++x, ++wx) {
            for (int z = 0, wz = start.z(); z < size.z(); ++z, ++wz) {
                int yAir = SKY_BLOCK_HEIGHT;
                for (; yAir >= 0; --yAir) {
                    if (getBlock(new Vector3i(wx, yAir, wz)) != Block.AIR) {
                        break;
                    }
                }
                heights[x][z] = yAir + 1;
            }
        }
        getBlocksUpdates().add(new BlocksUpdate(blocks, heights, start));
    }

    public boolean isChunkInMemory(Vector3ic position) {
        return getPositionToChunk().containsKey(position);
    }

    private Chunk generateChunk(Vector3ic position) {
        Chunk chunk = new Chunk(position);
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(chunk);
        }
        return chunk;
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
