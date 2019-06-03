package com.ternsip.glade.universe.entities.base;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class GraphicalRepository {

    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    @Getter
    private final Set<Graphical> entities = new HashSet<>();

    public void addGraphical(Graphical graphical) {
        entities.add(graphical);
    }

    public void removeGraphical(Graphical graphical) {
        entities.remove(graphical);
    }

    public void update() {
        entities.forEach(Graphical::update);
    }

    public void render() {
        getEntities()
                .stream()
                .filter(Graphical::isGraphicalInsideFrustum)
                .collect(Collectors.toMap(e -> e, Graphical::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new))
                .entrySet()
                .stream()
                .sorted(COMPARE_BY_PRIORITY.thenComparing(COMPARE_BY_DISTANCE_TO_CAMERA))
                .forEach(k -> k.getKey().render());
    }

}
