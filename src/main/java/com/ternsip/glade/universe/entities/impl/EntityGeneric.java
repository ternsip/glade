package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityTransformable;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends EntityTransformable<Graphical> {

    // TODO supplier, not function
    private final Function<Void, Graphical> loadVisual;

    @Override
    public Graphical getVisual() {
        return loadVisual.apply(null);
    }

    @Override
    public void update() {

    }

}
