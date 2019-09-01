package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class EffigyRepository implements IUniverse, IGraphics {

    private final Map<Entity, Effigy> entityToEffigy = new HashMap<>();

    private long lastSeenNumberOfEntitiesInFrustum = 0;

    public void render() {
        updateEntities();
        lastSeenNumberOfEntitiesInFrustum = 0;
        getEntityToEffigy().values().forEach(effigy -> {
            if (effigy.isVisible() && effigy.isGraphicalInsideFrustum()) {
                effigy.render();
                ++lastSeenNumberOfEntitiesInFrustum;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void updateEntities() {
        Set<Entity> entities = getUniverse().getEntityRepository().getEntities();
        entities.forEach(e -> getEntityToEffigy().computeIfAbsent(e, x -> e.getEffigy()));
        getEntityToEffigy().entrySet().removeIf(entry -> {
            Entity entity = entry.getKey();
            Effigy effigy = entry.getValue();
            if (!entities.contains(entity)) {
                entity.unregister();
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
        Entity cameraTarget = getUniverse().getEntityRepository().getCameraTarget();
        getEntityToEffigy().forEach((entity, effigy) -> {
            entity.update(effigy);
            if (entity == cameraTarget) {
                getGraphics().getCameraController().update(cameraTarget);
            }
        });
    }

}
