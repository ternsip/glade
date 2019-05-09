package com.ternsip.glade.model.parser;

import java.util.LinkedList;

public class Model {

    public LinkedList<ModelObject> objects = new LinkedList<ModelObject>();
    public int texture;

    public ModelObject addObject(String name) {
        ModelObject object = new ModelObject(name);
        objects.push(object);
        return object;
    }


}
