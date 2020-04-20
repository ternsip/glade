package com.ternsip.glade.graphics.visual.impl.basis;

import com.google.common.collect.ImmutableMap;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.LightSource;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.impl.EntityCameraEffects;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.chunks.Side;
import com.ternsip.glade.universe.parts.chunks.SidePosition;
import com.ternsip.glade.universe.parts.chunks.SidesUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.ternsip.glade.graphics.shader.base.RasterShader.INDICES;
import static com.ternsip.glade.graphics.shader.base.RasterShader.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.ChunkShader.*;
import static com.ternsip.glade.graphics.visual.impl.basis.EffigySides.SideIndexData.*;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.MAX_LIGHT_LEVEL;

@Slf4j
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

    private final Map<SidePosition, Integer> sidePosToActiveSideIndex = new HashMap<>();
    private final ArrayList<SidePosition> activeSides = new ArrayList<>();
    private final ArrayList<Mesh> meshes = new ArrayList<>();

    @Override
    public Matrix4fc getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render() {
        if (!getUniverseClient().getBlocksClientRepository().getSidesUpdates().isEmpty()) {
            SidesUpdate sidesUpdate = getUniverseClient().getBlocksClientRepository().getSidesUpdates().poll();
            recalculateSides(sidesUpdate.getSidesToRemove(), sidesUpdate.getSidesToAdd());
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

    private Light getSun() {
        EntitySun sun = getUniverseClient().getEntityClientRepository().getEntityByClass(EntitySun.class);
        return new LightSource(sun.getPositionInterpolated(), sun.getColor(), sun.getIntensity());
    }

    private void recalculateSides(List<SidePosition> sidesToRemove, List<Side> sidesToAdd) {
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


}
