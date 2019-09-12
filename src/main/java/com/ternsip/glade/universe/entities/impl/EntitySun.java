package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigySky;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Getter
public class EntitySun extends Entity<EffigySky> {

    private final Vector2f origin = new Vector2f(0, 0);
    private final Vector2f radius = new Vector2f(1, 1);
    private final Vector3f color = new Vector3f(1, 1, 1);

    @ServerSide
    private float phase = 0;

    @ServerSide
    private float delta = 0.001f;

    @Override
    public void update(EffigySky effigy) {
        super.update(effigy);
        effigy.setIntensity(getIntensity());
        effigy.setColor(getColor());
    }

    @Override
    public EffigySky getEffigy() {
        return new EffigySky();
    }

    @Override
    public void serverUpdate() {
        super.serverUpdate();
        phase += delta;
        phase %= 1;
        setPosition(getCorePosition());
    }

    public float getIntensity() {
        return 1;
        //return (float) Math.max(0.2, 1 - abs(1 / 3.0 - phase) * 3); TODO temporary
    }

    private Vector3fc getCorePosition() {
        return new Vector3f(
                origin.x() + (float) Math.cos(phase * 2f * Math.PI) * radius.x(),
                origin.y() + (float) Math.sin(phase * 2f * Math.PI) * radius.y(),
                0
        );
    }

}
