package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.UniverseClient;

public interface IUniverseClient {

    ThreadWrapper<UniverseClient> UNIVERSE_CLIENT_THREAD = new ThreadWrapper<>(UniverseClient::new, 1000L / 128);

    static void stopUniverseClientThread() {
        UNIVERSE_CLIENT_THREAD.stop();
    }

    static Thread getRootThread() {
        return UNIVERSE_CLIENT_THREAD.getObjective().getRootThread();
    }

    default boolean isUniverseClientThreadActive() {
        return UNIVERSE_CLIENT_THREAD.isActive();
    }

    default UniverseClient getUniverseClient() {
        UniverseClient universeClient = UNIVERSE_CLIENT_THREAD.getObjective();
        if (Thread.currentThread() == IUniverseServer.getRootThread()) {
            throw new IllegalArgumentException("You can not call Server from client thread");
        }
        return universeClient;
    }

}
