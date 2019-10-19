package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Sides implements Serializable {

    private Set<SidePosition> sidePositions = new HashSet<>();

    public boolean contains(SidePosition sidePosition) {
        return sidePositions.contains(sidePosition);
    }

    public void remove(SidePosition sidePosition) {
        sidePositions.remove(sidePosition);
    }

    public void add(SidePosition sidePosition) {
        sidePositions.add(sidePosition);
    }

    public boolean isEmpty() {
        return getSidePositions().isEmpty();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.sidePositions = new HashSet<>();
        int size = ois.readInt();
        for (int i = 0; i < size; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            BlockSide blockSide = BlockSide.getSideByIndex(ois.readInt());
            this.sidePositions.add(new SidePosition(x, y, z, blockSide));
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(sidePositions.size());
        for (SidePosition sidePosition : this.sidePositions) {
            oos.writeInt(sidePosition.getX());
            oos.writeInt(sidePosition.getY());
            oos.writeInt(sidePosition.getZ());
            oos.writeInt(sidePosition.getSide().getIndex());
        }
    }

}
