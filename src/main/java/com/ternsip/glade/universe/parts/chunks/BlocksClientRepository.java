package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class BlocksClientRepository extends BlocksRepositoryBase implements Threadable, IUniverseClient {

    public static final int VIEW_DISTANCE = 256;
    public static final byte MAX_LIGHT_LEVEL = 15;

    private final Map<SidePosition, SideData> sides = new HashMap<>();
    private final ConcurrentLinkedDeque<SidesUpdate> sidesUpdates = new ConcurrentLinkedDeque<>();
    private final byte[][][] skyLights = new byte[VIEW_DISTANCE][VIEW_DISTANCE][VIEW_DISTANCE]; // TODO this can be potentially compressed by grid compressor (what about performance?)
    private final byte[][][] emitLights = new byte[VIEW_DISTANCE][VIEW_DISTANCE][VIEW_DISTANCE];
    private final int[][] heights = new int[VIEW_DISTANCE][VIEW_DISTANCE];

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

    public byte getSkyLight(Vector3ic pos) {
        return getSkyLight(pos.x(), pos.y(), pos.z());
    }

    public byte getEmitLight(Vector3ic pos) {
        return getEmitLight(pos.x(), pos.y(), pos.z());
    }

    public int getHeight(int x, int z) {
        return heights[loopIndex(x)][loopIndex(z)];
    }

    public void setHeight(int x, int z, int value) {
        heights[loopIndex(x)][loopIndex(z)] = value;
    }

    public byte getSkyLight(int x, int y, int z) {
        return skyLights[loopIndex(x)][loopIndex(y)][loopIndex(z)];
    }

    public void setSkyLight(int x, int y, int z, byte value) {
        skyLights[loopIndex(x)][loopIndex(y)][loopIndex(z)] = value;
    }

    public byte getEmitLight(int x, int y, int z) {
        return emitLights[loopIndex(x)][loopIndex(y)][loopIndex(z)];
    }

    public void setEmitLight(int x, int y, int z, byte value) {
        emitLights[loopIndex(x)][loopIndex(y)][loopIndex(z)] = value;
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
    }

    @Override
    public void finish() {
    }

    @Override
    public void setBlock(Vector3ic pos, Block block) {
        super.setBlock(pos, block);
        visualUpdate(pos, new Vector3i(1));
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        super.setBlock(x, y, z, block);
        visualUpdate(new Vector3i(x, y, z), new Vector3i(1));
    }

    @Override
    public void setBlocks(Vector3ic start, Block[][][] region) {
        super.setBlocks(start, region);
        visualUpdate(start, new Vector3i(region[0].length, region[0][0].length, region[0][0].length));
    }

    public synchronized void visualUpdate(Vector3ic start, Vector3ic size) {
        // Recalculate height maps
        int minObservedHeight = SIZE_Y;
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
        } // TODO ssbo light data, layout buffer, no_sides_updates for light
        // TODO process only visible data

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(startLight).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(startLight).add(lightSize).add(new Vector3i(1)).min(SIZE);

        // Calculate which sides should be removed or added
        SidesUpdate sidesUpdate = new SidesUpdate();

        // Recalculating added/removed sides based on blocks state and putting them to the queue
        for (int x = startChanges.x(); x < endChangesExcluding.x(); ++x) {
            for (int z = startChanges.z(); z < endChangesExcluding.z(); ++z) {
                for (int y = startChanges.y(); y < endChangesExcluding.y(); ++y) {

                    Block block = getBlock(x, y, z);
                    for (BlockSide blockSide : BlockSide.values()) {
                        SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
                        SideData oldSideData = sides.get(sidePosition);
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
                            sidesUpdate.toAdd(new Side(sidePosition, newSideData));
                            sides.put(sidePosition, newSideData);
                        }
                        if (newSideData == null && oldSideData != null) {
                            sidesUpdate.toRemove(sidePosition);
                            sides.remove(sidePosition);
                        }
                    }

                }
            }
        }

        sidesUpdates.add(sidesUpdate);

    }

    private static int loopIndex(int a) {
        int ret = a % VIEW_DISTANCE;
        if (ret < 0)
            ret += VIEW_DISTANCE;
        return ret;
    }

}
