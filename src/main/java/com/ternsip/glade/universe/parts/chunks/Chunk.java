package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Chunk implements Serializable {

    public static final int RELAX_PERIOD_MILLISECONDS = 30000;
    public static final int SIZE = 32;
    public Timer timer = new Timer(RELAX_PERIOD_MILLISECONDS);
    public boolean modified = false;
    public Vector3ic pos;
    public Map<SidePosition, SideData> sidePosToSideData = new HashMap<>();
    public Map<Vector3ic, Block> posToEngagedBlock = new HashMap<>();

    public Chunk(Vector3ic pos) {
        this.pos = pos;
    }

    private void readObject(ObjectInputStream ois) throws IOException {
        pos = new Vector3i(ois.readInt(), ois.readInt(), ois.readInt());
        int sidesCount = ois.readInt();
        sidePosToSideData = new HashMap<>(sidesCount);
        timer = new Timer(RELAX_PERIOD_MILLISECONDS);
        modified = false;
        for (int i = 0; i < sidesCount; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            BlockSide blockSide = BlockSide.getSideByIndex(ois.readInt());
            byte skyLight = ois.readByte();
            byte emitLight = ois.readByte();
            Block block = Block.getBlockByIndex(ois.readInt());
            SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
            SideData sideData = new SideData(block, skyLight, emitLight);
            sidePosToSideData.put(sidePosition, sideData);
        }
        int sidesToAddSize = ois.readInt();
        posToEngagedBlock = new HashMap<>(sidesCount);
        for (int i = 0; i < sidesToAddSize; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            Block block = Block.getBlockByIndex(ois.readInt());
            posToEngagedBlock.put(new Vector3i(x, y, z), block);
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(pos.x());
        oos.writeInt(pos.y());
        oos.writeInt(pos.z());
        oos.writeInt(sidePosToSideData.size());
        for (Map.Entry<SidePosition, SideData> entry : sidePosToSideData.entrySet()) {
            oos.writeInt(entry.getKey().x);
            oos.writeInt(entry.getKey().y);
            oos.writeInt(entry.getKey().z);
            oos.writeInt(entry.getKey().side.getIndex());
            oos.writeByte(entry.getValue().skyLight);
            oos.writeByte(entry.getValue().emitLight);
            oos.writeInt(entry.getValue().block.getIndex());
        }
        oos.writeInt(posToEngagedBlock.size());
        for (Map.Entry<Vector3ic, Block> entry : posToEngagedBlock.entrySet()) {
            oos.writeInt(entry.getKey().x());
            oos.writeInt(entry.getKey().y());
            oos.writeInt(entry.getKey().z());
            oos.writeInt(entry.getValue().getIndex());
        }
    }

}