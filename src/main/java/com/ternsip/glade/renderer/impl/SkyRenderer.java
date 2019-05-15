package com.ternsip.glade.renderer.impl;

import com.ternsip.glade.renderer.base.Renderer;
import com.ternsip.glade.shader.base.ShaderProgram;
import com.ternsip.glade.shader.impl.SkyboxShader;
import com.ternsip.glade.universal.Material;
import com.ternsip.glade.universal.Mesh;

import static com.ternsip.glade.Glade.UNIVERSE;

@SuppressWarnings("unused")
public class SkyRenderer implements Renderer {

    private static final float SIZE = 500f;

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

    private Mesh skyBox = new Mesh(VERTICES, new float[0], new float[0], new float[0], new int[0], new float[0], new int[0], new Material());
    private SkyboxShader skyboxShader = ShaderProgram.createShader(SkyboxShader.class);

    public SkyRenderer() {
        skyboxShader.start();
        skyboxShader.getProjectionMatrix().load(UNIVERSE.getCamera().getProjectionMatrix());
        skyboxShader.stop();
    }

    public void render() {
        skyboxShader.start();
        skyboxShader.getSunVector().load(UNIVERSE.getSun().getPosition());
        skyboxShader.getViewMatrix().load(UNIVERSE.getCamera().createSkyViewMatrix());
        skyBox.render();
        skyboxShader.stop();
    }

    public void finish() {
        skyboxShader.finish();
        skyBox.finish();
    }

}
