package com.ternsip.glade.graphics.shader.uniforms;

import com.ternsip.glade.graphics.shader.base.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformSampler2DArray extends Uniform<Integer> {

    private Integer value;

    public void load(Integer value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = value;
            glUniform1i(getLocation(), value);
        }
    }

}
