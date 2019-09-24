package com.ternsip.glade.universe.bindings;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.display.KeyEvent;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Bindings implements IUniverseClient {

    private final Map<KeyState, Bind> keyStateToBind = new HashMap<>();
    private final Map<Bind, Collection<BindingCallback>> bindToCallbacks = new HashMap<>();
    private final Callback<KeyEvent> keyCallback = this::handleKeyEvent;

    public Bindings() {
        for (Bind bind : Bind.values()) {
            keyStateToBind.put(bind.getDefaultKeyState(), bind);
        }
        getUniverseClient().getEventIOReceiver().registerCallback(KeyEvent.class, keyCallback);
    }

    public void load() {

    }

    public void save() {

    }

    public void change(Bind bind, KeyState keyState) {

    }

    public void addBindCallback(Bind bind, BindingCallback callback) {
        getBindToCallbacks().computeIfAbsent(bind, k -> new ArrayList<>()).add(callback);
    }

    public void finish() {
        getUniverseClient().getEventIOReceiver().unregisterCallback(KeyEvent.class, keyCallback);
    }

    private void handleKeyEvent(KeyEvent event) {
        Bind bind = getKeyStateToBind().get(new KeyState(event.getKey(), event.getAction(), event.getMods()));
        Collection<BindingCallback> callbacks = getBindToCallbacks().get(bind);
        if (callbacks != null) {
            callbacks.forEach(BindingCallback::execute);
        }
    }

}
