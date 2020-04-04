package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.shader.base.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderRepository {

    private final Map<Class<? extends Shader>, Shader> keyToShader = new HashMap<>();

    public void finish() {
        keyToShader.values().forEach(Shader::finish);
    }

    @SuppressWarnings("unchecked")
    public <T extends Shader> T getShader(Class<T> shaderClass) {
        return (T) keyToShader.computeIfAbsent(shaderClass, e -> Shader.createShader(shaderClass));
    }

}
