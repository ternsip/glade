package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.ChunkShader.*;
import static com.ternsip.glade.graphics.visual.impl.basis.EffigyChunks.SideIndexData.*;
import static com.ternsip.glade.universe.parts.chunks.Chunks.MAX_LIGHT_LEVEL;

@Getter
@Setter
public class EffigyChunks extends Effigy<ChunkShader> implements Universal {

    private static final float SIDE = 0.5f;
    private static final float BLOCK_PHYSICAL_SIZE = 2 * SIDE;
    private static final float CHUNK_PHYSICAL_SIZE = BLOCK_PHYSICAL_SIZE * Chunk.SIZE;

    private static final CubeSideMeshData SIDE_FRONT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE, SIDE},
            new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            new boolean[]{true, false, false, false, false, true, true, true},
            BlockSide.FRONT
    );

    private static final CubeSideMeshData SIDE_RIGHT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
            new boolean[]{false, false, false, true, true, true, true, false},
            BlockSide.RIGHT
    );

    private static final CubeSideMeshData SIDE_TOP = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE},
            new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            new boolean[]{true, true, true, false, false, false, false, true},
            BlockSide.TOP
    );

    private static final CubeSideMeshData SIDE_LEFT = new CubeSideMeshData(
            new float[]{-SIDE, SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE},
            new float[]{-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0},
            new boolean[]{true, false, false, false, false, true, true, true},
            BlockSide.LEFT
    );

    private static final CubeSideMeshData SIDE_BOTTOM = new CubeSideMeshData(
            new float[]{-SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE},
            new float[]{0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0},
            new boolean[]{false, true, true, true, true, false, false, false},
            BlockSide.BOTTOM
    );

    private static final CubeSideMeshData SIDE_BACK = new CubeSideMeshData(
            new float[]{SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1},
            new boolean[]{false, true, true, true, true, false, false, false},
            BlockSide.BACK
    );

    private static final CubeSideMeshData ALL_SIDES[] = new CubeSideMeshData[]{
            SIDE_FRONT, SIDE_BACK, SIDE_LEFT, SIDE_RIGHT, SIDE_TOP, SIDE_BOTTOM
    };

    private final Map<SidePosition, Integer> sidePosToSideIndex = new HashMap<>();
    private final Map<Integer, SidePosition> sideIndexToSidePos = new HashMap<>();
    private int length = 0;
    // TODO FIX REMOVAL BUGS

    public void update(Collection<Vector3i> positions) {
        List<Integer> sidesToRemove = new ArrayList<>();
        List<SideData> sidesToAdd = new ArrayList<>();
        TexturePackRepository texturePackRepository = getGraphics().getGraphicalRepository().getTexturePackRepository();
        positions.forEach(pos -> {
            for (CubeSideMeshData meshDataSide : ALL_SIDES) {
                Integer sideToRemoveIndex = getSidePosToSideIndex().get(new SidePosition(pos, meshDataSide));
                if (sideToRemoveIndex != null) {
                    sidesToRemove.add(sideToRemoveIndex);
                }
            }
            if (!getUniverse().getChunks().isBlockLoaded(pos)) {
                return;
            }
            Block block = getUniverse().getChunks().getBlock(pos);
            TexturePackRepository.TextureCubeMap textureCubeMap = texturePackRepository.getCubeMap(block);

            for (CubeSideMeshData meshDataSide : ALL_SIDES) {
                if (isSideVisible(pos, meshDataSide.getBlockSide())) {
                    float sideLight = (float) getSideLight(pos, meshDataSide.getBlockSide()) / MAX_LIGHT_LEVEL;
                    sidesToAdd.add(new SideData(pos, sideLight, textureCubeMap, meshDataSide));
                }
            }
        });
        updateSides(sidesToRemove, sidesToAdd);
    }

    private void updateSides(List<Integer> sidesToRemove, List<SideData> sidesToAdd) {
        Material material = new Material(getGraphics().getGraphicalRepository().getTexturePackRepository().getBlockAtlasTexture());
        int oldLength = getLength();
        int newLength = oldLength + (sidesToAdd.size() - sidesToRemove.size());
        int meshesNumber = newLength / SIDES_PER_MESH + (newLength % SIDES_PER_MESH > 0 ? 1 : 0);
        while (getModel().getMeshes().size() < meshesNumber) {
            Mesh mesh = new Mesh(
                    new MeshAttributes()
                            .add(INDICES, Utils.arrayToBuffer(new int[SIDES_PER_MESH * INDEX_SIDE_SIZE]))
                            .add(VERTICES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * VERTEX_SIDE_SIZE]))
                            .add(COLORS, Utils.arrayToBuffer(new float[SIDES_PER_MESH * COLOR_SIDE_SIZE]))
                            .add(NORMALS, Utils.arrayToBuffer(new float[SIDES_PER_MESH * NORMAL_SIDE_SIZE]))
                            .add(TEXTURES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * TEXTURE_SIDE_SIZE])),
                    material, true
            );
            mesh.setIndicesCount(0);
            mesh.setVertexCount(0);
            getModel().getMeshes().add(mesh);
        }
        while (getModel().getMeshes().size() > meshesNumber) {
            int currentSize = getModel().getMeshes().size();
            getModel().getMeshes().get(currentSize - 1).finish();
            getModel().getMeshes().remove(currentSize - 1);
        }
        int sizeOfTheLastMesh = newLength % SIDES_PER_MESH;
        for (int i = 0; i < meshesNumber; ++i) {
            getModel().getMeshes().get(i).setVertexCount((i == meshesNumber - 1) ? (sizeOfTheLastMesh * VERTICES_PER_SIDE + 1) : SIDES_PER_MESH * VERTICES_PER_SIDE);
            getModel().getMeshes().get(i).setIndicesCount((i == meshesNumber - 1) ? (sizeOfTheLastMesh * SIDE_INDICES.length + 1) : SIDES_PER_MESH * SIDE_INDICES.length);
        }
        Iterator<Integer> toRemove = sidesToRemove.iterator();
        int endCounter = 0;
        for (SideData sideData : sidesToAdd) {
            if (toRemove.hasNext()) {
                fillSide(toRemove.next(), sideData);
            } else {
                fillSide(oldLength + endCounter, sideData);
                endCounter++;
            }
        }
        while (toRemove.hasNext()) {
            int pointer = toRemove.next();
            endCounter--;
            relocateSide(oldLength + endCounter, pointer);
        }
        if (sidesToRemove.size() > 0 || sidesToAdd.size() > 0) {
            for (Mesh mesh : getModel().getMeshes()) {
                mesh.updateBuffers();
            }
        }
        setLength(newLength);
    }

    private int getSideLight(Vector3ic pos, BlockSide side) {
        Vector3ic nextBlockWorldPos = new Vector3i(pos).add(side.getAdjacentBlockOffset());
        if (!getUniverse().getChunks().isBlockLoaded(nextBlockWorldPos)) {
            return 0;
        }
        return getUniverse().getChunks().getLight(nextBlockWorldPos);
    }

    private boolean isSideVisible(Vector3ic pos, BlockSide side) {
        Vector3ic nextBlockWorldPos = new Vector3i(pos).add(side.getAdjacentBlockOffset());
        if (!getUniverse().getChunks().isBlockLoaded(nextBlockWorldPos)) {
            return true;
        }
        Block curBlock = getUniverse().getChunks().getBlock(pos);
        if (curBlock == Block.AIR) {
            return false;
        }
        Block nextBlock = getUniverse().getChunks().getBlock(nextBlockWorldPos);
        return (nextBlock.isSemiTransparent() && (curBlock != nextBlock || !curBlock.isCombineSides()));
    }

    private void relocateSide(int sideIndexSrc, int sideIndexDst) {

        SidePosition sidePositionSrc = getSideIndexToSidePos().get(sideIndexSrc);
        SidePosition sidePositionDst = getSideIndexToSidePos().get(sideIndexDst);
        registerSidePosition(sideIndexDst, sidePositionSrc);
        getSideIndexToSidePos().remove(sideIndexSrc);
        getSidePosToSideIndex().remove(sidePositionDst);
        SideIndexData sideIndexDataSrc = new SideIndexData(sideIndexSrc, getModel());
        SideIndexData sideIndexDataDst = new SideIndexData(sideIndexDst, getModel());

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexDataDst.getIndices().put(i + sideIndexDataDst.getIndexPos(), SIDE_INDICES[i] + sideIndexDataDst.getVertexStart());
        }

        float[] vertices = new float[VERTEX_SIDE_SIZE];
        sideIndexDataSrc.getVertices().position(sideIndexDataSrc.getVertexPos());
        sideIndexDataDst.getVertices().position(sideIndexDataDst.getVertexPos());
        sideIndexDataSrc.getVertices().get(vertices);
        sideIndexDataDst.getVertices().put(vertices);

        float[] colors = new float[COLOR_SIDE_SIZE];
        sideIndexDataSrc.getColors().position(sideIndexDataSrc.getColorPos());
        sideIndexDataDst.getColors().position(sideIndexDataDst.getColorPos());
        sideIndexDataSrc.getColors().get(colors);
        sideIndexDataDst.getColors().put(colors);

        float[] normals = new float[NORMAL_SIDE_SIZE];
        sideIndexDataSrc.getNormals().position(sideIndexDataSrc.getNormalPos());
        sideIndexDataDst.getNormals().position(sideIndexDataDst.getNormalPos());
        sideIndexDataSrc.getNormals().get(normals);
        sideIndexDataDst.getNormals().put(normals);

        float[] textures = new float[TEXTURE_SIDE_SIZE];
        sideIndexDataSrc.getTextures().position(sideIndexDataSrc.getTexturePos());
        sideIndexDataDst.getTextures().position(sideIndexDataDst.getTexturePos());
        sideIndexDataSrc.getTextures().get(textures);
        sideIndexDataDst.getTextures().put(textures);

    }

    private void registerSidePosition(Integer sideIndex, SidePosition sidePosition) {
        getSidePosToSideIndex().put(sidePosition, sideIndex);
        getSideIndexToSidePos().put(sideIndex, sidePosition);
    }

    private void fillSide(int sideIndex, SideData sideData) {

        getSidePosToSideIndex().remove(getSideIndexToSidePos().get(sideIndex));
        registerSidePosition(sideIndex, new SidePosition(sideData.getPos(), sideData.getCubeSideMeshData()));
        SideIndexData sideIndexData = new SideIndexData(sideIndex, getModel());
        CubeSideMeshData cubeSideMeshData = sideData.getCubeSideMeshData();
        Vector3i pos = sideData.getPos();
        TextureRepository.AtlasFragment atlasFragment = sideData.getTextureCubeMap().getByBlockSide(cubeSideMeshData.getBlockSide());

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexData.getIndices().put(i + sideIndexData.getIndexPos(), SIDE_INDICES[i] + sideIndexData.getVertexStart());
        }
        for (int i = 0; i < VERTICES_PER_SIDE; i++) {
            int vIdx = i * VERTICES.getNumberPerVertex();
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos(), cubeSideMeshData.getVertices()[vIdx] + pos.x() * BLOCK_PHYSICAL_SIZE);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 1, cubeSideMeshData.getVertices()[vIdx + 1] + pos.y() * BLOCK_PHYSICAL_SIZE);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 2, cubeSideMeshData.getVertices()[vIdx + 2] + pos.z() * BLOCK_PHYSICAL_SIZE);

            int cIdx = i * COLORS.getNumberPerVertex();
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos(), 0f);
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos() + 1, 0f);
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos() + 2, 0f);
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos() + 3, sideData.getAmbientLight());

            int nIdx = i * NORMALS.getNumberPerVertex();
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos(), cubeSideMeshData.getNormals()[nIdx]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 1, cubeSideMeshData.getNormals()[nIdx + 1]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 2, cubeSideMeshData.getNormals()[nIdx + 2]);

            int tIdx = i * TEXTURES.getNumberPerVertex();
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos(), cubeSideMeshData.getTextures()[tIdx] ? atlasFragment.getEndU() : atlasFragment.getStartU());
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos() + 1, cubeSideMeshData.getTextures()[tIdx + 1] ? atlasFragment.getEndV() : atlasFragment.getStartV());
        }

    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render(Set<Light> lights) {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        for (Mesh mesh : getModel().getMeshes()) {
            getShader().getDiffuseMap().load(mesh.getMaterial().getDiffuseMap());
            getShader().getSpecularMap().load(mesh.getMaterial().getSpecularMap());
            getShader().getAmbientMap().load(mesh.getMaterial().getAmbientMap());
            getShader().getEmissiveMap().load(mesh.getMaterial().getEmissiveMap());
            mesh.render();
        }
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        return new Model(
                new ArrayList<>(),
                new Vector3f(SIDE),
                new Vector3f(0),
                new Vector3f(1)
        );
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public float getSquaredDistanceToCamera() {
        return 0;
    }

    @Override
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }

    @RequiredArgsConstructor
    @Getter
    public static class CubeSideMeshData {

        private final float[] vertices;
        private final float[] normals;
        private final boolean[] textures;
        private final BlockSide blockSide;

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
        public static final int COLOR_SIDE_SIZE = VERTICES_PER_SIDE * COLORS.getNumberPerVertex();

        int vertexStart;
        int vertexPos;
        int indexPos;
        int normalPos;
        int texturePos;
        int colorPos;

        IntBuffer indices;
        FloatBuffer vertices;
        FloatBuffer colors;
        FloatBuffer normals;
        FloatBuffer textures;

        public SideIndexData(int sideIndex, Model model) {

            int meshPos = sideIndex / SIDES_PER_MESH;
            int sideOffset = sideIndex % SIDES_PER_MESH;
            Mesh mesh = model.getMeshes().get(meshPos);
            MeshAttributes meshAttributes = mesh.getMeshAttributes();

            this.vertexStart = sideOffset * VERTICES_PER_SIDE;
            this.vertexPos = sideOffset * VERTEX_SIDE_SIZE;
            this.indexPos = sideOffset * INDEX_SIDE_SIZE;
            this.normalPos = sideOffset * NORMAL_SIDE_SIZE;
            this.texturePos = sideOffset * TEXTURE_SIDE_SIZE;
            this.colorPos = sideOffset * COLOR_SIDE_SIZE;

            this.indices = meshAttributes.getBuffer(INDICES);
            this.vertices = meshAttributes.getBuffer(VERTICES);
            this.colors = meshAttributes.getBuffer(COLORS);
            this.normals = meshAttributes.getBuffer(NORMALS);
            this.textures = meshAttributes.getBuffer(TEXTURES);

        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class SideData {

        private final Vector3i pos;
        private final float ambientLight;
        private final TexturePackRepository.TextureCubeMap textureCubeMap;
        private final CubeSideMeshData cubeSideMeshData;

    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class SidePosition {

        private final Vector3i pos;
        private final CubeSideMeshData side;
    }

}
