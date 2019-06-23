package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Sides implements Serializable {

    private Map<SidePosition, SideData> sides = new HashMap<>();

    public BlocksUpdate generateBlockUpdate() {
        return new BlocksUpdate(Collections.emptyList(), sides.entrySet().stream().map(enytry -> new Side(enytry.getKey(), enytry.getValue())).collect(Collectors.toList()));
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        sides = new HashMap<>();
        int size = ois.readInt();
        for (int i = 0; i < size; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            BlockSide blockSide = (BlockSide) ois.readObject();
            byte light = ois.readByte();
            Block block = (Block) ois.readObject();
            sides.put(new SidePosition(x, y, z, blockSide), new SideData(light, block));
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
            oos.writeByte(sideData.getLight());
            oos.writeObject(sideData.getBlock());
        }
    }

}
