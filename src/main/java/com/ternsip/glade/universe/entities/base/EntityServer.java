package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.interfaces.IUniverseServer;

import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class EntityServer extends EntityBase implements IUniverseServer {

    private static final Map<Class, Boolean> CLASS_IS_TRANSFERABLE = new HashMap<>();

    public void register() {
        getUniverseServer().getEntityServerRepository().register(this);
    }

    public void unregister() {
        getUniverseServer().getEntityServerRepository().unregister(this);
    }

    public void update() {
    }

    public void networkUpdate() {
    }

    public void writeToStream(ObjectOutputStream oos) throws Exception {}

    public boolean isTransferable() {
        return CLASS_IS_TRANSFERABLE.computeIfAbsent(getClass(), k -> {
            Method realMethod = Utils.findDeclaredMethodInHierarchy(getClass(), "getEntityClient", Connection.class);
            Method originalMethod = Utils.findDeclaredMethodInHierarchy(EntityServer.class, "getEntityClient", Connection.class);
            return !realMethod.equals(originalMethod);
        });
    }

    public EntityClient getEntityClient(Connection connection) {
        return null;
    }

}
