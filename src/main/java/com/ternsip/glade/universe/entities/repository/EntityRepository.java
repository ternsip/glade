package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.collisions.base.Colliding;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.MultiEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class EntityRepository implements Universal {

    public static final Entity NO_CAMERA_TARGET = null;

    private final Set<MultiEntity> multiEntities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Light> lights = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Entity cameraTarget = NO_CAMERA_TARGET;

    public void register(MultiEntity entity) {
        multiEntities.add(entity);
    }

    public void unregister(MultiEntity entity) {
        multiEntities.remove(entity);
    }

    public void register(Entity entity) {
        entities.add(entity);
        if (entity instanceof Light) {
            lights.add((Light) entity);
        }
        if (entity instanceof Colliding) {
            getUniverse().getCollisions().add((Colliding) entity);
        }
    }

    public void unregister(Entity entity) {
        entities.remove(entity);
        if (entity instanceof Light) {
            lights.remove(entity);
        }
        if (entity instanceof Colliding) {
            getUniverse().getCollisions().remove((Colliding) entity);
        }
    }

    public void update() {
        getMultiEntities().forEach(MultiEntity::update);
        getEntities().forEach(Entity::update);
    }

}
