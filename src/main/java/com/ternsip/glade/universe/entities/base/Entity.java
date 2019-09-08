package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.joml.Vector3fc;

import java.util.UUID;

/**
 * Class should be thread safe
 */
@Getter
public abstract class Entity<T extends Effigy> implements IUniverse {

    @Delegate
    private final Volumetric volumetric = new Volumetric();

    private final Volumetric prevVolumetric = new Volumetric();
    private final Timer lastTimeVolumetricSet = new Timer();

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
        if (isClientSideOnly()) {
            effigy.setFromAnother(getVolumetric());
            return;
        }
        effigy.setPosition(getPositionInterpolated());
        effigy.setRotation(getRotationInterpolated());
        effigy.setScale(getScaleInterpolated());
        effigy.setVisible(isVisible());
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
        return getPositionInterpolated();
    }

    @ServerSide
    public void setVolumetric(Volumetric volumetric) {
        getPrevVolumetric().setFromAnother(getVolumetric());
        getVolumetric().setFromAnother(volumetric);
        getLastTimeVolumetricSet().drop();
    }

    public Vector3fc getPositionInterpolated() {
        return Maths.interpolate(getVolumetric().getPosition(), getPrevVolumetric().getPosition(), getTimeMultiplier());
    }

    public Vector3fc getScaleInterpolated() {
        return Maths.interpolate(getVolumetric().getScale(), getPrevVolumetric().getScale(), getTimeMultiplier());
    }

    public Vector3fc getRotationInterpolated() {
        return Maths.interpolate(getVolumetric().getRotation(), getPrevVolumetric().getRotation(), getTimeMultiplier());
    }

    private float getTimeMultiplier() {
        return Maths.bound(0f, 1f, (150 - getLastTimeVolumetricSet().spent()) / 150f);
    }

}
