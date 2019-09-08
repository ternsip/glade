package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class EntityRepository implements IUniverse {

    private final ConcurrentHashMap<UUID, Entity> uuidToEntity = new ConcurrentHashMap<>();

    public abstract FieldBuffer getFieldBuffer();

    public final EntitiesChanges findEntitiesChanges() {
        Map<UUID, FieldValues> uuidToChanges = getUuidToEntity().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getFieldBuffer().pullChanges(e.getValue())));
        uuidToChanges.entrySet().removeIf(e -> e.getValue().isEmpty());
        return new EntitiesChanges(uuidToChanges);
    }

    public final void applyEntitiesChanges(EntitiesChanges changes) {
        changes.forEach((uuid, fieldValues) -> getFieldBuffer().applyChanges(fieldValues, getUuidToEntity().get(uuid)));
    }

}
