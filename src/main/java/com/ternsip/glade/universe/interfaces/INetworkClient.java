package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyThreadWrapper;
import com.ternsip.glade.network.NetworkClient;

public interface INetworkClient {

    LazyThreadWrapper<NetworkClient> CLIENT_THREAD = new LazyThreadWrapper<>(NetworkClient::new);

    default void stopClientThread() {
        if (CLIENT_THREAD.isInitialized()) {
            getClient().stop();
            CLIENT_THREAD.getThreadWrapper().stop();
            CLIENT_THREAD.getThreadWrapper().join();
        }
    }

    default NetworkClient getClient() {
        return CLIENT_THREAD.getObjective();
    }

}
