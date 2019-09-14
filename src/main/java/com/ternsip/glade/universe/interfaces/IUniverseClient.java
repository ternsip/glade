package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.graphics.interfaces.IGraphics;
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
        if (Thread.currentThread() != UNIVERSE_CLIENT_THREAD.getThread() &&
                Thread.currentThread() != IGraphics.MAIN_THREAD &&
                Thread.currentThread() != INetworkClient.CLIENT_THREAD.getThread()
        ) {
            throw new IllegalArgumentException("You can not call server from this thread");
        }
        return UNIVERSE_CLIENT_THREAD.getObjective();
    }

}
