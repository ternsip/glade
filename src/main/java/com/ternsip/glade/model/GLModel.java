package com.ternsip.glade.model;

import com.sun.prism.impl.BufferUtil;
import com.ternsip.glade.utils.Utils;
import de.matthiasmann.twl.utils.PNGDecoder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.opengl.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

@Getter
@Setter
public class GLModel {

    public static int VERTICES_ATTRIBUTE_POINTER_INDEX = 0;
    public static int NORMALS_ATTRIBUTE_POINTER_INDEX = 1;
    public static int COLORS_ATTRIBUTE_POINTER_INDEX = 2;
    public static int TEXTURES_ATTRIBUTE_POINTER_INDEX = 3;

    private int indicesCount;
    private int texture;
    private int vao;
    private int vboIndices;
    private int vboVertices;
    private int vboNormals;
    private int vboColors;
    private int vboTextures;

    public GLModel(
            float[] vertices,
            float[] normals,
            float[] colors,
            float[] textures,
            short[] indices,
            File textureFile
    ) {

        if (textureFile == null) {
            texture = 0; // TODO BIND SPECIAL DEBUG TEXTURE
        } else {
            texture = loadTexturePNG(textureFile);
        }
        if (indices == null || indices.length == 0) {
            indicesCount = vertices.length / 3; // TRIANGLES
        } else {
            indicesCount = indices.length;
        }
        if (colors == null) {
            colors = new float[vertices.length];
            Arrays.fill(colors, 1.0f);
        }

        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vboIndices = bindElementArrayVBO(indices);
        vboVertices = bindArrayVBO(VERTICES_ATTRIBUTE_POINTER_INDEX, 3, vertices);
        vboNormals = bindArrayVBO(NORMALS_ATTRIBUTE_POINTER_INDEX, 3, normals);
        vboColors = bindArrayVBO(COLORS_ATTRIBUTE_POINTER_INDEX, 3, colors);
        vboTextures = bindArrayVBO(TEXTURES_ATTRIBUTE_POINTER_INDEX, 2, textures);
        glBindVertexArray(0);

    }

    public static int bindArrayVBO(int attributePointerIndex, int attributePointerSize, float[] array) {
        FloatBuffer verticesBuffer = BufferUtil.newFloatBuffer(array.length);
        verticesBuffer.put(array);
        verticesBuffer.flip();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributePointerIndex, attributePointerSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public static int bindElementArrayVBO(short[] array) {
        ShortBuffer indicesBuffer = BufferUtil.newShortBuffer(array.length);
        indicesBuffer.put(array);
        indicesBuffer.flip();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void render() {

        // TODO MIGHT BE SLOW
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // TODO MAYBE MOVE IT ON UPPER CLASS
        //glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);

        glBindVertexArray(vao);
        glEnableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        glEnableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        glEnableVertexAttribArray(COLORS_ATTRIBUTE_POINTER_INDEX);
        glEnableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);

        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0);

        glDisableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        glDisableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        glDisableVertexAttribArray(COLORS_ATTRIBUTE_POINTER_INDEX);
        glDisableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);
        glBindVertexArray(0);
    }

    void cleanUp() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vboVertices);
        glDeleteBuffers(vboNormals);
        glDeleteBuffers(vboColors);
        glDeleteBuffers(vboTextures);
        glDeleteTextures(texture);
    }

    @SneakyThrows
    public static int loadTexturePNG(File file) {

        //load png file
        PNGDecoder decoder = new PNGDecoder(Utils.loadResourceAsStream(file));

        //create a byte buffer big enough to store RGBA values
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

        //decode
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

        //flip the buffer so its ready to read
        buffer.flip();

        //create a texture
        int id = glGenTextures();

        //bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        //tell opengl how to unpack bytes
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //set the texture parameters, can be GL_LINEAR or GL_NEAREST
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //upload texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        return id;
    }

}
