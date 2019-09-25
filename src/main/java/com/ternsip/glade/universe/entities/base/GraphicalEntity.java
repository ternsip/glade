package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.IOException;
import java.io.ObjectInputStream;

@Getter
public abstract class GraphicalEntity<T extends Effigy> extends EntityClient {

    @Delegate
    private final VolumetricInterpolated volumetricInterpolated = new VolumetricInterpolated();

    public void update(T effigy) {
        effigy.setFromVolumetricInterpolated(getVolumetricInterpolated());
    }

    /**
     * This method will be called only once in graphical thread
     * It should be supplied by graphical entity on client side
     */
    public abstract T getEffigy();

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        getVolumetricInterpolated().update(
                ois.readFloat(), ois.readFloat(), ois.readFloat(),
                ois.readFloat(), ois.readFloat(), ois.readFloat(),
                ois.readFloat(), ois.readFloat(), ois.readFloat(),
                ois.readBoolean()
        );
    }

}
