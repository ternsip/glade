package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyCube;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3ic;

import java.io.ObjectInputStream;

@RequiredArgsConstructor
@Getter
public class EntityCubeSelectionClient extends GraphicalEntity<EffigyCube> {

    @Override
    public void readFromStream(ObjectInputStream ois) {
    }

    @Override
    public void update() {
        super.update();
        EntityPlayer entityPlayer = getUniverseClient().getEntityClientRepository().getEntityByClass(EntityPlayer.class);
        LineSegmentf segment = entityPlayer.getEyeSegment();
        Vector3ic pos = getUniverseClient().getBlocksClientRepository().traverse(segment, (b, p) -> b != Block.AIR);
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
