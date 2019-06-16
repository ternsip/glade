package com.ternsip.glade.universe.collisions.base;

import org.joml.*;

import static org.joml.Intersectionf.INSIDE;
import static org.joml.Intersectionf.OUTSIDE;

public interface Obstacle {

    AABBf getAabb();

    /**
     * @return Collision point or null when there is no collision
     */
    Vector3fc collideSegment(LineSegmentf segment);

    default Vector3fc collideSegmentDefault(LineSegmentf segment, AABBf aabb) {
        Vector2f pResult = new Vector2f();
        int result = aabb.intersectLineSegment(segment, pResult);
        if (result == OUTSIDE || result == INSIDE) {
            return null;
        }
        float tNear = pResult.x();
        return new Vector3f(
                segment.aX + (segment.bX - segment.aX) * tNear,
                segment.aY + (segment.bY - segment.aY) * tNear,
                segment.aZ + (segment.bZ - segment.aZ) * tNear
        );
    }

}
