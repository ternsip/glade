package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Model;

import java.util.HashMap;
import java.util.Map;

public class ModelRepository {

    private final Map<Object, Model> modelKeyToModel = new HashMap<>();

    public void finish() {
        modelKeyToModel.values().forEach(Model::finish);
    }

    public Model getGraphicalModel(Graphical graphical) {
        return modelKeyToModel.computeIfAbsent(graphical.getModelKey(), e -> graphical.loadModel());
    }

}
