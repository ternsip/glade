package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class EntityRepository implements Universal {

    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private Entity aim = null;
    private Entity cameraTarget = null;
    private EntitySun sun = null;

    public void register(Entity entity) {
        entities.add(entity);
        if (entity instanceof Obstacle) {
            getUniverse().getCollisions().add((Obstacle) entity);
        }
    }

    public void unregister(Entity entity) {
        entities.remove(entity);
        if (entity instanceof Obstacle) {
            getUniverse().getCollisions().remove((Obstacle) entity);
        }
    }

    public void update() {
        getEntities().forEach(Entity::update);
    }

}
