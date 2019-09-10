package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;
import org.joml.Vector3fc;

import java.util.UUID;

/**
 * Class should be thread safe
 */
@Getter
public abstract class Entity<T extends Effigy> implements IUniverse {

    private final Volumetric volumetric = new Volumetric();
    private final UUID uuid = UUID.randomUUID();

    public void register() {
        if (isClientSideOnly()) {
            registerOnClient();
        } else {
            registerOnServer();
        }
    }

    public void unregister() {
        if (isClientSideOnly()) {
            unregisterOnClient();
        } else {
            unregisterOnServer();
        }
    }

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
        effigy.setFromVolumetric(getVolumetric());
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

    @ServerSide
    public void setVolumetric(Volumetric volumetric) {
        getVolumetric().setFromVolumetric(volumetric);
    }

    public void setPosition(Vector3fc position) {
        getVolumetric().setPosition(position);
        getVolumetric().updateTime();
    }

    public void setScale(Vector3fc scale) {
        getVolumetric().setScale(scale);
        getVolumetric().updateTime();
    }

    public void setRotation(Vector3fc rotation) {
        getVolumetric().setRotation(rotation);
        getVolumetric().updateTime();
    }

    public void setVisible(boolean visible) {
        getVolumetric().setVisible(visible);
        getVolumetric().updateTime();
    }

    public Vector3fc getPosition() {
        return getVolumetric().getPosition();
    }

    public Vector3fc getScale() {
        return getVolumetric().getScale();
    }

    public Vector3fc getRotation() {
        return getVolumetric().getRotation();
    }

    public boolean isVisible() {
        return getVolumetric().isVisible();
    }

}
