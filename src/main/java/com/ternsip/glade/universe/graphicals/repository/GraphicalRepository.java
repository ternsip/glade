package com.ternsip.glade.universe.graphicals.repository;

import com.ternsip.glade.graphics.general.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import com.ternsip.glade.universe.graphicals.base.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.universe.entities.repository.EntityRepository.NO_CAMERA_TARGET;

@Getter
public class GraphicalRepository implements Universal {

    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Graphical, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    private final TextureRepository textureRepository = new TextureRepository();
    private final ModelRepository modelRepository = new ModelRepository();
    private final ShaderRepository shaderRepository = new ShaderRepository();

    private final Camera camera = new Camera();
    private final CameraController cameraController = new ThirdPersonController();
    private final Set<Graphical> graphicals = new HashSet<>();
    private final Map<Entity, Visual> entityToVisual = new HashMap<>();

    private long lastSeenNumberOfEntitiesInFrustum = 0;

    public void addGraphical(Graphical graphical) {
        graphicals.add(graphical);
    }

    public void removeGraphical(Graphical graphical) {
        graphicals.remove(graphical);
    }

    public void render() {
        updateEntities();
        Set<Light> lights = getUniverse().getEntityRepository().getLights();
        HashMap<Graphical, Float> graphicalToDistance = getGraphicals()
                .stream()
                .filter(Graphical::isGraphicalInsideFrustum)
                .collect(Collectors.toMap(e -> e, Graphical::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new));
        lastSeenNumberOfEntitiesInFrustum = graphicalToDistance.size();
        graphicalToDistance.entrySet()
                .stream()
                .sorted(COMPARE_BY_PRIORITY.thenComparing(COMPARE_BY_DISTANCE_TO_CAMERA))
                .forEach(k -> k.getKey().render(lights));
    }

    @SuppressWarnings("unchecked")
    private void updateEntities() {
        Set<Entity> entities = getUniverse().getEntityRepository().getEntities();
        entities.forEach(e -> getEntityToVisual().computeIfAbsent(e, x -> e.getVisual()));
        getEntityToVisual().keySet().removeIf(e -> {
            boolean toRemove = !entities.contains(e);
            if (toRemove) {
                e.finish();
            }
            return toRemove;
        });
        EntityTransformable target = getUniverse().getEntityRepository().getCameraTarget();
        // That condition exists to prevent shuttering and dragging camera under asynchronous routines
        if (target != NO_CAMERA_TARGET) {
            Visual targetVisual = getEntityToVisual().get(target);
            getEntityToVisual().remove(target);
            getEntityToVisual().forEach(Entity::update);
            target.update(targetVisual);
            getCameraController().update(target);
            getEntityToVisual().put(target, targetVisual);
        } else {
            getEntityToVisual().forEach(Entity::update);
        }
    }

    public void finish() {
        getModelRepository().finish();
        getShaderRepository().finish();
        getTextureRepository().finish();
    }

}
