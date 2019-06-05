package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityGraphical;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends EntityGraphical<Graphical> {

    private final Graphical graphical;

    @Override
    public Graphical getVisual() {
        return graphical;
    }

}
