package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.visual.base.Effigy;

import java.util.HashMap;
import java.util.Map;

public class ModelRepository {

    private final Map<Object, Model> modelKeyToModel = new HashMap<>();

    public void finish() {
        modelKeyToModel.values().forEach(Model::finish);
    }

    public Model getGraphicalModel(Effigy effigy) {
        return modelKeyToModel.computeIfAbsent(effigy.getModelKey(), e -> effigy.loadModel());
    }

}
