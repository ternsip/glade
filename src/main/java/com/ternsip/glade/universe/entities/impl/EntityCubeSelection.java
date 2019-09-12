package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyCube;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class EntityCubeSelection extends Entity<EffigyCube> {

    private transient final EntityPlayer player;
    @ServerSide
    private Vector3f pos;

    public EntityCubeSelection() {
        player = null;
    }

    @Override
    public void update(EffigyCube effigy) {
        super.update(effigy);
    }

    @Override
    public EffigyCube getEffigy() {
        return new EffigyCube();
    }

    @Override
    public void serverUpdate() {
        super.serverUpdate();
        LineSegmentf segment = getPlayer().getEyeSegment();
        Vector3ic pos = getUniverseServer().getBlocks().traverse(segment, block -> block != Block.AIR);
        if (pos != null) {
            setVisible(true);
            setPosition(new Vector3f(pos));
        } else {
            setVisible(false);
        }
    }
}
