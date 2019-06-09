package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityRepository {

    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Light> lights = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void register(Entity entity) {
        entities.add(entity);
        if (entity instanceof Light) {
            lights.add((Light) entity);
        }
    }

    public void unregister(Entity entity) {
        entities.remove(entity);
        lights.remove(entity);
    }

}
