package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.display.DisplayManager;
import com.ternsip.glade.universe.entities.base.AbstractEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;


@RequiredArgsConstructor
@Getter
public class EntityFps extends AbstractEntity {

    private final long refreshIntervalMilliseconds;

    private EntityText fpsText;
    private long lastTimeStamp = 0;

    // TODO move this calculations to dispaly manager
    private float fpsSum = 0;
    private int fpsCount = 0;

    public void update() {
        fpsSum += DisplayManager.INSTANCE.getFps();
        fpsCount++;
        if (System.currentTimeMillis() <= lastTimeStamp + refreshIntervalMilliseconds) {
            return;
        }
        lastTimeStamp = System.currentTimeMillis();
        if (fpsText != null) {
            fpsText.finish();
        }
        fpsText = new EntityText(
                new File("fonts/default.png"),
                String.valueOf(fpsSum / fpsCount),
                new Vector3f(0, 0, 0),
                new Vector3f(2),
                new Vector4f(0, 1, 1, 1)
        );
        fpsSum = 0;
        fpsCount = 0;
    }
}
