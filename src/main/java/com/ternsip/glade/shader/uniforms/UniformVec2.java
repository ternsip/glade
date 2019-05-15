package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import static org.lwjgl.opengl.GL20.glUniform2f;


public class UniformVec2 extends Uniform<Vector2fc> {

    public void load(Vector2fc value) {
        glUniform2f(getLocation(), value.x(), value.y());
    }

}
