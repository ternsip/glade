package com.ternsip.glade.universe.graphicals.impl;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.SkyboxShader;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.Glade.UNIVERSE;

public class GraphicalSky extends Graphical<SkyboxShader> {

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

    @Override
    public void render() {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getSunVector().load(UNIVERSE.getSun().getPosition());
        getShader().getViewMatrix().load(getViewMatrix());
        getModel().getMeshes()[0].render();
        getShader().stop();
    }

    @Override
    public Class<SkyboxShader> getShaderClass() {
        return SkyboxShader.class;
    }

    @Override
    public Model loadModel() {
        return new Model(
                new Mesh[]{new Mesh(VERTICES, new Material())},
                new Vector3f(0),
                new Vector3f(0),
                new Vector3f(2 * SIZE)
        );
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    protected Matrix4fc getViewMatrix() {
        return DISPLAY_MANAGER.getCamera().getSkyViewMatrix();
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return DISPLAY_MANAGER.getCamera().getSkyProjectionMatrix();
    }


}
