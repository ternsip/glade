package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntitySides;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter(value = AccessLevel.PROTECTED)
@Setter
public abstract class EntityRepository {

    private final ConcurrentHashMap<UUID, Entity> uuidToEntity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends Entity>, Entity> classToAnyEntity = new ConcurrentHashMap<>();

    public abstract FieldBuffer getFieldBuffer();

    public void register(Entity entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
        getClassToAnyEntity().put(entity.getClass(), entity);
    }

    public void unregister(Entity entity) {
        getClassToAnyEntity().remove(entity.getClass());
        getUuidToEntity().remove(entity.getUuid());
        getUuidToEntity().values().forEach(e -> {
            if (entity.getClass() == entity.getClass()) {
                getClassToAnyEntity().put(entity.getClass(), entity);
            }
        });
    }

    public void unregister(UUID uuid) {
        unregister(getEntityByUUID(uuid));
    }

    public final EntitiesChanges findEntitiesChanges() {
        Map<UUID, FieldValues> uuidToChanges = getUuidToEntity().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getFieldBuffer().pullChanges(e.getValue())));
        uuidToChanges.entrySet().removeIf(e -> e.getValue().isEmpty());
        return new EntitiesChanges(uuidToChanges);
    }

    public final void applyEntitiesChanges(EntitiesChanges changes) {
        changes.forEach((uuid, fieldValues) -> getFieldBuffer().applyChanges(fieldValues, getEntityByUUID(uuid)));
    }

    public void finish() {
    }

    public final boolean isEntityExists(UUID uuid) {
        return getUuidToEntity().containsKey(uuid);
    }

    public final Entity getEntityByUUID(UUID uuid) {
        Entity entity = getUuidToEntity().get(uuid);
        if (entity == null) {
            throw new IllegalArgumentException(String.format("Entity does not exist %s", uuid));
        }
        return entity;
    }

    public final Collection<Entity> getEntities() {
        return getUuidToEntity().values();
    }

    @SuppressWarnings("unchecked")
    public final <T> T getSpecialEntity(Class<T> clazz) {
        T entity = (T) getClassToAnyEntity().get(clazz);
        if (entity == null) {
            throw new IllegalArgumentException(String.format("Entity does not exist %s", clazz));
        }
        return entity;
    }

    public EntitySun getEntitySun() {
        return getSpecialEntity(EntitySun.class);
    }

    public EntitySides getEntitySides() {
        return getSpecialEntity(EntitySides.class);
    }

}
