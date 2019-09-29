package com.ternsip.glade.universe.collisions.impl;

import com.ternsip.glade.universe.collisions.base.Obstacle;
import lombok.Getter;
import org.joml.AABBf;
import org.joml.LineSegmentf;
import org.joml.Vector3fc;

import javax.annotation.Nullable;

@Getter
public class GroundObstacle implements Obstacle {

    private final AABBf aabb = new AABBf(-Float.MAX_VALUE, 0, -Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    @Nullable
    @Override
    public Vector3fc collideSegment(LineSegmentf segment) {
        return collideSegmentDefault(segment, getAabb());
    }
}
