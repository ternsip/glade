package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class EntityText extends Entity {

    public static final float SIZE = 1f;
    public static float VERTICES[] = {SIZE, SIZE, 0, 0, SIZE, 0, 0, 0, 0, SIZE, 0, 0};
    public static float TEXCOORDS[] = {1, 0, 0, 0, 0, 1, 1, 1};
    public static float NORMALS[] = {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1};
    public static int INDICES[] = {0, 1, 2, 2, 3, 0};

    private final File font;
    private final String text;
    private final Vector3f rotationSpeed;

    protected Model loadModel() {
        int quad = 4;
        int power4 = 16;
        float scale = 1f / power4;
        float[] vertices = new float[3 * quad * text.length()];
        float[] textures = new float[2 * quad * text.length()];
        float[] normals = new float[3 * quad * text.length()];
        int[] indices = new int[6 * text.length()];
        for (int i = 0; i < text.length(); ++i) {
            for (int j = 0; j < quad; ++j) {
                vertices[3 * quad * i + j * 3] = VERTICES[j * 3] + i * SIZE - 0.5f * SIZE * text.length();
                vertices[3 * quad * i + j * 3 + 1] = VERTICES[j * 3 + 1];
                vertices[3 * quad * i + j * 3 + 2] = VERTICES[j * 3 + 2];
            }
            char ch = text.charAt(i);
            float u = (ch % power4) * scale;
            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            float v = (ch / power4) * scale;
            for (int j = 0; j < quad; ++j) {
                textures[2 * quad * i + j * 2] = u + TEXCOORDS[j * 2] * scale;
                textures[2 * quad * i + j * 2 + 1] = v + TEXCOORDS[j * 2 + 1] * scale;
            }
            System.arraycopy(NORMALS, 0, normals, 3 * quad * i, 3 * quad);
            for (int j = 0; j < 6; ++j) {
                indices[6 * i + j] = INDICES[j] + quad * i;
            }
        }
        Mesh mesh = new Mesh(vertices, normals, new float[0], textures, indices, new float[0], new int[0], new Material(new Texture(new Vector4f(0, 0, 1, 1), font)));
        return new Model(new Mesh[]{mesh}, new Animation(), new Vector3f(0), new Vector3f(0), new Vector3f(text.length()));
    }

    @Override
    public void update() {
        this.increaseRotation(getRotationSpeed());
    }

    @Override
    protected boolean isModelUnique() {
        return true;
    }
}
