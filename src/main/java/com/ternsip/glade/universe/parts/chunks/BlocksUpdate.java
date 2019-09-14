package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class BlocksUpdate implements Serializable {

    private List<SidePosition> sidesToRemove = new ArrayList<>();
    private List<Side> sidesToAdd = new ArrayList<>();

    public BlocksUpdate(Sides sides, boolean additive) {
        if (additive) {
            sides.getSides().forEach((pos, data) -> {
                add(new Side(pos, data));
            });
        } else {
            sides.getSides().forEach((pos, data) -> {
                remove(pos);
            });
        }
    }

    public boolean isEmpty() {
        return getSidesToAdd().isEmpty() && getSidesToRemove().isEmpty();
    }

    public void add(Side side) {
        getSidesToAdd().add(side);
    }

    public void remove(SidePosition sidePosition) {
        getSidesToRemove().add(sidePosition);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        int sidesToRemoveSize = ois.readInt();
        this.sidesToRemove = new ArrayList<>(sidesToRemoveSize);
        for (int i = 0; i < sidesToRemoveSize; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            BlockSide blockSide = BlockSide.getSideByIndex(ois.readInt());
            this.sidesToRemove.add(new SidePosition(x, y, z, blockSide));
        }
        int sidesToAddSize = ois.readInt();
        this.sidesToAdd = new ArrayList<>(sidesToAddSize);
        for (int i = 0; i < sidesToAddSize; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            BlockSide blockSide = BlockSide.getSideByIndex(ois.readInt());
            byte skyLight = ois.readByte();
            byte emitLight = ois.readByte();
            Block block = Block.getBlockByIndex(ois.readInt());
            this.sidesToAdd.add(new Side(new SidePosition(x, y, z, blockSide), new SideData(skyLight, emitLight, block)));
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(getSidesToRemove().size());
        for (SidePosition sidePosition : getSidesToRemove()) {
            oos.writeInt(sidePosition.getX());
            oos.writeInt(sidePosition.getY());
            oos.writeInt(sidePosition.getZ());
            oos.writeInt(sidePosition.getSide().getIndex());
        }
        oos.writeInt(getSidesToAdd().size());
        for (Side side : getSidesToAdd()) {
            SidePosition sidePosition = side.getSidePosition();
            SideData sideData = side.getSideData();
            oos.writeInt(sidePosition.getX());
            oos.writeInt(sidePosition.getY());
            oos.writeInt(sidePosition.getZ());
            oos.writeInt(sidePosition.getSide().getIndex());
            oos.writeByte(sideData.getSkyLight());
            oos.writeByte(sideData.getEmitLight());
            oos.writeInt(sideData.getBlock().getIndex());
        }
    }

}