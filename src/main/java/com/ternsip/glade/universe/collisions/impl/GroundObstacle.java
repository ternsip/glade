package com.ternsip.glade.universe.collisions.impl;

import com.ternsip.glade.universe.collisions.base.Colliding;
import lombok.Getter;
import org.joml.AABBf;
import org.joml.LineSegmentf;
import org.joml.Vector3fc;

@Getter
public class GroundObstacle implements Colliding {

    private final AABBf aabb = new AABBf(-Float.MAX_VALUE, 0, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    @Override
    public Vector3fc collideSegment(LineSegmentf segment) {
        return collideSegmentDefault(segment);
    }
}
