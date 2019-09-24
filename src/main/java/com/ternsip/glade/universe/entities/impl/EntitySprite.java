package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySprite;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class EntitySprite extends GraphicalEntity<EffigySprite> {

    private final File file;
    private final boolean ortho;
    private final boolean useAspect;

    @Override
    public EffigySprite getEffigy() {
        return new EffigySprite(getFile(), isOrtho(), isUseAspect());
    }

}
