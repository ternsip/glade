package com.ternsip.glade.graphics.visual.base;

import com.ternsip.glade.universe.common.Light;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Getter
@Setter
@RequiredArgsConstructor
public class LightSource implements Light {

    private final Vector3fc position;
    private final Vector3fc color;
    private final float intensity;

    public LightSource() {
        this.position = new Vector3f(0);
        this.color = new Vector3f(1);
        this.intensity = 1;
    }
}
