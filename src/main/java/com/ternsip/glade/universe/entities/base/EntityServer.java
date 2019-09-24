package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.universe.interfaces.IUniverseServer;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class EntityServer extends EntityBase implements IUniverseServer {

    @Getter(lazy = true)
    private final EntityClient entityClient = produceEntityClient();

    public void register() {
        getUniverseServer().getEntityServerRepository().register(this);
    }

    public void unregister() {
        getUniverseServer().getEntityServerRepository().unregister(this);
    }

    public void update() {
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws IOException {}

    @Override
    public void writeToStream(ObjectOutputStream oos) throws IOException {}

    @Nullable
    protected abstract EntityClient produceEntityClient();

    public boolean isTransferable() {
        return getEntityClient() != null;
    }


}
