package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.io.File;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.NORMALS;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.TEXTURES;

@RequiredArgsConstructor
@Getter
public class EffigySprite extends EffigyAnimated {

    private static final Matrix4fc EMPTY_MATRIX = new Matrix4f();

    private static final float[] VERTICES_DATA = new float[]{1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1, 0};
    private static final float[] NORMALS_DATA = new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1};
    private static final float[] TEXTURES_DATA = new float[]{1, 0, 0, 0, 0, 1, 1, 1};
    private static final int[] INDICES_DATA = {0, 1, 2, 2, 3, 0};

    private final File file;

    @Override
    public Model loadModel() {
        return new Model(new Mesh(new MeshAttributes()
                .add(VERTICES, VERTICES_DATA)
                .add(NORMALS, NORMALS_DATA)
                .add(TEXTURES, TEXTURES_DATA)
                .add(INDICES, INDICES_DATA),
                new Material(new Texture(file))
        ));
    }

    @Override
    public Object getModelKey() {
        return getFile();
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    protected Matrix4fc getViewMatrix() {
        return EMPTY_MATRIX;
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return getGraphics().getCamera().getOrthoProjectionMatrix();
    }

}
