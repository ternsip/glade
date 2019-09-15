package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.universe.UniverseClient;

public interface IUniverseClient {

    LazyThreadWrapper<UniverseClient> UNIVERSE_CLIENT_THREAD = new LazyThreadWrapper<>(UniverseClient::new, 1000L / 128);

    static void stopUniverseClientThread() {
        if (UNIVERSE_CLIENT_THREAD.isInitialized()) {
            UNIVERSE_CLIENT_THREAD.getThreadWrapper().stop();
        }
    }

    default boolean isUniverseClientThreadActive() {
        return UNIVERSE_CLIENT_THREAD.isInitialized() && UNIVERSE_CLIENT_THREAD.getThreadWrapper().isActive();
    }

    default UniverseClient getUniverseClient() {
        UNIVERSE_CLIENT_THREAD.touch();
        if (!isClientThread()) {
            throw new IllegalArgumentException("You can not call client from this thread");
        }
        return UNIVERSE_CLIENT_THREAD.getObjective();
    }

    default boolean isClientThread() {
        if (!UNIVERSE_CLIENT_THREAD.isInitialized()) {
            return false;
        }
        Thread thread = Thread.currentThread();
        return thread == UNIVERSE_CLIENT_THREAD.getThreadWrapper().getThread() ||
                thread == IGraphics.MAIN_THREAD ||
                thread == INetworkClient.CLIENT_THREAD.getThreadWrapper().getThread() ||
                thread == INetworkClient.CLIENT_THREAD.getObjective().getSenderThread().getThreadWrapper().getThread();
    }

}
