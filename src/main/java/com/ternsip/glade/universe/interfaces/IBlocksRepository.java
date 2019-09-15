package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;

public interface IBlocksRepository {

    LazyThreadWrapper<BlocksRepository> BLOCKS_REPOSITORY_THREAD = new LazyThreadWrapper<>(BlocksRepository::new);

    default void stopBlocksThread() {
        if (BLOCKS_REPOSITORY_THREAD.isInitialized()) {
            BLOCKS_REPOSITORY_THREAD.getThreadWrapper().stop();
        }
    }

    default BlocksRepository getBlocksRepository() {
        return BLOCKS_REPOSITORY_THREAD.getObjective();
    }

}
