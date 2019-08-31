package com.ternsip.glade.universe.common;

import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.network.NetworkClient;

public interface Client {

    ThreadWrapper<NetworkClient> CLIENT_THREAD = new ThreadWrapper<>(new NetworkClient());

    default void stopClientThread() {
        getClient().stop();
        CLIENT_THREAD.stop();
        CLIENT_THREAD.join();
    }

    default NetworkClient getClient() {
        return CLIENT_THREAD.getObjective();
    }

}
