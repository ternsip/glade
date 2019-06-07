package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.entities.base.EntityGraphical;
import com.ternsip.glade.universe.graphicals.base.Graphical;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.joml.Vector3f;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public class EntityGenericRotating extends EntityGraphical<Graphical> {

    private final Function<Void, Graphical> loadVisual;
    private final Vector3f rotationSpeed;

    @Override
    @SneakyThrows
    public Graphical getVisual() {
        return loadVisual.apply(null);
    }

    @Override
    public void update() {
        increaseRotation(getRotationSpeed());
    }
}
