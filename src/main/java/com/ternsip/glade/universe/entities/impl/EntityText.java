package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class EntityText extends Entity {

    public static float VERTICES[] = {
            .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, .5f
    };

    public static float TEXCOORDS[] = {
            1, 0, 0, 0, 0, 1, 1, 1
    };

    public static float NORMALS[] = {
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1
    };

    public static int INDICES[] = {
            0, 1, 2, 2, 3, 0,
    };

    private final File font;

    protected Model loadModel() {
        Mesh mesh = new Mesh(VERTICES, NORMALS, new float[0], TEXCOORDS, INDICES, new float[0], new int[0], new Material(new Texture(font)));
        return new Model(mesh);
    }

    @Override
    protected boolean isModelUnique() {
        return true;
    }

}
