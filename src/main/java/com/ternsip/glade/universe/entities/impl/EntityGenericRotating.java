package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityGraphical;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

@RequiredArgsConstructor
@Getter
public class EntityGenericRotating extends EntityGraphical<Graphical> {

    private final Graphical graphical;
    private final Vector3f rotationSpeed;

    @Override
    public Graphical getVisual() {
        return graphical;
    }

    @Override
    public void update() {
        increaseRotation(getRotationSpeed());
    }
}
