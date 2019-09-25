package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.entities.base.EntityBase;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter(value = AccessLevel.PROTECTED)
@Setter
public abstract class EntityRepository<K extends EntityBase> {

    private final ConcurrentHashMap<UUID, K> uuidToEntity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class, K> classToEntity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, K> uuidToTransferable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, GraphicalEntity> uuidToGraphicalEntity = new ConcurrentHashMap<>();

    public <T extends K> void register(T entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
        getClassToEntity().put(entity.getClass(), entity);
        if (entity instanceof GraphicalEntity) {
            getUuidToGraphicalEntity().put(entity.getUuid(), (GraphicalEntity) entity);
        }
    }

    public <T extends K> void unregister(T entity) {
        getUuidToEntity().remove(entity.getUuid());
        getClassToEntity().remove(entity.getClass());
        if (entity instanceof GraphicalEntity) {
            getUuidToGraphicalEntity().remove(entity.getUuid());
        }
    }

    public <T extends K> void registerTransferable(T entity) {
        getUuidToTransferable().put(entity.getUuid(), entity);
    }

    public <T extends K> void unregisterTransferable(UUID uuid) {
        getUuidToTransferable().remove(uuid);
    }

    public void unregister(UUID uuid) {
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
        return (T)getClassToEntity().get(clazz);
    }

    public Collection<GraphicalEntity> getGraphicalEntities() {
        return getUuidToGraphicalEntity().values();
    }

    @SneakyThrows
    public EntitiesState getEntitiesState() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                getUuidToTransferable().size();
                int processedEntities = 0;
                for (Map.Entry<UUID, K> entry : getUuidToTransferable().entrySet()) {
                    oos.writeLong(entry.getKey().getMostSignificantBits());
                    oos.writeLong(entry.getKey().getLeastSignificantBits());
                    entry.getValue().writeToStream(oos);
                    processedEntities++;
                }
                oos.flush();
                return new EntitiesState(bos.toByteArray(), processedEntities);
            }
        }
    }


    @SneakyThrows
    public void applyEntitiesState(EntitiesState entitiesState) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(entitiesState.getData())) {
            try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                K lastEntity = null;
                for (int i = 0; i < entitiesState.getSize(); ++i) {
                    try {
                        UUID uuid = new UUID(ois.readLong(), ois.readLong());
                        if (getUuidToTransferable().containsKey(uuid)) {
                            lastEntity = getUuidToTransferable().get(uuid);
                            lastEntity.readFromStream(ois);
                        } else {
                            throw new IllegalArgumentException(String.format("Entity with uuid - %s missing", uuid));
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException(String.format("Broken entity: %s - %s", lastEntity, e.getMessage()), e);
                    }
                }
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class EntitiesState implements Serializable {

        private final byte[] data;
        private final int size;

    }

}
