package com.ternsip.glade.universe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

@RequiredArgsConstructor
@Getter
@Setter
public class Balance {

    private float fogDensity = 0.007f;
    private float fogGradient = 1.5f;
    private Vector4f fogColor = new Vector4f(0.39f, 0.58f, 0.92f, 1f);

    private int viewDistance = 8; // TODO move it to Options class, and fog
    private int ticksPerSecond = 128;
    private Vector3fc gravity = new Vector3f(0, -0.005f, 0);

}
