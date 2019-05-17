package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.entities.base.Entity;

import java.io.File;

public class EntityText extends Entity {

    public static float VERTICES[] = {
            .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, .5f
    };

    // texture coord array
    public static float TEXCOORDS[] = {
            1, 0, 0, 0, 0, 1, 1, 1
    };

    // normal array
    public static float NORMALS[] = {
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1
    };

    // index array for glDrawElements()
    // A cube requires 36 indices = 6 sides * 2 tris * 3 verts
    public static int INDICES[] = {
            0, 1, 2, 2, 3, 0,    // v0-v1-v2, v2-v3-v0 (front)
    };

    protected Model loadModel() {
        Mesh mesh = new Mesh(VERTICES, NORMALS, new float[0], TEXCOORDS, INDICES, new float[0], new int[0], new Material(new Texture(new File("fonts/default.png"))));
        return new Model(mesh);
    }

}
