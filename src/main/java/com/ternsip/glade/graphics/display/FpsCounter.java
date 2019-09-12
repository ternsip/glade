package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.interfaces.IGraphics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FpsCounter implements IGraphics {

    private static final long FPS_MEASURE_TIME_MILLISECONDS = 250;

    private long lastFrameTime = System.currentTimeMillis();
    private long lastFpsTime = System.currentTimeMillis();
    private float deltaTime = 0;
    private float fps = 0;
    private long frameCount = 0;

    public void updateFps() {
        frameCount++;
        long currentFrameTime = System.currentTimeMillis();
        deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
        if (currentFrameTime - lastFpsTime > FPS_MEASURE_TIME_MILLISECONDS) {
            setFps(((1000f * frameCount) / (currentFrameTime - lastFpsTime)));
            lastFpsTime = currentFrameTime;
            frameCount = 0;
        }
    }

}
