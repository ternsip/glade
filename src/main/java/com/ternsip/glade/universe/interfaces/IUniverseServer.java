package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.UniverseServer;

public interface IUniverseServer {

    ThreadWrapper<UniverseServer> UNIVERSE_SERVER_THREAD = new ThreadWrapper<>(UniverseServer::new, 1000L / 128);

    static void stopUniverseServerThread() {
        UNIVERSE_SERVER_THREAD.stop();
    }

    static Thread getRootThread() {
        return UNIVERSE_SERVER_THREAD.getObjective().getRootThread();
    }

    default boolean isUniverseServerThreadActive() {
        return UNIVERSE_SERVER_THREAD.isActive();
    }

    // TODO add checking thread client or server to not call one from another
    default UniverseServer getUniverseServer() {
        UniverseServer universeServer = UNIVERSE_SERVER_THREAD.getObjective();
        if (Thread.currentThread() == IUniverseClient.getRootThread()) {
            throw new IllegalArgumentException("You can not call client from server thread");
        }
        return universeServer;
    }

}
