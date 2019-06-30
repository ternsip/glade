package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.display.Graphics;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2i;
import org.joml.Vector4f;

@RequiredArgsConstructor
public class EntityStatistics extends EntityDynamicText2D {

    @Override
    public void graphicalUpdate(Graphics graphics) {
        StringBuilder sb = new StringBuilder();
        sb.append("FPS : ").append(graphics.getWindowData().getFpsCounter().getFps()).append(System.lineSeparator());
        sb.append("Entities : ").append(graphics.getEffigyRepository().getLastSeenNumberOfEntitiesInFrustum());
        changeText(sb.toString(), new Vector2i(0, 0), new Vector2i(75, 75), new Vector4f(1, 1, 0, 1));
    }

}
