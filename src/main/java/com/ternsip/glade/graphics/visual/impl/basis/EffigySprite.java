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

import java.io.File;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.TEXTURES;

@RequiredArgsConstructor
@Getter
public class EffigySprite extends Effigy<SpriteShader> {

    protected static final float[] VERTICES_DATA = new float[]{1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1, 0};
    protected static final float[] TEXTURES_DATA = new float[]{1, 0, 0, 0, 0, 1, 1, 1};
    protected static final int[] INDICES_DATA = {0, 1, 2, 2, 3, 0};

    private static final Matrix4fc EMPTY_MATRIX = new Matrix4f();
    private static final Matrix4fc ORTHO_MATRIX = new Matrix4f();

    private final File file;
    private final boolean ortho;

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
        return new Model(new Mesh(new MeshAttributes()
                .add(VERTICES, VERTICES_DATA)
                .add(TEXTURES, TEXTURES_DATA)
                .add(INDICES, INDICES_DATA),
                new Material(new Texture(file))
        ));
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
