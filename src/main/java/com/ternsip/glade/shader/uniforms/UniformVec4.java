package com.ternsip.glade.shader.uniforms;

import com.ternsip.glade.shader.base.Uniform;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL20C.glUniform4f;

public class UniformVec4 extends Uniform<Vector4fc> {

    public void load(Vector4fc value) {
        glUniform4f(getLocation(), value.x(), value.y(), value.z(), value.w());
    }

}
