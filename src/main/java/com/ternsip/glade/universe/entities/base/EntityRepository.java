package com.ternsip.glade.universe.entities.base;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class EntityRepository {

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

}
