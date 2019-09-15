package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.universe.UniverseServer;

public interface IUniverseServer {

    LazyThreadWrapper<UniverseServer> UNIVERSE_SERVER_THREAD = new LazyThreadWrapper<>(UniverseServer::new, 1000L / 128);

    static void stopUniverseServerThread() {
        if (UNIVERSE_SERVER_THREAD.isInitialized()) {
            UNIVERSE_SERVER_THREAD.getThreadWrapper().stop();
        }
    }

    static void run() {
        UNIVERSE_SERVER_THREAD.getObjective().startServer();
    }

    default boolean isUniverseServerThreadActive() {
        return UNIVERSE_SERVER_THREAD.isInitialized() && UNIVERSE_SERVER_THREAD.getThreadWrapper().isActive();
    }

    default UniverseServer getUniverseServer() {
        UNIVERSE_SERVER_THREAD.touch();
        if (!isServerThread()) {
            throw new IllegalArgumentException("You can not call server from this thread");
        }
        return UNIVERSE_SERVER_THREAD.getObjective();
    }

    default boolean isServerThread() {
        if (!UNIVERSE_SERVER_THREAD.isInitialized()) {
            return false;
        }
        Thread thread = Thread.currentThread();
        return thread == UNIVERSE_SERVER_THREAD.getThreadWrapper().getThread() ||
                thread == IBlocksRepository.BLOCKS_REPOSITORY_THREAD.getThreadWrapper().getThread() ||
                thread == INetworkServer.SERVER_THREAD.getThreadWrapper().getThread() ||
                thread == INetworkServer.SERVER_THREAD.getObjective().getAcceptorThread().getThreadWrapper().getThread() ||
                thread == INetworkServer.SERVER_THREAD.getObjective().getSenderThread().getThreadWrapper().getThread();
    }

}
