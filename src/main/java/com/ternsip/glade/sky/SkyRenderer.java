package com.ternsip.glade.sky;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.shader.base.ShaderProgram;
import com.ternsip.glade.shader.impl.SkyboxShader;
import com.ternsip.glade.universal.Material;
import com.ternsip.glade.universal.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyRenderer {

    public static final Vector3f SKY_COLOR = new Vector3f(0.823f, 0.722f, 0.535f);

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

    private Mesh skyBox;
    private SkyboxShader skyboxShader;

    public SkyRenderer(Matrix4f projectionMatrix) {
        skyBox = new Mesh(VERTICES, new float[0], new float[0], new float[0], new int[0], new float[0], new int[0], new Material());
        skyboxShader = ShaderProgram.createShader(SkyboxShader.class);
        skyboxShader.start();
        skyboxShader.getProjectionMatrix().load(projectionMatrix);
        skyboxShader.stop();
    }

    public void render(Sun sun, Camera camera) {
        skyboxShader.start();
        skyboxShader.getSunVector().load(sun.getPosition());
        skyboxShader.getViewMatrix().load(camera.createSkyViewMatrix());
        skyBox.render();
        skyboxShader.stop();
    }

    public void cleanUp() {
        skyboxShader.cleanUp();
        skyBox.cleanUp();
    }

}
