package com.ternsip.glade.universe.parts.items;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class Item implements Serializable {

    private int count = 1;

    public abstract void use(EntityPlayerServer player);

    public abstract Object getKey();

}
