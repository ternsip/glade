package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;

@Getter
public class EntityDummy extends Entity {

    @Override
    public Effigy getEffigy() {
        return null;
    }

}
