package com.ternsip.glade.network;

@FunctionalInterface
public interface NetworkCallback<T> {

    void execute(Connection connection, T value);

}
