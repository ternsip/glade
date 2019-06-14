package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.visual.base.Effigy;

import java.util.HashMap;
import java.util.Map;

public class ShaderRepository {

    private final Map<Object, ShaderProgram> keyToShader = new HashMap<>();

    public void finish() {
        keyToShader.values().forEach(ShaderProgram::finish);
    }

    @SuppressWarnings("unchecked")
    public <T> T getShader(Effigy effigy) {
        return (T) keyToShader.computeIfAbsent(
                effigy.getShaderKey(),
                e -> ShaderProgram.createShader(effigy.getShaderClass())
        );
    }

    public void removeShader(Effigy effigy) {
        keyToShader.get(effigy.getShaderKey()).finish();
        keyToShader.remove(effigy.getShaderKey());
    }

}
