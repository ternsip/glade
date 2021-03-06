package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EffigyRepository implements IUniverseClient, IGraphics {

    /**
     * It is possible to create shader class order in order to reduce bandwidth to GPU due to binding different shader classes
     * Each time you bind another shader class it takes time.
     * It is not guaranteed to this approach will increase the performance
     * Do not sort effigies every render step in case you decide to implement that.
     */
    private final Map<GraphicalEntity, Effigy> entityToEffigy = new HashMap<>();

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

    public void finish() {
        getEntityToEffigy().values().forEach(Effigy::finish);
    }

    @SuppressWarnings("unchecked")
    private void updateEntities() {
        Collection<GraphicalEntity> entities = getUniverseClient().getEntityClientRepository().getGraphicalEntities();
        entities.forEach(e -> getEntityToEffigy().computeIfAbsent(e, x -> e.getEffigy()));
        getEntityToEffigy().entrySet().removeIf(entry -> {
            GraphicalEntity entity = entry.getKey();
            Effigy effigy = entry.getValue();
            if (!entities.contains(entity)) {
                effigy.finish();
                return true;
            }
            return false;
        });
        GraphicalEntity cameraTarget = getUniverseClient().getEntityClientRepository().getCameraTarget();
        getEntityToEffigy().forEach((entity, effigy) -> {
            entity.update(effigy);
            if (entity == cameraTarget) {
                getGraphics().getCameraController().setTarget(effigy.getCameraAttachmentPoint());
                getGraphics().getCameraController().update();
            }
        });
    }

}
