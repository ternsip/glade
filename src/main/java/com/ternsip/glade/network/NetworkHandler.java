package com.ternsip.glade.network;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class NetworkHandler {

    private final Map<Class, List<NetworkCallback>> handlers = new HashMap<>();

    public <T> void registerCallback(Class<T> clazz, NetworkCallback<T> callback) {
        getHandlers().computeIfAbsent(clazz, a -> new ArrayList<>()).add(callback);
    }

    public <T> void unregisterCallback(Class<T> clazz, NetworkCallback<T> callback) {
        getHandlers().get(clazz).remove(callback);
    }

    @SuppressWarnings("unchecked")
    public void handleObject(Connection connection, Object object) {
        List<NetworkCallback> callbacks = getHandlers().get(object.getClass());
        if (callbacks != null) {
            callbacks.forEach(e -> e.execute(connection, object));
        }
    }

}
