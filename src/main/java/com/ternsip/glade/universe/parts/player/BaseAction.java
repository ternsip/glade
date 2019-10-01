package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.interfaces.IUniverseServer;

import java.io.Serializable;

public abstract class BaseAction implements Serializable, IUniverseServer {

    public abstract void apply(EntityPlayerServer player);

}
