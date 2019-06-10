package com.ternsip.glade.universe.graphicals.repository;

import com.ternsip.glade.graphics.general.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.graphicals.base.Camera;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import com.ternsip.glade.universe.graphicals.base.Visual;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GraphicalRepository implements Universal {

    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    private final TextureRepository textureRepository;
    private final ModelRepository modelRepository = new ModelRepository();
    private final ShaderRepository shaderRepository = new ShaderRepository();

    private final Camera camera = new Camera();
    private final Set<Graphical> graphicals = new HashSet<>();
    private final Map<Entity, Visual> entityToVisual = new HashMap<>();

    public GraphicalRepository() {
        textureRepository = new TextureRepository();
        textureRepository.bind();
    }

    public void addGraphical(Graphical graphical) {
        graphicals.add(graphical);
    }

    public void removeGraphical(Graphical graphical) {
        graphicals.remove(graphical);
    }

    public void render() {
        camera.update();
        Set<Entity> entities = getUniverse().getEntityRepository().getEntities();
        entities.forEach(e -> entityToVisual.computeIfAbsent(e, x -> e.getVisual()));
        entityToVisual.keySet().removeIf(e -> {
            boolean toRemove = !entities.contains(e);
            if (toRemove) {
                e.finish();
            }
            return toRemove;
        });
        //noinspection unchecked
        entityToVisual.forEach(Entity::update);
        Set<Light> lights = getUniverse().getEntityRepository().getLights();
        graphicals
                .stream()
                .filter(Graphical::isGraphicalInsideFrustum)
                .collect(Collectors.toMap(e -> e, Graphical::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new))
                .entrySet()
                .stream()
                .sorted(COMPARE_BY_PRIORITY.thenComparing(COMPARE_BY_DISTANCE_TO_CAMERA))
                .forEach(k -> k.getKey().render(lights));
    }

    public void finish() {
        modelRepository.finish();
        shaderRepository.finish();
        textureRepository.unbind();
        textureRepository.finish();
    }

}
