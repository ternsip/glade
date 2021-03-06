package com.ternsip.glade.graphics.shader.uniforms;

import com.ternsip.glade.graphics.shader.base.Uniform;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import static org.lwjgl.opengl.GL20.glUniform2f;


public class UniformVec2 extends Uniform<Vector2fc> {

    private Vector2fc value;

    public void load(Vector2fc value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = new Vector2f(value);
            glUniform2f(getLocation(), value.x(), value.y());
        }
    }

}
