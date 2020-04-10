package com.ternsip.glade.graphics.general;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL15C.*;

/*
 * NOTE: You might need to write vec4 here, because SSBOs have specific
 * alignment requirements for struct members (vec3 is always treated
 * as vec4 in memory!) Or you might need special 4x alignment in size (not sure)
 * "https://www.safaribooksonline.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec3.html"
 */
public class ShaderBuffer {

    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @Getter
    private final int ssbo;
    private ByteBuffer data;

    public ShaderBuffer(int size) {
        this.ssbo = glGenBuffers();
        this.data = ByteBuffer.allocateDirect(size * Integer.BYTES);
        this.data.order(BYTE_ORDER);
        allocateBuffer();
    }

    public void allocateBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void updateSubBuffer(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * offset, sliceData(offset, size));
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void read(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * offset, sliceData(offset, size));
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public int readInt(int index) {
        return data.getInt(index * Integer.BYTES);
    }

    public void writeInt(int index, int value) {
        data.putInt(index * Integer.BYTES, value);
    }

    public void finish() {
        glDeleteBuffers(ssbo);
    }

    private ByteBuffer sliceData(int offset, int size) {
        ByteBuffer byteBuffer = data.slice();
        byteBuffer.order(BYTE_ORDER);
        byteBuffer.position(offset * Integer.BYTES);
        byteBuffer.limit(size * Integer.BYTES);
        return byteBuffer;
    }

}