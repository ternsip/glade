package com.ternsip.glade.graphics.visual.impl;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.graphical.Graphical;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.utils.Maths;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GraphicalChunk extends Graphical<ChunkShader> {

    public static final int SIZE = 16;
    public static final int VOLUME = SIZE * SIZE * SIZE;

    private static final float SIDE = 1f;

    private static final float[] VERTICES_FRONT = {SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE, SIDE};
    private static final float[] VERTICES_RIGHT = {SIDE, SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE};
    private static final float[] VERTICES_TOP = {SIDE, SIDE, SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE};
    private static final float[] VERTICES_LEFT = {-SIDE, SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE};
    private static final float[] VERTICES_BOTTOM = {-SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE};
    private static final float[] VERTICES_BACK = {SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE};

    public static boolean TEXTURES_FRONT[] = {true, false, false, false, false, true, true, true};
    public static boolean TEXTURES_RIGHT[] = {false, false, false, true, true, true, true, false};
    public static boolean TEXTURES_TOP[] = {true, true, true, false, false, false, false, true};
    public static boolean TEXTURES_LEFT[] = {true, false, false, false, false, true, true, true};
    public static boolean TEXTURES_BOTTOM[] = {false, true, true, true, true, false, false, false};
    public static boolean TEXTURES_BACK[] = {false, true, true, true, true, false, false, false};

    private static final float[] NORMALS_FRONT = {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1};
    private static final float[] NORMALS_RIGHT = {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};
    private static final float[] NORMALS_TOP = {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    private static final float[] NORMALS_LEFT = {-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0};
    private static final float[] NORMALS_BOTTOM = {0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0};
    private static final float[] NORMALS_BACK = {0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1};

    private static final int[] INDICES_ORDER = {0, 1, 2, 2, 3, 0};

    private final Block[] blocks;

    public GraphicalChunk(Block[] blocks) {
        this.blocks = blocks;
        if (blocks.length != VOLUME) {
            String msg = String.format("Chunk size should be %s, but %s", VOLUME, blocks.length);
            throw new IllegalArgumentException(msg);
        }
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
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Model loadModel() {

        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> textures = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        TexturePackRepository texturePackRepository = getDisplayManager().getGraphicalRepository().getTexturePackRepository();
        TextureRepository.AtlasDecoder atlasDecoder = texturePackRepository.getBlocksAtlasDecoder();

        for (int x = 0, blockIdx = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                for (int z = 0; z < SIZE; ++z, ++blockIdx) {

                    Block block = blocks[blockIdx];

                    if (block == Block.AIR) {
                        continue;
                    }

                    TexturePackRepository.TextureCubeMap textureCubeMap = texturePackRepository.getCubeMap(block);
                    TextureRepository.AtlasFragment fragmentFront = atlasDecoder.getFileToAtlasFragment().get(textureCubeMap.getSideFront());
                    TextureRepository.AtlasFragment fragmentBack = atlasDecoder.getFileToAtlasFragment().get(textureCubeMap.getSideBack());
                    TextureRepository.AtlasFragment fragmentLeft = atlasDecoder.getFileToAtlasFragment().get(textureCubeMap.getSideLeft());
                    TextureRepository.AtlasFragment fragmentRight = atlasDecoder.getFileToAtlasFragment().get(textureCubeMap.getSideRight());
                    TextureRepository.AtlasFragment fragmentTop = atlasDecoder.getFileToAtlasFragment().get(textureCubeMap.getSideTop());
                    TextureRepository.AtlasFragment fragmentBottom = atlasDecoder.getFileToAtlasFragment().get(textureCubeMap.getSideBottom());

                    Vector3fc verticesOffset = new Vector3f(x * 2 * SIDE, y * 2 * SIDE, z * 2 * SIDE);

                    vertices.addAll(produceVertices(VERTICES_FRONT, verticesOffset));
                    textures.addAll(produceTextures(TEXTURES_FRONT, fragmentFront));
                    normals.addAll(produceNormals(NORMALS_FRONT));
                    indices.addAll(produceIndices(INDICES_ORDER, vertices.size() / 3));

                    vertices.addAll(produceVertices(VERTICES_RIGHT, verticesOffset));
                    textures.addAll(produceTextures(TEXTURES_RIGHT, fragmentRight));
                    normals.addAll(produceNormals(NORMALS_RIGHT));
                    indices.addAll(produceIndices(INDICES_ORDER, vertices.size() / 3));

                    vertices.addAll(produceVertices(VERTICES_TOP, verticesOffset));
                    textures.addAll(produceTextures(TEXTURES_TOP, fragmentTop));
                    normals.addAll(produceNormals(NORMALS_TOP));
                    indices.addAll(produceIndices(INDICES_ORDER, vertices.size() / 3));

                    vertices.addAll(produceVertices(VERTICES_LEFT, verticesOffset));
                    textures.addAll(produceTextures(TEXTURES_LEFT, fragmentLeft));
                    normals.addAll(produceNormals(NORMALS_LEFT));
                    indices.addAll(produceIndices(INDICES_ORDER, vertices.size() / 3));

                    vertices.addAll(produceVertices(VERTICES_BOTTOM, verticesOffset));
                    textures.addAll(produceTextures(TEXTURES_BOTTOM, fragmentBottom));
                    normals.addAll(produceNormals(NORMALS_BOTTOM));
                    indices.addAll(produceIndices(INDICES_ORDER, vertices.size() / 3));

                    vertices.addAll(produceVertices(VERTICES_BACK, verticesOffset));
                    textures.addAll(produceTextures(TEXTURES_BACK, fragmentBack));
                    normals.addAll(produceNormals(NORMALS_BACK));
                    indices.addAll(produceIndices(INDICES_ORDER, vertices.size() / 3));

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
                        new Material(new Texture(atlasDecoder))
                )},
                new Vector3f(0),
                new Vector3f(0),
                new Vector3f(1)
        );
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        Matrix4fc projection = getDisplayManager().getGraphicalRepository().getCamera().getNormalProjectionMatrix();
        Matrix4fc view = getDisplayManager().getGraphicalRepository().getCamera().getViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        FrustumIntersection frustumIntersection = new FrustumIntersection(projectionViewMatrix);
        return frustumIntersection.testSphere(getAdjustedPosition(), 2 * SIDE * SIZE * 1.5f);
    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    private List<Float> produceVertices(float[] vertices, Vector3fc offset) {
        List<Float> result = new ArrayList<>(vertices.length);
        for (int i = 0; i < vertices.length; i += 3) {
            result.add(vertices[i] + offset.x());
            result.add(vertices[i + 1] + offset.y());
            result.add(vertices[i + 2] + offset.z());
        }
        return result;
    }

    private List<Float> produceNormals(float[] normals) {
        List<Float> result = new ArrayList<>(normals.length);
        for (float normal : normals) {
            result.add(normal);
        }
        return result;
    }

    private List<Integer> produceIndices(int[] indices, int offset) {
        List<Integer> result = new ArrayList<>(indices.length);
        for (int index : indices) {
            result.add(index + offset);
        }
        return result;
    }

    private List<Float> produceTextures(boolean[] textures, TextureRepository.AtlasFragment atlasFragment) {
        List<Float> result = new ArrayList<>(textures.length);
        for (int i = 0; i < textures.length; i += 2) {
            result.add(textures[i] ? atlasFragment.getEndU() : atlasFragment.getStartU());
            result.add(textures[i + 1] ? atlasFragment.getEndV() : atlasFragment.getStartV());
        }
        return result;
    }

}
