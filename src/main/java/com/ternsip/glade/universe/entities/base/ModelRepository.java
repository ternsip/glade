package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Model;

import java.util.HashMap;
import java.util.Map;

public class ModelRepository {

    private final Map<Object, Model> modelKeyToModel = new HashMap<>();

    public void finish() {
        modelKeyToModel.values().forEach(Model::finish);
    }

    public Model getEntityModel(Entity entity) {
        return modelKeyToModel.computeIfAbsent(entity.getModelKey(), e -> entity.loadModel());
    }

}
