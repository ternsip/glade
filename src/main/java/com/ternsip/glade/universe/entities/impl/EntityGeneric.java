package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.graphical.Graphical;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends EntityTransformable<Graphical> {

    private final Supplier<Graphical> loadVisual;

    @Override
    public Graphical getVisual() {
        return loadVisual.get();
    }

    @Override
    public void update() {

    }

}
