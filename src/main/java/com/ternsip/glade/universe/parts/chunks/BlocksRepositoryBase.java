package com.ternsip.glade.universe.parts.chunks;

import com.aparapi.Range;
import com.ternsip.glade.common.logic.*;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.storage.Storage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiFunction;

import static com.ternsip.glade.common.logic.Maths.frac;

@Getter
@Setter
@Slf4j
public abstract class BlocksRepositoryBase implements Threadable {

    public static final int SIZE_X = 512;
    public static final int SIZE_Y = 256;
    public static final int SIZE_Z = 512;
    public static final byte MAX_LIGHT_LEVEL = 15;
    public static final int UPDATE_LIMIT = 128;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);

    private final GridCompressor blocksCompressor = new GridCompressor();
    private final GridCompressor lightCompressor = new GridCompressor();
    private final GridCompressor heightCompressor = new GridCompressor();
    private final LightMassKernel lightMassKernel = new LightMassKernel();
    private final Map<Vector3ic, Chunk> posToChunk = new HashMap<>();
    private final ArrayList<SidePosition> sidesToRemove = new ArrayList<>();
    private final ArrayList<Side> sidesToAdd = new ArrayList<>();
    private final ConcurrentLinkedDeque<ChangeBlocksRequest> changeBlocksRequests = new ConcurrentLinkedDeque<>();
    private final Timer relaxationTimer = new Timer(1000);

    public static int combineToLight(byte sky, byte emit, byte opacity, byte selfEmit) {
        return (sky << 24) + (emit << 16) + (opacity << 8) + selfEmit;
    }

    public static byte getSkyLight(int light) {
        return (byte) (light >>> 24);
    }

    public static byte getEmitLight(int light) {
        return (byte) (light >>> 16);
    }

    public static int getOpacity(int light) {
        return (byte) (light >>> 8);
    }

    public static int getSelfEmit(int light) {
        return (byte) light;
    }

    public synchronized void setBlock(Vector3ic pos, Block block) {
        blocksCompressor.write(pos.x(), pos.y(), pos.z(), block.getIndex());
        changeBlocksRequests.add(new ChangeBlocksRequest(pos, new Vector3i(1)));
    }

    public synchronized void setBlock(int x, int y, int z, Block block) {
        blocksCompressor.write(x, y, z, block.getIndex());
        changeBlocksRequests.add(new ChangeBlocksRequest(new Vector3i(x, y, z), new Vector3i(1)));
    }

    public synchronized void setBlockSilently(int x, int y, int z, Block block) {
        blocksCompressor.write(x, y, z, block.getIndex());
    }

    public synchronized int getHeight(int x, int z) {
        return heightCompressor.read(x, 0, z);
    }

    public synchronized int getLight(int x, int y, int z) {
        return lightCompressor.read(x, y, z);
    }

    public synchronized void setBlocks(Vector3ic start, Block[][][] region) {
        int sizeX = region.length;
        int sizeY = region[0].length;
        int sizeZ = region[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    blocksCompressor.write(x + start.x(), y + start.y(), z + start.z(), region[x][y][z].getIndex());
                }
            }
        }
        changeBlocksRequests.add(new ChangeBlocksRequest(start, new Vector3i(region[0].length, region[0][0].length, region[0][0].length)));
    }

    public synchronized Block[][][] getBlocks(Vector3ic start, Vector3ic end) {
        if (!INDEXER.isInside(start) || !INDEXER.isInside(end)) {
            throw new IllegalArgumentException("You tried to get blocks out of limits.");
        }
        Vector3ic fixStart = new Vector3i(start).min(end);
        Vector3ic fixEnd = new Vector3i(start).max(end);
        Vector3ic size = new Vector3i(fixEnd).sub(fixStart).add(1, 1, 1);
        Block[][][] blocks = new Block[size.x()][size.y()][size.z()];
        for (int x = 0; x < size.x(); ++x) {
            for (int y = 0; y < size.y(); ++y) {
                for (int z = 0; z < size.z(); ++z) {
                    blocks[x][y][z] = Block.getBlockByIndex(blocksCompressor.read(x + start.x(), y + start.y(), z + start.z()));
                }
            }
        }
        return blocks;
    }

    public synchronized Block getBlock(Vector3ic pos) {
        return Block.getBlockByIndex(blocksCompressor.read(pos.x(), pos.y(), pos.z()));
    }

    public synchronized Block getBlock(int x, int y, int z) {
        return Block.getBlockByIndex(blocksCompressor.read(x, y, z));
    }

    public synchronized Block getBlockUniversal(int x, int y, int z) {
        return INDEXER.isInside(x, y, z) ? getBlock(x, y, z) : Block.AIR;
    }

    public synchronized boolean isBlockExists(Vector3ic pos) {
        return INDEXER.isInside(pos);
    }

    public synchronized boolean isBlockExists(int x, int y, int z) {
        return INDEXER.isInside(x, y, z);
    }

    // Using A Fast Voxel Traversal Algorithm for Ray Tracing by John Amanatides and Andrew Woo
    @Nullable
    public synchronized Vector3ic traverse(LineSegmentf segment, BiFunction<Block, Vector3i, Boolean> condition) {
        int cx = (int) Math.floor(segment.aX);
        int cy = (int) Math.floor(segment.aY);
        int cz = (int) Math.floor(segment.aZ);
        Vector3fc ray = new Vector3f(segment.bX - segment.aX, segment.bY - segment.aY, segment.bZ - segment.aZ);
        int dx = (int) Math.signum(ray.x());
        int dy = (int) Math.signum(ray.y());
        int dz = (int) Math.signum(ray.z());
        float tDeltaX = (dx == 0) ? Float.MAX_VALUE : dx / ray.x();
        float tMaxX = (dx == 0) ? Float.MAX_VALUE : ((dx > 0) ? tDeltaX * (1 - frac(segment.aX)) : tDeltaX * frac(segment.aX));
        float tDeltaY = (dy == 0) ? Float.MAX_VALUE : dy / ray.y();
        float tMaxY = (dy == 0) ? Float.MAX_VALUE : ((dy > 0) ? tDeltaY * (1 - frac(segment.aY)) : tDeltaY * frac(segment.aY));
        float tDeltaZ = (dz == 0) ? Float.MAX_VALUE : dz / ray.z();
        float tMaxZ = (dz == 0) ? Float.MAX_VALUE : ((dz > 0) ? tDeltaZ * (1 - frac(segment.aZ)) : tDeltaZ * frac(segment.aZ));
        if (checkVoxel(cx, cy, cz, condition)) {
            return new Vector3i(cx, cy, cz);
        }
        while (tMaxX <= 1 || tMaxY <= 1 || tMaxZ <= 1) {
            if (Maths.isFloatsEqual(tMaxX, tMaxZ) && Maths.isFloatsEqual(tMaxX, tMaxY)) {
                for (int ax = 0, nx = cx; ax <= 1; ++ax, nx += dx) {
                    for (int ay = 0, ny = cy; ay <= 1; ++ay, ny += dy) {
                        for (int az = 0, nz = cz; az <= 1; ++az, nz += dz) {
                            if ((ax > 0 || ay > 0 || az > 0) && checkVoxel(nx, ny, nz, condition)) {
                                return new Vector3i(nx, ny, nz);
                            }
                        }
                    }
                }
                cx += dx;
                cy += dy;
                cz += dz;
                tMaxX += tDeltaX;
                tMaxY += tDeltaY;
                tMaxZ += tDeltaZ;
                continue;
            }
            if (Maths.isFloatsEqual(tMaxX, tMaxZ) && tMaxX < tMaxY) {
                for (int ax = 0, nx = cx; ax <= 1; ++ax, nx += dx) {
                    for (int az = 0, nz = cz; az <= 1; ++az, nz += dz) {
                        if ((ax > 0 || az > 0) && checkVoxel(nx, cy, nz, condition)) {
                            return new Vector3i(nx, cy, nz);
                        }
                    }
                }
                cx += dx;
                cz += dz;
                tMaxX += tDeltaX;
                tMaxZ += tDeltaZ;
                continue;
            }
            if (Maths.isFloatsEqual(tMaxX, tMaxY) && tMaxX < tMaxZ) {
                for (int ax = 0, nx = cx; ax <= 1; ++ax, nx += dx) {
                    for (int ay = 0, ny = cy; ay <= 1; ++ay, ny += dy) {
                        if ((ax > 0 || ay > 0) && checkVoxel(nx, ny, cz, condition)) {
                            return new Vector3i(nx, ny, cz);
                        }
                    }
                }
                cx += dx;
                cy += dy;
                tMaxX += tDeltaX;
                tMaxY += tDeltaY;
                continue;
            }
            if (Maths.isFloatsEqual(tMaxY, tMaxZ) && tMaxY < tMaxX) {
                for (int ay = 0, ny = cy; ay <= 1; ++ay, ny += dy) {
                    for (int az = 0, nz = cz; az <= 1; ++az, nz += dz) {
                        if ((ay > 0 || az > 0) && checkVoxel(cx, ny, nz, condition)) {
                            return new Vector3i(cx, ny, nz);
                        }
                    }
                }
                cy += dy;
                cz += dz;
                tMaxY += tDeltaY;
                tMaxZ += tDeltaZ;
                continue;
            }
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    cx += dx;
                    tMaxX += tDeltaX;
                } else {
                    cz += dz;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    cy += dy;
                    tMaxY += tDeltaY;
                } else {
                    cz += dz;
                    tMaxZ += tDeltaZ;
                }
            }
            if (checkVoxel(cx, cy, cz, condition)) {
                return new Vector3i(cx, cy, cz);
            }
        }
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {
        if (!changeBlocksRequests.isEmpty()) {
            ChangeBlocksRequest changeBlocksRequest = changeBlocksRequests.poll();
            updateArea(changeBlocksRequest.getStart(), changeBlocksRequest.getSize());
        }
        if (relaxationTimer.isOver()) {
            relaxChunks();
            relaxationTimer.drop();
        }
    }

    @Override
    public void finish() {
        lightMassKernel.dispose();
    }

    public Vector3ic getChunkPosition(Vector3ic pos) {
        return new Vector3i(pos.x() / Chunk.SIZE, pos.y() / Chunk.SIZE, pos.z() / Chunk.SIZE);
    }

    public Chunk getChunk(Vector3ic pos) {
        Chunk chunk = posToChunk.get(pos);
        if (chunk == null) {
            chunk = getStorage().isExists(pos) ? getStorage().load(pos) : new Chunk(pos);
            posToChunk.put(pos, chunk);
        }
        chunk.timer.drop();
        return chunk;
    }

    synchronized void updateArea(Vector3ic start, Vector3ic size) {
        recalculateEngagedBlocksPartitive(start, size);
        recalculateHeights(start, size);
        recalculateLightPartitive(start, size);
        modifySides(start, size);
        onSidesUpdate(new SidesUpdate(new ArrayList<>(sidesToRemove), new ArrayList<>(sidesToAdd)));
        sidesToAdd.clear();
        sidesToRemove.clear();
    }

    protected abstract Storage getStorage();

    protected void compressGrids() {
        getLightCompressor().saveChunks();
        getLightCompressor().cleanTree();
        getHeightCompressor().saveChunks();
        getHeightCompressor().cleanTree();
        getBlocksCompressor().saveChunks();
        getBlocksCompressor().cleanTree();
    }

    protected void saveToDisk() {
        getStorage().save("block", getBlocksCompressor().toBytes());
        getStorage().save("light", getLightCompressor().toBytes());
        getStorage().save("height", getHeightCompressor().toBytes());
        posToChunk.forEach(this::saveChunk);
    }

    protected void loadFromDisk() {
        getBlocksCompressor().fromBytes(getStorage().load("block"));
        getLightCompressor().fromBytes(getStorage().load("light"));
        getHeightCompressor().fromBytes(getStorage().load("height"));
    }

    protected void relaxChunks() {
        posToChunk.entrySet().removeIf(entry -> {
            if (entry.getValue().timer.isOver()) {
                saveChunk(entry.getKey(), entry.getValue());
                return true;
            }
            return false;
        });
    }

    protected void saveChunk(Vector3ic pos, Chunk chunk) {
        if (chunk.modified) {
            getStorage().save(pos, chunk);
            chunk.modified = false;
        }
    }

    protected abstract void onSidesUpdate(SidesUpdate sidesUpdate);

    private synchronized void setHeight(int x, int z, int value) {
        heightCompressor.write(x, 0, z, value);
    }

    private synchronized void setLight(int x, int y, int z, int value) {
        lightCompressor.write(x, y, z, value);
    }

    private boolean checkVoxel(int x, int y, int z, BiFunction<Block, Vector3i, Boolean> condition) {
        return isBlockExists(x, y, z) && condition.apply(getBlock(x, y, z), new Vector3i(x, y, z));
    }

    private void recalculateLight(Vector3ic start, Vector3ic size) {

        Vector3ic endExcluding = new Vector3i(start).add(size);
        Vector3ic startLightUnsafe = new Vector3i(start).sub(new Vector3i(MAX_LIGHT_LEVEL));
        Vector3ic endLightExclusiveUnsafe = new Vector3i(endExcluding).add(new Vector3i(MAX_LIGHT_LEVEL));
        Vector3ic startLight = new Vector3i(startLightUnsafe).max(new Vector3i(0));
        Vector3ic endLightExclusive = new Vector3i(endLightExclusiveUnsafe).min(new Vector3i(SIZE));
        Vector3ic endLight = new Vector3i(endLightExclusive).sub(new Vector3i(1));
        Indexer lightIndexer = new Indexer(new Vector3i(endLightExclusive).sub(startLight));
        Indexer2D lightHeightIndexer = new Indexer2D(lightIndexer.getSizeX(), lightIndexer.getSizeZ());

        for (int x = 0, wx = startLight.x(); x < lightHeightIndexer.getSizeA(); ++x, ++wx) {
            for (int z = 0, wz = startLight.z(); z < lightHeightIndexer.getSizeB(); ++z, ++wz) {
                lightMassKernel.heightBuffer[(int) lightHeightIndexer.getIndex(x, z)] = getHeight(wx, wz);
            }
        }

        Arrays.fill(lightMassKernel.lightBuffer, 0, (int) lightIndexer.getVolume(), combineToLight((byte) 0, (byte) 0, (byte) 1, (byte) 0));

        Vector3ic startChunk = getChunkPosition(startLight);
        Vector3ic endChunk = getChunkPosition(endLight);
        for (int cx = startChunk.x(); cx <= endChunk.x(); ++cx) {
            for (int cy = startChunk.y(); cy <= endChunk.y(); ++cy) {
                for (int cz = startChunk.z(); cz <= endChunk.z(); ++cz) {
                    Chunk chunk = getChunk(new Vector3i(cx, cy, cz));
                    for (Map.Entry<Vector3ic, Block> entry : chunk.posToEngagedBlock.entrySet()) {
                        int lightX = entry.getKey().x() - startLight.x();
                        int lightY = entry.getKey().y() - startLight.y();
                        int lightZ = entry.getKey().z() - startLight.z();
                        if (!lightIndexer.isInside(lightX, lightY, lightZ)) {
                            continue;
                        }
                        Block block = entry.getValue();
                        int index = (int) lightIndexer.getIndex(lightX, lightY, lightZ);
                        lightMassKernel.lightBuffer[index] = combineToLight((byte) 0, (byte) 0, block.getLightOpacity(), block.getEmitLight());
                    }
                }
            }
        }

        // Add border light values to engage outer light
        for (int y = 0, wy = startLight.y(); y < lightIndexer.getSizeY(); ++y, ++wy) {
            for (int z = 0, wz = startLight.z(); z < lightIndexer.getSizeZ(); ++z, ++wz) {
                lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(0, y, z)] = getBorderLight(startLight.x(), wy, wz);
                lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(lightIndexer.getSizeX() - 1, y, z)] = getBorderLight(endLight.x(), wy, wz);
            }
        }
        for (int x = 0, wx = startLight.x(); x < lightIndexer.getSizeX(); ++x, ++wx) {
            for (int z = 0, wz = startLight.z(); z < lightIndexer.getSizeZ(); ++z, ++wz) {
                lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(x, 0, z)] = getBorderLight(wx, startLight.y(), wz);
                lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(x, lightIndexer.getSizeY() - 1, z)] = getBorderLight(wx, endLight.y(), wz);
            }
        }
        for (int x = 0, wx = startLight.x(); x < lightIndexer.getSizeX(); ++x, ++wx) {
            for (int y = 0, wy = startLight.y(); y < lightIndexer.getSizeY(); ++y, ++wy) {
                lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(x, y, 0)] = getBorderLight(wx, wy, startLight.z());
                lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(x, y, lightIndexer.getSizeZ() - 1)] = getBorderLight(wx, wy, endLight.z());
            }
        }

        lightMassKernel.calcSize = (int) lightIndexer.getVolume();
        lightMassKernel.startX = startLight.x();
        lightMassKernel.startY = startLight.y();
        lightMassKernel.startZ = startLight.z();
        lightMassKernel.sizeX = lightIndexer.getSizeX();
        lightMassKernel.sizeY = lightIndexer.getSizeY();
        lightMassKernel.sizeZ = lightIndexer.getSizeZ();
        lightMassKernel.maxX = lightIndexer.getSizeX() - 1;
        lightMassKernel.maxY = lightIndexer.getSizeY() - 1;
        lightMassKernel.maxZ = lightIndexer.getSizeZ() - 1;
        lightMassKernel.bannedStartX = startLightUnsafe.x() == startLight.x() ? 0 : -1;
        lightMassKernel.bannedStartY = startLightUnsafe.y() == startLight.y() ? 0 : -1;
        lightMassKernel.bannedStartZ = startLightUnsafe.z() == startLight.z() ? 0 : -1;
        lightMassKernel.bannedEndX = endLightExclusiveUnsafe.x() == endLightExclusive.x() ? lightMassKernel.maxX : -1;
        lightMassKernel.bannedEndY = endLightExclusiveUnsafe.y() == endLightExclusive.y() ? lightMassKernel.maxY : -1;
        lightMassKernel.bannedEndZ = endLightExclusiveUnsafe.z() == endLightExclusive.z() ? lightMassKernel.maxZ : -1;

        lightMassKernel.execute(Range.create(lightMassKernel.calcSize * MAX_LIGHT_LEVEL));

        for (int x = startLight.x(), dx = 0; dx < lightIndexer.getSizeX(); ++x, ++dx) {
            for (int y = startLight.y(), dy = 0; dy < lightIndexer.getSizeY(); ++y, ++dy) {
                for (int z = startLight.z(), dz = 0; dz < lightIndexer.getSizeZ(); ++z, ++dz) {
                    setLight(x, y, z, lightMassKernel.lightBuffer[(int) lightIndexer.getIndex(dx, dy, dz)]);
                }
            }
        }

    }

    private int getBorderLight(int x, int y, int z) {
        int light = getLight(x, y, z);
        Block block = getBlock(x, y, z);
        return combineToLight(getSkyLight(light), getEmitLight(light), block.getLightOpacity(), block.getEmitLight());
    }

    private void recalculateEngagedBlocksPartitive(Vector3ic start, Vector3ic size) {
        Timer timer = new Timer();
        for (int dx = 0; dx < size.x(); dx += UPDATE_LIMIT) {
            for (int dy = 0; dy < size.y(); dy += UPDATE_LIMIT) {
                for (int dz = 0; dz < size.z(); dz += UPDATE_LIMIT) {
                    int startX = dx + start.x();
                    int startY = dy + start.y();
                    int startZ = dz + start.z();
                    int sizeX = Math.min(UPDATE_LIMIT, size.x() - dx);
                    int sizeY = Math.min(UPDATE_LIMIT, size.y() - dy);
                    int sizeZ = Math.min(UPDATE_LIMIT, size.z() - dz);
                    recalculateEngagedBlocks(new Vector3i(startX, startY, startZ), new Vector3i(sizeX, sizeY, sizeZ));
                }
            }
        }
        log.debug("Engage blocks time: {}s", timer.spent() / 1000.0f);
    }

    private void recalculateEngagedBlocks(Vector3ic start, Vector3ic size) {

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(start).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(start).add(size).add(new Vector3i(1)).min(SIZE);
        Vector3ic sizeChanges = new Vector3i(endChangesExcluding).sub(startChanges);
        Indexer indexer = new Indexer(sizeChanges);
        Block[][][] blocks = getBlocks(startChanges, new Vector3i(endChangesExcluding).sub(new Vector3i(1)));
        for (int x = 0, wx = startChanges.x(); x < sizeChanges.x(); ++x, ++wx) {
            for (int y = 0, wy = startChanges.y(); y < sizeChanges.y(); ++y, ++wy) {
                for (int z = 0, wz = startChanges.z(); z < sizeChanges.z(); ++z, ++wz) {
                    Vector3i pos = new Vector3i(wx, wy, wz);
                    Chunk chunk = getChunk(getChunkPosition(pos));
                    chunk.modified = true;
                    Block block = blocks[x][y][z];
                    boolean engaged = false;
                    boolean occluded = true;
                    for (BlockSide blockSide : BlockSide.values()) {
                        int nx = x + blockSide.getAdjacentBlockOffset().x();
                        int ny = y + blockSide.getAdjacentBlockOffset().y();
                        int nz = z + blockSide.getAdjacentBlockOffset().z();
                        Block nextBlock = indexer.isInside(nx, ny, nz) ? blocks[nx][ny][nz] : Block.AIR;
                        engaged |= block.isVisible(nextBlock);
                        engaged |= nextBlock.isVisible(block);
                        occluded &= nextBlock.getLightOpacity() == MAX_LIGHT_LEVEL;
                    }
                    // Engage visible blocks, when at least one side is visible, to handle case with no-neighbour
                    // Engage non-occluded blocks with opacity > 1 (non-usual opacity), minimal opacity is 1
                    // Engage blocks that emits light
                    if (engaged || (block.getLightOpacity() > 1 && !occluded) || block.getEmitLight() > 0) {
                        chunk.posToEngagedBlock.put(pos, block);
                    } else {
                        chunk.posToEngagedBlock.remove(pos);
                        for (BlockSide blockSide : BlockSide.values()) {
                            SidePosition sidePosition = new SidePosition(wx, wy, wz, blockSide);
                            if (chunk.sidePosToSideData.remove(sidePosition) != null) {
                                sidesToRemove.add(sidePosition);
                            }
                        }
                    }
                }
            }
        }

    }

    private void recalculateHeights(Vector3ic start, Vector3ic size) {
        Timer timer = new Timer();
        Vector3ic endExcluding = new Vector3i(start).add(size);
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
                setHeight(x, z, height);
            }
        }
        log.debug("Heights calculation time: {}s", timer.spent() / 1000.0f);
    }

    private void recalculateLightPartitive(Vector3ic start, Vector3ic size) {
        Timer timer = new Timer();
        for (int x = 0; x < size.x(); x += UPDATE_LIMIT) {
            for (int y = 0; y < size.y(); y += UPDATE_LIMIT) {
                for (int z = 0; z < size.z(); z += UPDATE_LIMIT) {
                    int startX = x + start.x();
                    int startY = y + start.y();
                    int startZ = z + start.z();
                    int sizeX = Math.min(UPDATE_LIMIT, size.x() - x);
                    int sizeY = Math.min(UPDATE_LIMIT, size.y() - y);
                    int sizeZ = Math.min(UPDATE_LIMIT, size.z() - z);
                    recalculateLight(new Vector3i(startX, startY, startZ), new Vector3i(sizeX, sizeY, sizeZ));
                }
            }
        }
        log.debug("Light calculation time: {}s", timer.spent() / 1000.0f);
    }

    private void modifySides(Vector3ic start, Vector3ic size) {
        Timer timer = new Timer();
        Vector3ic endExcluding = new Vector3i(start).add(size);
        Vector3ic startLightUnsafe = new Vector3i(start).sub(new Vector3i(MAX_LIGHT_LEVEL));
        Vector3ic endLightExclusiveUnsafe = new Vector3i(endExcluding).add(new Vector3i(MAX_LIGHT_LEVEL));
        Vector3ic startLight = new Vector3i(startLightUnsafe).max(new Vector3i(0));
        Vector3ic endLightExclusive = new Vector3i(endLightExclusiveUnsafe).min(new Vector3i(SIZE));
        Vector3ic endLight = new Vector3i(endLightExclusive).sub(new Vector3i(1));
        Indexer lightIndexer = new Indexer(new Vector3i(endLightExclusive).sub(startLight));
        Vector3ic startChunk = getChunkPosition(startLight);
        Vector3ic endChunk = getChunkPosition(endLight);
        for (int cx = startChunk.x(); cx <= endChunk.x(); ++cx) {
            for (int cy = startChunk.y(); cy <= endChunk.y(); ++cy) {
                for (int cz = startChunk.z(); cz <= endChunk.z(); ++cz) {
                    Chunk chunk = getChunk(new Vector3i(cx, cy, cz));
                    for (Map.Entry<Vector3ic, Block> entry : chunk.posToEngagedBlock.entrySet()) {
                        Vector3ic pos = entry.getKey();
                        int lightX = pos.x() - startLight.x();
                        int lightY = pos.y() - startLight.y();
                        int lightZ = pos.z() - startLight.z();
                        if (!lightIndexer.isInside(lightX, lightY, lightZ)) {
                            continue;
                        }
                        Block block = entry.getValue();
                        for (BlockSide blockSide : BlockSide.values()) {
                            SidePosition sidePosition = new SidePosition(pos.x(), pos.y(), pos.z(), blockSide);
                            SideData oldSideData = chunk.sidePosToSideData.get(sidePosition);
                            SideData newSideData = null;
                            int nx = pos.x() + blockSide.getAdjacentBlockOffset().x();
                            int ny = pos.y() + blockSide.getAdjacentBlockOffset().y();
                            int nz = pos.z() + blockSide.getAdjacentBlockOffset().z();
                            if (block.isVisible(getBlockUniversal(nx, ny, nz))) {
                                int light = INDEXER.isInside(nx, ny, nz) ? getLight(nx, ny, nz) : 0;
                                newSideData = new SideData(block, getSkyLight(light), getEmitLight(light));
                            }
                            if (newSideData != null && !newSideData.equals(oldSideData)) {
                                sidesToAdd.add(new Side(sidePosition, newSideData));
                                chunk.sidePosToSideData.put(sidePosition, newSideData);
                                chunk.modified = true;
                            }
                            if (newSideData == null && oldSideData != null) {
                                sidesToRemove.add(sidePosition);
                                chunk.sidePosToSideData.remove(sidePosition);
                                chunk.modified = true;
                            }
                        }
                    }
                }
            }
        }
        log.debug("Side calculation time: {}s", timer.spent() / 1000.0f);
    }

    //private void updateObserver() {
    //    Vector3ic newPos = getUniverseClient().getBlocksClientRepository().getObservingPos();
    //    if (observingPos.equals(newPos)) {
    //        observingPos = newPos;
    //        //unloadChunks(true);
    //        return;
    //    }
    //}

    //public void unloadChunks(boolean unloadSides) {
    //    int viewDistance = getUniverseClient().getBalance().getViewDistance();
    //    Vector3ic observingPos = getUniverseClient().getBlocksClientRepository().getObservingPos();
    //    posToChunk.entrySet().removeIf(e -> {
    //        if (e.getKey().distanceSquared(observingPos) > viewDistance * viewDistance) {
    //            if (unloadSides) {
    //                sidesToRemove.addAll(e.getValue().sidePosToSideData.keySet());
    //            }
    //            return true;
    //        }
    //        return false;
    //    });
    //    if (unloadSides) {
    //        recalculateSides();
    //    }
    //}

}
