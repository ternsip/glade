package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformMatrix4 extends Uniform<Matrix4fc> {

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public void load(Matrix4fc value) {
        value.get(matrixBuffer);
        glUniformMatrix4fv(getLocation(), false, matrixBuffer);
    }

}
