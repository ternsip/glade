package com.ternsip.glade.model.parser;

import org.joml.Vector3f;

import java.util.LinkedList;

public class Model {

    public LinkedList<ModelObject> objects = new LinkedList<ModelObject>();
    public int texture;

    public ModelObject addObject(String name) {
        ModelObject object = new ModelObject(name);
        objects.push(object);
        return object;
    }

    public void render(Vector3f pos, Vector3f rot, float scale) {

        for (ModelObject modelObject : objects) {
            short[] indexArray = modelObject.indices;
            float[] vertexArray = modelObject.vertices;
            float[] textureCoordinates = modelObject.textureCoordinates;

           /* for (int i = 0; i < p.length; i += 3) {
                int a = p[i + 0] * 3;
                int b = p[i + 1] * 3;
                int c = p[i + 2] * 3;
                int at = p[i + 0] * 2;
                int bt = p[i + 1] * 2;
                int ct = p[i + 2] * 2;
                glBegin(GL_POLYGON);
                glTexCoord2f(t[at], t[at + 1]);
                glVertex3f(v[a + 0], v[a + 1], v[a + 2]);
                glTexCoord2f(t[bt], t[bt + 1]);
                glVertex3f(v[b + 0], v[b + 1], v[b + 2]);
                glTexCoord2f(t[ct], t[ct + 1]);
                glVertex3f(v[c + 0], v[c + 1], v[c + 2]);
                glEnd();
            }*/

        }



       // glPopMatrix();
    }


}
