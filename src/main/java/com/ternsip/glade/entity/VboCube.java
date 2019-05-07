package com.ternsip.glade.entity;

import com.sun.prism.impl.BufferUtil;
import com.ternsip.glade.Loader;
import com.ternsip.glade.entity.Entity;
import com.ternsip.glade.model.RawModel;
import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.model.parser.Model;
import com.ternsip.glade.texture.ModelTexture;
import com.ternsip.glade.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;


import java.io.File;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class VboCube {

    static float VERTICES[] = {
            .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, .5f, // v0,v1,v2,v3 (front)
            .5f, .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, // v0,v3,v4,v5 (right)
            .5f, .5f, .5f, .5f, .5f, -.5f, -.5f, .5f, -.5f, -.5f, .5f, .5f, // v0,v5,v6,v1 (top)
            -.5f, .5f, .5f, -.5f, .5f, -.5f, -.5f, -.5f, -.5f, -.5f, -.5f, .5f, // v1,v6,v7,v2 (left)
            -.5f, -.5f, -.5f, .5f, -.5f, -.5f, .5f, -.5f, .5f, -.5f, -.5f, .5f, // v7,v4,v3,v2 (bottom)
            .5f, -.5f, -.5f, -.5f, -.5f, -.5f, -.5f, .5f, -.5f, .5f, .5f, -.5f  // v4,v7,v6,v5 (back)
    };

    // texture coord array
    static float TEXCOORDS[] = {
            1, 0, 0, 0, 0, 1, 1, 1,               // v0,v1,v2,v3 (front)
            0, 0, 0, 1, 1, 1, 1, 0,               // v0,v3,v4,v5 (right)
            1, 1, 1, 0, 0, 0, 0, 1,               // v0,v5,v6,v1 (top)
            1, 0, 0, 0, 0, 1, 1, 1,               // v1,v6,v7,v2 (left)
            0, 1, 1, 1, 1, 0, 0, 0,               // v7,v4,v3,v2 (bottom)
            0, 1, 1, 1, 1, 0, 0, 0                // v4,v7,v6,v5 (back)
    };

    // normal array
    static float NORMALS[] = {
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,  // v0,v1,v2,v3 (front)
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,  // v0,v3,v4,v5 (right)
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,  // v0,v5,v6,v1 (top)
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,  // v1,v6,v7,v2 (left)
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,  // v7,v4,v3,v2 (bottom)
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1   // v4,v7,v6,v5 (back)
    };

    // colour array
    static float COLORS[] = {
            1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1,  // v0,v1,v2,v3 (front)
            1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1,  // v0,v3,v4,v5 (right)
            1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0,  // v0,v5,v6,v1 (top)
            1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0,  // v1,v6,v7,v2 (left)
            0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0,  // v7,v4,v3,v2 (bottom)
            0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1   // v4,v7,v6,v5 (back)
    };

    // index array for glDrawElements()
    // A cube requires 36 indices = 6 sides * 2 tris * 3 verts
    static short INDICES[] = {
            0, 1, 2, 2, 3, 0,    // v0-v1-v2, v2-v3-v0 (front)
            4, 5, 6, 6, 7, 4,    // v0-v3-v4, v4-v5-v0 (right)
            8, 9, 10, 10, 11, 8,    // v0-v5-v6, v6-v1-v0 (top)
            12, 13, 14, 14, 15, 12,    // v1-v6-v7, v7-v2-v1 (left)
            16, 17, 18, 18, 19, 16,    // v7-v4-v3, v3-v2-v7 (bottom)
            20, 21, 22, 22, 23, 20     // v4-v7-v6, v6-v5-v4 (back)
    };

    private int verticesNormalsColorsTexturesVBO;
    private int indicesVBO;
    private int nOffset;
    private int cOffset;
    private int tOffset;

    public static TexturedModel getRawModel(Loader loader, File texture) {
        return new TexturedModel(loader.loadToVAO(VERTICES, NORMALS, COLORS, TEXCOORDS, INDICES), new ModelTexture(Loader.loadTexturePNG(texture)));
    }

    public VboCube() {

        int vSize = VERTICES.length * Float.BYTES;
        int nSize = NORMALS.length * Float.BYTES;
        int cSize = COLORS.length * Float.BYTES;
        int tSize = TEXCOORDS.length * Float.BYTES;
        int iSize = INDICES.length * Short.BYTES;
        this.nOffset = vSize;
        this.cOffset = nOffset + nSize;
        this.tOffset = cOffset + cSize;

        FloatBuffer vertices = BufferUtil.newFloatBuffer(VERTICES.length + NORMALS.length + COLORS.length + TEXCOORDS.length);
        vertices.put(VERTICES);
        vertices.put(NORMALS);
        vertices.put(COLORS);
        vertices.put(TEXCOORDS);
        vertices.flip();

        ShortBuffer indices = BufferUtil.newShortBuffer(INDICES.length);
        indices.put(INDICES);
        indices.flip();

        int[] temp = new int[2];
        glGenBuffers(temp);
        verticesNormalsColorsTexturesVBO = temp[0];
        indicesVBO = temp[1];

        // copy vertex attribs data to VBO, reserve space
        glBindBuffer(GL_ARRAY_BUFFER, verticesNormalsColorsTexturesVBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // copy index data to VBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    public void render(Vector3f pos, Vector3f rot, float scale, int texture) {
        //Matrix4f transformationMatrix = Maths.createTransformationMatrix(pos, Entity.getRotationQuaternion(rot.x(), rot.y(), rot.z()), scale);
//
        //FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        //transformationMatrix.get(matrixBuffer);
        //glLoadMatrixf(matrixBuffer);
       //glPushMatrix();
       //glLoadIdentity();
       //glScalef(1.0f, 1.0f, 1.0f);

        //glShadeModel(GL_FLAT);
        //glNormal3f(0.0f, 0.0f, 1.0f);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);

        // bind VBOs before drawing
        glBindBuffer(GL_ARRAY_BUFFER, verticesNormalsColorsTexturesVBO);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);

        // enable vertex arrays
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        // specify vertex arrays with their offsets
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glNormalPointer(GL_FLOAT, 0, nOffset);
        glColorPointer(3, GL_FLOAT, 0, cOffset);
        glTexCoordPointer(2, GL_FLOAT, 0, tOffset);

        // finally draw a cube with glDrawElements()
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_SHORT, 0);
        //glDrawElements(GL_TRIANGLES, indices.capacity(), GL_UNSIGNED_SHORT, 0);

        // disable vertex arrays
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        // unbind VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

         //glPopMatrix();
    }

}
