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
        if (!isClientThread()) {
            throw new IllegalArgumentException("You can not call client from this thread");
        }
        return UNIVERSE_CLIENT_THREAD.getObjective();
    }

    default boolean isClientThread() {
        Thread thread = Thread.currentThread();
        return thread == UNIVERSE_CLIENT_THREAD.getThread() ||
                thread == IGraphics.MAIN_THREAD ||
                thread == INetworkClient.CLIENT_THREAD.getThreadWrapper().getThread() ||
                thread == INetworkClient.CLIENT_THREAD.getObjective().getSenderThread().getThreadWrapper().getThread();
    }

}
