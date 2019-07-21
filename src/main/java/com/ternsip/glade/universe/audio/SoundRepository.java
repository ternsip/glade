package com.ternsip.glade.universe.audio;

import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class SoundRepository implements Universal {

    private final Set<Sound> sounds = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void register(Sound sound) {
        getSounds().add(sound);
    }

    public void unregister(Sound sound) {
        getSounds().remove(sound);
    }

}
