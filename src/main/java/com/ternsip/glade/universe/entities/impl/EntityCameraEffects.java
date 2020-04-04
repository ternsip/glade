package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.universe.collisions.base.Collision;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import static com.ternsip.glade.graphics.camera.CameraController.MAX_DISTANCE_FROM_TARGET;
import static com.ternsip.glade.graphics.camera.CameraController.MIN_DISTANCE_FROM_TARGET;

@Getter
@Setter
public class EntityCameraEffects extends GraphicalEntity<EffigyDummy> {

    private final float CAMERA_RADIUS = 0.1f;

    private final Vector3f cameraTargetPosition = new Vector3f(0);
    private final Vector3f cameraPosition = new Vector3f(0);

    private boolean underWater = false;

    @Override
    public void update(EffigyDummy effigy) {
        super.update(effigy);
        Vector3ic camPos = new Vector3i((int) cameraPosition.x(), (int) cameraPosition.y(), (int) cameraPosition.z());
        setUnderWater(getUniverseClient().getBlocksClientRepository().isBlockExists(camPos) && getUniverseClient().getBlocksClientRepository().getBlock(camPos) == Block.WATER);
        getCameraPosition().set(effigy.getGraphics().getCamera().getPosition());
        getCameraTargetPosition().set(effigy.getGraphics().getCameraController().getTarget());
        effigy.getGraphics().getCameraController().setDistanceFix(findCameraDistanceFix());
    }

    @Override
    public EffigyDummy getEffigy() {
        return new EffigyDummy();
    }


    private float findCameraDistanceFix() {
        Vector3f diff = new Vector3f(getCameraPosition()).sub(getCameraTargetPosition());
        float closestPoint = Float.MAX_VALUE;
        if (diff.lengthSquared() < 1e-3f) {
            return closestPoint;
        }
        Vector3f end = diff.normalize().mul(MAX_DISTANCE_FROM_TARGET + 1).add(getCameraTargetPosition());
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                for (int dz = -1; dz <= 1; dz += 2) {
                    LineSegmentf targetCameraRay = new LineSegmentf(
                            getCameraTargetPosition().x() + dx * CAMERA_RADIUS,
                            getCameraTargetPosition().y() + dy * CAMERA_RADIUS,
                            getCameraTargetPosition().z() + dz * CAMERA_RADIUS,
                            end.x() + dx * CAMERA_RADIUS,
                            end.y() + dy * CAMERA_RADIUS,
                            end.z() + dz * CAMERA_RADIUS
                    );
                    closestPoint = Math.min(closestPoint, findCameraDistanceFix(targetCameraRay));
                }
            }
        }
        return closestPoint;
    }

    private float findCameraDistanceFix(LineSegmentf segment) {
        Collision cameraCollision = getUniverseClient().getCollisionsClient().collideSegmentFirstOrNull(segment);
        if (cameraCollision == null) {
            return Float.MAX_VALUE;
        }
        return Math.max(MIN_DISTANCE_FROM_TARGET, getCameraTargetPosition().distance(cameraCollision.getPosition()));
    }

}
