package com.ternsip.glade.universe.audio;

import com.ternsip.glade.universe.interfaces.Universal;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class SoundRepository implements Universal {

    private final Set<Sound> sounds = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Vector3f listenerPosition = new Vector3f(0);
    private final Vector3f orientationFront = new Vector3f(0);
    private final Vector3f orientationUp = new Vector3f(0);

    public void setListenerPosition(Vector3fc pos) {
        getListenerPosition().set(pos);
    }

    public void setListenerOrientationFront(Vector3fc orient) {
        getOrientationFront().set(orient.normalize(new Vector3f()));
    }

    public void setListenerOrientationUp(Vector3fc orient) {
        getOrientationUp().set(orient.normalize(new Vector3f()));
    }

    public void register(Sound sound) {
        getSounds().add(sound);
    }

    public void unregister(Sound sound) {
        getSounds().remove(sound);
    }

    public void clear() {
        getSounds().clear();
    }

}
