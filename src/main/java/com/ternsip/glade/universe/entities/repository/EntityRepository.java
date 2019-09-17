package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.network.NetworkSide;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter(value = AccessLevel.PROTECTED)
@Setter
public abstract class EntityRepository {

    private final ConcurrentHashMap<UUID, Entity> uuidToEntity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, EntitiesHolder> classToEntitiesHolder = new ConcurrentHashMap<>();

    public abstract FieldBuffer getFieldBuffer();

    public <T extends Entity> void register(T entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
        getEntitiesHolderByClass(entity.getClass()).add(entity);
    }

    public <T extends Entity> void unregister(T entity) {
        getUuidToEntity().remove(entity.getUuid());
        getEntitiesHolderByClass(entity.getClass()).remove(entity);
    }

    public void unregister(UUID uuid) {
        unregister(getEntityByUUID(uuid));
    }

    public final EntitiesChanges findEntitiesChanges() {
        Map<UUID, FieldValues> uuidToChanges = getOnlyTransferableEntities().stream().collect(Collectors.toMap(Entity::getUuid, e -> getFieldBuffer().pullChanges(e)));
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
            throw new IllegalArgumentException(String.format("Entity does not exist with UUID - %s", uuid));
        }
        return entity;
    }

    public final Collection<Entity> getEntities() {
        return getUuidToEntity().values();
    }

    @SuppressWarnings("unchecked")
    public final <T extends Entity> EntitiesHolder<T> getEntitiesHolderByClass(Class<T> clazz) {
        return (EntitiesHolder<T>) getClassToEntitiesHolder().computeIfAbsent(clazz, k -> new EntitiesHolder<>());
    }

    public final <T extends Entity> Set<T> getEntitiesByClass(Class<T> clazz) {
        return getEntitiesHolderByClass(clazz).getEntities();
    }

    public final <T extends Entity> T getEntityByClass(Class<T> clazz) {
        return getEntitiesByClass(clazz).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Entity does not exist with class - %s", clazz)));
    }

    public final Set<Entity> getOnlyTransferableEntities() {
        return getUuidToEntity().values().stream()
                .filter(e -> e.getNetworkExpectedSide() == NetworkSide.BOTH)
                .collect(Collectors.toSet());
    }

    @RequiredArgsConstructor
    @Getter
    public static class EntitiesHolder<T extends Entity> {

        private final Set<T> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());

        @SuppressWarnings("unchecked")
        public void add(Entity entity) {
            getEntities().add((T) entity);
        }

        @SuppressWarnings("unchecked")
        public void remove(Entity entity) {
            getEntities().remove((T) entity);
        }

    }

}
