package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class EntitySprite extends Entity {

    private final File file;
    private final boolean ortho;
    private final boolean useAspect;

    @Override
    public Effigy getEffigy() {
        return new EffigySprite(getFile(), isOrtho(), isUseAspect());
    }

}
