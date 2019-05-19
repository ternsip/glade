package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityRepository {

    private final Map<Method, Model> methodToModel = new HashMap<>();
    private final Map<Entity, Model> entityToUniqueModel = new HashMap<>();

    @Getter
    private final Set<Entity> entities = new HashSet<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        if (entityToUniqueModel.containsKey(entity)) {
            entityToUniqueModel.get(entity).finish();
            entityToUniqueModel.remove(entity);
        }
        entities.remove(entity);
    }

    public void update() {
        entities.forEach(Entity::update);
    }

    public void finish() {
        methodToModel.values().forEach(Model::finish);
        entityToUniqueModel.values().forEach(Model::finish);
    }

    @SneakyThrows
    Model getEntityModel(Entity entity) {
        if (entity.isModelUnique()) {
            return entityToUniqueModel.computeIfAbsent(entity, Entity::loadModel);
        }
        Method method = Utils.findDeclaredMethodInHierarchy(entity.getClass(), "loadModel");
        return methodToModel.computeIfAbsent(method, e -> entity.loadModel());
    }

}
