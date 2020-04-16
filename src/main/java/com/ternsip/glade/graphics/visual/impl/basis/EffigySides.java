package com.ternsip.glade.graphics.visual.impl.basis;

import com.google.common.collect.ImmutableMap;
import com.ternsip.glade.common.logic.*;
import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.shader.impl.LightMassShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.LightSource;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.impl.EntityCameraEffects;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.chunks.BlocksClientRepository;
import com.ternsip.glade.universe.parts.chunks.ChangeBlocksRequest;
import com.ternsip.glade.universe.parts.chunks.GridCompressor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.ternsip.glade.graphics.shader.base.RasterShader.INDICES;
import static com.ternsip.glade.graphics.shader.base.RasterShader.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.ChunkShader.*;
import static com.ternsip.glade.graphics.visual.impl.basis.EffigySides.SideIndexData.*;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.INDEXER;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.SIZE;

@Slf4j
public class EffigySides extends Effigy<ChunkShader> {

    public static final long TIME_PERIOD_MILLISECONDS = 60_000L;
    public static final float TIME_PERIOD_DIVISOR = 1000f;
    public static final int UPDATE_LIMIT = 128;
    public static final byte MAX_LIGHT_LEVEL = 15;
    private static final int BLOCK_TYPE_NORMAL = 0;
    private static final int BLOCK_TYPE_WATER = 1;
    private static final CubeSideMeshData SIDE_FRONT = new CubeSideMeshData(
            new float[]{1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            new boolean[]{true, false, false, false, false, true, true, true}
    );
    private static final CubeSideMeshData SIDE_RIGHT = new CubeSideMeshData(
            new float[]{1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0},
            new float[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
            new boolean[]{false, false, false, true, true, true, true, false}
    );
    private static final CubeSideMeshData SIDE_TOP = new CubeSideMeshData(
            new float[]{1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1},
            new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            new boolean[]{true, true, true, false, false, false, false, true}
    );
    private static final CubeSideMeshData SIDE_LEFT = new CubeSideMeshData(
            new float[]{0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            new float[]{-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0},
            new boolean[]{true, false, false, false, false, true, true, true}
    );
    private static final CubeSideMeshData SIDE_BOTTOM = new CubeSideMeshData(
            new float[]{0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1},
            new float[]{0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0},
            new boolean[]{false, true, true, true, true, false, false, false}
    );
    private static final CubeSideMeshData SIDE_BACK = new CubeSideMeshData(
            new float[]{1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0},
            new float[]{0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1},
            new boolean[]{false, true, true, true, true, false, false, false}
    );
    private static final Map<BlockSide, CubeSideMeshData> ALL_SIDES = ImmutableMap.<BlockSide, CubeSideMeshData>builder()
            .put(BlockSide.BACK, SIDE_BACK)
            .put(BlockSide.BOTTOM, SIDE_BOTTOM)
            .put(BlockSide.LEFT, SIDE_LEFT)
            .put(BlockSide.TOP, SIDE_TOP)
            .put(BlockSide.RIGHT, SIDE_RIGHT)
            .put(BlockSide.FRONT, SIDE_FRONT)
            .build();

    @Getter(lazy = true)
    private final LightMassShader lightMassShader = getGraphics().getShaderRepository().getShader(LightMassShader.class);
    private final ShaderBuffer lightBuffer = new ShaderBuffer(UPDATE_LIMIT * UPDATE_LIMIT * UPDATE_LIMIT);
    private final ShaderBuffer heightBuffer = new ShaderBuffer(UPDATE_LIMIT * UPDATE_LIMIT);
    private final Map<SidePosition, Integer> sidePosToActiveSideIndex = new HashMap<>();
    private final ArrayList<SidePosition> activeSides = new ArrayList<>();
    private final ArrayList<Mesh> meshes = new ArrayList<>();
    private final Map<Vector3ic, Chunk> posToChunk = new HashMap<>();
    private final ArrayList<SidePosition> sidesToRemove = new ArrayList<>();
    private final ArrayList<Side> sidesToAdd = new ArrayList<>();
    private final GridCompressor lightCompressor = new GridCompressor();
    private final GridCompressor heightCompressor = new GridCompressor();

    public Vector3ic getChunkPosition(Vector3ic pos) {
        return new Vector3i(pos.x() / Chunk.SIZE, pos.y() / Chunk.SIZE, pos.z() / Chunk.SIZE);
    }

    public Chunk getChunk(Vector3ic pos) {
        Chunk chunk = posToChunk.get(pos);
        if (chunk == null) {
            chunk = new Chunk();
            posToChunk.put(pos, chunk);
        }
        return chunk;
    }

    public int getHeight(int x, int z) {
        return heightCompressor.read(x, 0, z);
    }

    public void setHeight(int x, int z, int value) {
        heightCompressor.write(x, 0, z, value);
    }

    public int getLight(int x, int y, int z) {
        return lightCompressor.read(x, y, z);
    }

    public void setLight(int x, int y, int z, int value) {
        lightCompressor.write(x, y, z, value);
    }

    public int combineToLight(byte sky, byte emit, byte opacity, byte selfEmit) {
        return (sky << 24) + (emit << 16) + (opacity << 8) + selfEmit;
    }

    public byte getSkyLight(int light) {
        return (byte) (light >>> 24);
    }

    public byte getEmitLight(int light) {
        return (byte) (light >>> 16);
    }

    public int getOpacity(int light) {
        return (byte) (light >>> 8);
    }

    public int getSelfEmit(int light) {
        return (byte) light;
    }

    @Override
    public Matrix4fc getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    // TODO prevent changes during this
    public void render() {
        if (!getUniverseClient().getBlocksClientRepository().getChangeBlocksRequests().isEmpty()) {
            ChangeBlocksRequest changeBlocksRequest = getUniverseClient().getBlocksClientRepository().getChangeBlocksRequests().poll();
            Vector3ic start = changeBlocksRequest.getStart();
            Vector3ic size = changeBlocksRequest.getSize();
            synchronized (getUniverseClient().getBlocksClientRepository()) {
                recalculateEngagedBlocksPartitive(start, size);
                recalculateHeights(start, size);
                recalculateLightPartitive(start, size);
                modifySides(start, size);
                recalculateSides();
            }
        }
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        getShader().getTime().load((System.currentTimeMillis() % TIME_PERIOD_MILLISECONDS) / TIME_PERIOD_DIVISOR);
        getShader().getSun().load(getSun());
        getShader().getSamplers().loadDefault();
        boolean isUnderwater = getUniverseClient().getEntityClientRepository().getEntityByClass(EntityCameraEffects.class).isUnderWater();
        getShader().getFogColor().load(isUnderwater ? getUniverseClient().getBalance().getUnderwaterFogColor() : getUniverseClient().getBalance().getFogColor());
        getShader().getFogDensity().load(isUnderwater ? getUniverseClient().getBalance().getUnderwaterFogDensity() : getUniverseClient().getBalance().getFogDensity());
        getShader().getFogGradient().load(isUnderwater ? getUniverseClient().getBalance().getUnderwaterFogGradient() : getUniverseClient().getBalance().getFogGradient());
        for (Mesh mesh : meshes) {
            mesh.render();
        }
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        return Model.builder().build();
    }

    @Override
    public boolean deleteModelOnFinish() {
        // TODO remove this method
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        meshes.forEach(Mesh::finish);
        lightBuffer.finish();
        heightBuffer.finish();
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }

    public Light getSun() {
        EntitySun sun = getUniverseClient().getEntityClientRepository().getEntityByClass(EntitySun.class);
        return new LightSource(sun.getPositionInterpolated(), sun.getColor(), sun.getIntensity());
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
                heightBuffer.writeInt((int) lightHeightIndexer.getIndex(x, z), getHeight(wx, wz));
            }
        }

        lightBuffer.fill(0, (int) lightIndexer.getVolume(), combineToLight((byte) 0, (byte) 0, (byte) 1, (byte) 0));

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
                        lightBuffer.writeInt(index, combineToLight((byte) 0, (byte) 0, block.getLightOpacity(), block.getEmitLight()));
                    }
                }
            }
        }

        // Add border light values to engage outer light
        for (int y = 0, wy = startLight.y(); y < lightIndexer.getSizeY(); ++y, ++wy) {
            for (int z = 0, wz = startLight.z(); z < lightIndexer.getSizeZ(); ++z, ++wz) {
                lightBuffer.writeInt((int) lightIndexer.getIndex(0, y, z), getBorderLight(startLight.x(), wy, wz));
                lightBuffer.writeInt((int) lightIndexer.getIndex(lightIndexer.getSizeX() - 1, y, z), getBorderLight(endLight.x(), wy, wz));
            }
        }
        for (int x = 0, wx = startLight.x(); x < lightIndexer.getSizeX(); ++x, ++wx) {
            for (int z = 0, wz = startLight.z(); z < lightIndexer.getSizeZ(); ++z, ++wz) {
                lightBuffer.writeInt((int) lightIndexer.getIndex(x, 0, z), getBorderLight(wx, startLight.y(), wz));
                lightBuffer.writeInt((int) lightIndexer.getIndex(x, lightIndexer.getSizeY() - 1, z), getBorderLight(wx, endLight.y(), wz));
            }
        }
        for (int x = 0, wx = startLight.x(); x < lightIndexer.getSizeX(); ++x, ++wx) {
            for (int y = 0, wy = startLight.y(); y < lightIndexer.getSizeY(); ++y, ++wy) {
                lightBuffer.writeInt((int) lightIndexer.getIndex(x, y, 0), getBorderLight(wx, wy, startLight.z()));
                lightBuffer.writeInt((int) lightIndexer.getIndex(x, y, lightIndexer.getSizeZ() - 1), getBorderLight(wx, wy, endLight.z()));
            }
        }

        lightBuffer.updateSubBuffer(0, (int) lightIndexer.getVolume());
        heightBuffer.updateSubBuffer(0, (int) lightHeightIndexer.getVolume());

        getLightMassShader().start();
        getLightMassShader().getLightBuffer().load(lightBuffer);
        getLightMassShader().getHeightBuffer().load(heightBuffer);
        getLightMassShader().getStartX().load(startLight.x());
        getLightMassShader().getStartY().load(startLight.y());
        getLightMassShader().getStartZ().load(startLight.z());
        getLightMassShader().getSizeX().load(lightIndexer.getSizeX());
        getLightMassShader().getSizeY().load(lightIndexer.getSizeY());
        getLightMassShader().getSizeZ().load(lightIndexer.getSizeZ());
        getLightMassShader().getDisableStartX().load(startLightUnsafe.x() == startLight.x());
        getLightMassShader().getDisableStartY().load(startLightUnsafe.y() == startLight.y());
        getLightMassShader().getDisableStartZ().load(startLightUnsafe.z() == startLight.z());
        getLightMassShader().getDisableEndX().load(endLightExclusiveUnsafe.x() == endLightExclusive.x());
        getLightMassShader().getDisableEndY().load(endLightExclusiveUnsafe.y() == endLightExclusive.y());
        getLightMassShader().getDisableEndZ().load(endLightExclusiveUnsafe.z() == endLightExclusive.z());
        for (int i = 0; i < MAX_LIGHT_LEVEL; ++i) {
            getLightMassShader().compute((int) lightIndexer.getVolume());
            //glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
            //glMemoryBarrier(GL_ALL_BARRIER_BITS);
        }
        getLightMassShader().stop();

        lightBuffer.read(0, (int) lightIndexer.getVolume());
        for (int x = startLight.x(), dx = 0; dx < lightIndexer.getSizeX(); ++x, ++dx) {
            for (int y = startLight.y(), dy = 0; dy < lightIndexer.getSizeY(); ++y, ++dy) {
                for (int z = startLight.z(), dz = 0; dz < lightIndexer.getSizeZ(); ++z, ++dz) {
                    setLight(x, y, z, lightBuffer.readInt((int) lightIndexer.getIndex(dx, dy, dz)));
                }
            }
        }

    }

    private int getBorderLight(int x, int y, int z) {
        int light = getLight(x, y, z);
        Block block = getUniverseClient().getBlocksClientRepository().getBlock(x, y, z);
        return combineToLight(getSkyLight(light), getEmitLight(light), block.getLightOpacity(), block.getEmitLight());
    }

    private void recalculateEngagedBlocksPartitive(Vector3ic start, Vector3ic size) {
        Timer timer = new Timer();
        int maxSize = UPDATE_LIMIT - 2;
        for (int dx = 0; dx < size.x(); dx += maxSize) {
            for (int dy = 0; dy < size.y(); dy += maxSize) {
                for (int dz = 0; dz < size.z(); dz += maxSize) {
                    int startX = dx + start.x();
                    int startY = dy + start.y();
                    int startZ = dz + start.z();
                    int sizeX = Math.min(maxSize, size.x() - dx);
                    int sizeY = Math.min(maxSize, size.y() - dy);
                    int sizeZ = Math.min(maxSize, size.z() - dz);
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
        BlocksClientRepository repo = getUniverseClient().getBlocksClientRepository();
        Block[][][] blocks = repo.getBlocks(startChanges, new Vector3i(endChangesExcluding).sub(new Vector3i(1)));
        for (int x = 0, wx = startChanges.x(); x < sizeChanges.x(); ++x, ++wx) {
            for (int y = 0, wy = startChanges.y(); y < sizeChanges.y(); ++y, ++wy) {
                for (int z = 0, wz = startChanges.z(); z < sizeChanges.z(); ++z, ++wz) {
                    Vector3i pos = new Vector3i(wx, wy, wz);
                    Chunk chunk = getChunk(getChunkPosition(pos));
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
                    if (getUniverseClient().getBlocksClientRepository().getBlock(x, yAir, z) != Block.AIR) {
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
        int maxSize = UPDATE_LIMIT - MAX_LIGHT_LEVEL * 2;
        for (int x = 0; x < size.x(); x += maxSize) {
            for (int y = 0; y < size.y(); y += maxSize) {
                for (int z = 0; z < size.z(); z += maxSize) {
                    int startX = x + start.x();
                    int startY = y + start.y();
                    int startZ = z + start.z();
                    int sizeX = Math.min(maxSize, size.x() - x);
                    int sizeY = Math.min(maxSize, size.y() - y);
                    int sizeZ = Math.min(maxSize, size.z() - z);
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
        BlocksClientRepository repo = getUniverseClient().getBlocksClientRepository();
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
                            if (block.isVisible(repo.getBlockUniversal(nx, ny, nz))) {
                                int light = INDEXER.isInside(nx, ny, nz) ? getLight(nx, ny, nz) : 0;
                                newSideData = new SideData(block, getSkyLight(light), getEmitLight(light));
                            }
                            if (newSideData != null && !newSideData.equals(oldSideData)) {
                                sidesToAdd.add(new Side(sidePosition, newSideData));
                                chunk.sidePosToSideData.put(sidePosition, newSideData);
                            }
                            if (newSideData == null && oldSideData != null) {
                                sidesToRemove.add(sidePosition);
                                chunk.sidePosToSideData.remove(sidePosition);
                            }
                        }
                    }
                }
            }
        }
        log.debug("Side calculation time: {}s", timer.spent() / 1000.0f);
    }

    private void recalculateSides() {
        Timer timer = new Timer();
        if (sidesToAdd.isEmpty() && sidesToRemove.isEmpty()) {
            return;
        }
        int numberOfSidesThatExists = (int) sidesToAdd.stream().filter(side -> sidePosToActiveSideIndex.get(side.sidePosition) != null).count();
        int oldLength = activeSides.size();
        int newLength = oldLength + (sidesToAdd.size() - sidesToRemove.size()) - numberOfSidesThatExists;
        int meshesNumber = newLength / SIDES_PER_MESH + (newLength % SIDES_PER_MESH > 0 ? 1 : 0);
        while (meshes.size() < meshesNumber) {
            Mesh mesh = new Mesh(
                    new MeshAttributes()
                            .add(INDICES, Utils.arrayToBuffer(new int[SIDES_PER_MESH * INDEX_SIDE_SIZE]))
                            .add(VERTICES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * VERTEX_SIDE_SIZE]))
                            .add(SKY_LIGHT, Utils.arrayToBuffer(new float[SIDES_PER_MESH * SKY_LIGHT_SIDE_SIZE]))
                            .add(EMIT_LIGHT, Utils.arrayToBuffer(new float[SIDES_PER_MESH * EMIT_LIGHT_SIDE_SIZE]))
                            .add(NORMALS, Utils.arrayToBuffer(new float[SIDES_PER_MESH * NORMAL_SIDE_SIZE]))
                            .add(TEXTURES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * TEXTURE_SIDE_SIZE]))
                            .add(ATLAS_NUMBER, Utils.arrayToBuffer(new float[SIDES_PER_MESH * ATLAS_NUMBER_SIDE_SIZE]))
                            .add(ATLAS_LAYER, Utils.arrayToBuffer(new float[SIDES_PER_MESH * ATLAS_LAYER_SIDE_SIZE]))
                            .add(ATLAS_MAX_UV, Utils.arrayToBuffer(new float[SIDES_PER_MESH * ATLAS_MAX_UV_SIDE_SIZE]))
                            .add(BLOCK_TYPE, Utils.arrayToBuffer(new float[SIDES_PER_MESH * BLOCK_TYPE_SIDE_SIZE])),
                    new Material(), true
            );
            mesh.setIndicesCount(0);
            mesh.setVertexCount(0);
            meshes.add(mesh);
        }
        Iterator<SidePosition> toRemove = sidesToRemove.iterator();
        boolean[] changedMeshes = new boolean[meshes.size()];
        for (Side side : sidesToAdd) {
            SidePosition sidePositionSrc = side.sidePosition;
            Integer sideIndexSrc = sidePosToActiveSideIndex.get(sidePositionSrc);
            // If side already exists refresh it with new data
            if (sideIndexSrc != null) {
                fillSide(sideIndexSrc, side);
                changedMeshes[sideIndexSrc / SIDES_PER_MESH] = true;
                continue;
            }
            // If side need to be removed - fill it with side for adding
            if (toRemove.hasNext()) {
                SidePosition sidePosition = toRemove.next();
                int sideIndex = sidePosToActiveSideIndex.get(sidePosition);
                fillSide(sideIndex, side);
                changedMeshes[sideIndex / SIDES_PER_MESH] = true;
                Utils.assertThat(sideIndex < activeSides.size());
                sidePosToActiveSideIndex.remove(sidePosition);
                sidePosToActiveSideIndex.put(sidePositionSrc, sideIndex);
                activeSides.set(sideIndex, sidePositionSrc);
                continue;
            }
            // Just append side to the end
            int sideIndex = activeSides.size();
            fillSide(sideIndex, side);
            changedMeshes[sideIndex / SIDES_PER_MESH] = true;
            sidePosToActiveSideIndex.put(sidePositionSrc, sideIndex);
            activeSides.add(sidePositionSrc);
        }
        // Relocate last side to the place of one that should be removed
        while (toRemove.hasNext()) {
            SidePosition sidePositionDst = toRemove.next();
            int sideIndexDst = sidePosToActiveSideIndex.get(sidePositionDst);
            int sideIndexSrc = activeSides.size() - 1;
            relocateSide(sideIndexSrc, sideIndexDst);
            changedMeshes[sideIndexSrc / SIDES_PER_MESH] = true;
            changedMeshes[sideIndexDst / SIDES_PER_MESH] = true;
            Utils.assertThat(sideIndexDst < activeSides.size());
            SidePosition sidePositionSrc = activeSides.get(sideIndexSrc);
            sidePosToActiveSideIndex.put(sidePositionSrc, sideIndexDst);
            sidePosToActiveSideIndex.remove(sidePositionDst);
            activeSides.set(sideIndexDst, sidePositionSrc);
            activeSides.remove(sideIndexSrc);
        }
        while (meshes.size() > meshesNumber) {
            int currentSize = meshes.size();
            meshes.get(currentSize - 1).finish();
            meshes.remove(currentSize - 1);
        }
        int sizeOfTheLastMesh = newLength % SIDES_PER_MESH;
        for (int i = 0; i < meshesNumber; ++i) {
            if (changedMeshes[i]) {
                meshes.get(i).setVertexCount((i == meshesNumber - 1) ? (sizeOfTheLastMesh * VERTICES_PER_SIDE) : SIDES_PER_MESH * VERTICES_PER_SIDE);
                meshes.get(i).setIndicesCount((i == meshesNumber - 1) ? (sizeOfTheLastMesh * SIDE_INDICES.length) : SIDES_PER_MESH * SIDE_INDICES.length);
                meshes.get(i).updateBuffers();
            }
        }
        sidesToRemove.clear();
        sidesToAdd.clear();
        log.debug("Mesh refresh time: {}s", timer.spent() / 1000.0f);
    }

    private void relocateSide(int sideIndexSrc, int sideIndexDst) {

        if (sideIndexDst == sideIndexSrc) {
            return;
        }

        SideIndexData sideIndexDataSrc = new SideIndexData(sideIndexSrc, meshes);
        SideIndexData sideIndexDataDst = new SideIndexData(sideIndexDst, meshes);

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexDataDst.indices.put(i + sideIndexDataDst.indexPos, SIDE_INDICES[i] + sideIndexDataDst.vertexStart);
        }

        float[] vertices = new float[VERTEX_SIDE_SIZE];
        sideIndexDataSrc.vertices.position(sideIndexDataSrc.vertexPos);
        sideIndexDataSrc.vertices.get(vertices);
        sideIndexDataDst.vertices.position(sideIndexDataDst.vertexPos);
        sideIndexDataDst.vertices.put(vertices);

        float[] skyLights = new float[SKY_LIGHT_SIDE_SIZE];
        sideIndexDataSrc.skyLights.position(sideIndexDataSrc.skyLightPos);
        sideIndexDataSrc.skyLights.get(skyLights);
        sideIndexDataDst.skyLights.position(sideIndexDataDst.skyLightPos);
        sideIndexDataDst.skyLights.put(skyLights);

        float[] emitLights = new float[EMIT_LIGHT_SIDE_SIZE];
        sideIndexDataSrc.emitLights.position(sideIndexDataSrc.emitLightPos);
        sideIndexDataSrc.emitLights.get(emitLights);
        sideIndexDataDst.emitLights.position(sideIndexDataDst.emitLightPos);
        sideIndexDataDst.emitLights.put(emitLights);

        float[] blockTypes = new float[BLOCK_TYPE_SIDE_SIZE];
        sideIndexDataSrc.blockTypes.position(sideIndexDataSrc.blockTypePos);
        sideIndexDataSrc.blockTypes.get(blockTypes);
        sideIndexDataDst.blockTypes.position(sideIndexDataDst.blockTypePos);
        sideIndexDataDst.blockTypes.put(blockTypes);

        float[] normals = new float[NORMAL_SIDE_SIZE];
        sideIndexDataSrc.normals.position(sideIndexDataSrc.normalPos);
        sideIndexDataSrc.normals.get(normals);
        sideIndexDataDst.normals.position(sideIndexDataDst.normalPos);
        sideIndexDataDst.normals.put(normals);

        float[] textures = new float[TEXTURE_SIDE_SIZE];
        sideIndexDataSrc.textures.position(sideIndexDataSrc.texturePos);
        sideIndexDataSrc.textures.get(textures);
        sideIndexDataDst.textures.position(sideIndexDataDst.texturePos);
        sideIndexDataDst.textures.put(textures);

        float[] atlasNumber = new float[ATLAS_NUMBER_SIDE_SIZE];
        sideIndexDataSrc.atlasNumber.position(sideIndexDataSrc.atlasNumberPos);
        sideIndexDataSrc.atlasNumber.get(atlasNumber);
        sideIndexDataDst.atlasNumber.position(sideIndexDataDst.atlasNumberPos);
        sideIndexDataDst.atlasNumber.put(atlasNumber);

        float[] atlasLayer = new float[ATLAS_LAYER_SIDE_SIZE];
        sideIndexDataSrc.atlasLayer.position(sideIndexDataSrc.atlasLayerPos);
        sideIndexDataSrc.atlasLayer.get(atlasLayer);
        sideIndexDataDst.atlasLayer.position(sideIndexDataDst.atlasLayerPos);
        sideIndexDataDst.atlasLayer.put(atlasLayer);

        float[] atlasMaxUV = new float[ATLAS_MAX_UV_SIDE_SIZE];
        sideIndexDataSrc.atlasMaxUV.position(sideIndexDataSrc.atlasMaxUVPos);
        sideIndexDataSrc.atlasMaxUV.get(atlasMaxUV);
        sideIndexDataDst.atlasMaxUV.position(sideIndexDataDst.atlasMaxUVPos);
        sideIndexDataDst.atlasMaxUV.put(atlasMaxUV);

    }

    private void fillSide(int sideIndex, Side side) {

        SideIndexData sideIndexData = new SideIndexData(sideIndex, meshes);
        BlockSide blockSide = side.sidePosition.side;
        CubeSideMeshData cubeSideMeshData = ALL_SIDES.get(blockSide);
        int dx = side.sidePosition.x;
        int dy = side.sidePosition.y;
        int dz = side.sidePosition.z;
        Texture atlasFragment = getGraphics().getTexturePackRepository().getCubeMap(side.sideData.block).getTextureByBlockSide(blockSide);

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexData.indices.put(i + sideIndexData.indexPos, SIDE_INDICES[i] + sideIndexData.vertexStart);
        }

        int blockType = side.sideData.block == Block.WATER ? BLOCK_TYPE_WATER : BLOCK_TYPE_NORMAL;

        for (int i = 0; i < VERTICES_PER_SIDE; i++) {
            int vIdx = i * VERTICES.getNumberPerVertex();
            sideIndexData.vertices.put(vIdx + sideIndexData.vertexPos, cubeSideMeshData.vertices[vIdx] + dx);
            sideIndexData.vertices.put(vIdx + sideIndexData.vertexPos + 1, cubeSideMeshData.vertices[vIdx + 1] + dy);
            sideIndexData.vertices.put(vIdx + sideIndexData.vertexPos + 2, cubeSideMeshData.vertices[vIdx + 2] + dz);

            sideIndexData.skyLights.put(i * SKY_LIGHT.getNumberPerVertex() + sideIndexData.skyLightPos, (float) side.sideData.skyLight / MAX_LIGHT_LEVEL);
            sideIndexData.emitLights.put(i * EMIT_LIGHT.getNumberPerVertex() + sideIndexData.emitLightPos, (float) side.sideData.emitLight / MAX_LIGHT_LEVEL);
            sideIndexData.blockTypes.put(i * BLOCK_TYPE.getNumberPerVertex() + sideIndexData.blockTypePos, blockType);

            int nIdx = i * NORMALS.getNumberPerVertex();
            sideIndexData.normals.put(nIdx + sideIndexData.normalPos, cubeSideMeshData.normals[nIdx]);
            sideIndexData.normals.put(nIdx + sideIndexData.normalPos + 1, cubeSideMeshData.normals[nIdx + 1]);
            sideIndexData.normals.put(nIdx + sideIndexData.normalPos + 2, cubeSideMeshData.normals[nIdx + 2]);

            int tIdx = i * TEXTURES.getNumberPerVertex();
            sideIndexData.textures.put(tIdx + sideIndexData.texturePos, cubeSideMeshData.textures[tIdx] ? 1 : 0);
            sideIndexData.textures.put(tIdx + sideIndexData.texturePos + 1, cubeSideMeshData.textures[tIdx + 1] ? 1 : 0);

            int aNumberIdx = i * ATLAS_NUMBER.getNumberPerVertex();
            sideIndexData.atlasNumber.put(aNumberIdx + sideIndexData.atlasNumberPos, (float) atlasFragment.getAtlasTexture().getAtlasNumber());

            int aLayerIdx = i * ATLAS_LAYER.getNumberPerVertex();
            sideIndexData.atlasLayer.put(aLayerIdx + sideIndexData.atlasLayerPos, (float) atlasFragment.getAtlasTexture().getLayer());

            int aMaxUVIdx = i * ATLAS_MAX_UV.getNumberPerVertex();
            sideIndexData.atlasMaxUV.put(aMaxUVIdx + sideIndexData.atlasMaxUVPos, atlasFragment.getAtlasTexture().getMaxUV().x());
            sideIndexData.atlasMaxUV.put(aMaxUVIdx + sideIndexData.atlasMaxUVPos + 1, atlasFragment.getAtlasTexture().getMaxUV().y());

        }

    }

    @RequiredArgsConstructor
    private static class CubeSideMeshData {

        private final float[] vertices;
        private final float[] normals;
        private final boolean[] textures;

    }

    @RequiredArgsConstructor
    public static class SideIndexData {

        static final int VERTICES_PER_SIDE = 4;
        static final int[] SIDE_INDICES = new int[]{0, 1, 2, 2, 3, 0};
        static final int SIDES_PER_MESH = Mesh.MAX_VERTICES / VERTICES_PER_SIDE;

        static final int VERTEX_SIDE_SIZE = VERTICES_PER_SIDE * VERTICES.getNumberPerVertex();
        static final int SKY_LIGHT_SIDE_SIZE = VERTICES_PER_SIDE * SKY_LIGHT.getNumberPerVertex();
        static final int EMIT_LIGHT_SIDE_SIZE = VERTICES_PER_SIDE * EMIT_LIGHT.getNumberPerVertex();
        static final int INDEX_SIDE_SIZE = SIDE_INDICES.length;
        static final int NORMAL_SIDE_SIZE = VERTICES_PER_SIDE * NORMALS.getNumberPerVertex();
        static final int TEXTURE_SIDE_SIZE = VERTICES_PER_SIDE * TEXTURES.getNumberPerVertex();
        static final int ATLAS_NUMBER_SIDE_SIZE = VERTICES_PER_SIDE * ATLAS_NUMBER.getNumberPerVertex();
        static final int ATLAS_LAYER_SIDE_SIZE = VERTICES_PER_SIDE * ATLAS_LAYER.getNumberPerVertex();
        static final int ATLAS_MAX_UV_SIDE_SIZE = VERTICES_PER_SIDE * ATLAS_MAX_UV.getNumberPerVertex();
        static final int BLOCK_TYPE_SIDE_SIZE = VERTICES_PER_SIDE * BLOCK_TYPE.getNumberPerVertex();

        int vertexStart;
        int vertexPos;
        int skyLightPos;
        int emitLightPos;
        int indexPos;
        int normalPos;
        int texturePos;
        int atlasNumberPos;
        int atlasLayerPos;
        int atlasMaxUVPos;
        int blockTypePos;

        IntBuffer indices;
        FloatBuffer vertices;
        FloatBuffer skyLights;
        FloatBuffer emitLights;
        FloatBuffer normals;
        FloatBuffer textures;
        FloatBuffer atlasNumber;
        FloatBuffer atlasLayer;
        FloatBuffer atlasMaxUV;
        FloatBuffer blockTypes;

        SideIndexData(int sideIndex, ArrayList<Mesh> meshes) {

            int meshPos = sideIndex / SIDES_PER_MESH;
            int sideOffset = sideIndex % SIDES_PER_MESH;
            Utils.assertThat(meshes.size() > meshPos);
            Mesh mesh = meshes.get(meshPos);
            MeshAttributes meshAttributes = mesh.getMeshAttributes();

            this.vertexStart = sideOffset * VERTICES_PER_SIDE;
            this.vertexPos = sideOffset * VERTEX_SIDE_SIZE;
            this.skyLightPos = sideOffset * SKY_LIGHT_SIDE_SIZE;
            this.emitLightPos = sideOffset * EMIT_LIGHT_SIDE_SIZE;
            this.indexPos = sideOffset * INDEX_SIDE_SIZE;
            this.normalPos = sideOffset * NORMAL_SIDE_SIZE;
            this.texturePos = sideOffset * TEXTURE_SIDE_SIZE;
            this.atlasNumberPos = sideOffset * ATLAS_NUMBER_SIDE_SIZE;
            this.atlasLayerPos = sideOffset * ATLAS_LAYER_SIDE_SIZE;
            this.atlasMaxUVPos = sideOffset * ATLAS_MAX_UV_SIDE_SIZE;
            this.blockTypePos = sideOffset * BLOCK_TYPE_SIDE_SIZE;

            this.indices = meshAttributes.getBuffer(INDICES);
            this.vertices = meshAttributes.getBuffer(VERTICES);
            this.skyLights = meshAttributes.getBuffer(SKY_LIGHT);
            this.emitLights = meshAttributes.getBuffer(EMIT_LIGHT);
            this.normals = meshAttributes.getBuffer(NORMALS);
            this.textures = meshAttributes.getBuffer(TEXTURES);
            this.atlasNumber = meshAttributes.getBuffer(ATLAS_NUMBER);
            this.atlasLayer = meshAttributes.getBuffer(ATLAS_LAYER);
            this.atlasMaxUV = meshAttributes.getBuffer(ATLAS_MAX_UV);
            this.blockTypes = meshAttributes.getBuffer(BLOCK_TYPE);

        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class SidePosition {

        private final int x;
        private final int y;
        private final int z;
        private final BlockSide side;

    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class Side {

        private final SidePosition sidePosition;
        private final SideData sideData;

    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class SideData {

        private final Block block;
        private final byte skyLight;
        private final byte emitLight;

    }

    private static class Chunk {

        private static final int SIZE = 32;
        private final Map<SidePosition, SideData> sidePosToSideData = new HashMap<>();
        private final Map<Vector3ic, Block> posToEngagedBlock = new HashMap<>();

    }


}
