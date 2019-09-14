package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.UniverseServer;

public interface IUniverseServer {

    ThreadWrapper<UniverseServer> UNIVERSE_SERVER_THREAD = new ThreadWrapper<>(UniverseServer::new, 1000L / 128);

    static void stopUniverseServerThread() {
        UNIVERSE_SERVER_THREAD.stop();
    }

    default boolean isUniverseServerThreadActive() {
        return UNIVERSE_SERVER_THREAD.isActive();
    }

    default UniverseServer getUniverseServer() {
        if (Thread.currentThread() != UNIVERSE_SERVER_THREAD.getThread() &&
                Thread.currentThread() != INetworkServer.SERVER_THREAD.getThread() &&
                Thread.currentThread() != IBlocksRepository.BLOCKS_THREAD.getThread()) {
            throw new IllegalArgumentException("You can not call server from this thread");
        }
        return UNIVERSE_SERVER_THREAD.getObjective();
    }

}
