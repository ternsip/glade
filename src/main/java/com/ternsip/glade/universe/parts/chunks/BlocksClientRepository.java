package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Indexer2D;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Getter
public class BlocksClientRepository extends BlocksRepositoryBase implements Threadable, IUniverseClient {

    public static final int VIEW_DISTANCE = 256;
    public static final Indexer INDEXER = new Indexer(new Vector3i(VIEW_DISTANCE));
    public static final Indexer2D INDEXER_XZ = new Indexer2D(VIEW_DISTANCE, VIEW_DISTANCE);
    public static final byte MAX_LIGHT_LEVEL = 15;

    private final Map<SidePosition, Block> sides = new HashMap<>();
    private final ConcurrentLinkedDeque<SidesUpdate> sidesUpdates = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<ChangeBlocksRequest> changeBlocksRequests = new ConcurrentLinkedDeque<>();

    @Override
    public void init() {
    }

    @Override
    public void update() {
    }

    @Override
    public void finish() {
    }

    @Override
    public void setBlock(Vector3ic pos, Block block) {
        super.setBlock(pos, block);
        visualUpdate(pos, new Vector3i(1));
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        super.setBlock(x, y, z, block);
        visualUpdate(new Vector3i(x, y, z), new Vector3i(1));
    }

    @Override
    public void setBlocks(Vector3ic start, Block[][][] region) {
        super.setBlocks(start, region);
        visualUpdate(start, new Vector3i(region[0].length, region[0][0].length, region[0][0].length));
    }

    public synchronized void visualUpdate(Vector3ic start, Vector3ic size) {

        // Add border blocks to engage neighbour side-recalculation
        Vector3ic startChanges = new Vector3i(start).sub(new Vector3i(1)).max(new Vector3i(0));
        Vector3ic endChangesExcluding = new Vector3i(start).add(size).add(new Vector3i(1)).min(SIZE);

        // Calculate which sides should be removed or added
        SidesUpdate sidesUpdate = new SidesUpdate();

        // Recalculating added/removed sides based on blocks state and putting them to the queue
        for (int x = startChanges.x(); x < endChangesExcluding.x(); ++x) {
            for (int z = startChanges.z(); z < endChangesExcluding.z(); ++z) {
                for (int y = startChanges.y(); y < endChangesExcluding.y(); ++y) {

                    Block block = getBlock(x, y, z);
                    for (BlockSide blockSide : BlockSide.values()) {
                        SidePosition sidePosition = new SidePosition(x, y, z, blockSide);
                        Block oldSideBlock = sides.get(sidePosition);
                        Block newSideBlock = null;
                        if (block != Block.AIR) {
                            int nx = x + blockSide.getAdjacentBlockOffset().x();
                            int ny = y + blockSide.getAdjacentBlockOffset().y();
                            int nz = z + blockSide.getAdjacentBlockOffset().z();
                            if (INDEXER.isInside(nx, ny, nz)) {
                                Block nextBlock = getBlock(nx, ny, nz);
                                if (nextBlock == null || (nextBlock.isSemiTransparent() && (block != nextBlock || !block.isCombineSides()))) {
                                    newSideBlock = block;
                                }
                            } else {
                                newSideBlock = block;
                            }
                        }
                        if (newSideBlock != null && newSideBlock != oldSideBlock) {
                            sidesUpdate.toAdd(new Side(sidePosition, newSideBlock));
                            sides.put(sidePosition, newSideBlock);
                        }
                        if (newSideBlock == null && oldSideBlock != null) {
                            sidesUpdate.toRemove(sidePosition);
                            sides.remove(sidePosition);
                        }
                    }

                }
            }
        }

        sidesUpdates.add(sidesUpdate);
        changeBlocksRequests.add(new ChangeBlocksRequest(start, size));

    }

}
