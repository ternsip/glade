package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.BlocksServerRepository;
import com.ternsip.glade.universe.parts.tools.Schematic;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class TreeGenerator implements ChunkGenerator {

    private static final int MIN_HEIGHT = 45;
    private static final int TREE_ATTEMPTS = 100;

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void populate(BlocksServerRepository blocksServerRepository, int startX, int startZ, int endX, int endZ) {
        Random random = new Random(0);
        File[] files = new File("schematics/trees").listFiles();
        if (files == null) {
            return;
        }
        List<Schematic> trees = Arrays.stream(files)
                .filter(f -> f.getName().endsWith(Schematic.EXTENSION))
                .map(Schematic::new)
                .collect(Collectors.toList());
        if (trees.size() == 0) {
            return;
        }
        for (int i = 0; i < TREE_ATTEMPTS; ++i) {
            int x = startX + random.nextInt(endX - startX + 1);
            int z = startZ + random.nextInt(endZ - startZ + 1);
            int y = BlocksServerRepository.SIZE_Y - 1;
            for (; y >= MIN_HEIGHT; --y) {
                if (blocksServerRepository.getBlock(x, y, z) == Block.LAWN) {
                    break;
                }
            }
            if (y < MIN_HEIGHT) {
                continue;
            }
            Schematic tree = trees.get(random.nextInt(trees.size()));
            Vector3ic treeStart = new Vector3i(x, y + 1, z);
            Vector3ic treeEnd = new Vector3i(treeStart).add(tree.getSize());
            if (!blocksServerRepository.isBlockExists(treeEnd)) {
                continue;
            }
            tree.putInternal(treeStart, blocksServerRepository, true);
        }
    }

}
