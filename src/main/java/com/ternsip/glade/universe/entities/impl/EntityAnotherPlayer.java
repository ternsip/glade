package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityAnotherPlayer extends Entity<EffigyBoy> {

    public EffigyBoy getEffigy() {
        return new EffigyBoy();
    }

}
