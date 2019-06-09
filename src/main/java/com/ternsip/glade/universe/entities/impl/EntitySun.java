package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.graphicals.impl.GraphicalSky;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class EntitySun extends Entity<GraphicalSky> implements Light {

    // TODO supposed to run moon on the night
    private float phase;
    private float delta;
    private Vector2f origin;
    private Vector2f size;
    private Vector3f colour;

    public EntitySun(Vector2f origin, Vector2f size, Vector3f colour) {
        super();
        this.phase = 0;
        this.delta = 0.005f;
        this.origin = origin;
        this.size = size;
        this.colour = colour;
    }

    @Override
    public GraphicalSky getVisual() {
        return new GraphicalSky();
    }

    @Override
    public void update(GraphicalSky visual) {
        visual.setSunPosition(getPosition());
    }

    @Override
    public void update() {
        phase += delta;
        phase %= 1;
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(
                origin.x() + (float) Math.sin(phase * 2f * Math.PI) * size.x(),
                origin.y() + (float) Math.cos(phase * 2f * Math.PI) * size.y(),
                2000
        );
    }

    @Override
    public float getIntensity() {
        return 1;
    }

    @Override
    public Vector3f getColor() {
        return new Vector3f(1, 1, 1);
    }

}
