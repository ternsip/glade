package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.universe.UniverseClient;

public interface IUniverseClient {

    ThreadWrapper<UniverseClient> UNIVERSE_CLIENT_THREAD = new ThreadWrapper<>(UniverseClient::new, 1000L / 128);

    static void stopUniverseClientThread() {
        UNIVERSE_CLIENT_THREAD.stop();
    }

    default boolean isUniverseClientThreadActive() {
        return UNIVERSE_CLIENT_THREAD.isActive();
    }

    default UniverseClient getUniverseClient() {
        return UNIVERSE_CLIENT_THREAD.getObjective();
    }

}
