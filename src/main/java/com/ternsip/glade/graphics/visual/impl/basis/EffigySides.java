package com.ternsip.glade.graphics.visual.impl.basis;

import com.google.common.collect.ImmutableMap;
import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Indexer2D;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
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
import com.ternsip.glade.universe.parts.chunks.ChangeBlocksRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.ternsip.glade.graphics.shader.base.RasterShader.INDICES;
import static com.ternsip.glade.graphics.shader.base.RasterShader.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.ChunkShader.*;
import static com.ternsip.glade.graphics.visual.impl.basis.EffigySides.SideIndexData.*;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.*;

public class EffigySides extends Effigy<ChunkShader> {

    public static final long TIME_PERIOD_MILLISECONDS = 60_000L;
    public static final float TIME_PERIOD_DIVISOR = 1000f;
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

    public static final int VIEW_AREA_X = 256;
    public static final int VIEW_AREA_Y = SIZE_Y;
    public static final int VIEW_AREA_Z = 256;
    public static final Indexer VISIBILITY_INDEXER = new Indexer(VIEW_AREA_X, SIZE_Y, VIEW_AREA_Z);
    public static final Indexer2D HEIGHTS_INDEXER = new Indexer2D(VIEW_AREA_X, VIEW_AREA_Z);
    public static final byte MAX_LIGHT_LEVEL = 15;

    @Getter(lazy = true)
    private final LightMassShader lightMassShader = getGraphics().getShaderRepository().getShader(LightMassShader.class);

    private final ShaderBuffer skyBuffer = new ShaderBuffer((int) VISIBILITY_INDEXER.getVolume());
    private final ShaderBuffer emitBuffer = new ShaderBuffer((int) VISIBILITY_INDEXER.getVolume());
    private final ShaderBuffer selfEmitBuffer = new ShaderBuffer((int) VISIBILITY_INDEXER.getVolume());
    private final ShaderBuffer opacityBuffer = new ShaderBuffer((int) VISIBILITY_INDEXER.getVolume());
    private final ShaderBuffer heightBuffer = new ShaderBuffer((int) HEIGHTS_INDEXER.getVolume());

    private final Map<SidePosition, Block> sidePosToBlock = new HashMap<>();
    private final Map<SidePosition, Integer> sides = new HashMap<>();
    private final ArrayList<SidePosition> activeSides = new ArrayList<>();
    private final ArrayList<Mesh> meshes = new ArrayList<>();

    public void updateBuffers(Vector3ic start, Vector3ic size) {
        Vector3ic endExcluding = new Vector3i(start).add(size);
        int minIndexMinor = Integer.MAX_VALUE;
        int maxIndexMinor = -1;
        int minIndexMajor = Integer.MAX_VALUE;
        int maxIndexMajor = -1;
        int minIndexHeightMinor = Integer.MAX_VALUE;
        int maxIndexHeightMinor = -1;
        int minIndexHeightMajor = Integer.MAX_VALUE;
        int maxIndexHeightMajor = -1;
        int startIndex = (int) VISIBILITY_INDEXER.getIndexLooping(start.x(), start.y(), start.z());
        int startHeightIndex = (int) HEIGHTS_INDEXER.getIndexLooping(start.x(), start.z());
        for (int x = start.x(); x < endExcluding.x(); ++x) {
            for (int z = start.z(); z < endExcluding.z(); ++z) {
                for (int y = start.y(); y < endExcluding.y(); ++y) {
                    int index = (int) VISIBILITY_INDEXER.getIndexLooping(x, y, z);
                    Block block = getUniverseClient().getBlocksClientRepository().getBlock(x, y, z);
                    selfEmitBuffer.writeInt(index, block.getEmitLight());
                    opacityBuffer.writeInt(index, block.getLightOpacity());
                    if (index < startIndex) {
                        minIndexMinor = Math.min(minIndexMinor, index);
                        maxIndexMinor = Math.max(maxIndexMinor, index);
                    } else {
                        minIndexMajor = Math.min(minIndexMajor, index);
                        maxIndexMajor = Math.max(maxIndexMajor, index);
                    }
                }
                int heightIndex = (int) HEIGHTS_INDEXER.getIndexLooping(x, z);
                if (heightBuffer.readInt(heightIndex) <= endExcluding.y()) {
                    int yAir = endExcluding.y() - 1;
                    for (; yAir >= 0; --yAir) {
                        if (getUniverseClient().getBlocksClientRepository().getBlock(x, yAir, z) != Block.AIR) {
                            break;
                        }
                    }
                    heightBuffer.writeInt(heightIndex, yAir + 1);
                }
                if (heightIndex < startHeightIndex) {
                    minIndexHeightMinor = Math.min(minIndexHeightMinor, heightIndex);
                    maxIndexHeightMinor = Math.max(maxIndexHeightMinor, heightIndex);
                } else {
                    minIndexHeightMajor = Math.min(minIndexHeightMajor, heightIndex);
                    maxIndexHeightMajor = Math.max(maxIndexHeightMajor, heightIndex);
                }
            }
        }
        if (maxIndexMinor != -1) {
            selfEmitBuffer.updateSubBuffer(minIndexMinor, maxIndexMinor + 1 - minIndexMinor);
            opacityBuffer.updateSubBuffer(minIndexMinor, maxIndexMinor + 1 - minIndexMinor);
            heightBuffer.updateSubBuffer(minIndexHeightMinor, maxIndexHeightMinor + 1 - minIndexHeightMinor);
        }
        selfEmitBuffer.updateSubBuffer(minIndexMajor, maxIndexMajor + 1 - minIndexMajor);
        opacityBuffer.updateSubBuffer(minIndexMajor, maxIndexMajor + 1 - minIndexMajor);
        heightBuffer.updateSubBuffer(minIndexHeightMajor, maxIndexHeightMajor + 1 - minIndexHeightMajor);
    }

    public void applyChanges(ChangeBlocksRequest changeBlocksRequest) {

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(changeBlocksRequest.getStart()).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(changeBlocksRequest.getStart()).add(changeBlocksRequest.getSize()).add(new Vector3i(1)).min(SIZE);

        // Calculate which sides should be removed or added
        List<SidePosition> sidesToRemove = new ArrayList<>();
        List<Side> sidesToAdd = new ArrayList<>();

        // Recalculating added/removed sides based on blocks state and putting them to the queue
        for (int x = startChanges.x(); x < endChangesExcluding.x(); ++x) {
            for (int z = startChanges.z(); z < endChangesExcluding.z(); ++z) {
                for (int y = startChanges.y(); y < endChangesExcluding.y(); ++y) {

                    Block block = getUniverseClient().getBlocksClientRepository().getBlock(x, y, z); // TODO prevent changes during this
                    for (BlockSide blockSide : BlockSide.values()) {
                        SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
                        Block oldSideBlock = sidePosToBlock.get(sidePosition);
                        Block newSideBlock = null;
                        if (block != Block.AIR) {
                            int nx = x + blockSide.getAdjacentBlockOffset().x();
                            int ny = y + blockSide.getAdjacentBlockOffset().y();
                            int nz = z + blockSide.getAdjacentBlockOffset().z();
                            if (INDEXER.isInside(nx, ny, nz)) {
                                Block nextBlock = getUniverseClient().getBlocksClientRepository().getBlock(nx, ny, nz);
                                if (nextBlock == null || (nextBlock.isSemiTransparent() && (block != nextBlock || !block.isCombineSides()))) {
                                    newSideBlock = block;
                                }
                            } else {
                                newSideBlock = block;
                            }
                        }
                        if (newSideBlock != null && newSideBlock != oldSideBlock) {
                            sidesToAdd.add(new Side(sidePosition, newSideBlock));
                            sidePosToBlock.put(sidePosition, newSideBlock);
                        }
                        if (newSideBlock == null && oldSideBlock != null) {
                            sidesToRemove.add(sidePosition);
                            sidePosToBlock.remove(sidePosition);
                        }
                    }

                }
            }
        }

        if (sidesToAdd.isEmpty() && sidesToRemove.isEmpty()) {
            return;
        }

        int numberOfSidesThatExists = (int) sidesToAdd.stream().filter(side -> sides.get(side.getSidePosition()) != null).count();
        int oldLength = activeSides.size();
        int newLength = oldLength + (sidesToAdd.size() - sidesToRemove.size()) - numberOfSidesThatExists;
        int meshesNumber = newLength / SIDES_PER_MESH + (newLength % SIDES_PER_MESH > 0 ? 1 : 0);
        while (meshes.size() < meshesNumber) {
            Mesh mesh = new Mesh(
                    new MeshAttributes()
                            .add(INDICES, Utils.arrayToBuffer(new int[SIDES_PER_MESH * INDEX_SIDE_SIZE]))
                            .add(VERTICES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * VERTEX_SIDE_SIZE]))
                            .add(NORMALS, Utils.arrayToBuffer(new float[SIDES_PER_MESH * NORMAL_SIDE_SIZE]))
                            .add(TEXTURES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * TEXTURE_SIDE_SIZE]))
                            .add(ATLAS_NUMBER, Utils.arrayToBuffer(new float[SIDES_PER_MESH * ATLAS_NUMBER_SIDE_SIZE]))
                            .add(ATLAS_LAYER, Utils.arrayToBuffer(new float[SIDES_PER_MESH * ATLAS_LAYER_SIDE_SIZE]))
                            .add(ATLAS_MAX_UV, Utils.arrayToBuffer(new float[SIDES_PER_MESH * ATLAS_MAX_UV_SIDE_SIZE]))
                            .add(BLOCK_TYPE, Utils.arrayToBuffer(new float[SIDES_PER_MESH * BLOCK_TYPE_SIDE_SIZE]))
                            .add(FACE_INDEX, Utils.arrayToBuffer(new float[SIDES_PER_MESH * FACE_INDEX_SIDE_SIZE])),
                    new Material(), true
            );
            mesh.setIndicesCount(0);
            mesh.setVertexCount(0);
            meshes.add(mesh);
        }
        Iterator<SidePosition> toRemove = sidesToRemove.iterator();
        boolean[] changedMeshes = new boolean[meshes.size()];
        for (Side side : sidesToAdd) {
            SidePosition sidePositionSrc = side.getSidePosition();
            Integer sideIndexSrc = sides.get(sidePositionSrc);
            // If side already exists refresh it with new data
            if (sideIndexSrc != null) {
                fillSide(sideIndexSrc, side);
                changedMeshes[sideIndexSrc / SIDES_PER_MESH] = true;
                continue;
            }
            // If side need to be removed - fill it with side for adding
            if (toRemove.hasNext()) {
                SidePosition sidePosition = toRemove.next();
                int sideIndex = sides.get(sidePosition);
                fillSide(sideIndex, side);
                changedMeshes[sideIndex / SIDES_PER_MESH] = true;
                Utils.assertThat(sideIndex < activeSides.size());
                sides.remove(sidePosition);
                sides.put(sidePositionSrc, sideIndex);
                activeSides.set(sideIndex, sidePositionSrc);
                continue;
            }
            // Just append side to the end
            int sideIndex = activeSides.size();
            fillSide(sideIndex, side);
            changedMeshes[sideIndex / SIDES_PER_MESH] = true;
            sides.put(sidePositionSrc, sideIndex);
            activeSides.add(sidePositionSrc);
        }
        // Relocate last side to the place of one that should be removed
        while (toRemove.hasNext()) {
            SidePosition sidePositionDst = toRemove.next();
            int sideIndexDst = sides.get(sidePositionDst);
            int sideIndexSrc = activeSides.size() - 1;
            relocateSide(sideIndexSrc, sideIndexDst);
            changedMeshes[sideIndexSrc / SIDES_PER_MESH] = true;
            changedMeshes[sideIndexDst / SIDES_PER_MESH] = true;
            Utils.assertThat(sideIndexDst < activeSides.size());
            SidePosition sidePositionSrc = activeSides.get(sideIndexSrc);
            sides.put(sidePositionSrc, sideIndexDst);
            sides.remove(sidePositionDst);
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
    }

    @Override
    public Matrix4fc getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render() {
        if (!getUniverseClient().getBlocksClientRepository().getChangeBlocksRequests().isEmpty()) {
            ChangeBlocksRequest changeBlocksRequest = getUniverseClient().getBlocksClientRepository().getChangeBlocksRequests().poll();
            Vector3ic startLight = new Vector3i(changeBlocksRequest.getStart()).sub(new Vector3i(MAX_LIGHT_LEVEL - 1)).max(new Vector3i(0));
            Vector3ic endLightExcluding = new Vector3i(changeBlocksRequest.getEndExcluding()).add(new Vector3i(MAX_LIGHT_LEVEL - 1)).min(SIZE);
            Vector3ic lightSize = new Vector3i(endLightExcluding).sub(startLight).min(VISIBILITY_INDEXER.getSize());
            updateBuffers(startLight, lightSize);
            getLightMassShader().start();
            getLightMassShader().getSkyBuffer().load(skyBuffer);
            getLightMassShader().getEmitBuffer().load(emitBuffer);
            getLightMassShader().getSelfEmitBuffer().load(selfEmitBuffer);
            getLightMassShader().getOpacityBuffer().load(opacityBuffer);
            getLightMassShader().getHeightBuffer().load(heightBuffer);
            getLightMassShader().getStartX().load(startLight.x());
            getLightMassShader().getStartY().load(startLight.y());
            getLightMassShader().getStartZ().load(startLight.z());
            getLightMassShader().getSizeX().load(lightSize.x());
            getLightMassShader().getSizeY().load(lightSize.y());
            getLightMassShader().getSizeZ().load(lightSize.z());
            for (int i = 0; i < MAX_LIGHT_LEVEL; ++i) {
                getLightMassShader().compute(lightSize.x() * lightSize.y() * lightSize.z());
                //glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
                //glMemoryBarrier(GL_ALL_BARRIER_BITS);
            }
            //skyBuffer.read();
            getLightMassShader().stop();

            applyChanges(changeBlocksRequest);
        }

        getShader().start();
        getShader().getEmitBuffer().load(emitBuffer);
        getShader().getSkyBuffer().load(skyBuffer);
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
        skyBuffer.finish();
        emitBuffer.finish();
        selfEmitBuffer.finish();
        opacityBuffer.finish();
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

    private void relocateSide(int sideIndexSrc, int sideIndexDst) {

        if (sideIndexDst == sideIndexSrc) {
            return;
        }

        SideIndexData sideIndexDataSrc = new SideIndexData(sideIndexSrc, meshes);
        SideIndexData sideIndexDataDst = new SideIndexData(sideIndexDst, meshes);

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexDataDst.getIndices().put(i + sideIndexDataDst.getIndexPos(), SIDE_INDICES[i] + sideIndexDataDst.getVertexStart());
        }

        float[] vertices = new float[VERTEX_SIDE_SIZE];
        sideIndexDataSrc.getVertices().position(sideIndexDataSrc.getVertexPos());
        sideIndexDataSrc.getVertices().get(vertices);
        sideIndexDataDst.getVertices().position(sideIndexDataDst.getVertexPos());
        sideIndexDataDst.getVertices().put(vertices);

        float[] blockTypes = new float[BLOCK_TYPE_SIDE_SIZE];
        sideIndexDataSrc.getBlockTypes().position(sideIndexDataSrc.getBlockTypePos());
        sideIndexDataSrc.getBlockTypes().get(blockTypes);
        sideIndexDataDst.getBlockTypes().position(sideIndexDataDst.getBlockTypePos());
        sideIndexDataDst.getBlockTypes().put(blockTypes);

        float[] normals = new float[NORMAL_SIDE_SIZE];
        sideIndexDataSrc.getNormals().position(sideIndexDataSrc.getNormalPos());
        sideIndexDataSrc.getNormals().get(normals);
        sideIndexDataDst.getNormals().position(sideIndexDataDst.getNormalPos());
        sideIndexDataDst.getNormals().put(normals);

        float[] textures = new float[TEXTURE_SIDE_SIZE];
        sideIndexDataSrc.getTextures().position(sideIndexDataSrc.getTexturePos());
        sideIndexDataSrc.getTextures().get(textures);
        sideIndexDataDst.getTextures().position(sideIndexDataDst.getTexturePos());
        sideIndexDataDst.getTextures().put(textures);

        float[] atlasNumber = new float[ATLAS_NUMBER_SIDE_SIZE];
        sideIndexDataSrc.getAtlasNumber().position(sideIndexDataSrc.getAtlasNumberPos());
        sideIndexDataSrc.getAtlasNumber().get(atlasNumber);
        sideIndexDataDst.getAtlasNumber().position(sideIndexDataDst.getAtlasNumberPos());
        sideIndexDataDst.getAtlasNumber().put(atlasNumber);

        float[] atlasLayer = new float[ATLAS_LAYER_SIDE_SIZE];
        sideIndexDataSrc.getAtlasLayer().position(sideIndexDataSrc.getAtlasLayerPos());
        sideIndexDataSrc.getAtlasLayer().get(atlasLayer);
        sideIndexDataDst.getAtlasLayer().position(sideIndexDataDst.getAtlasLayerPos());
        sideIndexDataDst.getAtlasLayer().put(atlasLayer);

        float[] atlasMaxUV = new float[ATLAS_MAX_UV_SIDE_SIZE];
        sideIndexDataSrc.getAtlasMaxUV().position(sideIndexDataSrc.getAtlasMaxUVPos());
        sideIndexDataSrc.getAtlasMaxUV().get(atlasMaxUV);
        sideIndexDataDst.getAtlasMaxUV().position(sideIndexDataDst.getAtlasMaxUVPos());
        sideIndexDataDst.getAtlasMaxUV().put(atlasMaxUV);

        float[] faceIndices = new float[FACE_INDEX_SIDE_SIZE];
        sideIndexDataSrc.getFaceIndices().position(sideIndexDataSrc.getFaceIndexPos());
        sideIndexDataSrc.getFaceIndices().get(faceIndices);
        sideIndexDataDst.getFaceIndices().position(sideIndexDataDst.getFaceIndexPos());
        sideIndexDataDst.getFaceIndices().put(faceIndices);

    }

    private void fillSide(int sideIndex, Side side) {

        SideIndexData sideIndexData = new SideIndexData(sideIndex, meshes);
        BlockSide blockSide = side.getSidePosition().getSide();
        CubeSideMeshData cubeSideMeshData = ALL_SIDES.get(blockSide);
        int dx = side.getSidePosition().getX();
        int dy = side.getSidePosition().getY();
        int dz = side.getSidePosition().getZ();
        int index = (int) VISIBILITY_INDEXER.getIndexLooping(
                dx + blockSide.getAdjacentBlockOffset().x(),
                dy + blockSide.getAdjacentBlockOffset().y(),
                dz + blockSide.getAdjacentBlockOffset().z()
        );
        Texture atlasFragment = getGraphics().getTexturePackRepository().getCubeMap(side.getBlock()).getTextureByBlockSide(blockSide);

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexData.getIndices().put(i + sideIndexData.getIndexPos(), SIDE_INDICES[i] + sideIndexData.getVertexStart());
        }

        int blockType = side.getBlock() == Block.WATER ? BLOCK_TYPE_WATER : BLOCK_TYPE_NORMAL;

        for (int i = 0; i < VERTICES_PER_SIDE; i++) {
            int vIdx = i * VERTICES.getNumberPerVertex();
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos(), cubeSideMeshData.getVertices()[vIdx] + dx);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 1, cubeSideMeshData.getVertices()[vIdx + 1] + dy);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 2, cubeSideMeshData.getVertices()[vIdx + 2] + dz);

            sideIndexData.getBlockTypes().put(i * BLOCK_TYPE.getNumberPerVertex() + sideIndexData.getBlockTypePos(), blockType);

            int nIdx = i * NORMALS.getNumberPerVertex();
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos(), cubeSideMeshData.getNormals()[nIdx]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 1, cubeSideMeshData.getNormals()[nIdx + 1]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 2, cubeSideMeshData.getNormals()[nIdx + 2]);

            int tIdx = i * TEXTURES.getNumberPerVertex();
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos(), cubeSideMeshData.getTextures()[tIdx] ? 1 : 0);
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos() + 1, cubeSideMeshData.getTextures()[tIdx + 1] ? 1 : 0);

            int aNumberIdx = i * ATLAS_NUMBER.getNumberPerVertex();
            sideIndexData.getAtlasNumber().put(aNumberIdx + sideIndexData.getAtlasNumberPos(), (float) atlasFragment.getAtlasTexture().getAtlasNumber());

            int aLayerIdx = i * ATLAS_LAYER.getNumberPerVertex();
            sideIndexData.getAtlasLayer().put(aLayerIdx + sideIndexData.getAtlasLayerPos(), (float) atlasFragment.getAtlasTexture().getLayer());

            int aMaxUVIdx = i * ATLAS_MAX_UV.getNumberPerVertex();
            sideIndexData.getAtlasMaxUV().put(aMaxUVIdx + sideIndexData.getAtlasMaxUVPos(), atlasFragment.getAtlasTexture().getMaxUV().x());
            sideIndexData.getAtlasMaxUV().put(aMaxUVIdx + sideIndexData.getAtlasMaxUVPos() + 1, atlasFragment.getAtlasTexture().getMaxUV().y());

            sideIndexData.getFaceIndices().put(i * FACE_INDEX.getNumberPerVertex() + sideIndexData.getFaceIndexPos(), index);
        }

    }

    @RequiredArgsConstructor
    @Getter
    public static class CubeSideMeshData {

        private final float[] vertices;
        private final float[] normals;
        private final boolean[] textures;

    }

    @RequiredArgsConstructor
    @Getter
    public static class SideIndexData {

        public static final int VERTICES_PER_SIDE = 4;
        public static final int[] SIDE_INDICES = new int[]{0, 1, 2, 2, 3, 0};
        public static final int SIDES_PER_MESH = Mesh.MAX_VERTICES / VERTICES_PER_SIDE;

        public static final int VERTEX_SIDE_SIZE = VERTICES_PER_SIDE * VERTICES.getNumberPerVertex();
        public static final int INDEX_SIDE_SIZE = SIDE_INDICES.length;
        public static final int NORMAL_SIDE_SIZE = VERTICES_PER_SIDE * NORMALS.getNumberPerVertex();
        public static final int TEXTURE_SIDE_SIZE = VERTICES_PER_SIDE * TEXTURES.getNumberPerVertex();
        public static final int ATLAS_NUMBER_SIDE_SIZE = VERTICES_PER_SIDE * ATLAS_NUMBER.getNumberPerVertex();
        public static final int ATLAS_LAYER_SIDE_SIZE = VERTICES_PER_SIDE * ATLAS_LAYER.getNumberPerVertex();
        public static final int ATLAS_MAX_UV_SIDE_SIZE = VERTICES_PER_SIDE * ATLAS_MAX_UV.getNumberPerVertex();
        public static final int BLOCK_TYPE_SIDE_SIZE = VERTICES_PER_SIDE * BLOCK_TYPE.getNumberPerVertex();
        public static final int FACE_INDEX_SIDE_SIZE = VERTICES_PER_SIDE * FACE_INDEX.getNumberPerVertex();

        int vertexStart;
        int vertexPos;
        int indexPos;
        int normalPos;
        int texturePos;
        int atlasNumberPos;
        int atlasLayerPos;
        int atlasMaxUVPos;
        int blockTypePos;
        int faceIndexPos;

        IntBuffer indices;
        FloatBuffer vertices;
        FloatBuffer normals;
        FloatBuffer textures;
        FloatBuffer atlasNumber;
        FloatBuffer atlasLayer;
        FloatBuffer atlasMaxUV;
        FloatBuffer blockTypes;
        FloatBuffer faceIndices;

        public SideIndexData(int sideIndex, ArrayList<Mesh> meshes) {

            int meshPos = sideIndex / SIDES_PER_MESH;
            int sideOffset = sideIndex % SIDES_PER_MESH;
            Utils.assertThat(meshes.size() > meshPos);
            Mesh mesh = meshes.get(meshPos);
            MeshAttributes meshAttributes = mesh.getMeshAttributes();

            this.vertexStart = sideOffset * VERTICES_PER_SIDE;
            this.vertexPos = sideOffset * VERTEX_SIDE_SIZE;
            this.indexPos = sideOffset * INDEX_SIDE_SIZE;
            this.normalPos = sideOffset * NORMAL_SIDE_SIZE;
            this.texturePos = sideOffset * TEXTURE_SIDE_SIZE;
            this.atlasNumberPos = sideOffset * ATLAS_NUMBER_SIDE_SIZE;
            this.atlasLayerPos = sideOffset * ATLAS_LAYER_SIDE_SIZE;
            this.atlasMaxUVPos = sideOffset * ATLAS_MAX_UV_SIDE_SIZE;
            this.blockTypePos = sideOffset * BLOCK_TYPE_SIDE_SIZE;
            this.faceIndexPos = sideOffset * FACE_INDEX_SIDE_SIZE;

            this.indices = meshAttributes.getBuffer(INDICES);
            this.vertices = meshAttributes.getBuffer(VERTICES);
            this.normals = meshAttributes.getBuffer(NORMALS);
            this.textures = meshAttributes.getBuffer(TEXTURES);
            this.atlasNumber = meshAttributes.getBuffer(ATLAS_NUMBER);
            this.atlasLayer = meshAttributes.getBuffer(ATLAS_LAYER);
            this.atlasMaxUV = meshAttributes.getBuffer(ATLAS_MAX_UV);
            this.blockTypes = meshAttributes.getBuffer(BLOCK_TYPE);
            this.faceIndices = meshAttributes.getBuffer(FACE_INDEX);

        }
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public class SidePosition implements Serializable {

        private final int x;
        private final int y;
        private final int z;
        private final BlockSide side;

    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public class Side implements Serializable {

        private final SidePosition sidePosition;
        private final Block block;

    }

}
