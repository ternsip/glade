package com.ternsip.glade.network;

import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Packet implements Serializable {

    public abstract void apply(Connection connection);

}
