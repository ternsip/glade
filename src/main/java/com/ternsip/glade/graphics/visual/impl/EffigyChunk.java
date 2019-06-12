package com.ternsip.glade.graphics.visual.impl;

import com.ternsip.glade.common.Maths;
import com.ternsip.glade.common.Utils;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.graphical.Effigy;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3ic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class EffigyChunk extends Effigy<ChunkShader> {

    public static final int SIZE = 16;
    public static final int VOLUME = SIZE * SIZE * SIZE;

    private static final float SIDE = 1f;
    private static final float BLOCK_PHYSICAL_SIZE = 2 * SIDE;
    private static final float CHUNK_PHYSICAL_SIZE = BLOCK_PHYSICAL_SIZE * SIZE;

    private static final CubeSideMeshData SIDE_FRONT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE, SIDE},
            new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            new boolean[]{true, false, false, false, false, true, true, true},
            new int[]{0, 1, 2, 2, 3, 0}
    );

    private static final CubeSideMeshData SIDE_RIGHT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
            new boolean[]{false, false, false, true, true, true, true, false},
            new int[]{0, 1, 2, 2, 3, 0}
    );

    private static final CubeSideMeshData SIDE_TOP = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE},
            new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            new boolean[]{true, true, true, false, false, false, false, true},
            new int[]{0, 1, 2, 2, 3, 0}
    );

    private static final CubeSideMeshData SIDE_LEFT = new CubeSideMeshData(
            new float[]{-SIDE, SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE},
            new float[]{-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0},
            new boolean[]{true, false, false, false, false, true, true, true},
            new int[]{0, 1, 2, 2, 3, 0}
    );

    private static final CubeSideMeshData SIDE_BOTTOM = new CubeSideMeshData(
            new float[]{-SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE},
            new float[]{0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0},
            new boolean[]{false, true, true, true, true, false, false, false},
            new int[]{0, 1, 2, 2, 3, 0}
    );

    private static final CubeSideMeshData SIDE_BACK = new CubeSideMeshData(
            new float[]{SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1},
            new boolean[]{false, true, true, true, true, false, false, false},
            new int[]{0, 1, 2, 2, 3, 0}
    );

    private final Block[] blocks;
    private final Vector3ic chunkPosition;

    public EffigyChunk(Block[] blocks, Vector3ic chunkPosition) {
        this.blocks = blocks;
        this.chunkPosition = chunkPosition;
        if (blocks.length != VOLUME) {
            String msg = String.format("Chunk size should be %s, but %s", VOLUME, blocks.length);
            throw new IllegalArgumentException(msg);
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
        ArrayList<Float> vertices = new ArrayList<>(VOLUME * 3);
        ArrayList<Float> textures = new ArrayList<>(VOLUME * 2);
        ArrayList<Float> normals = new ArrayList<>(VOLUME * 3);
        ArrayList<Integer> indices = new ArrayList<>(VOLUME * 2);

        TexturePackRepository texturePackRepository = getGraphics().getGraphicalRepository().getTexturePackRepository();

        Vector3f blockOffset = new Vector3f(0, 0, 0);

        for (int x = 0, blockIdx = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                for (int z = 0; z < SIZE; ++z, ++blockIdx) {

                    Block block = blocks[blockIdx];
                    if (block == Block.AIR) {
                        continue;
                    }
                    TexturePackRepository.TextureCubeMap textureCubeMap = texturePackRepository.getCubeMap(block);
                    blockOffset.set(x * BLOCK_PHYSICAL_SIZE, y * BLOCK_PHYSICAL_SIZE, z * BLOCK_PHYSICAL_SIZE);

                    SIDE_FRONT.fillArrays(vertices, normals, textures, indices, blockOffset, textureCubeMap.getSideFront());
                    SIDE_RIGHT.fillArrays(vertices, normals, textures, indices, blockOffset, textureCubeMap.getSideRight());
                    SIDE_TOP.fillArrays(vertices, normals, textures, indices, blockOffset, textureCubeMap.getSideTop());
                    SIDE_LEFT.fillArrays(vertices, normals, textures, indices, blockOffset, textureCubeMap.getSideLeft());
                    SIDE_BOTTOM.fillArrays(vertices, normals, textures, indices, blockOffset, textureCubeMap.getSideBottom());
                    SIDE_BACK.fillArrays(vertices, normals, textures, indices, blockOffset, textureCubeMap.getSideBack());

                }
            }
        }
        return new Model(
                new Mesh[]{new Mesh(
                        Utils.listToFloatArray(vertices),
                        Utils.listToFloatArray(normals),
                        new float[0],
                        Utils.listToFloatArray(textures),
                        Utils.listToIntArray(indices),
                        new float[0],
                        new int[0],
                        new Material(texturePackRepository.getBlockAtlasTexture())
                )},
                new Vector3f(new Vector3f(chunkPosition).mul(CHUNK_PHYSICAL_SIZE)),
                new Vector3f(0),
                new Vector3f(1)
        );
    }

    @Override
    public int getPriority() {
        return 0;
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

    @RequiredArgsConstructor
    @Getter
    public static class CubeSideMeshData {

        private final float[] vertices;
        private final float[] normals;
        private final boolean[] textures;
        private final int[] indices;

        public void fillArrays(
                List<Float> vertices,
                List<Float> normals,
                List<Float> textures,
                List<Integer> indices,
                Vector3f blockOffset,
                TextureRepository.AtlasFragment atlasFragment
        ) {
            for (int i = 0; i < this.vertices.length; i += 3) {
                vertices.add(this.vertices[i] + blockOffset.x());
                vertices.add(this.vertices[i + 1] + blockOffset.y());
                vertices.add(this.vertices[i + 2] + blockOffset.z());
            }
            for (float normal : this.normals) {
                normals.add(normal);
            }
            int offset = vertices.size() / 3;
            for (int index : this.indices) {
                indices.add(index + offset);
            }
            for (int i = 0; i < this.textures.length; i += 2) {
                textures.add(this.textures[i] ? atlasFragment.getEndU() : atlasFragment.getStartU());
                textures.add(this.textures[i + 1] ? atlasFragment.getEndV() : atlasFragment.getStartV());
            }
        }

    }


}
