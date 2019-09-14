package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;

public interface IBlocksRepository {

    LazyThreadWrapper<BlocksRepository> BLOCKS_THREAD = new LazyThreadWrapper<>(BlocksRepository::new);

    default void stopBlocksThread() {
        if (BLOCKS_THREAD.isInitialized()) {
            BLOCKS_THREAD.getThreadWrapper().stop();
        }
    }

    // TODO Rename to getBlocksRepository
    default BlocksRepository getBlocks() {
        return BLOCKS_THREAD.getObjective();
    }

}
