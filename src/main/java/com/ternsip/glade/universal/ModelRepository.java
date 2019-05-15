package com.ternsip.glade.universal;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ModelRepository {

    private final Map<Method, Model> methodToModel = new HashMap<>();

    public void loadAllModels() {
        Reflections reflections = new Reflections();
        reflections.getSubTypesOf(Entity.class).forEach(Utils::createInstanceSilently);
    }

    public void finish() {
        getMethodToModel().values().forEach(Model::finish);
    }

}
