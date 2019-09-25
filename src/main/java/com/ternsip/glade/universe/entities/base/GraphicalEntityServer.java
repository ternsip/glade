package com.ternsip.glade.universe.entities.base;

import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.ObjectOutputStream;

@Getter
public abstract class GraphicalEntityServer extends EntityServer {

    @Delegate
    private final Volumetric volumetric = new Volumetric();

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        oos.writeFloat(getPosition().x());
        oos.writeFloat(getPosition().y());
        oos.writeFloat(getPosition().z());
        oos.writeFloat(getRotation().x());
        oos.writeFloat(getRotation().y());
        oos.writeFloat(getRotation().z());
        oos.writeFloat(getScale().x());
        oos.writeFloat(getScale().y());
        oos.writeFloat(getScale().z());
        oos.writeBoolean(isVisible());
    }

}
