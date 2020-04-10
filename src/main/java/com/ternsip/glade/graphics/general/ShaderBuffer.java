package com.ternsip.glade.graphics.general;

import lombok.Getter;

import java.util.Arrays;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL15C.*;

@Getter
public class ShaderBuffer {

    private final int ssbo;
    private final int[] data;

    public ShaderBuffer(int[] data) {
        this.ssbo = glGenBuffers();
        this.data = data;
        allocateBuffer();
    }

    public void allocateBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getSsbo());
        glBufferData(GL_SHADER_STORAGE_BUFFER, getData(), GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void updateSubBuffer(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getSsbo());
        int[] data = Arrays.copyOfRange(getData(), offset, offset + size);
        // TODO do not copy every time, update from origin instead using pointers, or use some kind of buffer
        //IntBuffer intBuffer = IntBuffer.wrap(getData(), offset, size).slice(); (FOR SOME REASON DOESN'T WORK)
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * offset, data);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void finish() {
        glDeleteBuffers(getSsbo());
    }

}
