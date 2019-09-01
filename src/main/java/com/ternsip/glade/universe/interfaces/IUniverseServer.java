package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.UniverseServer;

public interface IUniverseServer {

    ThreadWrapper<UniverseServer> UNIVERSE_SERVER_THREAD = new ThreadWrapper<>(UniverseServer::new, 1000L / 128);

    static void run(int port) {
        UNIVERSE_SERVER_THREAD.getObjective().getServer().bind(port);
    }

    default boolean isUniverseServerThreadActive() {
        return UNIVERSE_SERVER_THREAD.isActive();
    }

    static void stopUniverseServerThread() {
        UNIVERSE_SERVER_THREAD.stop();
    }

    default UniverseServer getUniverseServer() {
        return UNIVERSE_SERVER_THREAD.getObjective();
    }

}
