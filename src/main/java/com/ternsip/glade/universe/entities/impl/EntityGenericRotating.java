package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.graphical.Effigy;
import com.ternsip.glade.universe.entities.base.EntityTransformable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGenericRotating extends EntityTransformable<Effigy> {

    private final Supplier<Effigy> loadVisual;
    private final Vector3f rotationSpeed;

    @Override
    public Effigy getVisual() {
        return loadVisual.get();
    }

    @Override
    public void update() {
        increaseRotation(getRotationSpeed());
    }
}
