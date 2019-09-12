package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;

public interface IBlocksRepository {

    ThreadWrapper<BlocksRepository> BLOCKS_THREAD = new ThreadWrapper<>(BlocksRepository::new);

    default void stopBlocksThread() {
        BLOCKS_THREAD.stop();
    }

    // TODO Rename to getBlocksRepository
    default BlocksRepository getBlocks() {
        return BLOCKS_THREAD.getObjective();
    }

}
