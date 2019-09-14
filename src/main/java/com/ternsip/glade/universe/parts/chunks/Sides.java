package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Sides implements Serializable {

    private Map<SidePosition, SideData> sides = new HashMap<>();

    public SideData get(SidePosition sidePosition) {
        return sides.get(sidePosition);
    }

    public void remove(SidePosition sidePosition) {
        sides.remove(sidePosition);
    }

    public void put(SidePosition sidePosition, SideData sideData) {
        sides.put(sidePosition, sideData);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.sides = new HashMap<>();
        int size = ois.readInt();
        for (int i = 0; i < size; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            BlockSide blockSide = (BlockSide) ois.readObject();
            byte skyLight = ois.readByte();
            byte emitLight = ois.readByte();
            Block block = Block.getBlockByIndex(ois.readInt());
            SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
            SideData sideData = new SideData(skyLight, emitLight, block);
            this.sides.put(sidePosition, sideData);
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(sides.size());
        for (Map.Entry<SidePosition, SideData> entry : sides.entrySet()) {
            SidePosition sidePosition = entry.getKey();
            SideData sideData = entry.getValue();
            oos.writeInt(sidePosition.getX());
            oos.writeInt(sidePosition.getY());
            oos.writeInt(sidePosition.getZ());
            oos.writeObject(sidePosition.getSide());
            oos.writeByte(sideData.getSkyLight());
            oos.writeByte(sideData.getEmitLight());
            oos.writeInt(sideData.getBlock().getIndex());
        }
    }

}
