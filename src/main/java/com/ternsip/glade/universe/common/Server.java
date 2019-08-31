package com.ternsip.glade.universe.common;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.network.NetworkServer;

public interface Server {

    ThreadWrapper<NetworkServer> SERVER_THREAD = new ThreadWrapper<>(NetworkServer::new);

    default void stopServerThread() {
        getServer().stop();
        SERVER_THREAD.stop();
        SERVER_THREAD.join();
    }

    default NetworkServer getServer() {
        return SERVER_THREAD.getObjective();
    }

}
