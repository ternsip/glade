package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.joml.Vector3fc;

import java.util.UUID;

/**
 * Class should be thread safe
 */
@Getter
public abstract class ClientServerEntity<T extends Effigy> extends Entity<T> {

    @Delegate
    private final Volumetric volumetric = new Volumetric();

    private final UUID uuid = UUID.randomUUID();

    public void registerOnServer() {
        getUniverse().getEntityServerRepository().register(this);
    }

    public void registerOnClient() {
        getUniverse().getEntityClientRepository().register(this);
    }

    public void unregisterOnServer() {
        getUniverse().getEntityServerRepository().register(this);
    }

    public void unregisterOnClient() {
        getUniverse().getEntityClientRepository().unregister(getUuid());
    }

    public void update(T effigy) {
        effigy.setFromAnother(getVolumetric());
    }

    // This method can be called only in graphics, it should be supplied
    public abstract T getEffigy();

    public boolean isClientSideOnly() {
        // TODO use annotations
        return false;
    }

    public void clientUpdate() {
    }

    public void serverUpdate() {
    }

    public Vector3fc getCameraAttachmentPoint() {
        return getPosition();
    }

}
