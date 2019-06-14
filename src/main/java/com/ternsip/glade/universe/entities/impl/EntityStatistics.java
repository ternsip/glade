package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.DynamicText2D;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;

@RequiredArgsConstructor
public class EntityStatistics extends Entity<DynamicText2D> {

    @Override
    public DynamicText2D getVisual() {
        return new DynamicText2D(new File("fonts/default.png"));
    }

    @Override
    public void update(DynamicText2D visual) {
        StringBuilder sb = new StringBuilder();
        sb.append("FPS : ").append(visual.getGraphics().getWindowData().getFps()).append(System.lineSeparator());
        sb.append("Entities : ").append(visual.getGraphics().getGraphicalRepository().getLastSeenNumberOfEntitiesInFrustum());
        visual.changeText(sb.toString(), new Vector2i(0, 0), new Vector2i(75, 75), new Vector4f(1, 1, 0, 1));
    }

    @Override
    public void update() {

    }

}
