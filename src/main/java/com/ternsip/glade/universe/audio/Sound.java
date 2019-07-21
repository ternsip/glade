package com.ternsip.glade.universe.audio;

import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class Sound implements Universal {

    private final File file;
    private final Vector3fc position;
    private final float magnitude;

    public Sound(File file) {
        this.file = file;
        this.position = new Vector3f(0);
        this.magnitude = 1;
    }

    public void register() {
        getUniverse().getSoundRepository().register(this);
    }

}
