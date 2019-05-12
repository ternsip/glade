package com.ternsip.glade.sky;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.shader.sky.SkyboxShader;
import com.ternsip.glade.universal.Material;
import com.ternsip.glade.universal.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.ternsip.glade.universal.Mesh.*;

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
        skyBox = new Mesh(VERTICES, SKIP_ARRAY_FLOAT, SKIP_ARRAY_FLOAT, SKIP_ARRAY_INT, SKIP_ARRAY_FLOAT, SKIP_ARRAY_INT, new Material(SKIP_TEXTURE));
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
