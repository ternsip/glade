package com.ternsip.glade.network;

import com.ternsip.glade.universe.interfaces.IUniverse;

import java.io.Serializable;

public interface Packet extends Serializable, IUniverse {

    void apply(Connection connection);

}
