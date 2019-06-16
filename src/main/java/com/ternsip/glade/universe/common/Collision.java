package com.ternsip.glade.universe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3fc;

@RequiredArgsConstructor
@Getter
public class Collision {

    private final boolean collision;
    private final Object object;
    private final Vector3fc position;

}