package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.camera.Camera;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.SkyboxShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.LightSource;
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

    public static final float SIZE = Camera.FAR_PLANE * 0.5f;

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

    private float intensity = 1;
    private Vector3fc color = new Vector3f(1);

    @Override
    public void render() {
        getShader().startRaster();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getSun().load(new LightSource(getPosition(), getColor(), getIntensity()));
        getShader().getViewMatrix().load(getViewMatrix());
        getModel().getMeshes().get(0).render();
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        return Model.builder()
                .meshes(Collections.singletonList(new Mesh(new MeshAttributes().add(VERTICES, SKY_VERTICES), new Material())))
                .baseOffset(new Vector3f(0))
                .baseRotation(new Vector3f(0))
                .baseScale(new Vector3f(2 * SIZE))
                .build();
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
        return getGraphics().getCamera().getProjectionMatrix();
    }

}
