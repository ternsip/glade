package com.ternsip.glade.universe.parts.generators;

import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;
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

    private final int minHeight = 45;
    private final int treeAttempts = BlocksRepository.SIZE_X * BlocksRepository.SIZE_Z / 100;

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
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
        for (int i = 0; i < treeAttempts; ++i) {
            int x = random.nextInt(BlocksRepository.SIZE_X);
            int z = random.nextInt(BlocksRepository.SIZE_Z);
            int y = BlocksRepository.SIZE_Y - 1;
            for (; y >= minHeight; --y) {
                if (blocksRepository.getBlockInternal(x, y, z) == Block.LAWN) {
                    break;
                }
            }
            if (y < minHeight) {
                continue;
            }
            Schematic tree = trees.get(random.nextInt(trees.size()));
            Vector3ic start = new Vector3i(x, y + 1, z);
            Vector3ic end = new Vector3i(start).add(tree.getSize());
            if (!blocksRepository.isBlockExists(end)) {
                continue;
            }
            blocksRepository.putSchematicInternal(start, tree);
        }
    }
}
