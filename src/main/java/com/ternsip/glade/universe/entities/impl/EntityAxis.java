package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.universe.entities.base.Entity;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import static com.ternsip.glade.Glade.UNIVERSE;

public class EntityAxis extends Entity {

    @Override
    protected Model loadModel() {
        Material red = new Material(new Texture(new Vector4f(1, 0, 0, 0.5f)));
        Material greed = new Material(new Texture(new Vector4f(0, 1, 0, 0.5f)));
        Material blue = new Material(new Texture(new Vector4f(0, 0, 1, 0.5f)));
        float proportion = 1 / 30f;
        Mesh meshX = EntityCube.createAABBMesh(new Vector3f(1, proportion, proportion), red);
        Mesh meshY = EntityCube.createAABBMesh(new Vector3f(proportion, 1, proportion), greed);
        Mesh meshZ = EntityCube.createAABBMesh(new Vector3f(proportion, proportion, 1), blue);
        return new Model(new Mesh[]{meshX, meshY, meshZ}, new Animation(), new Vector3f(0), new Vector3f(0), new Vector3f(5));
    }

    @Override
    public Vector3f getPosition() {
        Vector3f pos = new Vector3f(UNIVERSE.getCamera().getPosition());
        Vector3f look = new Vector3f(UNIVERSE.getCamera().getDirection());
        return pos.add(look.mul(10));
    }
}
