package com.ternsip.glade.universe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@RequiredArgsConstructor
@Getter
@Setter
public class Balance {

    private float playerArmLength = 5;
    private float playerExamineLength = 10;

    private float fogDensity = 0.0075f;
    private float fogGradient = 5.0f;
    private Vector3f fogColor = new Vector3f(0.0f, 0.5f, 0.75f);

    private float underwaterFogDensity = 0.15f;
    private float underwaterFogGradient = 5.0f;
    private Vector3f underwaterFogColor = new Vector3f(0.097f, 0.097f, 0.43f);

    private int physicalTicksPerSecond = 128;
    private int networkTicksPerSecond = 20;
    private Vector3fc gravity = new Vector3f(0, -0.005f, 0);

}
