package com.ternsip.glade.model;

import com.ternsip.glade.utils.Utils;
import de.matthiasmann.twl.utils.PNGDecoder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.ByteBuffer;

import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@Getter
@Setter
public class GLModel {

    public static float[] SKIP_ARRAY = new float[0];
    public static short[] SKIP_ELEMENT_ARRAY = new short[0];
    public static File SKIP_TEXTURE = new File("");

    public static int VERTICES_ATTRIBUTE_POINTER_INDEX = 0;
    public static int NORMALS_ATTRIBUTE_POINTER_INDEX = 1;
    public static int COLORS_ATTRIBUTE_POINTER_INDEX = 2;
    public static int TEXTURES_ATTRIBUTE_POINTER_INDEX = 3;

    private static int NO_TEXTURE = -1;
    private static int NO_VBO = -1;

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
        texture = textureFile == SKIP_TEXTURE ? NO_TEXTURE : loadTexturePNG(textureFile);
        indicesCount = indices == SKIP_ELEMENT_ARRAY ? vertices.length / 3 : indices.length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vboIndices = bindElementArrayVBO(indices);
        vboVertices = bindArrayVBO(VERTICES_ATTRIBUTE_POINTER_INDEX, 3, vertices);
        vboNormals = bindArrayVBO(NORMALS_ATTRIBUTE_POINTER_INDEX, 3, normals);
        vboColors = bindArrayVBO(COLORS_ATTRIBUTE_POINTER_INDEX, 3, colors);
        vboTextures = bindArrayVBO(TEXTURES_ATTRIBUTE_POINTER_INDEX, 2, textures);
        glBindVertexArray(0);

    }

    private static int bindArrayVBO(int attributePointerIndex, int attributePointerSize, float[] array) {
        if (array == SKIP_ARRAY) {
            return NO_VBO;
        }
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, arrayToBuffer(array), GL_STATIC_DRAW);
        glVertexAttribPointer(attributePointerIndex, attributePointerSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private static int bindElementArrayVBO(short[] array) {
        if (array == SKIP_ELEMENT_ARRAY) {
            return NO_VBO;
        }
        int vbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, arrayToBuffer(array), GL_STATIC_DRAW);
        return vbo;
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
        // TODO control this
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        //upload texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        return id;
    }

    public void render() {

        if (texture != NO_TEXTURE) {
            glBindTexture(GL_TEXTURE_2D, texture);
        }

        glBindVertexArray(vao);
        if (vboVertices != NO_VBO) glEnableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        if (vboNormals != NO_VBO) glEnableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        if (vboColors != NO_VBO) glEnableVertexAttribArray(COLORS_ATTRIBUTE_POINTER_INDEX);
        if (vboTextures != NO_VBO) glEnableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);

        if (vboIndices == NO_VBO) {
            glDrawArrays(GL11.GL_TRIANGLES, 0, indicesCount);
        } else {
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0);
        }

        if (vboVertices != NO_VBO) glDisableVertexAttribArray(VERTICES_ATTRIBUTE_POINTER_INDEX);
        if (vboNormals != NO_VBO) glDisableVertexAttribArray(NORMALS_ATTRIBUTE_POINTER_INDEX);
        if (vboColors != NO_VBO) glDisableVertexAttribArray(COLORS_ATTRIBUTE_POINTER_INDEX);
        if (vboTextures != NO_VBO) glDisableVertexAttribArray(TEXTURES_ATTRIBUTE_POINTER_INDEX);

        glBindVertexArray(0);
    }

    void cleanUp() {
        glDeleteVertexArrays(vao);
        if (vboVertices != NO_VBO) glDeleteBuffers(vboVertices);
        if (vboNormals != NO_VBO) glDeleteBuffers(vboNormals);
        if (vboColors != NO_VBO) glDeleteBuffers(vboColors);
        if (vboTextures != NO_VBO) glDeleteBuffers(vboTextures);
        if (texture != NO_TEXTURE) glDeleteTextures(texture);
    }

}
