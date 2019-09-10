package com.ternsip.glade.network;

import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Packet implements Serializable, IUniverse {

    public abstract void apply(Connection connection);

}
