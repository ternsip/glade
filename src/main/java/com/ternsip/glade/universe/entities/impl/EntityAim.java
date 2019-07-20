package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import org.joml.Vector3f;

import java.io.File;

@Getter
public class EntityAim extends Entity {

    public EntityAim() {
        setScale(new Vector3f(0.01f));
    }

    @Override
    public Effigy getEffigy() {
        return new EffigySprite(new File("tools/aim.png"), true, true);
    }

}
