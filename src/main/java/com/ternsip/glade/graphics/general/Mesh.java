package com.ternsip.glade.graphics.general;

import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

@Getter
@Setter
public class Mesh {

    public static final float MIN_INTERNAL_SIZE = 0.01f;
    public static final int MAX_WEIGHTS = 4;
    public static final int MAX_BONES = 180;

    public static int VERTICES_ATTRIBUTE_POINTER_INDEX = 0;
    public static int NORMALS_ATTRIBUTE_POINTER_INDEX = 1;
    public static int COLORS_ATTRIBUTE_POINTER_INDEX = 2;
    public static int TEXTURES_ATTRIBUTE_POINTER_INDEX = 3;
    public static int WEIGHTS_ATTRIBUTE_POINTER_INDEX = 4;
    public static int BONES_ATTRIBUTE_POINTER_INDEX = 5;

    private static int NO_VBO = -1;

    private final int indicesCount;
    private final Material material;
    private final int vao;
    private final int vboIndices;
    private final int vboVertices;
    private final int vboNormals;
    private final int vboColors;
    private final int vboTextures;
    private final int vboWeights;
    private final int vboBones;
    private final float normalizingScale;

    public Mesh(float[] vertices, Material material) {
        this(vertices, new float[0], new float[0], new float[0], new int[0], new float[0], new int[0], material);
    }

    public Mesh(
            float[] vertices,
            float[] normals,
            float[] colors,
            float[] textures,
            int[] indices,
            float[] weights,
            int[] bones,
            Material material
    ) {
        indicesCount = indices.length == 0 ? vertices.length / 3 : indices.length;

        if (textures.length == 0) {
            textures = new float[(2 * vertices.length) / 3];
        }

        Utils.assertThat(vertices.length > 0);
        Utils.assertThat(vertices.length % 3 == 0);
        Utils.assertThat(normals.length == 0 || vertices.length == normals.length);
        Utils.assertThat(colors.length == 0 || (4 * vertices.length / 3) == colors.length);
        Utils.assertThat(textures.length == 0 || (2 * vertices.length) / 3 == textures.length);
        Utils.assertThat(weights.length == 0 || (MAX_WEIGHTS * vertices.length) / 3 == weights.length);
        Utils.assertThat(bones.length == 0 || (MAX_WEIGHTS * vertices.length) / 3 == bones.length);

        this.normalizingScale = calculateNormalizingScale(vertices);
        this.material = material;
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vboIndices = bindElementArrayVBO(indices);
        vboVertices = bindArrayVBO(VERTICES_ATTRIBUTE_POINTER_INDEX, 3, vertices);
        vboNormals = bindArrayVBO(NORMALS_ATTRIBUTE_POINTER_INDEX, 3, normals);
        vboColors = bindArrayVBO(COLORS_ATTRIBUTE_POINTER_INDEX, 4, colors);
        vboTextures = bindArrayVBO(TEXTURES_ATTRIBUTE_POINTER_INDEX, 2, textures);
        vboWeights = bindArrayVBO(WEIGHTS_ATTRIBUTE_POINTER_INDEX, MAX_WEIGHTS, weights);
        vboBones = bindArrayVBO(BONES_ATTRIBUTE_POINTER_INDEX, MAX_WEIGHTS, bones);
        glBindVertexArray(0);

    }

    private static float calculateNormalizingScale(float[] vertices) {
        Vector3f lowestPoint = new Vector3f(Float.MAX_VALUE / 4);
        Vector3f highestPoint = new Vector3f(-Float.MAX_VALUE / 4);
        for (int i = 0; i < vertices.length / 3; ++i) {
            lowestPoint.set(
                    Math.min(lowestPoint.x(), vertices[i * 3]),
                    Math.min(lowestPoint.y(), vertices[i * 3 + 1]),
                    Math.min(lowestPoint.z(), vertices[i * 3 + 2])
            );
            highestPoint.set(
                    Math.max(highestPoint.x(), vertices[i * 3]),
                    Math.max(highestPoint.y(), vertices[i * 3 + 1]),
                    Math.max(highestPoint.z(), vertices[i * 3 + 2])
            );
        }
        Vector3f bounds = highestPoint.sub(lowestPoint, new Vector3f()).max(new Vector3f(MIN_INTERNAL_SIZE));
        return 2 / Math.max(bounds.x(), Math.max(bounds.y(), bounds.z()));
    }

    private static int bindArrayVBO(int index, int nPerVertex, float[] array) {
        if (array.length == 0) {
            return NO_VBO;
        }
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, arrayToBuffer(array), GL_STATIC_DRAW);
        glVertexAttribPointer(index, nPerVertex, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private static int bindArrayVBO(int index, int nPerVertex, int[] array) {
        if (array.length == 0) {
            return NO_VBO;
        }
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, arrayToBuffer(array), GL_STATIC_DRAW);
        glVertexAttribIPointer(index, nPerVertex, GL_INT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private static int bindElementArrayVBO(int[] array) {
        if (array.length == 0) {
            return NO_VBO;
        }
        int vbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, arrayToBuffer(array), GL_STATIC_DRAW);
        return vbo;
    }

    public void render() {

        glBindVertexArray(vao);

        if (vboVertices != NO_VBO) glEnableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        if (vboNormals != NO_VBO) glEnableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        if (vboColors != NO_VBO) glEnableVertexAttribArray(COLORS_ATTRIBUTE_POINTER_INDEX);
        if (vboTextures != NO_VBO) glEnableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);
        if (vboWeights != NO_VBO) glEnableVertexAttribArray(WEIGHTS_ATTRIBUTE_POINTER_INDEX);
        if (vboBones != NO_VBO) glEnableVertexAttribArray(BONES_ATTRIBUTE_POINTER_INDEX);

        if (vboIndices == NO_VBO) {
            glDrawArrays(GL_TRIANGLES, 0, indicesCount);
        } else {
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
        }

        if (vboVertices != NO_VBO) glDisableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        if (vboNormals != NO_VBO) glDisableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        if (vboColors != NO_VBO) glDisableVertexAttribArray(COLORS_ATTRIBUTE_POINTER_INDEX);
        if (vboTextures != NO_VBO) glDisableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);
        if (vboWeights != NO_VBO) glDisableVertexAttribArray(WEIGHTS_ATTRIBUTE_POINTER_INDEX);
        if (vboBones != NO_VBO) glDisableVertexAttribArray(BONES_ATTRIBUTE_POINTER_INDEX);

        glBindVertexArray(0);

    }

    public void finish() {
        glDeleteVertexArrays(vao);
        if (vboVertices != NO_VBO) glDeleteBuffers(vboVertices);
        if (vboNormals != NO_VBO) glDeleteBuffers(vboNormals);
        if (vboTextures != NO_VBO) glDeleteBuffers(vboTextures);
        if (vboWeights != NO_VBO) glDeleteBuffers(vboWeights);
        if (vboBones != NO_VBO) glDeleteBuffers(vboBones);
    }

}
