package com.ternsip.glade.graphics.shader.uniforms;

import com.ternsip.glade.graphics.shader.base.Uniform;
import com.ternsip.glade.universe.common.Light;

import java.util.Set;

public class UniformLightArray extends Uniform<Set<Light>> {

    public static final int MAX_LIGHTS = 16;
    private UniformLight[] uniformLights;

    public UniformLightArray(int size) {
        uniformLights = new UniformLight[size];
        for (int i = 0; i < size; i++) {
            uniformLights[i] = new UniformLight();
        }
    }

    @Override
    public void locate(int programID, String name) {
        for (int i = 0; i < uniformLights.length; ++i) {
            uniformLights[i].locate(programID, name + "[" + i + "]");
        }
    }

    public void load(Set<Light> value) {
        int pos = 0;
        for (Light light : value) {
            uniformLights[pos].load(light);
            ++pos;
            if (pos >= MAX_LIGHTS) {
                break;
            }
        }
    }

}
