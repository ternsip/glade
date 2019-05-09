package com.ternsip.glade.entity;


import com.ternsip.glade.model.GLModel;

import java.io.File;

public class Cube {

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
            .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, .5f, // v0,v1,v2,v3 (front)
            .5f, .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, // v0,v3,v4,v5 (right)
            .5f, .5f, .5f, .5f, .5f, -.5f, -.5f, .5f, -.5f, -.5f, .5f, .5f, // v0,v5,v6,v1 (top)
            -.5f, .5f, .5f, -.5f, .5f, -.5f, -.5f, -.5f, -.5f, -.5f, -.5f, .5f, // v1,v6,v7,v2 (left)
            -.5f, -.5f, -.5f, .5f, -.5f, -.5f, .5f, -.5f, .5f, -.5f, -.5f, .5f, // v7,v4,v3,v2 (bottom)
            .5f, -.5f, -.5f, -.5f, -.5f, -.5f, -.5f, .5f, -.5f, .5f, .5f, -.5f  // v4,v7,v6,v5 (back)
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

    // colour array
    public static float COLORS[] = {
            1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1,  // v0,v1,v2,v3 (front)
            1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1,  // v0,v3,v4,v5 (right)
            1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0,  // v0,v5,v6,v1 (top)
            1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0,  // v1,v6,v7,v2 (left)
            0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0,  // v7,v4,v3,v2 (bottom)
            0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1   // v4,v7,v6,v5 (back)
    };

    // index array for glDrawElements()
    // A cube requires 36 indices = 6 sides * 2 tris * 3 verts
    public static short INDICES[] = {
            0, 1, 2, 2, 3, 0,    // v0-v1-v2, v2-v3-v0 (front)
            4, 5, 6, 6, 7, 4,    // v0-v3-v4, v4-v5-v0 (right)
            8, 9, 10, 10, 11, 8,    // v0-v5-v6, v6-v1-v0 (top)
            12, 13, 14, 14, 15, 12,    // v1-v6-v7, v7-v2-v1 (left)
            16, 17, 18, 18, 19, 16,    // v7-v4-v3, v3-v2-v7 (bottom)
            20, 21, 22, 22, 23, 20     // v4-v7-v6, v6-v5-v4 (back)
    };

    public static GLModel generateGLModel() {
        return new GLModel(VERTICES, NORMALS, COLORS, TEXCOORDS, INDICES, new File("models/others/stall.png"));
    }

}

