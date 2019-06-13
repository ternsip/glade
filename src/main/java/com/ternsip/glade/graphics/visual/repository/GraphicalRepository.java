package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.camera.Camera;
import com.ternsip.glade.graphics.camera.CameraController;
import com.ternsip.glade.graphics.camera.ThirdPersonController;
import com.ternsip.glade.graphics.visual.base.graphical.Effigy;
import com.ternsip.glade.graphics.visual.base.graphical.Visual;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.universe.entities.repository.EntityRepository.NO_CAMERA_TARGET;

@Getter
public class GraphicalRepository implements Universal {

    private static final Comparator<Map.Entry<Effigy, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Effigy, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    private final TextureRepository textureRepository = new TextureRepository();

    @Getter(lazy = true)
    private final TexturePackRepository texturePackRepository = new TexturePackRepository();

    private final ModelRepository modelRepository = new ModelRepository();
    private final ShaderRepository shaderRepository = new ShaderRepository();

    private final Camera camera = new Camera();
    private final CameraController cameraController = new ThirdPersonController();
    private final Set<Effigy> effigies = new HashSet<>();
    private final Map<Entity, Visual> entityToVisual = new HashMap<>();

    private long lastSeenNumberOfEntitiesInFrustum = 0;

    public void addGraphical(Effigy effigy) {
        effigies.add(effigy);
    }

    public void removeGraphical(Effigy effigy) {
        effigies.remove(effigy);
    }

    public void render() {
        updateEntities();
        Set<Light> lights = getUniverse().getEntityRepository().getLights();
        HashMap<Effigy, Float> graphicalToDistance = getEffigies()
                .stream()
                .filter(Effigy::isGraphicalInsideFrustum)
                .collect(Collectors.toMap(e -> e, Effigy::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new));
        lastSeenNumberOfEntitiesInFrustum = graphicalToDistance.size();
        graphicalToDistance.entrySet()
                .stream()
                .sorted(COMPARE_BY_PRIORITY.thenComparing(COMPARE_BY_DISTANCE_TO_CAMERA))
                .forEach(k -> k.getKey().render(lights));
    }

    public void finish() {
        getModelRepository().finish();
        getShaderRepository().finish();
        getTextureRepository().finish();
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

}
