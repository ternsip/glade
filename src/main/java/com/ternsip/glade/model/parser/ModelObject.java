package com.ternsip.glade.model.parser;

import com.ternsip.glade.model.GLModel;
import lombok.Getter;
import lombok.Setter;

import static com.ternsip.glade.model.GLModel.SKIP_ARRAY;
import static com.ternsip.glade.model.GLModel.SKIP_TEXTURE;

@Getter
@Setter
public class ModelObject {

    private String name;
    private float[] vertices;
    private int[] indices;
    private float[] textureCoordinates;

    private GLModel model;

    public ModelObject(String name) {
        this.name = name;
    }

    public GLModel getGLModel() {
        if (model == null) {
            model = new GLModel(vertices, SKIP_ARRAY, SKIP_ARRAY, textureCoordinates, indices, SKIP_TEXTURE);
        }
        return model;
    }

}
