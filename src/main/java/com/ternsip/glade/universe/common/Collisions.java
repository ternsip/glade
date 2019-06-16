package com.ternsip.glade.universe.common;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Collisions implements Universal {

    public Collision collideSegment(Vector3fc start, Vector3fc end) {
        return new Collision(
                end.y() <= 0,
                "ground",
                new Vector3f(end.x(), Math.max(end.y(), 0), end.z())
        );
    }

}
