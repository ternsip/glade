package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.interfaces.IUniverseServer;

import java.io.Serializable;

public abstract class BaseAction implements Serializable, IUniverseClient, IUniverseServer {

    public abstract void applyOnServer(EntityPlayerServer player);

    public abstract void applyOnClient(EntityPlayer player);

}
