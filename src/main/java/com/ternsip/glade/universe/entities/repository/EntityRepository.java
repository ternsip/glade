package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class EntityRepository {

    private final ConcurrentHashMap<UUID, Entity> uuidToEntity = new ConcurrentHashMap<>();

    public abstract FieldBuffer getFieldBuffer();

    public final EntitiesChanges findEntitiesChanges() {
        Map<UUID, FieldValues> uuidToChanges = getUuidToEntity().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getFieldBuffer().pullChanges(e.getValue())));
        uuidToChanges.entrySet().removeIf(e -> e.getValue().isEmpty());
        return new EntitiesChanges(uuidToChanges);
    }

    public final void applyEntitiesChanges(EntitiesChanges changes) {
        changes.forEach((uuid, fieldValues) -> {
            Entity entity = getUuidToEntity().get(uuid);
            if (entity == null) {
                // TODO fix sending data to unprepared connections
                throw new IllegalArgumentException(String.format("No such entity with id %s", uuid));
            }
            getFieldBuffer().applyChanges(fieldValues, entity);
        });
    }

    public abstract void finish();

}
