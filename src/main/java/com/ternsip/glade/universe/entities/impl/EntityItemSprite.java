package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyItemSprite;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityItemSprite extends GraphicalEntity<EffigyItemSprite> {

    private final Object key;
    private final boolean ortho;
    private final boolean useAspect;

    @Override
    public EffigyItemSprite getEffigy() {
        return new EffigyItemSprite(getKey(), isOrtho(), isUseAspect());
    }

}
