package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import com.ternsip.glade.universe.parts.chunks.Chunks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.*;

import java.util.ArrayList;
import java.util.Set;

@Getter
@Setter
public class EffigyChunk extends Effigy<ChunkShader> {

    private static final float SIDE = 0.5f;
    private static final float BLOCK_PHYSICAL_SIZE = 2 * SIDE;
    private static final float CHUNK_PHYSICAL_SIZE = BLOCK_PHYSICAL_SIZE * Chunk.SIZE;

    private static final CubeSideMeshData SIDE_FRONT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE, SIDE},
            new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            new boolean[]{true, false, false, false, false, true, true, true},
            new int[]{0, 1, 2, 2, 3, 0},
            BlockSide.FRONT
    );

    private static final CubeSideMeshData SIDE_RIGHT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
            new boolean[]{false, false, false, true, true, true, true, false},
            new int[]{0, 1, 2, 2, 3, 0},
            BlockSide.RIGHT
    );

    private static final CubeSideMeshData SIDE_TOP = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE},
            new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            new boolean[]{true, true, true, false, false, false, false, true},
            new int[]{0, 1, 2, 2, 3, 0},
            BlockSide.TOP
    );

    private static final CubeSideMeshData SIDE_LEFT = new CubeSideMeshData(
            new float[]{-SIDE, SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE},
            new float[]{-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0},
            new boolean[]{true, false, false, false, false, true, true, true},
            new int[]{0, 1, 2, 2, 3, 0},
            BlockSide.LEFT
    );

    private static final CubeSideMeshData SIDE_BOTTOM = new CubeSideMeshData(
            new float[]{-SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE},
            new float[]{0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0},
            new boolean[]{false, true, true, true, true, false, false, false},
            new int[]{0, 1, 2, 2, 3, 0},
            BlockSide.BOTTOM
    );

    private static final CubeSideMeshData SIDE_BACK = new CubeSideMeshData(
            new float[]{SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1},
            new boolean[]{false, true, true, true, true, false, false, false},
            new int[]{0, 1, 2, 2, 3, 0},
            BlockSide.BACK
    );

    private static final CubeSideMeshData ALL_SIDES[] = new CubeSideMeshData[]{
            SIDE_FRONT, SIDE_BACK, SIDE_LEFT, SIDE_RIGHT, SIDE_TOP, SIDE_BOTTOM
    };

    private final Chunk chunk;

    public EffigyChunk(Chunk chunk) {
        this.chunk = chunk;
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

        ChunkCombinator chunkCombinator = new ChunkCombinator();
        TexturePackRepository texturePackRepository = getGraphics().getGraphicalRepository().getTexturePackRepository();
        Vector3f blockOffset = new Vector3f(0, 0, 0);

        chunk.forEach((Vector3ic pos, Block block) -> {
            if (block == Block.AIR) {
                return;
            }
            TexturePackRepository.TextureCubeMap textureCubeMap = texturePackRepository.getCubeMap(block);
            blockOffset.set(pos.x() * BLOCK_PHYSICAL_SIZE, pos.y() * BLOCK_PHYSICAL_SIZE, pos.z() * BLOCK_PHYSICAL_SIZE);

            for (CubeSideMeshData meshDataSide : ALL_SIDES) {
                if (isSideVisible(pos, meshDataSide.blockSide)) {
                    chunkCombinator.fillArrays(blockOffset, 0.5f, textureCubeMap, meshDataSide);
                }
            }
        });

        if (chunkCombinator.isEmpty()) {
            return new Model();
        }

        return new Model(
                new Mesh[]{new Mesh(
                        Utils.listToFloatArray(chunkCombinator.getVertices()),
                        Utils.listToFloatArray(chunkCombinator.getNormals()),
                        Utils.listToFloatArray(chunkCombinator.getColors()),
                        Utils.listToFloatArray(chunkCombinator.getTextures()),
                        Utils.listToIntArray(chunkCombinator.getIndices()),
                        new float[0],
                        new int[0],
                        new Material(texturePackRepository.getBlockAtlasTexture())
                )},
                new Vector3f(new Vector3f(getChunk().getChunkPosition()).mul(CHUNK_PHYSICAL_SIZE)),
                new Vector3f(0),
                new Vector3f(1)
        );

    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return getFrustumIntersection().testSphere(getAdjustedPosition(), CHUNK_PHYSICAL_SIZE * 1.5f);
    }

    @Override
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }

    private boolean isSideVisible(Vector3ic pos, BlockSide side) {
        Chunks chunks = getChunk().getUniverse().getChunks();
        Vector3ic worldPos = getChunk().toWorldPos(pos);
        Vector3ic nextBlockWorldPos = new Vector3i(worldPos).add(side.getAdjacentBlockOffset());
        if (!chunks.isBlockLoaded(nextBlockWorldPos)) {
            return true;
        }
        Block curBlock = chunks.getBlock(worldPos);
        Block nextBlock = chunks.getBlock(nextBlockWorldPos);
        return (nextBlock.isSemiTransparent() && (curBlock != nextBlock || !curBlock.isCombineSides()));
    }

    @RequiredArgsConstructor
    @Getter
    public static class CubeSideMeshData {

        private final float[] vertices;
        private final float[] normals;
        private final boolean[] textures;
        private final int[] indices;
        private final BlockSide blockSide;

    }

    @Getter
    private static class ChunkCombinator {

        private final ArrayList<Float> vertices = new ArrayList<>(Chunk.VOLUME * 3);
        private final ArrayList<Float> textures = new ArrayList<>(Chunk.VOLUME * 2);
        private final ArrayList<Float> normals = new ArrayList<>(Chunk.VOLUME * 3);
        private final ArrayList<Float> colors = new ArrayList<>(Chunk.VOLUME * 4);
        private final ArrayList<Integer> indices = new ArrayList<>(Chunk.VOLUME * 2);

        public void fillArrays(
                Vector3f blockOffset,
                float ambientLight,
                TexturePackRepository.TextureCubeMap textureCubeMap,
                CubeSideMeshData cubeSideMeshData
        ) {
            int offset = vertices.size() / 3;
            for (int index : cubeSideMeshData.getIndices()) {
                indices.add(index + offset);
            }
            for (int i = 0; i < cubeSideMeshData.getVertices().length; i += 3) {
                vertices.add(cubeSideMeshData.getVertices()[i] + blockOffset.x());
                vertices.add(cubeSideMeshData.getVertices()[i + 1] + blockOffset.y());
                vertices.add(cubeSideMeshData.getVertices()[i + 2] + blockOffset.z());

                colors.add(0f);
                colors.add(0f);
                colors.add(0f);
                colors.add(ambientLight);
            }
            for (float normal : cubeSideMeshData.getNormals()) {
                normals.add(normal);
            }
            TextureRepository.AtlasFragment atlasFragment = textureCubeMap.getByBlockSide(cubeSideMeshData.getBlockSide());
            for (int i = 0; i < cubeSideMeshData.getTextures().length; i += 2) {
                textures.add(cubeSideMeshData.getTextures()[i] ? atlasFragment.getEndU() : atlasFragment.getStartU());
                textures.add(cubeSideMeshData.getTextures()[i + 1] ? atlasFragment.getEndV() : atlasFragment.getStartV());
            }
        }

        public boolean isEmpty() {
            return vertices.isEmpty();
        }

    }


}
