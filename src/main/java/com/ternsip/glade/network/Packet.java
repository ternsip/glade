package com.ternsip.glade.network;

import com.ternsip.glade.universe.common.Universal;

import java.io.Serializable;

public interface Packet extends Serializable, Universal {

    void apply(Connection connection);

}
