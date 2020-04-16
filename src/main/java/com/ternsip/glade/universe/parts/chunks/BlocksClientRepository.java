package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Getter
public class BlocksClientRepository extends BlocksRepositoryBase implements Threadable, IUniverseClient {

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
    public synchronized void setBlock(Vector3ic pos, Block block) {
        super.setBlock(pos, block);
        visualUpdate(pos, new Vector3i(1));
    }

    @Override
    public synchronized void setBlock(int x, int y, int z, Block block) {
        super.setBlock(x, y, z, block);
        visualUpdate(new Vector3i(x, y, z), new Vector3i(1));
    }

    @Override
    public synchronized void setBlocks(Vector3ic start, Block[][][] region) {
        super.setBlocks(start, region);
        visualUpdate(start, new Vector3i(region[0].length, region[0][0].length, region[0][0].length));
    }

    public void visualUpdate(Vector3ic start, Vector3ic size) {
        changeBlocksRequests.add(new ChangeBlocksRequest(start, size));
    }

}
