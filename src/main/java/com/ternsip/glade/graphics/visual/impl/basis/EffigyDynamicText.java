package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.*;

import java.io.File;
import java.lang.Math;
import java.util.ArrayList;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.TEXTURES;

@Getter
@Setter
public class EffigyDynamicText extends EffigySprite {

    private static final float TEXT_COMPRESSION = 0.8f;

    private final Vector4fc color;
    private String text;

    public EffigyDynamicText(File file, boolean ortho, Vector4fc color, String text) {
        super(file, ortho);
        this.text = text;
        this.color = color;
    }

    @Override
    public void render() {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        Matrix4f transform = new Matrix4f(getTransformationMatrix());
        transform.translate(-TEXT_COMPRESSION * 0.5f * getText().length(), 0, 0);
        for (int i = 0; i < getText().length(); ++i) {
            char symbol = getText().charAt(i);
            Mesh symbolMesh = getModel().getMeshes().get(symbol);
            getShader().getTransformationMatrix().load(transform);
            getShader().getDiffuseMap().load(symbolMesh.getMaterial().getDiffuseMap());
            symbolMesh.render();
            transform.translate(TEXT_COMPRESSION, 0, 0);
        }
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        ArrayList<Mesh> meshes = new ArrayList<>();
        for (char symbol = 0; symbol < 256; ++symbol) {
            meshes.add(generateGlyphMesh(getFile(), symbol, getColor()));
        }
        return new Model(meshes);
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        Vector3fc scale = getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * getText().length() * 1.5f;
        return getFrustumIntersection().testSphere(getAdjustedPosition(), delta);
    }

    public void alignOnScreen(Vector2ic pos, Vector2ic maxChars) {
        Vector3f newScale = new Vector3f(2f / maxChars.x(), 2f / maxChars.y(), 1);
        Vector3f newPosition = new Vector3f(
                -1f + newScale.x() * (getText().length() + 1) * 0.5f * TEXT_COMPRESSION + pos.x() * newScale.x(),
                -(-1f + newScale.y() + pos.y() * newScale.y()),
                0
        );
        setScale(newScale);
        setPosition(newPosition);
    }

    private static Mesh generateGlyphMesh(File file, char symbol, Vector4fc color) {
        int quad = 4;
        int power4 = 16;
        float unitSize = 1f / power4;
        float u = (symbol % power4) * unitSize;
        @SuppressWarnings("IntegerDivisionInFloatingPointContext")
        float v = (symbol / power4) * unitSize;
        float[] textures = new float[TEXTURES_DATA.length];
        for (int j = 0; j < quad; ++j) {
            textures[j * 2] = u + TEXTURES_DATA[j * 2] * unitSize;
            textures[j * 2 + 1] = v + TEXTURES_DATA[j * 2 + 1] * unitSize;
        }
        return new Mesh(new MeshAttributes()
                .add(VERTICES, VERTICES_DATA)
                .add(TEXTURES, textures)
                .add(INDICES, INDICES_DATA),
                new Material(new Texture(color, file))
        );
    }


    @Override
    public Object getModelKey() {
        return new TextKey(getFile(), getColor());
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class TextKey {

        private final File file;
        private final Vector4fc color;

    }

}
