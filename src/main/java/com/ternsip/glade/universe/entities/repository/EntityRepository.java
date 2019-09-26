package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.entities.base.EntityBase;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter(value = AccessLevel.PROTECTED)
@Setter
public abstract class EntityRepository<K extends EntityBase> {

    private final ConcurrentHashMap<UUID, K> uuidToEntity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class, K> classToEntity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, GraphicalEntity> uuidToGraphicalEntity = new ConcurrentHashMap<>();

    public synchronized <T extends K> void register(T entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
        getClassToEntity().put(entity.getClass(), entity);
        if (entity instanceof GraphicalEntity) {
            getUuidToGraphicalEntity().put(entity.getUuid(), (GraphicalEntity) entity);
        }
    }

    public synchronized <T extends K> void unregister(T entity) {
        getUuidToEntity().remove(entity.getUuid());
        getClassToEntity().remove(entity.getClass());
        if (entity instanceof GraphicalEntity) {
            getUuidToGraphicalEntity().remove(entity.getUuid());
        }
    }

    public synchronized void unregister(UUID uuid) {
        unregister(getEntityByUUID(uuid));
    }

    public void finish() {
    }

    public final boolean isEntityExists(UUID uuid) {
        return getUuidToEntity().containsKey(uuid);
    }

    public final K getEntityByUUID(UUID uuid) {
        K entity = getUuidToEntity().get(uuid);
        if (entity == null) {
            throw new IllegalArgumentException(String.format("Entity with UUID - %s does not exist", uuid));
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public final <T extends K> T getEntityByClass(Class<T> clazz) {
        if (!getClassToEntity().containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("Entity with class - %s does not exist", clazz));
        }
        return (T) getClassToEntity().get(clazz);
    }

    public Collection<GraphicalEntity> getGraphicalEntities() {
        return getUuidToGraphicalEntity().values();
    }

}
