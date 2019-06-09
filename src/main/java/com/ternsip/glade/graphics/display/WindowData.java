package com.ternsip.glade.graphics.display;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2i;

@Getter
@Setter
public class WindowData {

    private static final long FPS_MEASURE_TIME_MILLISECONDS = 250;

    private long lastFrameTime;
    private long lastFpsTime;
    private float deltaTime;
    private float fps;
    private long window;
    private Vector2i windowSize;
    private long frameCount = 0;

    public WindowData(long window, Vector2i windowSize) {
        this.lastFrameTime = System.currentTimeMillis();
        this.lastFpsTime = System.currentTimeMillis();
        this.deltaTime = 0;
        this.fps = 0;
        this.window = window;
        this.windowSize = windowSize;
    }


    public void update() {
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
