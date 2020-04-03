package com.ternsip.glade.universe.parts.items;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class Item implements Serializable, IUniverseClient, IUniverseServer {

    private int count = 1;

    public abstract void useOnServer(EntityPlayerServer player);

    public abstract void useOnClient(EntityPlayer player);

    public abstract Object getKey();

}
