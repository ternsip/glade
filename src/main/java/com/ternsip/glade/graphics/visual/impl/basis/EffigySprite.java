package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.SpriteShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;
import java.util.Collections;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.TEXTURES;

@RequiredArgsConstructor
@Getter
public class EffigySprite extends Effigy<SpriteShader> {

    protected static final float[] VERTICES_DATA = new float[]{1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1, 0};
    protected static final float[] TEXTURES_DATA = new float[]{1, 0, 0, 0, 0, 1, 1, 1};
    protected static final int[] INDICES_DATA = {0, 1, 2, 2, 3, 0};
    protected static final float ORTHO_SCALE = 0.0001f;

    private static final Matrix4fc EMPTY_MATRIX = new Matrix4f();
    private static final Matrix4fc ORTHO_MATRIX = new Matrix4f();

    private final File file;
    private final boolean ortho;
    private final boolean useAspect;

    public float getRatioX() {
        if (!isUseAspect()) {
            return 1;
        }
        float gRatio = getGraphics().getWindowData().getRatio();
        return gRatio < 1 ? 1f / gRatio : 1;
    }

    public float getRatioY() {
        if (!isUseAspect()) {
            return 1;
        }
        float gRatio = getGraphics().getWindowData().getRatio();
        return gRatio > 1 ? gRatio : 1;
    }

    @Override
    public Vector3fc getAdjustedScale() {
        return new Vector3f(super.getAdjustedScale()).mul(new Vector3f(getRatioX(), getRatioY(), 1));
    }

    @Override
    public Vector3fc getAdjustedPosition() {
        Vector3fc pos = super.getAdjustedPosition();
        if (isOrtho()) {
            return new Vector3f(pos.x(), pos.y(), (pos.z() + 1) * ORTHO_SCALE - 1);
        }
        return pos;
    }

    @Override
    public void render() {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        for (Mesh mesh : getModel().getMeshes()) {
            getShader().getDiffuseMap().load(mesh.getMaterial().getDiffuseMap());
            mesh.render();
        }
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        Mesh mesh = new Mesh(new MeshAttributes()
                .add(VERTICES, VERTICES_DATA)
                .add(TEXTURES, TEXTURES_DATA)
                .add(INDICES, INDICES_DATA),
                new Material(new Texture(file)));
        return Model.builder().meshes(Collections.singletonList(mesh)).build();
    }

    @Override
    public Class<SpriteShader> getShaderClass() {
        return SpriteShader.class;
    }

    @Override
    public Object getModelKey() {
        return new SpriteKey(getFile());
    }

    @Override
    protected Matrix4fc getViewMatrix() {
        return isOrtho() ? EMPTY_MATRIX : super.getViewMatrix();
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return isOrtho() ? ORTHO_MATRIX : super.getProjectionMatrix();
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class SpriteKey {

        private final File file;

    }

}
