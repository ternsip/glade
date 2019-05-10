package com.ternsip.glade.model.loader.engine.globjects;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.ternsip.glade.utils.Utils.arrayToBuffer;
import static com.ternsip.glade.utils.Utils.bufferToArray;

public class Vbo {

    private final int vboId;
    private final int type;

    private Vbo(int vboId, int type) {
        this.vboId = vboId;
        this.type = type;
    }

    public static Vbo create(int type) {
        int id = GL15.glGenBuffers();
        return new Vbo(id, type);
    }

    public void bind() {
        GL15.glBindBuffer(type, vboId);
    }

    public void unbind() {
        GL15.glBindBuffer(type, 0);
    }

    public void storeData(float[] data) {
        storeData(arrayToBuffer(data));
    }

    public void storeData(int[] data) {
        storeData(arrayToBuffer(data));
    }

    public void storeData(IntBuffer data) {
        GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
    }

    public void storeData(FloatBuffer data) {
        GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
    }

    public void delete() {
        GL15.glDeleteBuffers(vboId);
    }

}
