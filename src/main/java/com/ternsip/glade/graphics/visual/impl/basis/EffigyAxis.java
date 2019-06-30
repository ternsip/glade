package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import com.ternsip.glade.graphics.visual.impl.test.EffigyCube;
import org.joml.*;

import java.util.Arrays;

public class EffigyAxis extends EffigyAnimated {

    @Override
    public Matrix4f getTransformationMatrix() {
        Vector3fc totalScale = getAdjustedScale().mul(getAnimation().getModel().getNormalizingScale());
        Matrix4fc view = getGraphics().getCamera().getViewMatrix();
        Quaternionfc rotQuaternion = view.getNormalizedRotation(new Quaternionf());
        return view
                .invert(new Matrix4f())
                // TODO make it automatic
                .translate(-1f, 0.7f, -1f)
                .rotate(rotQuaternion)
                .scale(totalScale);
    }

    @Override
    public Model loadModel() {
        Material red = new Material(new Texture(new Vector4f(1, 0, 0, 0.5f)));
        Material greed = new Material(new Texture(new Vector4f(0, 1, 0, 0.5f)));
        Material blue = new Material(new Texture(new Vector4f(0, 0, 1, 0.5f)));
        float proportion = 1 / 30f;
        Mesh meshX = EffigyCube.createAABBMesh(new Vector3f(1, proportion, proportion), red);
        Mesh meshY = EffigyCube.createAABBMesh(new Vector3f(proportion, 1, proportion), greed);
        Mesh meshZ = EffigyCube.createAABBMesh(new Vector3f(proportion, proportion, 1), blue);
        return new Model(Arrays.asList(meshX, meshY, meshZ), new Vector3f(0), new Vector3f(0), new Vector3f(0.075f));
    }


    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

}
