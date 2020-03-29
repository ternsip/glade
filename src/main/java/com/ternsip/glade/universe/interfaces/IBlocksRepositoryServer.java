package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.universe.parts.chunks.BlocksServerRepository;

public interface IBlocksRepositoryServer {

    LazyThreadWrapper<BlocksServerRepository> BLOCKS_SERVER_REPOSITORY_THREAD = new LazyThreadWrapper<>(BlocksServerRepository::new);

    default void stopBlocksServerThread() {
        if (BLOCKS_SERVER_REPOSITORY_THREAD.isInitialized()) {
            BLOCKS_SERVER_REPOSITORY_THREAD.getThreadWrapper().stop();
        }
    }

    default BlocksServerRepository getBlocksServerRepository() {
        return BLOCKS_SERVER_REPOSITORY_THREAD.getObjective();
    }

}
