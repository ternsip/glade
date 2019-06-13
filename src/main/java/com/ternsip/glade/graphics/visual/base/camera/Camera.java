package com.ternsip.glade.graphics.visual.base.camera;

import com.ternsip.glade.common.events.display.ResizeEvent;
import com.ternsip.glade.graphics.display.Graphical;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Getter
@Setter
public class Camera implements Graphical {

    private static final float FOV = (float) Math.toRadians(80);
    private static final float NEAR_PLANE = 0.1f;
    private static final float NORMAL_DISTANCE = 1000;
    private static final float FAR_DISTANCE = 100000f;

    private Matrix4fc normalProjectionMatrix;
    private Matrix4fc farProjectionMatrix;
    private Matrix4fc orthoProjectionMatrix;

    private Vector3fc position = new Vector3f(0);
    private Matrix4fc viewMatrix = new Matrix4f();

    public Camera() {
        getGraphics().getEventSnapReceiver().registerCallback(ResizeEvent.class, e -> this.recalculateProjectionMatrices(e.getWidth(), e.getHeight()));
        recalculateProjectionMatrices(getGraphics().getWindowData().getWidth(), getGraphics().getWindowData().getHeight());
    }

    public static Matrix4f createProjectionMatrix(float viewDistance, float aspectRatio) {
        return new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, viewDistance);
    }

    public static Matrix4fc createOrthoProjectionMatrix(float viewDistance, float width, float height) {
        return new Matrix4f();
    }

    private void recalculateProjectionMatrices(int width, int height) {
        float ratio = (float) width / height;
        normalProjectionMatrix = createProjectionMatrix(NORMAL_DISTANCE, ratio);
        farProjectionMatrix = createProjectionMatrix(FAR_DISTANCE, ratio);
        orthoProjectionMatrix = createOrthoProjectionMatrix(FAR_DISTANCE, width, height);
    }

}