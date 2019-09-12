package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.network.ClientSide;
import com.ternsip.glade.network.NetworkSide;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.joml.Vector3fc;

import java.util.UUID;

/**
 * Class should be thread safe
 */
@Getter
public abstract class Entity<T extends Effigy> implements IUniverseClient, IUniverseServer {

    @Delegate
    private transient final VolumetricInterpolated volumetricInterpolated = new VolumetricInterpolated();

    @Delegate(excludes = VolumetricSetters.class)
    private final Volumetric volumetric = new Volumetric();

    private final UUID uuid = UUID.randomUUID();
    private final NetworkSide networkSide = findNetworkSide();

    public void register() {
        if (getNetworkSide() == NetworkSide.CLIENT) {
            getUniverseClient().getEntityClientRepository().register(this);
        } else {
            getUniverseServer().getEntityServerRepository().register(this);
        }
    }

    public void unregister() {
        if (getNetworkSide() == NetworkSide.CLIENT) {
            getUniverseClient().getEntityClientRepository().unregister(getUuid());
        } else {
            getUniverseServer().getEntityServerRepository().register(this);
        }
    }

    public void update(T effigy) {
        effigy.setFromVolumetricInterpolated(getVolumetricInterpolated());
    }

    // This method can be called only in graphics, it should be supplied
    public abstract T getEffigy();

    public NetworkSide findNetworkSide() {
        if (Utils.isAnnotationPresentInHierarchy(getClass(), ClientSide.class)) {
            return NetworkSide.CLIENT;
        }
        return NetworkSide.SERVER;
    }

    public void clientUpdate() {
    }

    public void serverUpdate() {
    }

    @ServerSide
    public void setVolumetric(Volumetric volumetric) {
        getVolumetric().setFromVolumetric(volumetric);
        getVolumetricInterpolated().updateWithVolumetric(volumetric);
    }

    public void setPosition(Vector3fc position) {
        getVolumetric().setPosition(position);
        getVolumetric().updateTime();
        getVolumetricInterpolated().updateWithVolumetric(getVolumetric());
    }

    public void setScale(Vector3fc scale) {
        getVolumetric().setScale(scale);
        getVolumetric().updateTime();
        getVolumetricInterpolated().updateWithVolumetric(getVolumetric());
    }

    public void setRotation(Vector3fc rotation) {
        getVolumetric().setRotation(rotation);
        getVolumetric().updateTime();
        getVolumetricInterpolated().updateWithVolumetric(getVolumetric());
    }

    public void setVisible(boolean visible) {
        getVolumetric().setVisible(visible);
        getVolumetric().updateTime();
        getVolumetricInterpolated().updateWithVolumetric(getVolumetric());
    }

}
