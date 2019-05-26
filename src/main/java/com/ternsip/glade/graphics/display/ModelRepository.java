package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.general.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModelRepository {

    private final Map<Object, Model> modelKeyToModel = new HashMap<>();

    public Model getEntityModelOrCompute(Object key, Function<? super Object, ? extends Model> mappingFunction) {
        return modelKeyToModel.computeIfAbsent(key, mappingFunction);
    }

    public void finish() {
        modelKeyToModel.values().forEach(Model::finish);
    }

}
