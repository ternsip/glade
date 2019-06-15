package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.camera.Camera;
import com.ternsip.glade.graphics.camera.CameraController;
import com.ternsip.glade.graphics.camera.ThirdPersonController;
import com.ternsip.glade.graphics.display.Graphical;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ternsip.glade.universe.entities.repository.EntityRepository.NO_CAMERA_TARGET;

@Getter
public class GraphicalRepository implements Universal, Graphical {

    private static final Comparator<Map.Entry<Effigy, Float>> COMPARE_BY_PRIORITY = Comparator.comparing(e -> e.getKey().getPriority());
    private static final Comparator<Map.Entry<Effigy, Float>> COMPARE_BY_DISTANCE_TO_CAMERA = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());

    private final TextureRepository textureRepository = new TextureRepository();

    @Getter(lazy = true)
    private final TexturePackRepository texturePackRepository = new TexturePackRepository();

    private final ModelRepository modelRepository = new ModelRepository();
    private final ShaderRepository shaderRepository = new ShaderRepository();

    private final Camera camera = new Camera();
    private final CameraController cameraController = new ThirdPersonController();
    private final Map<Entity, Effigy> entityToEffigy = new HashMap<>();

    private long lastSeenNumberOfEntitiesInFrustum = 0;

    public void render() {
        updateEntities();
        Set<Light> lights = getUniverse().getEntityRepository().getLights();
        HashMap<Effigy, Float> graphicalToDistance = getEntityToEffigy().values().stream()
                .filter(Effigy::isGraphicalInsideFrustum)
                .collect(Collectors.toMap(e -> e, Effigy::getSquaredDistanceToCamera, (a, b) -> a, HashMap::new));
        lastSeenNumberOfEntitiesInFrustum = graphicalToDistance.size();
        graphicalToDistance.entrySet().stream()
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
        getUniverse().getEntityRepository().getMultiEntities().forEach(e -> e.graphicalUpdate(getGraphics()));
        Set<Entity> entities = getUniverse().getEntityRepository().getEntities();
        entities.forEach(e -> getEntityToEffigy().computeIfAbsent(e, x -> e.getEffigy()));
        getEntityToEffigy().entrySet().removeIf(entry -> {
            Entity entity = entry.getKey();
            Effigy effigy = entry.getValue();
            if (!entities.contains(entity)) {
                entity.finish();
                effigy.finish();
                return true;
            }
            return false;
        });
        getEntityToEffigy().entrySet().forEach(entry -> {
            Entity entity = entry.getKey();
            Effigy effigy = entry.getValue();
            if (entity.isVisualReloadRequired()) {
                effigy.finish();
                entry.setValue(entity.getEffigy());
                entity.setVisualReloadRequired(false);
            }
        });
        Entity target = getUniverse().getEntityRepository().getCameraTarget();
        // TODO try to get rid of this
        // That condition exists to prevent shuttering and dragging camera under asynchronous routines
        if (target != NO_CAMERA_TARGET) {
            Effigy targetVisual = getEntityToEffigy().get(target);
            getEntityToEffigy().remove(target);
            getEntityToEffigy().forEach(Entity::update);
            target.update(targetVisual);
            getCameraController().update(target);
            getEntityToEffigy().put(target, targetVisual);
        } else {
            getEntityToEffigy().forEach(Entity::update);
        }
    }

}
