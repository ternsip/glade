package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.AbstractGraphical;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@RequiredArgsConstructor
public class GraphicalFps extends AbstractGraphical {

    private final long refreshIntervalMilliseconds;

    private GraphicalText fpsText;
    private long lastTimeStamp = 0;

    // TODO move this calculations to dispaly manager
    private float fpsSum = 0;
    private int fpsCount = 0;

    public void update() {
        fpsSum += DISPLAY_MANAGER.getFps();
        fpsCount++;
        if (System.currentTimeMillis() <= lastTimeStamp + refreshIntervalMilliseconds) {
            return;
        }
        lastTimeStamp = System.currentTimeMillis();
        if (fpsText != null) {
            fpsText.finish();
        }
        fpsText = new GraphicalText(
                new File("fonts/default.png"),
                String.valueOf(fpsSum / fpsCount),
                new Vector2i(0, 0),
                new Vector2i(75, 75),
                new Vector4f(0, 1, 1, 1)
        );
        fpsSum = 0;
        fpsCount = 0;
    }
}
