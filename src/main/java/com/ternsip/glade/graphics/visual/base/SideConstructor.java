package com.ternsip.glade.graphics.visual.base;

import com.google.common.collect.ImmutableMap;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.display.Graphical;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import com.ternsip.glade.universe.parts.chunks.Side;
import com.ternsip.glade.universe.parts.chunks.SidePosition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.ChunkShader.*;
import static com.ternsip.glade.graphics.visual.base.SideConstructor.SideIndexData.*;
import static com.ternsip.glade.universe.parts.chunks.Blocks.MAX_LIGHT_LEVEL;

public class SideConstructor implements Graphical {

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

    private final Map<SidePosition, Integer> sides = new HashMap<>();
    private ArrayList<SidePosition> activeSides = new ArrayList<>();

    @Getter
    private ArrayList<Mesh> meshes = new ArrayList<>();

    public void applyChanges(BlocksUpdate changes) {

        if (changes.isEmpty()) {
            return;
        }

        TexturePackRepository texturePackRepository = getGraphics().getTexturePackRepository();
        List<SidePosition> sidesToRemove = changes.getSidesToRemove();
        List<Side> sidesToAdd = changes.getSidesToAdd();

        Material material = new Material(texturePackRepository.getBlockAtlasTexture());
        int oldLength = activeSides.size();
        int newLength = oldLength + (sidesToAdd.size() - sidesToRemove.size());
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
                            .add(TEXTURES_START, Utils.arrayToBuffer(new float[SIDES_PER_MESH * TEXTURE_SIDE_SIZE]))
                            .add(TEXTURES_END, Utils.arrayToBuffer(new float[SIDES_PER_MESH * TEXTURE_SIDE_SIZE]))
                            .add(BLOCK_TYPE, Utils.arrayToBuffer(new float[SIDES_PER_MESH * BLOCK_TYPE_SIDE_SIZE])),
                    material, true
            );
            mesh.setIndicesCount(0);
            mesh.setVertexCount(0);
            meshes.add(mesh);
        }
        Iterator<SidePosition> toRemove = sidesToRemove.iterator();
        boolean changedMeshes[] = new boolean[meshes.size()];
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

        float[] skyLights = new float[SKY_LIGHT_SIDE_SIZE];
        sideIndexDataSrc.getSkyLights().position(sideIndexDataSrc.getSkyLightPos());
        sideIndexDataSrc.getSkyLights().get(skyLights);
        sideIndexDataDst.getSkyLights().position(sideIndexDataDst.getSkyLightPos());
        sideIndexDataDst.getSkyLights().put(skyLights);

        float[] emitLights = new float[EMIT_LIGHT_SIDE_SIZE];
        sideIndexDataSrc.getEmitLights().position(sideIndexDataSrc.getEmitLightPos());
        sideIndexDataSrc.getEmitLights().get(emitLights);
        sideIndexDataDst.getEmitLights().position(sideIndexDataDst.getEmitLightPos());
        sideIndexDataDst.getEmitLights().put(emitLights);

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

        float[] texturesStart = new float[TEXTURE_SIDE_SIZE];
        sideIndexDataSrc.getTexturesStart().position(sideIndexDataSrc.getTexturePos());
        sideIndexDataSrc.getTexturesStart().get(texturesStart);
        sideIndexDataDst.getTexturesStart().position(sideIndexDataDst.getTexturePos());
        sideIndexDataDst.getTexturesStart().put(texturesStart);

        float[] texturesEnd = new float[TEXTURE_SIDE_SIZE];
        sideIndexDataSrc.getTexturesEnd().position(sideIndexDataSrc.getTexturePos());
        sideIndexDataSrc.getTexturesEnd().get(texturesEnd);
        sideIndexDataDst.getTexturesEnd().position(sideIndexDataDst.getTexturePos());
        sideIndexDataDst.getTexturesEnd().put(texturesEnd);

    }

    private void fillSide(int sideIndex, Side side) {

        SideIndexData sideIndexData = new SideIndexData(sideIndex, meshes);
        BlockSide blockSide = side.getSidePosition().getSide();
        CubeSideMeshData cubeSideMeshData = ALL_SIDES.get(blockSide);
        int dx = side.getSidePosition().getX();
        int dy = side.getSidePosition().getY();
        int dz = side.getSidePosition().getZ();
        TextureRepository.AtlasFragment atlasFragment = getGraphics().getTexturePackRepository().getCubeMap(side.getSideData().getBlock()).getByBlockSide(blockSide);

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexData.getIndices().put(i + sideIndexData.getIndexPos(), SIDE_INDICES[i] + sideIndexData.getVertexStart());
        }

        int blockType = side.getSideData().getBlock() == Block.WATER ? BLOCK_TYPE_WATER : BLOCK_TYPE_NORMAL;

        for (int i = 0; i < VERTICES_PER_SIDE; i++) {
            int vIdx = i * VERTICES.getNumberPerVertex();
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos(), cubeSideMeshData.getVertices()[vIdx] + dx);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 1, cubeSideMeshData.getVertices()[vIdx + 1] + dy);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 2, cubeSideMeshData.getVertices()[vIdx + 2] + dz);

            sideIndexData.getSkyLights().put(i * SKY_LIGHT.getNumberPerVertex() + sideIndexData.getSkyLightPos(), (float) side.getSideData().getSkyLight() / MAX_LIGHT_LEVEL);
            sideIndexData.getEmitLights().put(i * EMIT_LIGHT.getNumberPerVertex() + sideIndexData.getEmitLightPos(), (float) side.getSideData().getEmitLight() / MAX_LIGHT_LEVEL);
            sideIndexData.getBlockTypes().put(i * BLOCK_TYPE.getNumberPerVertex() + sideIndexData.getBlockTypePos(), blockType);

            int nIdx = i * NORMALS.getNumberPerVertex();
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos(), cubeSideMeshData.getNormals()[nIdx]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 1, cubeSideMeshData.getNormals()[nIdx + 1]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 2, cubeSideMeshData.getNormals()[nIdx + 2]);

            int tIdx = i * TEXTURES.getNumberPerVertex();
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos(), cubeSideMeshData.getTextures()[tIdx] ? atlasFragment.getEndU() : atlasFragment.getStartU());
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos() + 1, cubeSideMeshData.getTextures()[tIdx + 1] ? atlasFragment.getEndV() : atlasFragment.getStartV());

            sideIndexData.getTexturesStart().put(tIdx + sideIndexData.getTexturePos(), atlasFragment.getStartU());
            sideIndexData.getTexturesStart().put(tIdx + sideIndexData.getTexturePos() + 1, atlasFragment.getStartV());

            sideIndexData.getTexturesEnd().put(tIdx + sideIndexData.getTexturePos(), atlasFragment.getEndU());
            sideIndexData.getTexturesEnd().put(tIdx + sideIndexData.getTexturePos() + 1, atlasFragment.getEndV());
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
        public static final int SKY_LIGHT_SIDE_SIZE = VERTICES_PER_SIDE * SKY_LIGHT.getNumberPerVertex();
        public static final int EMIT_LIGHT_SIDE_SIZE = VERTICES_PER_SIDE * EMIT_LIGHT.getNumberPerVertex();
        public static final int BLOCK_TYPE_SIDE_SIZE = VERTICES_PER_SIDE * BLOCK_TYPE.getNumberPerVertex();

        int vertexStart;
        int vertexPos;
        int indexPos;
        int normalPos;
        int texturePos;
        int skyLightPos;
        int emitLightPos;
        int blockTypePos;

        IntBuffer indices;
        FloatBuffer vertices;
        FloatBuffer skyLights;
        FloatBuffer emitLights;
        FloatBuffer normals;
        FloatBuffer textures;
        FloatBuffer texturesStart;
        FloatBuffer texturesEnd;
        FloatBuffer blockTypes;

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
            this.skyLightPos = sideOffset * SKY_LIGHT_SIDE_SIZE;
            this.emitLightPos = sideOffset * EMIT_LIGHT_SIDE_SIZE;
            this.blockTypePos = sideOffset * BLOCK_TYPE_SIDE_SIZE;

            this.indices = meshAttributes.getBuffer(INDICES);
            this.vertices = meshAttributes.getBuffer(VERTICES);
            this.skyLights = meshAttributes.getBuffer(SKY_LIGHT);
            this.emitLights = meshAttributes.getBuffer(EMIT_LIGHT);
            this.normals = meshAttributes.getBuffer(NORMALS);
            this.textures = meshAttributes.getBuffer(TEXTURES);
            this.texturesStart = meshAttributes.getBuffer(TEXTURES_START);
            this.texturesEnd = meshAttributes.getBuffer(TEXTURES_END);
            this.blockTypes = meshAttributes.getBuffer(BLOCK_TYPE);

        }
    }

}
