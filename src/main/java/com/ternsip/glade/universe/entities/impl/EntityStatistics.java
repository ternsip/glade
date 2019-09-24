package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Getter
@Setter
public class EntityStatistics extends GraphicalEntity<EffigyDummy> {

    private LineSegmentf eyeSegment = new LineSegmentf();
    private Block lookingAtBlock = Block.AIR;
    private Vector3i lookingAtBlockPosition = new Vector3i(0);

    @Override
    public void update(EffigyDummy effigy) {
        super.update(effigy);
        Vector3fc eye = effigy.getGraphics().getCameraController().getTarget();
        // TODO take eye length from options
        Vector3fc direction = effigy.getGraphics().getCameraController().getLookDirection().mul(10, new Vector3f());
        setEyeSegment(new LineSegmentf(eye, eye.add(direction, new Vector3f())));
    }

    @Override
    public EffigyDummy getEffigy() {
        return new EffigyDummy();
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws IOException {
        super.readFromStream(ois);
        setLookingAtBlock(Block.getBlockByIndex(ois.readInt()));
        getLookingAtBlockPosition().set(ois.readInt(), ois.readInt(), ois.readInt());
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws IOException {
        super.writeToStream(oos);
        oos.writeFloat(getEyeSegment().aX);
        oos.writeFloat(getEyeSegment().aY);
        oos.writeFloat(getEyeSegment().aZ);
        oos.writeFloat(getEyeSegment().bX);
        oos.writeFloat(getEyeSegment().bY);
        oos.writeFloat(getEyeSegment().bZ);
    }

}
