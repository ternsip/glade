package com.ternsip.glade.graphics.general;

import com.ternsip.glade.common.logic.Utils;
import lombok.Getter;

import java.util.Arrays;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL15C.*;

public class ShaderBuffer {

    @Getter
    private final int ssbo;
    private int[] data;

    public ShaderBuffer(int size) {
        this.ssbo = glGenBuffers();
        this.data = new int[size];
        allocateBuffer();
    }

    public void allocateBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void updateSubBuffer(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        int[] subData = Arrays.copyOfRange(data, offset, offset + size);
        // TODO do not copy every time, update from origin instead using pointers, or use some kind of buffer
        //IntBuffer intBuffer = IntBuffer.wrap(getData(), offset, size).slice(); (FOR SOME REASON DOESN'T WORK)
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * offset, subData);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void read() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        data = Utils.bufferToArray(glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_WRITE).asIntBuffer()); // TODO reuse old buffer
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public int readInt(int index) {
        return data[index];
    }

    public void writeInt(int index, int value) {
        data[index] = value;
    }

    public void finish() {
        glDeleteBuffers(ssbo);
    }

}
