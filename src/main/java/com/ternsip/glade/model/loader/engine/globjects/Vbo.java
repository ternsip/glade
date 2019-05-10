package com.ternsip.glade.model.loader.engine.globjects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static org.lwjgl.opengl.GL15.*;

public class Vbo {

    private final int vboId;
    private final int type;

    private Vbo(int vboId, int type) {
        this.vboId = vboId;
        this.type = type;
    }

    public static Vbo create(int type) {
        int id = glGenBuffers();
        return new Vbo(id, type);
    }

    public void bind() {
        glBindBuffer(type, vboId);
    }

    public void unbind() {
        glBindBuffer(type, 0);
    }

    public void storeData(float[] data) {
        glBufferData(type, arrayToBuffer(data), GL_STATIC_DRAW);
    }

    public void storeData(int[] data) {
        glBufferData(type, arrayToBuffer(data), GL_STATIC_DRAW);
    }

    public void delete() {
        glDeleteBuffers(vboId);
    }

}
