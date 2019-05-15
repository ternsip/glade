package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.SneakyThrows;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ModelRepository {

    private final Map<Method, Model> methodToModel = new HashMap<>();

    public void loadAllModels() {
        Reflections reflections = new Reflections();
        reflections.getSubTypesOf(Entity.class).forEach(this::runModelLoading);
    }

    @SneakyThrows
    private void runModelLoading(Class<? extends Entity> clazz) {
        clazz.newInstance();
    }

}
