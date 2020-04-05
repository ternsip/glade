package com.ternsip.glade.graphics.general;

import lombok.Getter;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL15.*;

@Getter
public class ShaderBuffer {

    private final int ssbo;
    private final int[] data;

    public ShaderBuffer(int[] data) {
        this.ssbo = glGenBuffers();
        this.data = data;
        updateBuffer();
    }

    public void updateBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getSsbo());
        glBufferData(GL_SHADER_STORAGE_BUFFER, getData(), GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void updateSubBuffer(int offset, int[] subData) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getSsbo());
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * offset, subData);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void finish() {
        glDeleteBuffers(getSsbo());
    }

}
