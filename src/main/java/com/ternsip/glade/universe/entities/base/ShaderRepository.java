package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;

import java.util.HashMap;
import java.util.Map;

public class ShaderRepository {

    private final Map<Object, ShaderProgram> keyToShader = new HashMap<>();

    public void finish() {
        keyToShader.values().forEach(ShaderProgram::finish);
    }

    @SuppressWarnings("unchecked")
    public <T> T getEntityShader(Entity entity) {
        return (T) keyToShader.computeIfAbsent(
                entity.getShaderClass(),
                e -> ShaderProgram.createShader(entity.getShaderClass())
        );
    }

}
