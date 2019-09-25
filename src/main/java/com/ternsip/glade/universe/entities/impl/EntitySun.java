package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySky;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;

@Getter
@Setter
public class EntitySun extends GraphicalEntity<EffigySky> {

    private final Vector3f color = new Vector3f(1, 1, 1);
    private float phase = 0;
    private float delta = 0.001f;

    @Override
    public void update(EffigySky effigy) {
        super.update(effigy);
        effigy.setIntensity(getIntensity());
        effigy.setColor(getColor());
    }

    @Override
    public EffigySky getEffigy() {
        return new EffigySky();
    }

    public float getIntensity() {
        return 1;
        //return (float) Math.max(0.2, 1 - abs(1 / 3.0 - phase) * 3); TODO temporary, also this can be decided by server
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        super.readFromStream(ois);
        setPhase(ois.readFloat());
        setDelta(ois.readFloat());
    }

}
