package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.NORMALS;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.TEXTURES;

@RequiredArgsConstructor
@Getter
public class Effigy3DText extends EffigyAnimated {

    public static final float SIZE = 1f;
    public static float SYMBOL_VERTICES[] = {SIZE, SIZE, 0, -SIZE, SIZE, 0, -SIZE, -SIZE, 0, SIZE, -SIZE, 0};
    public static float SYMBOL_TEXCOORDS[] = {1, 0, 0, 0, 0, 1, 1, 1};
    public static float SYMBOL_NORMALS[] = {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1};
    public static int SYMBOL_INDICES[] = {0, 1, 2, 2, 3, 0};

    private final File font;
    private final String text;

    public static Mesh createTextMesh(String text, Material material) {
        int quad = 4;
        int power4 = 16;
        float unitSize = 1f / power4;
        float[] vertices = new float[3 * quad * text.length()];
        float[] textures = new float[2 * quad * text.length()];
        float[] normals = new float[3 * quad * text.length()];
        int[] indices = new int[6 * text.length()];
        float scaleX = 1 / (text.length() * SIZE);
        float scaleY = 1 / SIZE;
        float scaleZ = 1 / SIZE;
        for (int i = 0; i < text.length(); ++i) {
            int offset = 3 * quad * i;
            for (int j = 0; j < quad; ++j) {
                vertices[offset + j * 3] = (SYMBOL_VERTICES[j * 3] + i * SIZE - 0.5f * SIZE * text.length()) * scaleX;
                vertices[offset + j * 3 + 1] = (SYMBOL_VERTICES[j * 3 + 1]) * scaleY;
                vertices[offset + j * 3 + 2] = (SYMBOL_VERTICES[j * 3 + 2]) * scaleZ;
            }
            char ch = text.charAt(i);
            float u = (ch % power4) * unitSize;
            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            float v = (ch / power4) * unitSize;
            for (int j = 0; j < quad; ++j) {
                textures[2 * quad * i + j * 2] = u + SYMBOL_TEXCOORDS[j * 2] * unitSize;
                textures[2 * quad * i + j * 2 + 1] = v + SYMBOL_TEXCOORDS[j * 2 + 1] * unitSize;
            }
            System.arraycopy(SYMBOL_NORMALS, 0, normals, 3 * quad * i, 3 * quad);
            for (int j = 0; j < 6; ++j) {
                indices[6 * i + j] = SYMBOL_INDICES[j] + quad * i;
            }
        }
        return new Mesh(new MeshAttributes()
                .add(VERTICES, vertices)
                .add(NORMALS, normals)
                .add(TEXTURES, textures)
                .add(INDICES, indices),
                material
        );
    }

    public Model loadModel() {
        Mesh mesh = createTextMesh(text, new Material(new Texture(new Vector4f(0, 0, 1, 1), font)));
        return new Model(new Mesh[]{mesh}, new Vector3f(0), new Vector3f(0), new Vector3f(text.length(), 1, 1));
    }

    @Override
    public Object getModelKey() {
        return this;
    }
}
