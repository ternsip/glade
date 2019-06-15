package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockMaterial;
import com.ternsip.glade.universe.parts.generators.ChunkGenerator;
import com.ternsip.glade.universe.storage.Storage;
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<Vector2ic, HeightMap> positionToHeightMap = new ConcurrentHashMap<>();

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
            chunk.setLogicReloadRequired(true);
            chunk.setVisualReloadRequired(false);
            getPositionToChunk().put(chunk.getPosition(), chunk);
        }
        getHeightMap(new Vector2i(toSlicePosition(position)));
        return getPositionToChunk().get(position);
    }

    public void unloadChunk(Vector3ic position) {
        if (isChunkInMemory(position)) {
            saveChunk(getPositionToChunk().get(position));
            getPositionToChunk().remove(position);
            Vector2ic slicePos = toSlicePosition(position);
            if (isSliceUnloaded(slicePos)) {
                unloadHeightMap(slicePos);
            }
        }
    }

    public HeightMap getHeightMap(Vector2ic position) {
        if (!isHeightMapInMemory(position)) {
            if (!isHeightMapGenerated(position)) {
                HeightMap heightMap = generateHeightMap(position);
                saveHeightMap(heightMap);
            }
            HeightMap heightMap = loadHeightMap(position);
            getPositionToHeightMap().put(heightMap.getPosition(), heightMap);
        }
        return getPositionToHeightMap().get(position);
    }

    public void unloadHeightMap(Vector2ic position) {
        if (isHeightMapInMemory(position)) {
            saveHeightMap(getPositionToHeightMap().get(position));
            getPositionToHeightMap().remove(position);
        }
    }

    public boolean isSliceUnloaded(Vector2ic slicePos) {
        return getPositionToChunk().keySet().stream().noneMatch(e -> slicePos.equals(e.x(), e.z()));
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

    public int getLight(Vector3ic pos) {
        return getChunk(getChunkPositionForBlock(pos)).getLight(getBlockPositionInsideChunk(pos));
    }

    public void setLight(Vector3ic pos, int light) {
        getChunk(getChunkPositionForBlock(pos)).setLight(getBlockPositionInsideChunk(pos), light);
    }

    public Vector2i toSlicePosition(Vector3ic pos) {
        return new Vector2i(pos.x(), pos.z());
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

    public void recalculateBlockRegion(Vector3ic start, Vector3ic size) {

        int sizeX = size.x() + 2 * MAX_LIGHT_LEVEL;
        int sizeY = size.y() + 2 * MAX_LIGHT_LEVEL;
        int sizeZ = size.z() + 2 * MAX_LIGHT_LEVEL;

        Chunks chunks = getUniverse().getChunks();

        // Recalculate height maps
        for (int x = start.x() - MAX_LIGHT_LEVEL, sx = 0; sx < size.x() + MAX_LIGHT_LEVEL; ++x, ++sx) {
            for (int z = start.z() - MAX_LIGHT_LEVEL, sz = 0; sz < size.z() + MAX_LIGHT_LEVEL; ++z, ++sz) {

                Vector3i wPos = new Vector3i(x, 0, z);
                Vector2i slicePos = toSlicePosition(getChunkPositionForBlock(wPos));
                if (!isHeightMapInMemory(slicePos)) {
                    continue;
                }

                Vector2i positionInsideSlice = toSlicePosition(getBlockPositionInsideChunk(wPos));
                int sliceX = positionInsideSlice.x();
                int sliceZ = positionInsideSlice.y();

                HeightMap heightMap = getHeightMap(slicePos);

                int yAir = SKY_BLOCK_HEIGHT;
                for (; yAir >= 0; --yAir) {
                    if (chunks.getBlock(new Vector3i(x, yAir, z)) != Block.AIR) {
                        break;
                    }
                }
                heightMap.getHeights()[sliceX][sliceZ].setThroughAir(yAir + 1);

                int yGas = SKY_BLOCK_HEIGHT;
                for (; yGas >= 0; --yGas) {
                    if (chunks.getBlock(new Vector3i(x, yGas, z)).getBlockMaterial() != BlockMaterial.GAS) {
                        break;
                    }
                }
                heightMap.getHeights()[sliceX][sliceZ].setThroughGas(yGas + 1);

                int yGasAndLiquid = SKY_BLOCK_HEIGHT;
                for (; yGasAndLiquid >= 0; --yGasAndLiquid) {
                    BlockMaterial material = chunks.getBlock(new Vector3i(x, yGasAndLiquid, z)).getBlockMaterial();
                    if (material != BlockMaterial.GAS && material != BlockMaterial.LIQUID) {
                        break;
                    }
                }
                heightMap.getHeights()[sliceX][sliceZ].setThroughGasAndLiquid(yGasAndLiquid + 1);

                int ySoil = SKY_BLOCK_HEIGHT;
                for (; ySoil >= 0; --ySoil) {
                    BlockMaterial material = chunks.getBlock(new Vector3i(x, ySoil, z)).getBlockMaterial();
                    if (material == BlockMaterial.SOIL) {
                        break;
                    }
                }
                heightMap.getHeights()[sliceX][sliceZ].setUntilSoil(ySoil + 1);

            }
        }

        // Recalculate light maps
        int[][][] lightCoverage = new int[sizeX][sizeY][sizeZ];
        int[][][] lightOpacity = new int[sizeX][sizeY][sizeZ];
        Queue<Integer> queue = new ArrayDeque<>();

        for (int x = 0, wx = start.x() - MAX_LIGHT_LEVEL; x < sizeX; ++x, ++wx) {
            for (int z = 0, wz = start.z() - MAX_LIGHT_LEVEL; z < sizeZ; ++z, ++wz) {
                Vector3ic wPos = new Vector3i(wx, 0, wz);
                Vector2ic slicePos = toSlicePosition(getChunkPositionForBlock(wPos));
                if (!isHeightMapInMemory(slicePos)) {
                    for (int y = 0; y < sizeY; ++y) {
                        lightCoverage[x][y][z] = 0;
                        lightOpacity[x][y][z] = MAX_LIGHT_LEVEL;
                    }
                    continue;
                }
                Vector2i positionInsideSlice = toSlicePosition(getBlockPositionInsideChunk(wPos));
                Heights heights = getHeightMap(slicePos).getHeights(positionInsideSlice);
                for (int y = 0, wy = start.y() - MAX_LIGHT_LEVEL; y < sizeY; ++y, ++wy) {
                    wPos = new Vector3i(wx, wy, wz);
                    Vector3ic chunkPos = getChunkPositionForBlock(wPos);
                    lightCoverage[x][y][z] = 0;
                    lightOpacity[x][y][z] = MAX_LIGHT_LEVEL;
                    if (isChunkInMemory(chunkPos)) {
                        Block block = getBlock(wPos);
                        lightCoverage[x][y][z] = block.getEmitLight();
                        lightOpacity[x][y][z] = block.getLightOpacity();
                    }
                    if (heights.getThroughAir() <= wPos.y()) {
                        lightCoverage[x][y][z] = MAX_LIGHT_LEVEL;
                    }
                    if (lightCoverage[x][y][z] > 0 && heights.getThroughAir() >= wPos.y()) {
                        queue.add(x + y * sizeX * sizeZ + z * sizeX);
                    }
                }
            }
        }

        int[] dx = {1, 0, 0, -1, 0, 0};
        int[] dy = {0, 1, 0, 0, -1, 0};
        int[] dz = {0, 0, 1, 0, 0, -1};
        while (!queue.isEmpty()) {
            Integer top = queue.poll();
            int x = top % sizeX;
            int y = top / (sizeX * sizeZ);
            int z = (top / sizeX) % sizeZ;
            int lightLevel = lightCoverage[x][y][z];
            for (int k = 0; k < dx.length; ++k) {
                int nx = x + dx[k];
                int ny = y + dy[k];
                int nz = z + dz[k];
                if (nx < 0 || ny < 0 || nz < 0 || nx >= sizeX || ny >= sizeY || nz >= sizeZ) {
                    continue;
                }
                int destinationLight = lightLevel - lightOpacity[nx][ny][nz];
                if (lightCoverage[nx][ny][nz] < destinationLight) {
                    lightCoverage[nx][ny][nz] = destinationLight;
                    queue.add(nx + ny * sizeX * sizeZ + nz * sizeX);
                }
            }
        }

        for (int x = MAX_LIGHT_LEVEL, wx = start.x(), ax = 0; ax < size.x(); ++x, ++wx, ++ax) {
            for (int z = MAX_LIGHT_LEVEL, wz = start.z(), az = 0; az < size.z(); ++z, ++wz, ++az) {
                for (int y = MAX_LIGHT_LEVEL, wy = start.y(), ay = 0; ay < size.y(); ++y, ++wy, ++ay) {
                    setLight(new Vector3i(wx, wy, wz), lightCoverage[x][y][z]);
                }
            }
        }

        Vector3ic startChunk = getChunkPositionForBlock(start);
        Vector3ic endChunk = getChunkPositionForBlock(new Vector3i(start).add(size));
        for (int x = startChunk.x(); x <= endChunk.x(); ++x) {
            for (int y = startChunk.y(); y <= endChunk.y(); ++y) {
                for (int z = startChunk.z(); z <= endChunk.z(); ++z) {
                    getChunk(new Vector3i(x, y, z)).setVisualReloadRequired(true);
                }
            }
        }

    }

    public boolean isHeightMapInMemory(Vector2ic position) {
        return getPositionToHeightMap().containsKey(position);
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

    private HeightMap generateHeightMap(Vector2ic position) {
        return new HeightMap(position);
    }

    private boolean isHeightMapGenerated(Vector2ic position) {
        return getStorage().isExists(position);
    }

    private void saveHeightMap(HeightMap heightMap) {
        getStorage().save(heightMap.getPosition(), heightMap);
    }

    private HeightMap loadHeightMap(Vector2ic position) {
        return getStorage().load(position);
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
