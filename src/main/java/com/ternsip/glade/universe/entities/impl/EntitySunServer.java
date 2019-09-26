package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.ObjectOutputStream;

@Getter
public class EntitySunServer extends GraphicalEntityServer {

    private final Vector2f origin = new Vector2f(0, 0);
    private final Vector2f radius = new Vector2f(1, 1);
    private float phase = 0;
    private float delta = 0.001f;

    @Override
    public void update() {
        super.update();
        phase += delta;
        phase %= 1;
        setPosition(getCorePosition());
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        super.writeToStream(oos);
        oos.writeFloat(getPhase());
        oos.writeFloat(getDelta());
    }

    @Override
    public EntityClient getEntityClient(Connection connection) {
        return new EntitySun();
    }

    private Vector3fc getCorePosition() {
        return new Vector3f(
                origin.x() + (float) Math.cos(phase * 2f * Math.PI) * radius.x(),
                origin.y() + (float) Math.sin(phase * 2f * Math.PI) * radius.y(),
                0
        );
    }

}
