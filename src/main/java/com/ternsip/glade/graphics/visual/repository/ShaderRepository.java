package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.visual.base.graphical.Graphical;

import java.util.HashMap;
import java.util.Map;

public class ShaderRepository {

    private final Map<Object, ShaderProgram> keyToShader = new HashMap<>();

    public void finish() {
        keyToShader.values().forEach(ShaderProgram::finish);
    }

    @SuppressWarnings("unchecked")
    public <T> T getGraphicalShader(Graphical graphical) {
        return (T) keyToShader.computeIfAbsent(
                graphical.getShaderKey(),
                e -> ShaderProgram.createShader(graphical.getShaderClass())
        );
    }

}
