package com.ternsip.glade.universe.graphicals.repository;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import com.ternsip.glade.universe.graphicals.base.Visual;

import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.Glade.UNIVERSE;

public class GraphicalRepository {

    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    private final Set<Graphical> graphicals = new HashSet<>();
    private final Map<Entity, Visual> entityToVisual = new HashMap<>();

    public void addGraphical(Graphical graphical) {
        graphicals.add(graphical);
    }

    public void removeGraphical(Graphical graphical) {
        graphicals.remove(graphical);
    }

    public void render() {
        Set<Entity> entities = UNIVERSE.getEntityRepository().getEntities();
        entities.forEach(e -> entityToVisual.computeIfAbsent(e, Entity::getVisual));
        entityToVisual.keySet().removeIf(e -> {
            boolean toRemove = !entities.contains(e);
            if (toRemove) {
                e.finish();
            }
            return toRemove;
        });
        entityToVisual.values().forEach(Visual::update);
        graphicals.forEach(Graphical::update); // TODO TEMP solution, it should only depend on entities
        graphicals
                .stream()
                .filter(Graphical::isGraphicalInsideFrustum)
                .collect(Collectors.toMap(e -> e, Graphical::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new))
                .entrySet()
                .stream()
                .sorted(COMPARE_BY_PRIORITY.thenComparing(COMPARE_BY_DISTANCE_TO_CAMERA))
                .forEach(k -> k.getKey().render());
    }

}
