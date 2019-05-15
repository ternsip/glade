package com.ternsip.glade.graphics.shader.uniforms;

import com.ternsip.glade.graphics.shader.base.Uniform;
import org.joml.Matrix4f;

public class UniformMatrix4Array extends Uniform<Matrix4f[]> {

    private UniformMatrix4[] uniformMatrices;

    public UniformMatrix4Array(int size) {
        uniformMatrices = new UniformMatrix4[size];
        for (int i = 0; i < size; i++) {
            uniformMatrices[i] = new UniformMatrix4();
        }
    }

    @Override
    public void locate(int programID, String name) {
        for (int i = 0; i < uniformMatrices.length; ++i) {
            uniformMatrices[i].locate(programID, name + "[" + i + "]");
        }
    }

    public void load(Matrix4f[] value) {
        for (int i = 0; i < value.length; i++) {
            uniformMatrices[i].load(value[i]);
        }
    }

}
