package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.utils.Maths;
import org.joml.*;

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
        return new Model(new Mesh[]{meshX, meshY, meshZ}, new Animation(), new Vector3f(0), new Vector3f(0), new Vector3f(0.075f));
    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Vector3fc totalScale = getAdjustedScale().mul(getAnimator().getModel().getNormalizingScale());
        Matrix4fc view = UNIVERSE.getCamera().getFullViewMatrix();
        Quaternionfc rotQuaternion = view.getNormalizedRotation(new Quaternionf());
        return view
                .invert(new Matrix4f())
                .translate(-1.5f, 0.8f, -1f)
                .rotate(rotQuaternion)
                .scale(totalScale);
    }

    @Override
    public Vector3f getAdjustedPosition() {
        Vector3f pos = new Vector3f(UNIVERSE.getCamera().getPosition());
        Vector3f look = new Vector3f(UNIVERSE.getCamera().getFrontDirection());
        return pos.add(look);
    }
}
