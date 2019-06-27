package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * This class and all folded data should be thread safe
 */
public class Blocks implements Universal {

    public static final byte MAX_LIGHT_LEVEL = 15;
    public static final int SIZE_X = 1024;
    public static final int SIZE_Y = 256;
    public static final int SIZE_Z = 1024;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);

    private static final int LIGHT_UPDATE_COMBINE_DISTANCE = 4;
    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    private static final int UPDATE_SIZE = 256;

    private final Storage storage;
    private final Timer lightUpdateTimer = new Timer(200);
    private final Timer relaxationTimer = new Timer(200);
    private final Map<Vector2ic, Chunk> chunks = new HashMap<>(); // TODO Vector2ic -> ChunkPos class
    private final Deque<LightUpdateRequest> lightUpdateRequests = new ConcurrentLinkedDeque<>();

    @Getter
    private final Deque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

    public Blocks() {
        this.storage = new Storage("blocks_meta");
        if (!storage.isExists()) {
            for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
                chunkGenerator.populate(this);
            }
            for (int x = 0; x < SIZE_X; x += UPDATE_SIZE) {
                for (int z = 0; z < SIZE_Z; z += UPDATE_SIZE) {
                    int sizeX = x + UPDATE_SIZE > SIZE_X ? SIZE_X - x : UPDATE_SIZE;
                    int sizeZ = z + UPDATE_SIZE > SIZE_Z ? SIZE_Z - z : UPDATE_SIZE;
                    recalculateBlockRegion(new Vector3i(x, 0, z), new Vector3i(sizeX, SIZE_Y, sizeZ));
                    relaxChunks();
                }
            }
            chunks.values().forEach(this::saveChunk);
        } else {
            // TODO use networking
            for (int x = 0, cx = 0; x < SIZE_X; x += Chunk.SIZE_X, ++cx) {
                for (int z = 0, cz = 0; z < SIZE_Z; z += Chunk.SIZE_Z, ++cz) {
                    blocksUpdates.add(getChunk(cx, cz).getSides().generateBlockUpdate());
                }
            }
        }
    }

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    public void setBlock(Vector3ic pos, Block block) {
        setBlock(pos.x(), pos.y(), pos.z(), block);
        setEmitLight(pos.x(), pos.y(), pos.z(), (byte) (MAX_LIGHT_LEVEL / 4));
        updateRegionProcrastinating(pos);
        relaxChunks();
    }

    public void setBlocks(Vector3ic start, Block[][][] regionBlocks) {
        Vector3ic size = new Vector3i(regionBlocks.length, regionBlocks[0].length, regionBlocks[0][0].length);
        Vector3ic endExcluding = new Vector3i(start).add(size).min(SIZE);
        for (int x = start.x(), dx = 0; x < endExcluding.x(); ++x, ++dx) {
            for (int y = start.y(), dy = 0; y < endExcluding.y(); ++y, ++dy) {
                for (int z = start.z(), dz = 0; z < endExcluding.z(); ++z, ++dz) {
                    setBlock(x, y, z, regionBlocks[dx][dy][dz]);
                }
            }
        }
        updateRegionProcrastinating(start, size);
        relaxChunks();
    }

    public Block getBlock(Vector3ic pos) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    public byte getSkyLight(Vector3ic pos) {
        return getSkyLight(pos.x(), pos.y(), pos.z());
    }

    public byte getEmitLight(Vector3ic pos) {
        return getEmitLight(pos.x(), pos.y(), pos.z());
    }

    public void setBlock(int x, int y, int z, Block block) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.getBlocks()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = block;
        chunk.setModified(true);
    }

    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.getBlocks()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    public void setSkyLight(int x, int y, int z, byte light) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.getSkyLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = light;
        chunk.setModified(true);
    }

    public byte getSkyLight(int x, int y, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.getSkyLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    public void setEmitLight(int x, int y, int z, byte light) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.getEmitLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = light;
        chunk.setModified(true);
    }

    public byte getEmitLight(int x, int y, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.getEmitLights()[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    public void setHeight(int x, int z, int height) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.getHeights()[x % Chunk.SIZE_X][z % Chunk.SIZE_Z] = height;
        chunk.setModified(true);
    }

    public int getHeight(int x, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.getHeights()[x % Chunk.SIZE_X][z % Chunk.SIZE_Z];
    }

    public SideData getSideData(SidePosition sPos) {
        Chunk chunk = getChunk(sPos.getX() / Chunk.SIZE_X, sPos.getZ() / Chunk.SIZE_Z);
        return chunk.getSides().get(sPos);
    }

    public void addSide(SidePosition sPos, SideData sideData) {
        Chunk chunk = getChunk(sPos.getX() / Chunk.SIZE_X, sPos.getZ() / Chunk.SIZE_Z);
        chunk.getSides().put(sPos, sideData);
    }

    public void removeSide(SidePosition sPos) {
        Chunk chunk = getChunk(sPos.getX() / Chunk.SIZE_X, sPos.getZ() / Chunk.SIZE_Z);
        chunk.getSides().remove(sPos);
    }

    public boolean isBlockExists(Vector3ic pos) {
        return INDEXER.isInside(pos);
    }

    public void finish() {
        chunks.values().forEach(this::saveChunk);
        storage.finish();
    }

    public void update() {
        if (lightUpdateTimer.isOver()) {
            ArrayList<LightUpdateRequest> updateRequests = new ArrayList<>();
            while (!lightUpdateRequests.isEmpty()) {
                updateRequests.add(lightUpdateRequests.poll());
            }
            boolean[] used = new boolean[updateRequests.size()];
            for (int i = 0; i < updateRequests.size(); ++i) {
                if (used[i]) {
                    continue;
                }
                Vector3i aMin = new Vector3i(updateRequests.get(i).getStart());
                Vector3i aMax = new Vector3i(updateRequests.get(i).getEndExcluding());
                for (int j = i + 1; j < updateRequests.size(); ++j) {
                    if (!used[j]) {
                        Vector3ic bMin = updateRequests.get(j).getStart();
                        Vector3ic bMax = updateRequests.get(j).getEndExcluding();
                        boolean isOverlapping =
                                (aMin.x() < bMax.x() + LIGHT_UPDATE_COMBINE_DISTANCE && aMax.x() + LIGHT_UPDATE_COMBINE_DISTANCE > bMin.x()) &&
                                        (aMin.y() < bMax.y() + LIGHT_UPDATE_COMBINE_DISTANCE && aMax.y() + LIGHT_UPDATE_COMBINE_DISTANCE > bMin.y()) &&
                                        (aMin.z() < bMax.z() + LIGHT_UPDATE_COMBINE_DISTANCE && aMax.z() + LIGHT_UPDATE_COMBINE_DISTANCE > bMin.z());
                        if (isOverlapping) {
                            used[j] = true;
                            aMin.min(bMin);
                            aMax.max(bMax);
                        }
                    }
                }
                recalculateBlockRegion(aMin, new Vector3i(aMax).sub(aMin));
            }
            lightUpdateTimer.drop();
        }
    }

    private Chunk getChunk(int x, int z) {
        Vector2i pos = new Vector2i(x, z);
        return chunks.computeIfAbsent(pos, e -> {
            if (storage.isExists(pos)) {
                Chunk chunk = storage.load(pos);
                chunks.put(pos, chunk);
                return chunk;
            }
            return new Chunk(x, z);
        });
    }

    private void relaxChunks() {
        if (relaxationTimer.isOver()) {
            chunks.entrySet().removeIf(entry -> {
                Chunk chunk = entry.getValue();
                if (chunk.getTimer().isOver()) {
                    saveChunk(chunk);
                    return true;
                }
                return false;
            });
            relaxationTimer.drop();
        }
    }

    private void saveChunk(Chunk chunk) {
        if (chunk.isModified()) {
            storage.save(new Vector2i(chunk.getXPos(), chunk.getZPos()), chunk);
            chunk.setModified(false);
        }
    }

    private void recalculateBlockRegion(Vector3ic start, Vector3ic size) {

        Utils.assertThat(size.x() > 0 || size.y() > 0 || size.z() > 0);

        // Recalculate height maps
        Vector3ic endExcluding = new Vector3i(start).add(size);
        int minObservedHeight = SIZE_Y;
        for (int x = start.x(); x < endExcluding.x(); ++x) {
            for (int z = start.z(); z < endExcluding.z(); ++z) {
                if (getHeight(x, z) > endExcluding.y()) {
                    continue;
                }
                int yAir = endExcluding.y() - 1;
                for (; yAir >= 0; --yAir) {
                    if (getBlock(x, yAir, z) != Block.AIR) {
                        break;
                    }
                }
                int height = yAir + 1;
                minObservedHeight = Math.min(Math.min(minObservedHeight, getHeight(x, z)), height);
                setHeight(x, z, height);
            }
        }

        // Recalculate light maps
        Queue<Long> queue = new ArrayDeque<>();
        Vector3ic newStart = new Vector3i(start.x(), Math.min(start.y(), minObservedHeight), start.z());
        Vector3ic newEndExcluding = new Vector3i(endExcluding);
        Vector3ic startLight = new Vector3i(newStart).sub(new Vector3i(MAX_LIGHT_LEVEL - 1)).max(new Vector3i(0));
        Vector3ic endLightExcluding = new Vector3i(newEndExcluding).add(new Vector3i(MAX_LIGHT_LEVEL - 1)).min(SIZE);
        Vector3ic lightSize = new Vector3i(endLightExcluding).sub(startLight);

        for (int x = startLight.x(); x < endLightExcluding.x(); ++x) {
            for (int z = startLight.z(); z < endLightExcluding.z(); ++z) {
                for (int y = startLight.y(); y < endLightExcluding.y(); ++y) {
                    byte emitLight = getBlock(x, y, z).getEmitLight();
                    byte skyLight = y >= getHeight(x, z) ? MAX_LIGHT_LEVEL : 0;
                    setEmitLight(x, y, z, emitLight);
                    // TODO sky if all 8 around above exists -> exists too
                    setSkyLight(x, y, z, skyLight);
                    if (emitLight > 0 || skyLight > 0) {
                        queue.add(INDEXER.getIndex(x, y, z));
                    }
                }
            }
        }

        // Add border blocks to engage outer light
        ArrayList<Vector3i> borderPositions = new ArrayList<>();
        for (int y = startLight.y(); y < endLightExcluding.y(); ++y) {
            for (int z = startLight.z(); z < endLightExcluding.z(); ++z) {
                borderPositions.add(new Vector3i(startLight.x() - 1, y, z));
                borderPositions.add(new Vector3i(endLightExcluding.x(), y, z));
            }
        }
        for (int x = startLight.x(); x < endLightExcluding.x(); ++x) {
            for (int z = startLight.z(); z < endLightExcluding.z(); ++z) {
                borderPositions.add(new Vector3i(x, startLight.y() - 1, z));
                borderPositions.add(new Vector3i(x, endLightExcluding.y(), z));
            }
        }
        for (int x = startLight.x(); x < endLightExcluding.x(); ++x) {
            for (int y = startLight.y(); y < endLightExcluding.y(); ++y) {
                borderPositions.add(new Vector3i(x, y, startLight.z() - 1));
                borderPositions.add(new Vector3i(x, y, endLightExcluding.z()));
            }
        }
        for (Vector3i borderPos : borderPositions) {
            if (INDEXER.isInside(borderPos) && (getSkyLight(borderPos.x(), borderPos.y(), borderPos.z()) > 0 ||
                    getEmitLight(borderPos.x(), borderPos.y(), borderPos.z()) > 0)) {
                queue.add(INDEXER.getIndex(borderPos));
            }
        }

        // Start light propagation BFS
        int[] dx = {1, 0, 0, -1, 0, 0};
        int[] dy = {0, 1, 0, 0, -1, 0};
        int[] dz = {0, 0, 1, 0, 0, -1};
        while (!queue.isEmpty()) {
            Long top = queue.poll();
            int x = INDEXER.getX(top);
            int y = INDEXER.getY(top);
            int z = INDEXER.getZ(top);
            int skyLightLevel = getSkyLight(x, y, z);
            int emitLightLevel = getEmitLight(x, y, z);
            for (int k = 0; k < dx.length; ++k) {
                int nx = x + dx[k];
                int ny = y + dy[k];
                int nz = z + dz[k];
                if (!INDEXER.isInside(nx, ny, nz)) {
                    continue;
                }
                int dstLightOpacity = getBlock(nx, ny, nz).getLightOpacity();
                int dstSkyLight = skyLightLevel - dstLightOpacity;
                int dstEmitLight = emitLightLevel - dstLightOpacity;
                byte nSkyLight = getSkyLight(nx, ny, nz);
                byte nEmitLight = getEmitLight(nx, ny, nz);
                if (nSkyLight < dstSkyLight || nEmitLight < dstEmitLight) {
                    setSkyLight(nx, ny, nz, (byte) Math.max(dstSkyLight, nSkyLight));
                    setEmitLight(nx, ny, nz, (byte) Math.max(dstEmitLight, nEmitLight));
                    queue.add(INDEXER.getIndex(nx, ny, nz));
                }
            }
        }

        visualUpdate(startLight, lightSize);
    }

    private void updateRegionProcrastinating(Vector3ic pos) {
        updateRegionProcrastinating(pos, new Vector3i(1));
    }

    private void updateRegionProcrastinating(Vector3ic start, Vector3ic size) {
        visualUpdate(start, size);
        lightUpdateRequests.add(new LightUpdateRequest(start, size));
    }

    private void visualUpdate(Vector3ic start, Vector3ic size) {

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(start).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(start).add(size).add(new Vector3i(1)).min(SIZE);

        // Calculate which sides should be removed or added
        List<Side> sidesToAdd = new ArrayList<>();
        List<SidePosition> sidesToRemove = new ArrayList<>();

        for (int x = startChanges.x(); x < endChangesExcluding.x(); ++x) {
            for (int z = startChanges.z(); z < endChangesExcluding.z(); ++z) {
                for (int y = startChanges.y(); y < endChangesExcluding.y(); ++y) {

                    Block block = getBlock(x, y, z);
                    for (BlockSide blockSide : BlockSide.values()) {
                        SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
                        SideData oldSideData = getSideData(sidePosition);
                        SideData newSideData = null;
                        if (block != Block.AIR) {
                            int nx = x + blockSide.getAdjacentBlockOffset().x();
                            int ny = y + blockSide.getAdjacentBlockOffset().y();
                            int nz = z + blockSide.getAdjacentBlockOffset().z();
                            if (INDEXER.isInside(nx, ny, nz)) {
                                Block nextBlock = getBlock(nx, ny, nz);
                                if (nextBlock == null || (nextBlock.isSemiTransparent() && (block != nextBlock || !block.isCombineSides()))) {
                                    byte nSkyLight = getSkyLight(nx, ny, nz);
                                    byte nEmitLight = getEmitLight(nx, ny, nz);
                                    newSideData = new SideData((byte) Math.min(MAX_LIGHT_LEVEL, nSkyLight + nEmitLight), block);
                                }
                            } else {
                                newSideData = new SideData((byte) 0, block);
                            }
                        }
                        if (newSideData != null && !newSideData.equals(oldSideData)) {
                            sidesToAdd.add(new Side(sidePosition, newSideData));
                            addSide(sidePosition, newSideData);
                        }
                        if (newSideData == null && oldSideData != null) {
                            sidesToRemove.add(sidePosition);
                            removeSide(sidePosition);
                        }
                    }

                }
            }
        }

        if (sidesToRemove.size() > 0 || sidesToAdd.size() > 0) {
            getBlocksUpdates().add(new BlocksUpdate(sidesToRemove, sidesToAdd));
        }

    }

}
