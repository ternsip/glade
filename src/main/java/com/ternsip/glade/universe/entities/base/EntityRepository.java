package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Model;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityRepository {

    private final Map<Object, Model> modelKeyToModel = new HashMap<>();

    @Getter
    private final Set<Entity> entities = new HashSet<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void update() {
        entities.forEach(Entity::update);
    }

    public void finish() {
        modelKeyToModel.values().forEach(Model::finish);
    }

    public Model getEntityModel(Entity entity) {
        return modelKeyToModel.computeIfAbsent(entity.getModelKey(), e -> entity.loadModel());
    }

}
