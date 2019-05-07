package com.ternsip.glade.model.parser;

/**
 *
 */
public class ModelObject {
    private String name;
    public float[] vertices;
    public short[] indices;
    public float[] textureCoordinates;

    public ModelObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
