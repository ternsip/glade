package com.ternsip.glade.graphics.renderer.impl;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.renderer.base.Renderer;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.impl.SkyboxShader;
import com.ternsip.glade.universe.Universe;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
@Getter
@Component
@RequiredArgsConstructor
public class SkyRenderer implements Renderer {

    public static final float SIZE = 10000f;

    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };

    private final Mesh skyBox = new Mesh(VERTICES, new Material());
    private final SkyboxShader skyboxShader = ShaderProgram.createShader(SkyboxShader.class);
    private final Universe universe;

    @PostConstruct
    public void applyProjectionMatrix() {
        skyboxShader.start();
        skyboxShader.getProjectionMatrix().load(getUniverse().getCamera().getSkyProjectionMatrix());
        skyboxShader.stop();
    }

    public void render() {
        skyboxShader.start();
        skyboxShader.getSunVector().load(getUniverse().getSun().getPosition());
        skyboxShader.getViewMatrix().load(getUniverse().getCamera().getSkyViewMatrix());
        skyBox.render();
        skyboxShader.stop();
    }

    public void finish() {
        skyboxShader.finish();
        skyBox.finish();
    }

    @Override
    public int getPriority() {
        return 0;
    }

}
