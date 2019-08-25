package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.*;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class and all folded data should be thread safe
 */
@Slf4j
public class Blocks implements Threadable {

    public static final int CHUNKS_X = 8;
    public static final int CHUNKS_Z = 8;
    public static final byte MAX_LIGHT_LEVEL = 15;
    public static final int SIZE_X = CHUNKS_X * Chunk.SIZE_X;
    public static final int SIZE_Y = 256;
    public static final int SIZE_Z = CHUNKS_Z * Chunk.SIZE_Z;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);

    private static final int MAX_TRAVERSAL_LENGTH = 256;
    private static final int LIGHT_UPDATE_COMBINE_DISTANCE = 4;
    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    private static final int UPDATE_SIZE = 256;

    private final Storage storage;
    private final Timer lightUpdateTimer = new Timer(200);
    private final Timer relaxationTimer = new Timer(200);
    private final Chunk[][] chunks = new Chunk[CHUNKS_X][CHUNKS_Z];
    private final Set<Chunk> loadedChunks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Deque<VisualUpdateRequest> lightUpdateRequests = new ConcurrentLinkedDeque<>();
    private final Deque<VisualUpdateRequest> visualUpdateRequests = new ConcurrentLinkedDeque<>();

    @Getter
    private final Deque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

    public Blocks() {
        this.storage = new Storage("blocks_meta");
        if (!storage.isExists()) {
            generateAll();
        }
    }

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    public void requestBlockUpdates(
            Vector3ic prevPos,
            Vector3ic nextPos,
            int prevViewDistance,
            int nextViewDistance
    ) {

        int prevLength = prevViewDistance - 1;
        int nextLength = nextViewDistance - 1;

        int middlePrevChunkX = prevPos.x() / Chunk.SIZE_X;
        int middlePrevChunkZ = prevPos.z() / Chunk.SIZE_Z;
        int startPrevChunkX = middlePrevChunkX - prevLength;
        int startPrevChunkZ = middlePrevChunkZ - prevLength;
        int endPrevChunkX = middlePrevChunkX + prevLength;
        int endPrevChunkZ = middlePrevChunkZ + prevLength;

        int middleNextChunkX = nextPos.x() / Chunk.SIZE_X;
        int middleNextChunkZ = nextPos.z() / Chunk.SIZE_Z;
        int startNextChunkX = middleNextChunkX - nextLength;
        int startNextChunkZ = middleNextChunkZ - nextLength;
        int endNextChunkX = middleNextChunkX + nextLength;
        int endNextChunkZ = middleNextChunkZ + nextLength;

        if (startNextChunkX == startPrevChunkX && startNextChunkZ == startPrevChunkZ) {
            return;
        }
        requestBlockUpdates(startNextChunkX, startNextChunkZ, endNextChunkX, endNextChunkZ, startPrevChunkX, startPrevChunkZ, endPrevChunkX, endPrevChunkZ, true);
        requestBlockUpdates(startPrevChunkX, startPrevChunkZ, endPrevChunkX, endPrevChunkZ, startNextChunkX, startNextChunkZ, endNextChunkX, endNextChunkZ, false);

    }

    private void requestBlockUpdates(
            int startNextChunkX, int startNextChunkZ, int endNextChunkX, int endNextChunkZ,
            int startPrevChunkX, int startPrevChunkZ, int endPrevChunkX, int endPrevChunkZ,
            boolean additive
    ) {
        if (startNextChunkX >= CHUNKS_X || startNextChunkZ >= CHUNKS_Z || endNextChunkX < 0 || endNextChunkZ < 0) {
            return;
        }
        int scx = Maths.bound(0, CHUNKS_X - 1, startNextChunkX);
        int scz = Maths.bound(0, CHUNKS_Z - 1, startNextChunkZ);
        int ecx = Maths.bound(0, CHUNKS_X - 1, endNextChunkX);
        int ecz = Maths.bound(0, CHUNKS_Z - 1, endNextChunkZ);
        for (int cx = scx; cx <= ecx; ++cx) {
            for (int cz = scz; cz <= ecz; ++cz) {
                if (cx < startPrevChunkX || cx > endPrevChunkX || cz < startPrevChunkZ || cz > endPrevChunkZ) {
                    blocksUpdates.add(new BlocksUpdate(getChunk(cx, cz).sides, additive));
                }
            }
        }
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

    public boolean isBlockExists(Vector3ic pos) {
        return INDEXER.isInside(pos);
    }

    public void setBlock(int x, int y, int z, Block block) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.blocks[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = block;
        chunk.modified = true;
    }

    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.blocks[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    public void setSkyLight(int x, int y, int z, byte light) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.skyLights[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = light;
        chunk.modified = true;
    }

    public byte getSkyLight(int x, int y, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.skyLights[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    public void setEmitLight(int x, int y, int z, byte light) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.emitLights[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z] = light;
        chunk.modified = true;
    }

    public byte getEmitLight(int x, int y, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.emitLights[x % Chunk.SIZE_X][y][z % Chunk.SIZE_Z];
    }

    public void setHeight(int x, int z, int height) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        chunk.heights[x % Chunk.SIZE_X][z % Chunk.SIZE_Z] = height;
        chunk.modified = true;
    }

    public int getHeight(int x, int z) {
        Chunk chunk = getChunk(x / Chunk.SIZE_X, z / Chunk.SIZE_Z);
        return chunk.heights[x % Chunk.SIZE_X][z % Chunk.SIZE_Z];
    }

    public SideData getSideData(SidePosition sPos) {
        Chunk chunk = getChunk(sPos.getX() / Chunk.SIZE_X, sPos.getZ() / Chunk.SIZE_Z);
        return chunk.sides.get(sPos);
    }

    public void addSide(SidePosition sPos, SideData sideData) {
        Chunk chunk = getChunk(sPos.getX() / Chunk.SIZE_X, sPos.getZ() / Chunk.SIZE_Z);
        chunk.sides.put(sPos, sideData);
        chunk.modified = true;
    }

    public void removeSide(SidePosition sPos) {
        Chunk chunk = getChunk(sPos.getX() / Chunk.SIZE_X, sPos.getZ() / Chunk.SIZE_Z);
        chunk.sides.remove(sPos);
        chunk.modified = true;
    }

    @Override
    public void finish() {
        loadedChunks.forEach(this::saveChunk);
        storage.finish();
    }

    @Override
    public void init() {
    }

    @Override
    @SneakyThrows
    public void update() {
        processVisualRequests();
        if (lightUpdateTimer.isOver()) {
            processLightRequests();
            lightUpdateTimer.drop();
        } else {
            Thread.sleep(lightUpdateTimer.demand());
        }
    }

    // Using A Fast Voxel Traversal Algorithm for Ray Tracing by John Amanatides and Andrew Woo
    public @Nullable Vector3ic traverse(LineSegmentf segment, Function<Block, Boolean> condition) {
        Vector3fc ray = new Vector3f(
                segment.bX - segment.aX,
                segment.bY - segment.aY,
                segment.bZ - segment.aZ
        );
        Vector3fc step = new Vector3f(
                (ray.x() >= 0) ? 1f : -1f,
                (ray.y() >= 0) ? 1f : -1f,
                (ray.z() >= 0) ? 1f : -1f
        );
        Vector3i currentVoxel = new Vector3i(
                (int) Math.floor(segment.aX),
                (int) Math.floor(segment.aY),
                (int) Math.floor(segment.aZ)
        );
        Vector3ic lastVoxel = new Vector3i(
                (int) Math.floor(segment.bX),
                (int) Math.floor(segment.bY),
                (int) Math.floor(segment.bZ)
        );
        // Distance along the ray to the next voxel border from the current position (tMaxX, tMaxY, tMaxZ)
        Vector3fc nextBoundary = new Vector3f(currentVoxel).add(step);
        // tMax - distance until next intersection with voxel-border
        // the value of t at which the ray crosses the first vertical voxel boundary
        Vector3f tMax = new Vector3f(
                (Math.abs(ray.x()) != 0) ? (nextBoundary.x() - segment.aX) / ray.x() : Float.MAX_VALUE,
                (Math.abs(ray.y()) != 0) ? (nextBoundary.y() - segment.aY) / ray.y() : Float.MAX_VALUE,
                (Math.abs(ray.z()) != 0) ? (nextBoundary.z() - segment.aZ) / ray.z() : Float.MAX_VALUE
        );
        // How far along the ray we must move for the horizontal component to equal the width of a voxel
        // the direction in which we traverse the grid. Can only be Float.MAX_VALUE if we never go in that direction
        Vector3fc tDelta = new Vector3f(
                (Math.abs(ray.x()) != 0) ? 1 / ray.x() * step.x() : Float.MAX_VALUE,
                (Math.abs(ray.y()) != 0) ? 1 / ray.y() * step.y() : Float.MAX_VALUE,
                (Math.abs(ray.z()) != 0) ? 1 / ray.z() * step.z() : Float.MAX_VALUE
        );
        Vector3i diff = new Vector3i(0);
        boolean negativeRay = false;
        if (currentVoxel.x() != lastVoxel.x() && ray.x() < 0) {
            diff.x--;
            negativeRay = true;
        }
        if (currentVoxel.y() != lastVoxel.y() && ray.y() < 0) {
            diff.y--;
            negativeRay = true;
        }
        if (currentVoxel.z() != lastVoxel.z() && ray.z() < 0) {
            diff.z--;
            negativeRay = true;
        }
        ArrayList<Vector3ic> positions = new ArrayList<>();
        if (checkVoxel(currentVoxel, condition)) {
            positions.add(new Vector3i(currentVoxel));
        }
        if (negativeRay) {
            currentVoxel.add(diff);
            if (checkVoxel(currentVoxel, condition)) {
                positions.add(new Vector3i(currentVoxel));
            }
        }
        int counter = 0;
        while (!lastVoxel.equals(currentVoxel)) {
            ++counter;
            if (tMax.x < tMax.y) {
                if (tMax.x < tMax.z) {
                    currentVoxel.x += step.x();
                    tMax.x += tDelta.x();
                } else {
                    currentVoxel.z += step.z();
                    tMax.z += tDelta.z();
                }
            } else {
                if (tMax.y < tMax.z) {
                    currentVoxel.y += step.y();
                    tMax.y += tDelta.y();
                } else {
                    currentVoxel.z += step.z();
                    tMax.z += tDelta.z();
                }
            }
            if (checkVoxel(currentVoxel, condition)) {
                positions.add(new Vector3i(currentVoxel));
            }
            if (counter > MAX_TRAVERSAL_LENGTH) {
                log.error("Potential loop inside chunks voxels traversal algorithm. Manual avoiding...");
                break;
            }
        }
        // TODO It is superfluous, Remove this
        Vector3fc start = new Vector3f(segment.aX, segment.aY, segment.aZ);
        Vector3ic closest = null;
        for (Vector3ic pos : positions) {
            if (closest == null || start.distanceSquared(pos.x(), pos.y(), pos.z()) < start.distanceSquared(closest.x(), closest.y(), closest.z())) {
                closest = pos;
            }
        }
        return closest;
    }

    private void processVisualRequests() {
        while (!visualUpdateRequests.isEmpty()) {
            VisualUpdateRequest visualUpdate = visualUpdateRequests.poll();
            visualUpdate(visualUpdate.getStart(), visualUpdate.getSize());
        }
    }

    private void processLightRequests() {
        ArrayList<VisualUpdateRequest> updateRequests = new ArrayList<>();
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
    }

    private synchronized void generateAll() {
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
        loadedChunks.forEach(this::saveChunk);
    }

    private synchronized Chunk getChunk(int x, int z) {
        if (chunks[x][z] == null) {
            Vector2i pos = new Vector2i(x, z);
            chunks[x][z] = storage.isExists(pos) ? storage.load(pos) : new Chunk(x, z);
            loadedChunks.add(chunks[x][z]);
        }
        chunks[x][z].timer.drop();
        return chunks[x][z];
    }

    private synchronized void relaxChunks() {
        if (relaxationTimer.isOver()) {
            loadedChunks.removeIf(chunk -> {
                if (chunk.timer.isOver()) {
                    chunks[chunk.xPos][chunk.zPos] = null;
                    saveChunk(chunk);
                    return true;
                }
                return false;
            });
            relaxationTimer.drop();
        }
    }

    private synchronized void saveChunk(Chunk chunk) {
        if (chunk.modified) {
            storage.save(new Vector2i(chunk.xPos, chunk.zPos), chunk);
            chunk.modified = false;
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
        visualUpdateRequests.add(new VisualUpdateRequest(start, size));
        lightUpdateRequests.add(new VisualUpdateRequest(start, size));
    }

    /**
     * In this function we are recalculating added/removed sides based on blocks state and putting them to the queue
     */
    private void visualUpdate(Vector3ic start, Vector3ic size) {

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(start).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(start).add(size).add(new Vector3i(1)).min(SIZE);

        // Calculate which sides should be removed or added
        BlocksUpdate blocksUpdate = new BlocksUpdate();

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
                                    newSideData = new SideData(getSkyLight(nx, ny, nz), getEmitLight(nx, ny, nz), block);
                                }
                            } else {
                                newSideData = new SideData((byte) 0, (byte) 0, block);
                            }
                        }
                        if (newSideData != null && !newSideData.equals(oldSideData)) {
                            blocksUpdate.add(new Side(sidePosition, newSideData));
                            addSide(sidePosition, newSideData);
                        }
                        if (newSideData == null && oldSideData != null) {
                            blocksUpdate.remove(sidePosition);
                            removeSide(sidePosition);
                        }
                    }

                }
            }
        }

        if (!blocksUpdate.isEmpty()) {
            getBlocksUpdates().add(blocksUpdate);
        }

    }

    private boolean checkVoxel(Vector3i pos, Function<Block, Boolean> condition) {
        return isBlockExists(pos) && condition.apply(getBlock(pos));
    }

}
