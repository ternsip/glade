package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyCube;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

public class EntityCubeSelection extends Entity<EffigyCube> {

    @Override
    public void update(EffigyCube effigy) {
        super.update(effigy);
        Vector3fc eye = effigy.getGraphics().getCameraController().getTarget();
        Vector3fc direction = effigy.getGraphics().getCameraController().getLookDirection().mul(10, new Vector3f());
        LineSegmentf segment = new LineSegmentf(eye, eye.add(direction, new Vector3f()));
        Vector3ic pos = getUniverseClient().getBlocks().traverse(segment, block -> block != Block.AIR);
        if (pos != null) {
            setVisible(true);
            setPosition(new Vector3f(pos));
        } else {
            setVisible(false);
        }
    }

    @Override
    public EffigyCube getEffigy() {
        return new EffigyCube();
    }

}
