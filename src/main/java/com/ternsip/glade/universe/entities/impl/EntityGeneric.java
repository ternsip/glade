package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityGraphical;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends EntityGraphical<Graphical> {

    // TODO supplier, not function
    private final Function<Void, Graphical> loadVisual;

    @Override
    @SneakyThrows
    public Graphical getVisual() {
        return loadVisual.apply(null);
    }

    @Override
    public void update() {

    }

}
