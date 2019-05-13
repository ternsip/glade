package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;
import org.joml.Vector3fc;

import static org.lwjgl.opengl.GL20.glUniform3f;

public class UniformVec3 extends Uniform<Vector3fc> {

    public void load(Vector3fc value) {
        glUniform3f(getLocation(), value.x(), value.y(), value.z());
    }

}
