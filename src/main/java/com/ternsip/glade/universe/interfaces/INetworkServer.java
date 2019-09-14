package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.network.NetworkServer;

public interface INetworkServer {

    LazyThreadWrapper<NetworkServer> SERVER_THREAD = new LazyThreadWrapper<>(NetworkServer::new, 1000L / 128);

    default void stopServerThread() {
        if (SERVER_THREAD.isInitialized()) {
            getServer().stop();
            SERVER_THREAD.getThreadWrapper().stop();
            SERVER_THREAD.getThreadWrapper().join();
        }
    }

    default NetworkServer getServer() {
        return SERVER_THREAD.getObjective();
    }

}
