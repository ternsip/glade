package com.ternsip.glade.universe.common;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.parts.chunks.BlocksRepository;

public interface Blocks {

    ThreadWrapper<BlocksRepository> BLOCKS_THREAD = new ThreadWrapper<>(BlocksRepository::new);

    default void stopBlocksThread() {
        BLOCKS_THREAD.stop();
    }

    default BlocksRepository getBlocks() {
        return BLOCKS_THREAD.getObjective();
    }

}
