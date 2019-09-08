package com.ternsip.glade.universe.entities.repository;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public final class EntitiesChanges extends HashMap<UUID, FieldValues> {

    public EntitiesChanges(Map<? extends UUID, ? extends FieldValues> m) {
        super(m);
    }

}
