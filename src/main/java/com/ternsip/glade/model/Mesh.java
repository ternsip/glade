package com.ternsip.glade.model;

import com.ternsip.glade.universal.Material;
import com.ternsip.glade.universal.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.Arrays;

import static com.ternsip.glade.model.loader.engine.textures.TextureUtils.loadTexturePNG;
import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@Getter
@Setter
// TODO rename to Mesh
public class Mesh {

    public static final int MAX_WEIGHTS = 3;

    public static float[] SKIP_ARRAY_FLOAT = new float[0];
    public static int[] SKIP_ARRAY_INT = new int[0];
    public static File SKIP_TEXTURE = new File("");

    public static int VERTICES_ATTRIBUTE_POINTER_INDEX = 0;
    public static int NORMALS_ATTRIBUTE_POINTER_INDEX = 1;
    public static int TEXTURES_ATTRIBUTE_POINTER_INDEX = 2;
    public static int WEIGHTS_ATTRIBUTE_POINTER_INDEX = 3;
    public static int JOINTS_ATTRIBUTE_POINTER_INDEX = 4;

    private static int NO_TEXTURE = -1;
    private static int NO_VBO = -1;

    private int indicesCount;
    private Material material;
    private int vao;
    private int vboIndices;
    private int vboVertices;
    private int vboNormals;
    private int vboTextures;
    private int vboWeights;
    private int vboJoints;

    public Mesh(
            float[] vertices,
            float[] normals,
            float[] textures,
            int[] indices,
            float[] weights,
            int[] joints,
            Material material
    ) {
        indicesCount = indices == SKIP_ARRAY_INT ? vertices.length / 3 : indices.length;
        this.material = material;
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vboIndices = bindElementArrayVBO(indices);
        vboVertices = bindArrayVBO(VERTICES_ATTRIBUTE_POINTER_INDEX, 3, vertices);
        vboNormals = bindArrayVBO(NORMALS_ATTRIBUTE_POINTER_INDEX, 3, normals);
        vboTextures = bindArrayVBO(TEXTURES_ATTRIBUTE_POINTER_INDEX, 2, textures);
        vboWeights = bindArrayVBO(WEIGHTS_ATTRIBUTE_POINTER_INDEX, MAX_WEIGHTS, weights);
        vboJoints = bindArrayVBO(JOINTS_ATTRIBUTE_POINTER_INDEX, MAX_WEIGHTS, joints);
        glBindVertexArray(0);

    }

    private static int bindArrayVBO(int index, int nPerVertex, float[] array) {
        if (array == SKIP_ARRAY_FLOAT) {
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
        if (array == SKIP_ARRAY_INT) {
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
        if (array == SKIP_ARRAY_INT) {
            return NO_VBO;
        }
        int vbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, arrayToBuffer(array), GL_STATIC_DRAW);
        return vbo;
    }

    public void render() {

        // Bind textures
        Texture texture = material != null ? material.getTexture() : null;
        if (texture != null) {
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0);

            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        Texture normalMap = material != null ? material.getNormalMap() : null;
        if (normalMap != null) {
            // Activate second texture bank
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, normalMap.getId());
        }

        glBindVertexArray(vao);
        if (vboVertices != NO_VBO) glEnableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        if (vboNormals != NO_VBO) glEnableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        if (vboTextures != NO_VBO) glEnableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);
        if (vboWeights != NO_VBO) glEnableVertexAttribArray(WEIGHTS_ATTRIBUTE_POINTER_INDEX);
        if (vboJoints != NO_VBO) glEnableVertexAttribArray(JOINTS_ATTRIBUTE_POINTER_INDEX);

        if (vboIndices == NO_VBO) {
            glDrawArrays(GL11.GL_TRIANGLES, 0, indicesCount);
        } else {
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
        }

        if (vboVertices != NO_VBO) glDisableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        if (vboNormals != NO_VBO) glDisableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        if (vboTextures != NO_VBO) glDisableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);
        if (vboWeights != NO_VBO) glDisableVertexAttribArray(WEIGHTS_ATTRIBUTE_POINTER_INDEX);
        if (vboJoints != NO_VBO) glDisableVertexAttribArray(JOINTS_ATTRIBUTE_POINTER_INDEX);

        glBindVertexArray(0);
    }

    public void cleanUp() {
        glDeleteVertexArrays(vao);
        if (vboVertices != NO_VBO) glDeleteBuffers(vboVertices);
        if (vboNormals != NO_VBO) glDeleteBuffers(vboNormals);
        if (vboTextures != NO_VBO) glDeleteBuffers(vboTextures);
        if (vboWeights != NO_VBO) glDeleteBuffers(vboWeights);
        if (vboJoints != NO_VBO) glDeleteBuffers(vboJoints);
        material.cleanUp();
    }

}