package com.ternsip.glade.universe.parts.chunks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class MovementRequest {

    private final Vector3ic prevPos;
    private final Vector3ic nextPos;

}