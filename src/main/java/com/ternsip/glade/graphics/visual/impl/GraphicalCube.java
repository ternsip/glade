package com.ternsip.glade.graphics.visual.impl;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.visual.base.GraphicalAnimated;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

public class GraphicalCube extends GraphicalAnimated {

    public static float SIZE = 1f;

    // unit cube
    // A cube has 6 sides and each side has 4 vertices, therefore, the total number
    // of vertices is 24 (6 sides * 4 verts), and 72 floats in the vertex array
    // since each vertex has 3 components (x,y,z) (= 24 * 3)
    //    v6----- v5
    //   /|      /|
    //  v1------v0|
    //  | |     | |
    //  | v7----|-v4
    //  |/      |/
    //  v2------v3

    public static float VERTICES[] = {
            SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, // v0,v1,v2,v3 (front)
            SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, // v0,v3,v4,v5 (right)
            SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, // v0,v5,v6,v1 (top)
            -SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, // v1,v6,v7,v2 (left)
            -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, // v7,v4,v3,v2 (bottom)
            SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE  // v4,v7,v6,v5 (back)
    };

    // texture coord array
    public static float TEXCOORDS[] = {
            1, 0, 0, 0, 0, 1, 1, 1,               // v0,v1,v2,v3 (front)
            0, 0, 0, 1, 1, 1, 1, 0,               // v0,v3,v4,v5 (right)
            1, 1, 1, 0, 0, 0, 0, 1,               // v0,v5,v6,v1 (top)
            1, 0, 0, 0, 0, 1, 1, 1,               // v1,v6,v7,v2 (left)
            0, 1, 1, 1, 1, 0, 0, 0,               // v7,v4,v3,v2 (bottom)
            0, 1, 1, 1, 1, 0, 0, 0                // v4,v7,v6,v5 (back)
    };

    // normal array
    public static float NORMALS[] = {
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,  // v0,v1,v2,v3 (front)
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,  // v0,v3,v4,v5 (right)
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,  // v0,v5,v6,v1 (top)
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,  // v1,v6,v7,v2 (left)
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,  // v7,v4,v3,v2 (bottom)
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1   // v4,v7,v6,v5 (back)
    };

    // A cube requires 36 indices = 6 sides * 2 tris * 3 verts
    public static int INDICES[] = {
            0, 1, 2, 2, 3, 0,    // v0-v1-v2, v2-v3-v0 (front)
            4, 5, 6, 6, 7, 4,    // v0-v3-v4, v4-v5-v0 (right)
            8, 9, 10, 10, 11, 8,    // v0-v5-v6, v6-v1-v0 (top)
            12, 13, 14, 14, 15, 12,    // v1-v6-v7, v7-v2-v1 (left)
            16, 17, 18, 18, 19, 16,    // v7-v4-v3, v3-v2-v7 (bottom)
            20, 21, 22, 22, 23, 20     // v4-v7-v6, v6-v5-v4 (back)
    };

    public static Mesh createAABBMesh(Vector3f scale, Material material) {
        float[] vertices = new float[VERTICES.length];
        for (int i = 0; i < vertices.length / 3; ++i) {
            vertices[i * 3] = VERTICES[i * 3] * scale.x();
            vertices[i * 3 + 1] = VERTICES[i * 3 + 1] * scale.y();
            vertices[i * 3 + 2] = VERTICES[i * 3 + 2] * scale.z();
        }
        return new Mesh(vertices, NORMALS, new float[0], TEXCOORDS, INDICES, new float[0], new int[0], material);
    }

    public Model loadModel() {
        Material material = new Material(new Texture(new Vector4f(1.0f, 1.0f, 1.0f, 0.4f), new File("models/others/stall.png")));
        return new Model(createAABBMesh(new Vector3f(1), material));
    }

}