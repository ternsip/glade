package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.storage.Storage;
import lombok.AccessLevel;
import lombok.Getter;
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
    public static final int SIZE_X = 256;
    public static final int SIZE_Y = 256;
    public static final int SIZE_Z = 256;
    public static final int VOLUME = SIZE_X * SIZE_Y * SIZE_Z;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);

    private static final int LIGHT_UPDATE_COMBINE_DISTANCE = 4;
    private static final String BLOCKS_KEY = "blocks";
    private static final String SKY_LIGHTS_KEY = "skyLights";
    private static final String EMIT_LIGHTS_KEY = "emitLights";
    private static final String HEIGHTS_KEY = "heights";
    private static final String SIDES_KEY = "sides";
    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();

    private final Storage storage;
    private final Block[][][] blocks;
    private final byte[][][] skyLights;
    private final byte[][][] emitLights;
    private final int[][] heights;
    private final Sides sides;
    private final Timer lightUpdateTimer = new Timer(1000L);

    @Getter(AccessLevel.PUBLIC)
    private final Deque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

    @Getter(AccessLevel.PUBLIC)
    private final Deque<LightUpdateRequest> lightUpdateRequests = new ConcurrentLinkedDeque<>();

    public Blocks() {
        this.storage = new Storage("chunks");
        blocks = storage.isExists(BLOCKS_KEY) ? storage.load(BLOCKS_KEY) : new Block[SIZE_X][SIZE_Y][SIZE_Z];
        skyLights = storage.isExists(SKY_LIGHTS_KEY) ? storage.load(SKY_LIGHTS_KEY) : new byte[SIZE_X][SIZE_Y][SIZE_Z];
        emitLights = storage.isExists(EMIT_LIGHTS_KEY) ? storage.load(EMIT_LIGHTS_KEY) : new byte[SIZE_X][SIZE_Y][SIZE_Z];
        heights = storage.isExists(HEIGHTS_KEY) ? storage.load(HEIGHTS_KEY) : new int[SIZE_X][SIZE_Z];
        sides = storage.isExists(SIDES_KEY) ? storage.load(SIDES_KEY) : new Sides();
        if (!storage.isExists(BLOCKS_KEY)) {
            for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
                chunkGenerator.populate(blocks);
            }
            recalculateBlockRegion(new Vector3i(0), SIZE);
            save();
        } else {
            if (sides.getSides().size() > 0) {
                getBlocksUpdates().add(sides.generateBlockUpdate());
            }
        }
    }

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    public Block getBlock(Vector3ic pos) {
        return blocks[pos.x()][pos.y()][pos.z()];
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void setBlock(Vector3ic pos, Block block) {
        blocks[pos.x()][pos.y()][pos.z()] = block;
        updateRegionProcrastinating(pos);
    }

    public void setBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block;
        updateRegionProcrastinating(new Vector3i(x, y, z));
    }

    public void setBlocks(Vector3ic start, Block[][][] regionBlocks) {
        Vector3ic size = new Vector3i(regionBlocks.length, regionBlocks[0].length, regionBlocks[0][0].length);
        Vector3ic endExcluding = new Vector3i(start).add(size).min(SIZE);
        for (int x = start.x(), dx = 0; x < endExcluding.x(); ++x, ++dx) {
            for (int y = start.y(), dy = 0; y < endExcluding.y(); ++y, ++dy) {
                for (int z = start.z(), dz = 0; z < endExcluding.z(); ++z, ++dz) {
                    blocks[x][y][z] = regionBlocks[dx][dy][dz];
                }
            }
        }
        updateRegionProcrastinating(start, size);
    }

    public byte getSkyLight(Vector3ic pos) {
        return skyLights[pos.x()][pos.y()][pos.z()];
    }

    public byte getSkyLight(int x, int y, int z) {
        return skyLights[x][y][z];
    }

    public void setSkyLight(Vector3ic pos, byte light) {
        skyLights[pos.x()][pos.y()][pos.z()] = light;
    }

    public void setSkyLight(int x, int y, int z, byte light) {
        skyLights[x][y][z] = light;
    }

    public byte getEmitLight(Vector3ic pos) {
        return emitLights[pos.x()][pos.y()][pos.z()];
    }

    public byte getEmitLight(int x, int y, int z) {
        return emitLights[x][y][z];
    }

    public void setEmitLight(Vector3ic pos, byte light) {
        emitLights[pos.x()][pos.y()][pos.z()] = light;
    }

    public void setEmitLight(int x, int y, int z, byte light) {
        emitLights[x][y][z] = light;
    }

    public int getHeight(int x, int z) {
        return heights[x][z];
    }

    public void setHeight(int x, int z, int height) {
        heights[x][z] = height;
    }

    public boolean isBlockExists(Vector3ic pos) {
        return INDEXER.isInside(pos);
    }

    public void finish() {
        save();
        storage.commit();
        storage.finish();
    }

    private void recalculateBlockRegion(Vector3ic start, Vector3ic size) {

        Utils.assertThat(size.x() > 0 || size.y() > 0 || size.z() > 0);

        // Recalculate height maps
        Vector3ic endExcluding = new Vector3i(start).add(size);
        int minObservedHeight = SIZE_Y;
        for (int x = start.x(); x < endExcluding.x(); ++x) {
            for (int z = start.z(); z < endExcluding.z(); ++z) {
                int yAir = SIZE_Y - 1;
                for (; yAir >= 0; --yAir) {
                    if (blocks[x][yAir][z] != Block.AIR) {
                        break;
                    }
                }
                int height = yAir + 1;
                minObservedHeight = Math.min(Math.min(minObservedHeight, heights[x][z]), height);
                heights[x][z] = height;
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
                    emitLights[x][y][z] = blocks[x][y][z].getEmitLight();
                    // TODO sky if all 8 around above exists -> exists too
                    skyLights[x][y][z] = y >= heights[x][z] ? MAX_LIGHT_LEVEL : 0;
                    if (emitLights[x][y][z] > 0 || skyLights[x][y][z] > 0) {
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
            if (INDEXER.isInside(borderPos) && (skyLights[borderPos.x()][borderPos.y()][borderPos.z()] > 0 ||
                    emitLights[borderPos.x()][borderPos.y()][borderPos.z()] > 0)) {
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
            int skyLightLevel = skyLights[x][y][z];
            int emitLightLevel = emitLights[x][y][z];
            for (int k = 0; k < dx.length; ++k) {
                int nx = x + dx[k];
                int ny = y + dy[k];
                int nz = z + dz[k];
                if (!INDEXER.isInside(nx, ny, nz)) {
                    continue;
                }
                int dstLightOpacity = blocks[nx][ny][nz].getLightOpacity();
                int dstSkyLight = skyLightLevel - dstLightOpacity;
                int dstEmitLight = emitLightLevel - dstLightOpacity;
                if (skyLights[nx][ny][nz] < dstSkyLight || emitLights[nx][ny][nz] < dstEmitLight) {
                    skyLights[nx][ny][nz] = (byte) Math.max(dstSkyLight, skyLights[nx][ny][nz]);
                    emitLights[nx][ny][nz] = (byte) Math.max(dstEmitLight, emitLights[nx][ny][nz]);
                    queue.add(INDEXER.getIndex(nx, ny, nz));
                }
            }
        }

        visualUpdate(startLight, lightSize);
    }

    public void update() {
        if (lightUpdateTimer.isOver()) {
            ArrayList<LightUpdateRequest> updateRequests = new ArrayList<>();
            while (!getLightUpdateRequests().isEmpty()) {
                updateRequests.add(getLightUpdateRequests().poll());
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

    private void updateRegionProcrastinating(Vector3ic pos) {
        updateRegionProcrastinating(pos, new Vector3i(1));
    }

    private void updateRegionProcrastinating(Vector3ic start, Vector3ic size) {
        visualUpdate(start, size);
        getLightUpdateRequests().add(new LightUpdateRequest(start, size));
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

                    Block block = blocks[x][y][z];
                    for (BlockSide blockSide : BlockSide.values()) {
                        SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
                        SideData oldSideData = sides.getSides().get(sidePosition);
                        SideData newSideData = null;
                        if (block != Block.AIR) {
                            int nx = x + blockSide.getAdjacentBlockOffset().x();
                            int ny = y + blockSide.getAdjacentBlockOffset().y();
                            int nz = z + blockSide.getAdjacentBlockOffset().z();
                            if (INDEXER.isInside(nx, ny, nz)) {
                                Block nextBlock = blocks[nx][ny][nz];
                                if (nextBlock == null || (nextBlock.isSemiTransparent() && (block != nextBlock || !block.isCombineSides()))) {
                                    newSideData = new SideData((byte) Math.min(MAX_LIGHT_LEVEL, skyLights[nx][ny][nz] + emitLights[nx][ny][nz]), block);
                                }
                            } else {
                                newSideData = new SideData((byte) 0, block);
                            }
                        }
                        if (newSideData != null && !newSideData.equals(oldSideData)) {
                            sidesToAdd.add(new Side(sidePosition, newSideData));
                            sides.getSides().put(sidePosition, newSideData);
                        }
                        if (newSideData == null && oldSideData != null) {
                            sidesToRemove.add(sidePosition);
                            sides.getSides().remove(sidePosition);
                        }
                    }

                }
            }
        }

        if (sidesToRemove.size() > 0 || sidesToAdd.size() > 0) {
            getBlocksUpdates().add(new BlocksUpdate(sidesToRemove, sidesToAdd));
        }

    }

    private void save() {
        storage.save(BLOCKS_KEY, blocks);
        storage.save(SKY_LIGHTS_KEY, skyLights);
        storage.save(EMIT_LIGHTS_KEY, emitLights);
        storage.save(HEIGHTS_KEY, heights);
        storage.save(SIDES_KEY, sides);
    }

}
