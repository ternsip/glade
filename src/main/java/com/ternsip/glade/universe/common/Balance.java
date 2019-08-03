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

    private int viewDistance = 4; // TODO move it to Options class
    private int ticksPerSecond = 128;
    private Vector3fc gravity = new Vector3f(0, -0.005f, 0);

}
