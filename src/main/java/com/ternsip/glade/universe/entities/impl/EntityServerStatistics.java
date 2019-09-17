package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.network.ClientSide;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

@Getter
@Setter
public class EntityServerStatistics extends Entity<Effigy> {

    @ClientSide
    private LineSegmentf eyeSegment = new LineSegmentf();

    @ServerSide
    private Block lookingAtBlock = Block.AIR;

    @ServerSide
    private Vector3i lookingAtBlockPosition = new Vector3i(0);

    @Override
    public void update(Effigy effigy) {
        super.update(effigy);
        Vector3fc eye = effigy.getGraphics().getCameraController().getTarget();
        // TODO take eye length from options
        Vector3fc direction = effigy.getGraphics().getCameraController().getLookDirection().mul(10, new Vector3f());
        setEyeSegment(new LineSegmentf(eye, eye.add(direction, new Vector3f())));
    }

    @Override
    public Effigy getEffigy() {
        return new EffigyDummy();
    }

    @Override
    public void serverUpdate() {
        super.serverUpdate();
        Vector3ic pos = getUniverseServer().getBlocksRepository().traverse(getEyeSegment(), block -> block != Block.AIR);
        if (pos != null) {
            setLookingAtBlock(getUniverseServer().getBlocksRepository().getBlock(pos));
            setLookingAtBlockPosition(new Vector3i(pos));
        } else {
            setLookingAtBlockPosition(new Vector3i((int) getEyeSegment().aX, (int) getEyeSegment().aY, (int) getEyeSegment().aZ));
            setLookingAtBlock(Block.AIR);
        }
    }
}
