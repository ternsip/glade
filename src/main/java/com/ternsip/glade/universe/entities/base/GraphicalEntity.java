package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.common.VolumetricInterpolated;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.joml.Vector3ic;

import java.io.ObjectInputStream;

import static com.ternsip.glade.common.logic.Maths.round;

@Getter
@Setter
// TODO rename to GraphicalClientEntity
public abstract class GraphicalEntity<T extends Effigy> extends EntityClient {

    @Delegate
    private final VolumetricInterpolated volumetricInterpolated = new VolumetricInterpolated();

    private boolean visible = true;
    private float skyIntensity = 1f;
    private float emitIntensity = 1f;

    public void update(T effigy) {
        effigy.setFromVolumetricInterpolated(getVolumetricInterpolated());
        effigy.setSkyIntensity(getSkyIntensity());
        effigy.setEmitIntensity(getEmitIntensity());
        effigy.setVisible(isVisible());
    }

    @Override
    public void update() {
        super.update();
        Vector3ic blockPos = round(getPosition());
        setSkyIntensity(1); // TODO fix that
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
                ois.readFloat(), ois.readFloat(), ois.readFloat()
        );
    }

}
