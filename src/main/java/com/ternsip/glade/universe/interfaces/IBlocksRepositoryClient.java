package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.universe.parts.chunks.BlocksClientRepository;

public interface IBlocksRepositoryClient {

    LazyThreadWrapper<BlocksClientRepository> BLOCKS_CLIENT_REPOSITORY_THREAD = new LazyThreadWrapper<>(BlocksClientRepository::new);

    default void stopBlocksClientThread() {
        if (BLOCKS_CLIENT_REPOSITORY_THREAD.isInitialized()) {
            BLOCKS_CLIENT_REPOSITORY_THREAD.getThreadWrapper().stop();
        }
    }

    default BlocksClientRepository getBlocksClientRepository() {
        return BLOCKS_CLIENT_REPOSITORY_THREAD.getObjective();
    }

}
