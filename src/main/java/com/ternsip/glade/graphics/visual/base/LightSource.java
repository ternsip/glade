package com.ternsip.glade.graphics.visual.base;

import com.ternsip.glade.universe.common.Light;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

@RequiredArgsConstructor
@Getter
public class LightSource implements Light {

    private final Vector3f position;
    private final float intensity;
    private final Vector3f color;

}