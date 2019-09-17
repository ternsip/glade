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
    private transient final NetworkSide networkExpectedSide = findNetworkExpectedSide();
    private transient final NetworkSide networkSide = findNetworkSide();

    public void register() {
        checkSideValidity();
        if (getNetworkSide() == NetworkSide.CLIENT) {
            onClientRegister();
        } else {
            onServerRegister();
        }
    }

    public void unregister() {
        checkSideValidity();
        if (getNetworkSide() == NetworkSide.CLIENT) {
            onClientUnregister();
        } else {
            onServerUnregister();
        }
    }

    public void onServerRegister() {
        getUniverseServer().getEntityServerRepository().register(this);
    }

    public void onClientRegister() {
        getUniverseClient().getEntityClientRepository().register(this);
    }

    public void onServerUnregister() {
        getUniverseServer().getEntityServerRepository().unregister(this);
    }

    public void onClientUnregister() {
        getUniverseClient().getEntityClientRepository().unregister(getUuid());
    }

    public void update(T effigy) {
        effigy.setFromVolumetricInterpolated(getVolumetricInterpolated());
    }

    /**
     * This method will be called only once in graphical thread
     * It should be supplied by entity
     */
    public abstract T getEffigy();

    public NetworkSide findNetworkExpectedSide() {
        if (Utils.isAnnotationPresentInHierarchy(getClass(), ClientSide.class)) {
            return NetworkSide.CLIENT;
        }
        if (Utils.isAnnotationPresentInHierarchy(getClass(), ServerSide.class)) {
            return NetworkSide.SERVER;
        }
        return NetworkSide.BOTH;
    }

    public NetworkSide findNetworkSide() {
        return isClientThread() ? NetworkSide.CLIENT : NetworkSide.SERVER;
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

    private void checkSideValidity() {
        if (getNetworkExpectedSide() != NetworkSide.BOTH && getNetworkExpectedSide() != getNetworkSide()) {
            throw new IllegalArgumentException("This is not valid network side for this entity");
        }
    }

}
