package com.ternsip.glade.graphics.shader.uniforms;

import com.ternsip.glade.graphics.shader.base.Uniform;
import com.ternsip.glade.universe.common.Light;

public class UniformLight extends Uniform<Light> {

    private UniformVec3 pos = new UniformVec3();
    private UniformFloat intensity = new UniformFloat();
    private UniformVec3 color = new UniformVec3();

    @Override
    public void locate(int programID, String name) {
        pos.locate(programID, name + ".pos");
        intensity.locate(programID, name + ".intensity");
        color.locate(programID, name + ".color");
    }

    @Override
    public void load(Light value) {
        pos.load(value.getPosition());
        intensity.load(value.getIntensity());
        color.load(value.getColor());
    }
}
