package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityRepository {

    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());

}
