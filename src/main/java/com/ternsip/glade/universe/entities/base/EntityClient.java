package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Getter
public abstract class EntityClient extends EntityBase implements IUniverseClient {

    public void register() {
        getUniverseClient().getEntityClientRepository().register(this);
    }

    public void unregister() {
        getUniverseClient().getEntityClientRepository().unregister(getUuid());
    }

    public void update() {
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {}

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {}


}
