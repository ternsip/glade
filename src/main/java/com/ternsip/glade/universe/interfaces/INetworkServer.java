package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.network.NetworkServer;

public interface INetworkServer {

    ThreadWrapper<NetworkServer> SERVER_THREAD = new ThreadWrapper<>(NetworkServer::new, 1000L / 128);

    default void stopServerThread() {
        getServer().stop();
        SERVER_THREAD.stop();
        SERVER_THREAD.join();
    }

    default NetworkServer getServer() {
        return SERVER_THREAD.getObjective();
    }

}
