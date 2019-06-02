package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.entities.base.EntityDefault;
import org.joml.*;

import static com.ternsip.glade.Glade.UNIVERSE;

public class EntityAxis extends EntityDefault {

    @Override
    protected Model loadModel() {
        Material red = new Material(new Texture(new Vector4f(1, 0, 0, 0.5f)));
        Material greed = new Material(new Texture(new Vector4f(0, 1, 0, 0.5f)));
        Material blue = new Material(new Texture(new Vector4f(0, 0, 1, 0.5f)));
        float proportion = 1 / 30f;
        Mesh meshX = EntityCube.createAABBMesh(new Vector3f(1, proportion, proportion), red);
        Mesh meshY = EntityCube.createAABBMesh(new Vector3f(proportion, 1, proportion), greed);
        Mesh meshZ = EntityCube.createAABBMesh(new Vector3f(proportion, proportion, 1), blue);
        return new Model(new Mesh[]{meshX, meshY, meshZ}, new Vector3f(0), new Vector3f(0), new Vector3f(0.075f));
    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Vector3fc totalScale = getAdjustedScale().mul(getAnimation().getModel().getNormalizingScale());
        Matrix4fc view = UNIVERSE.getCamera().getFullViewMatrix();
        Quaternionfc rotQuaternion = view.getNormalizedRotation(new Quaternionf());
        return view
                .invert(new Matrix4f())
                // TODO make it automatic
                .translate(-1f, 0.7f, -1f)
                .rotate(rotQuaternion)
                .scale(totalScale);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    protected boolean isEntityInsideFrustum() {
        return true;
    }

}
