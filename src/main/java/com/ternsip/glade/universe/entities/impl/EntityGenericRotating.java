package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGenericRotating extends Entity {

    private final Supplier<Effigy> loadVisual;
    private final Vector3f rotationSpeed;

    @Override
    public Effigy getEffigy() {
        return loadVisual.get();
    }

    @Override
    public void update() {
        increaseRotation(getRotationSpeed());
    }
}
