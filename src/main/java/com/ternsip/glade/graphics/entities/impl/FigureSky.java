package com.ternsip.glade.graphics.entities.impl;

import com.ternsip.glade.graphics.entities.base.BaseFigure;
import com.ternsip.glade.graphics.entities.base.Figure;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FigureSky implements Figure {

    public static final float SIZE = 10000f;

    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };

    private float phase;
    private float delta;
    private Vector2f origin;
    private Vector2f size;
    private Vector3f colour;

    public FigureSky(Vector2f origin, Vector2f size, Vector3f colour) {
        this.phase = 0;
        this.delta = 0.005f;
        this.origin = origin;
        this.size = size;
        this.colour = colour;
    }

    public void update() {
        phase += delta;
    }

    @Override
    protected Model loadModel() {
        return new Model(
                new Mesh[]{new Mesh(VERTICES, new Material())},
                new Vector3f(0),
                new Vector3f(0),
                new Vector3f(2 * SIZE)
        );
    }

    public Vector3f getSkyPosition() {
        return new Vector3f(
                origin.x() + (float) Math.sin(phase * 2f * Math.PI) * size.x(),
                origin.y() + (float) Math.cos(phase * 2f * Math.PI) * size.y(),
                2000
        );
    }

    public Vector3f getColour() {
        return colour;
    }
}
