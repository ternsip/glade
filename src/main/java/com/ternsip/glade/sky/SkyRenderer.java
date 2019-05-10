package com.ternsip.glade.sky;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.shader.sky.SkyboxShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.ternsip.glade.model.GLModel.*;

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

    private GLModel skyBox;
    private SkyboxShader skyboxShader;

    public SkyRenderer(Matrix4f projectionMatrix) {
        skyBox = new GLModel(VERTICES, SKIP_ARRAY, SKIP_ARRAY, SKIP_ARRAY, SKIP_ELEMENT_ARRAY, SKIP_TEXTURE);
        skyboxShader = new SkyboxShader();
        skyboxShader.start();
        skyboxShader.loadProjectionMatrix(projectionMatrix);
        skyboxShader.stop();
    }

    public void render(Sun sun, Camera camera) {
        skyboxShader.start();
        skyboxShader.loadSunVector(sun.getPosition());
        skyboxShader.loadViewMatrix(camera);
        skyBox.render();
        skyboxShader.stop();
    }

    public void cleanUp() {
        skyboxShader.cleanUp();
    }

}
