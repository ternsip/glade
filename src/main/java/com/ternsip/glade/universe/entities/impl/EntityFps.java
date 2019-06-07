package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.graphicals.impl.GraphicalDynamicText;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@RequiredArgsConstructor
public class EntityFps extends Entity<GraphicalDynamicText> {

    private final long refreshIntervalMilliseconds;

    private long lastTimeStamp = 0;

    // TODO move this calculations to display manager
    private float fpsSum = 0;
    private int fpsCount = 0;

    @Override
    public GraphicalDynamicText getVisual() {
        return new GraphicalDynamicText(new File("fonts/default.png"));
    }

    @Override
    public void update(GraphicalDynamicText visual) {
        fpsSum += DISPLAY_MANAGER.getFps();
        fpsCount++;
        if (System.currentTimeMillis() > lastTimeStamp + refreshIntervalMilliseconds) {
            visual.changeText(String.valueOf(fpsSum / fpsCount));
            fpsSum = 0;
            fpsCount = 0;
            lastTimeStamp = System.currentTimeMillis();
        }
    }

    @Override
    public void update() {

    }

}
