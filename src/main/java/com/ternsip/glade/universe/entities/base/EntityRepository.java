package com.ternsip.glade.universe.entities.base;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class EntityRepository {

    private static final Comparator<Map.Entry<Entity, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Entity, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    @Getter
    private final Set<Entity> entities = new HashSet<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void update() {
        entities.forEach(Entity::update);
    }

    public void render() {
        getEntities()
                .stream()
                .filter(Entity::isEntityInsideFrustum)
                .collect(Collectors.toMap(e -> e, Entity::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new))
                .entrySet()
                .stream()
                .sorted(COMPARE_BY_PRIORITY.thenComparing(COMPARE_BY_DISTANCE_TO_CAMERA))
                .forEach(k -> k.getKey().render());
    }

}
