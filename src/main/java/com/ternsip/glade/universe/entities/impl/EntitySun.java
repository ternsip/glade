package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySky;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static java.lang.Math.abs;

@Getter
public class EntitySun extends Entity<EffigySky> implements Light {

    private float phase = 0;
    private float delta = 0.001f;
    private Vector2f origin = new Vector2f(0, 0);
    private Vector2f size = new Vector2f(1, 1);
    private Vector3f color = new Vector3f(1, 1, 1);

    @Override
    public void update(EffigySky effigy) {
    }

    @Override
    public EffigySky getEffigy() {
        return new EffigySky();
    }

    @Override
    public void update() {
        phase += delta;
        phase %= 1;
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(
                origin.x() + (float) Math.cos(phase * 2f * Math.PI) * size.x(),
                origin.y() + (float) Math.sin(phase * 2f * Math.PI) * size.y(),
                0
        );
    }

    @Override
    public float getIntensity() {
        return (float) Math.max(0.2, 1 - abs(1/3.0 - phase) * 3);
    }

    @Override
    public Vector3f getColor() {
        return new Vector3f(1, 1, 1);
    }

}
