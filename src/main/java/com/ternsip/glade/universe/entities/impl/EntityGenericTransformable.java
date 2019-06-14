package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGenericTransformable extends EntityTransformable<Effigy> {

    private final Supplier<Effigy> loadVisual;

    @Override
    public Effigy getVisual() {
        return loadVisual.get();
    }

    @Override
    public void update() {
    }

}
