package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectInputStream;

@Getter
@Setter
public class EntityAnotherPlayer extends GraphicalEntity<EffigyBoy> {

    @Override
    public EffigyBoy getEffigy() {
        return new EffigyBoy();
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        super.readFromStream(ois);
        float skyIntensity = ois.readFloat();
    }

}
