package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.SkyboxShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collections;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;

@Getter
@Setter
public class EffigySky extends Effigy<SkyboxShader> {

    public static final float SIZE = 10000f;

    private static final float[] SKY_VERTICES = {
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
    private float phase = 0;

    @Override
    public void render() {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getSunVector().load(getSunPosition());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getPhase().load(getPhase());
        getModel().getMeshes().get(0).render();
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        return new Model(
                Collections.singletonList(new Mesh(new MeshAttributes().add(VERTICES, SKY_VERTICES), new Material())),
                new Vector3f(0),
                new Vector3f(0),
                new Vector3f(2 * SIZE)
        );
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public Class<SkyboxShader> getShaderClass() {
        return SkyboxShader.class;
    }

    @Override
    protected Matrix4fc getViewMatrix() {
        Matrix4fc viewMatrix = getGraphics().getCamera().getViewMatrix();
        return new Matrix4f(viewMatrix).m30(0).m31(0).m32(0);
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return getGraphics().getCamera().getFarProjectionMatrix();
    }

}
