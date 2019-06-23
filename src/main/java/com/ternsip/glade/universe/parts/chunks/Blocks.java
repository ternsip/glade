package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
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

    public static final byte MAX_LIGHT_LEVEL = 16;
    public static final int SIZE_X = 256;
    public static final int SIZE_Y = 256;
    public static final int SIZE_Z = 256;
    public static final int VOLUME = SIZE_X * SIZE_Y * SIZE_Z;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);

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
    private final Map<SidePosition, SideData> sides;

    @Getter(AccessLevel.PUBLIC)
    private final Deque<BlocksUpdate> blocksUpdates = new ConcurrentLinkedDeque<>();

    public Blocks() {
        this.storage = new Storage("chunks");
        blocks = storage.isExists(BLOCKS_KEY) ? storage.load(BLOCKS_KEY) : new Block[SIZE_X][SIZE_Y][SIZE_Z];
        skyLights = storage.isExists(SKY_LIGHTS_KEY) ? storage.load(SKY_LIGHTS_KEY) : new byte[SIZE_X][SIZE_Y][SIZE_Z];
        emitLights = storage.isExists(EMIT_LIGHTS_KEY) ? storage.load(EMIT_LIGHTS_KEY) : new byte[SIZE_X][SIZE_Y][SIZE_Z];
        heights = storage.isExists(HEIGHTS_KEY) ? storage.load(HEIGHTS_KEY) : new int[SIZE_X][SIZE_Z];
        sides = storage.isExists(SIDES_KEY) ? storage.load(SIDES_KEY) : new HashMap<>();
        if (!storage.isExists(BLOCKS_KEY)) {
            for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
                chunkGenerator.populate(blocks);
            }
            recalculateBlockRegion(new Vector3i(0), SIZE);
            save();
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

    public Block setBlock(Vector3ic pos, Block block) {
        return blocks[pos.x()][pos.y()][pos.z()] = block;
    }

    public Block setBlock(int x, int y, int z, Block block) {
        return blocks[x][y][z] = block;
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
        storage.commit();
        storage.finish();
    }

    public void recalculateBlockRegion(Vector3ic pos) {
        recalculateBlockRegion(pos, new Vector3i(1));
    }

    public void recalculateBlockRegion(Vector3ic start, Vector3ic size) {

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
        Queue<Integer> queue = new ArrayDeque<>();
        Vector3ic newStart = new Vector3i(start.x(), Math.min(start.y(), minObservedHeight), start.z());
        Vector3ic newEndExcluding = new Vector3i(endExcluding);
        Vector3ic startLight = new Vector3i(newStart).sub(new Vector3i(MAX_LIGHT_LEVEL)).max(new Vector3i(0));
        Vector3ic endLightExcluding = new Vector3i(newEndExcluding).add(new Vector3i(MAX_LIGHT_LEVEL)).min(SIZE);

        for (int x = startLight.x(); x < endLightExcluding.x(); ++x) {
            for (int z = startLight.z(); z < endLightExcluding.z(); ++z) {
                for (int y = startLight.y(); y < endLightExcluding.y(); ++y) {
                    emitLights[x][y][z] = blocks[x][y][z].getEmitLight();
                    // TODO sky if all 8 around above exists -> exists too
                    skyLights[x][y][z] = y >= heights[x][z] ? MAX_LIGHT_LEVEL : 0;
                    if ((emitLights[x][y][z] > 0 || skyLights[x][y][z] > 0) &&
                            x >= newStart.x() && y >= newStart.y() && z >= newStart.z() &&
                            x < newEndExcluding.x() && y < newEndExcluding.y() && z < newEndExcluding.z()) {
                        queue.add(INDEXER.getIndex(x, y, z));
                    }
                }
            }
        }

        // Start light propagation BFS
        int[] dx = {1, 0, 0, -1, 0, 0};
        int[] dy = {0, 1, 0, 0, -1, 0};
        int[] dz = {0, 0, 1, 0, 0, -1};
        while (!queue.isEmpty()) {
            Integer top = queue.poll();
            int x = INDEXER.getX(top);
            int y = INDEXER.getY(top);
            int z = INDEXER.getZ(top);
            byte skyLightLevel = skyLights[x][y][z];
            byte emitLightLevel = emitLights[x][y][z];
            for (int k = 0; k < dx.length; ++k) {
                int nx = x + dx[k];
                int ny = y + dy[k];
                int nz = z + dz[k];
                if (!INDEXER.isInside(nx, ny, nz)) {
                    continue;
                }
                byte dstLightOpacity = blocks[nx][ny][nz].getLightOpacity();
                byte dstSkyLight = (byte) (skyLightLevel - dstLightOpacity);
                byte dstEmitLight = (byte) (emitLightLevel - dstLightOpacity);
                if (skyLights[nx][ny][nz] < dstSkyLight || emitLights[nx][ny][nz] < dstEmitLight) {
                    skyLights[nx][ny][nz] = (byte) Math.max(dstSkyLight, skyLights[nx][ny][nz]);
                    emitLights[nx][ny][nz] = (byte) Math.max(dstEmitLight,  emitLights[nx][ny][nz]);
                    queue.add(INDEXER.getIndex(nx, ny, nz));
                }
            }
        }

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(startLight).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(endLightExcluding).add(new Vector3i(1)).min(SIZE);

        // Calculate which sides should be removed or added
        List<Side> sidesToAdd = new ArrayList<>();
        List<SidePosition> sidesToRemove = new ArrayList<>();

        for (int x = startChanges.x(); x < endChangesExcluding.x(); ++x) {
            for (int z = startChanges.z(); z < endChangesExcluding.z(); ++z) {
                for (int y = startChanges.y(); y < endChangesExcluding.y(); ++y) {

                    Block block = blocks[x][y][z];
                    for (BlockSide blockSide : BlockSide.values()) {
                        SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
                        SideData oldSideData = sides.get(sidePosition);
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
                            sides.put(sidePosition, newSideData);
                        }
                        if (newSideData == null && oldSideData != null) {
                            sidesToRemove.add(sidePosition);
                            sides.remove(sidePosition);
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
    }

}
