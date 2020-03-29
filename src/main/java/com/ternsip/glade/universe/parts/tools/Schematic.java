package com.ternsip.glade.universe.parts.tools;

import com.ternsip.glade.universe.interfaces.IUniverseServer;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.BlocksServerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.*;

@RequiredArgsConstructor
@Getter
public class Schematic implements IUniverseServer {

    public static final String EXTENSION = ".schematic";

    public volatile Block[][][] blocks;

    @SneakyThrows
    public Schematic(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                this.blocks = (Block[][][]) ois.readObject();
            }
        }
    }

    public Schematic(Vector3ic start, Vector3ic end) {
        this.blocks = getUniverseServer().getBlocksServerRepository().getBlocks(start, end);
    }

    public Vector3ic getSize() {
        return new Vector3i(blocks[0][0].length, blocks[0].length, blocks.length);
    }

    @SneakyThrows
    public void save(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(getBlocks());
            }
        }
    }

    public void put(Vector3ic pos) {
        getUniverseServer().getBlocksServerRepository().setBlocks(pos, getBlocks());
    }

    public void putInternal(Vector3ic start, BlocksServerRepository blocksServerRepository, boolean ignoreAir) {
        Vector3ic size = getSize();
        for (int x = 0, wx = start.x(); x < size.x(); ++x, ++wx) {
            for (int y = 0, wy = start.y(); y < size.y(); ++y, ++wy) {
                for (int z = 0, wz = start.z(); z < size.z(); ++z, ++wz) {
                    Block block = blocks[x][y][z];
                    if (ignoreAir && block == Block.AIR) {
                        continue;
                    }
                    blocksServerRepository.setBlock(wx, wy, wz, block);
                }
            }
        }
    }

}
