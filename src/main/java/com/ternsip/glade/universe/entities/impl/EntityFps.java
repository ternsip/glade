package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.graphicals.impl.GraphicalDynamicText;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@RequiredArgsConstructor
public class EntityFps extends Entity<GraphicalDynamicText> {

    @Override
    public GraphicalDynamicText getVisual() {
        return new GraphicalDynamicText(new File("fonts/default.png"));
    }

    @Override
    public void update(GraphicalDynamicText visual) {
        visual.changeText(String.valueOf(DISPLAY_MANAGER.getWindowData().getFps()));
    }

    @Override
    public void update() {

    }

}
