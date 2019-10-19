package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EngagedBlocks implements Serializable {

    private Map<Vector3ic, Block> positionToBlock = new HashMap<>();

    public boolean isExists(Vector3ic position) {
        return getPositionToBlock().containsKey(position);
    }

    public void remove(Vector3ic position) {
        getPositionToBlock().remove(position);
    }

    public void put(Vector3ic position, Block block) {
        getPositionToBlock().put(position, block);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.positionToBlock = new HashMap<>();
        int size = ois.readInt();
        for (int i = 0; i < size; ++i) {
            int x = ois.readInt();
            int y = ois.readInt();
            int z = ois.readInt();
            Block block = Block.getBlockByIndex(ois.readInt());
            this.positionToBlock.put(new Vector3i(x, y, z), block);
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(positionToBlock.size());
        for (Map.Entry<Vector3ic, Block> entry : this.positionToBlock.entrySet()) {
            oos.writeInt(entry.getKey().x());
            oos.writeInt(entry.getKey().y());
            oos.writeInt(entry.getKey().z());
            oos.writeInt(entry.getValue().getIndex());
        }
    }

}
