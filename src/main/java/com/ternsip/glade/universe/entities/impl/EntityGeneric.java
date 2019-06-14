package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends Entity {

    private final Supplier<Effigy> loadVisual;

    @Override
    public Effigy getEffigy() {
        return loadVisual.get();
    }

}
