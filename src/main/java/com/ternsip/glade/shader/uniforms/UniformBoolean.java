package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformBoolean extends Uniform<Boolean> {

    private Boolean value;

    public void load(Boolean value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = value;
            glUniform1f(getLocation(), value ? 1f : 0f);
        }
    }

}
