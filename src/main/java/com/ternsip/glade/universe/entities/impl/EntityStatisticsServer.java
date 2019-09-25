package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.LineSegmentf;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import javax.annotation.Nullable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Getter
@Setter
public class EntityStatisticsServer extends GraphicalEntityServer {

    private final LineSegmentf eyeSegment = new LineSegmentf();
    private final Vector3i lookingAtBlockPosition = new Vector3i(0);
    private Block lookingAtBlock = Block.AIR;

    @Override
    public void update() {
        super.update();
        Vector3ic pos = getUniverseServer().getBlocksRepository().traverse(getEyeSegment(), block -> block != Block.AIR);
        if (pos != null) {
            setLookingAtBlock(getUniverseServer().getBlocksRepository().getBlock(pos));
            getLookingAtBlockPosition().set(pos);
        } else {
            getLookingAtBlockPosition().set((int) getEyeSegment().aX, (int) getEyeSegment().aY, (int) getEyeSegment().aZ);
            setLookingAtBlock(Block.AIR);
        }
    }

    @Nullable
    @Override
    protected EntityClient produceEntityClient() {
        return new EntityStatistics();
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        super.readFromStream(ois);
        getEyeSegment().aX = ois.readFloat();
        getEyeSegment().aY = ois.readFloat();
        getEyeSegment().aZ = ois.readFloat();
        getEyeSegment().bX = ois.readFloat();
        getEyeSegment().bY = ois.readFloat();
        getEyeSegment().bZ = ois.readFloat();
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        super.writeToStream(oos);
        oos.writeInt(getLookingAtBlock().getIndex());
        oos.writeInt(getLookingAtBlockPosition().x());
        oos.writeInt(getLookingAtBlockPosition().y());
        oos.writeInt(getLookingAtBlockPosition().z());
    }

}
