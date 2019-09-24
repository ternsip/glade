package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyCube;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.EntityGeneric;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3ic;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@Getter
public class EntityCubeSelectionServer extends GraphicalEntityServer {

    private final EntityPlayerServer player;

    @Override
    public void update() {
        super.update();
        LineSegmentf segment = getPlayer().getEyeSegment();
        Vector3ic pos = getUniverseServer().getBlocksRepository().traverse(segment, block -> block != Block.AIR);
        if (pos != null) {
            setVisible(true);
            setPosition(new Vector3f(pos));
        } else {
            setVisible(false);
        }
    }

    @Nullable
    @Override
    protected EntityClient produceEntityClient() {
        return new EntityGeneric(EffigyCube::new);
    }
}
