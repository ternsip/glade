package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EffigyRepository implements IUniverseClient, IGraphics {

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
        Collection<Entity> entities = getUniverseClient().getEntityClientRepository().getUuidToEntity().values();
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
        Entity cameraTarget = getUniverseClient().getEntityClientRepository().getCameraTarget();
        getEntityToEffigy().forEach((entity, effigy) -> {
            entity.update(effigy);
            if (entity == cameraTarget) {
                getGraphics().getCameraController().setTarget(effigy.getCameraAttachmentPoint());
                getGraphics().getCameraController().update();
            }
        });
    }

}
