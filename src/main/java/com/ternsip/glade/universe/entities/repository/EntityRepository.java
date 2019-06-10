package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class EntityRepository {

    public static final EntityTransformable NO_CAMERA_TARGET = null;

    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Light> lights = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private EntityTransformable cameraTarget = NO_CAMERA_TARGET;

    public void register(Entity entity) {
        entities.add(entity);
        if (entity instanceof Light) {
            lights.add((Light) entity);
        }
    }

    public void unregister(Entity entity) {
        entities.remove(entity);
        if (entity instanceof Light) {
            lights.remove(entity);
        }
    }

}
