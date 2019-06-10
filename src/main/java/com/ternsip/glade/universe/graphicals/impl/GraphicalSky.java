package com.ternsip.glade.universe.graphicals.impl;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.SkyboxShader;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Set;

@Getter
@Setter
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

    private Vector3fc sunPosition = new Vector3f(0);

    @Override
    public void render(Set<Light> lights) {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getSunVector().load(getSunPosition());
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
        Matrix4fc viewMatrix = getDisplayManager().getGraphicalRepository().getCamera().getViewMatrix();
        return new Matrix4f(viewMatrix).m30(0).m31(0).m32(0);
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return getDisplayManager().getGraphicalRepository().getCamera().getFarProjectionMatrix();
    }

}
