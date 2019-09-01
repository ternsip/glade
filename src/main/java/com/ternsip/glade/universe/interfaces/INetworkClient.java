package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.network.NetworkClient;

public interface INetworkClient {

    ThreadWrapper<NetworkClient> CLIENT_THREAD = new ThreadWrapper<>(NetworkClient::new);

    default void stopClientThread() {
        getClient().stop();
        CLIENT_THREAD.stop();
        CLIENT_THREAD.join();
    }

    default NetworkClient getClient() {
        return CLIENT_THREAD.getObjective();
    }

}
