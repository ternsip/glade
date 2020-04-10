package com.ternsip.glade.graphics.general;

import lombok.Getter;

import java.util.Arrays;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL15C.*;

/*
 * NOTE: You might need to write vec4 here, because SSBOs have specific
 * alignment requirements for struct members (vec3 is always treated
 * as vec4 in memory!) Or you might need special 4x alignment in size (not sure)
 * "https://www.safaribooksonline.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec3.html"
 */
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

    public void read(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        int[] subData = Arrays.copyOfRange(data, offset, offset + size);// TODO reuse old buffer
        glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, subData);
        for (int i = offset, di = 0; di < size; ++i, ++di) {
            data[i] = subData[di];
        }
        //data = Utils.bufferToArray(glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY).asIntBuffer());
        //glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
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
