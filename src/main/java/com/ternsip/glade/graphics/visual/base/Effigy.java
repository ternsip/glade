package com.ternsip.glade.graphics.visual.base;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.universe.entities.base.Volumetric;
import com.ternsip.glade.universe.interfaces.IUniverse;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.joml.*;

import java.lang.Math;

@Getter
public abstract class Effigy<SHADER extends ShaderProgram> implements IGraphics, IUniverse {

    @Delegate
    private final Volumetric volumetric = new Volumetric();

    @Getter(lazy = true)
    private final Model model = getGraphics().getModelRepository().getEffigyModel(this);

    @Getter(lazy = true)
    private final SHADER shader = getGraphics().getShaderRepository().getShader(this);

    public Matrix4fc getTransformationMatrix() {
        Vector3fc totalScale = getAdjustedScale().mul(getModel().getNormalizingScale());
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(totalScale);
    }

    public Vector3f getAdjustedScale() {
        return new Vector3f(getScale()).mul(getModel().getBaseScale());
    }

    public Vector3f getAdjustedRotation() {
        return new Vector3f(getRotation()).add(getModel().getBaseRotation());
    }

    public Vector3f getAdjustedPosition() {
        return new Vector3f(getPosition()).add(getModel().getBaseOffset());
    }

    public abstract void render();

    public abstract Model loadModel();

    public boolean deleteModelOnFinish() {
        return false;
    }

    public boolean deleteShaderOnFinish() {
        return false;
    }

    public void finish() {
        if (deleteModelOnFinish()) {
            getGraphics().getModelRepository().removeEffigyModel(this);
        }
        if (deleteShaderOnFinish()) {
            getGraphics().getShaderRepository().removeShader(this);
        }
    }

    public boolean isGraphicalInsideFrustum() {
        Vector3fc scale = getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * 1.5f;
        return getFrustumIntersection().testSphere(getAdjustedPosition(), delta);
    }

    public FrustumIntersection getFrustumIntersection() {
        return new FrustumIntersection(getProjectionMatrix().mul(getViewMatrix(), new Matrix4f()));
    }

    public Object getShaderKey() {
        return getShaderClass();
    }

    public abstract Class<SHADER> getShaderClass();

    public Object getModelKey() {
        return Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
    }

    public Vector3fc getCameraAttachmentPoint() {
        return getPosition();
    }

    protected Matrix4fc getViewMatrix() {
        return getGraphics().getCamera().getViewMatrix();
    }

    protected Matrix4fc getProjectionMatrix() {
        return getGraphics().getCamera().getNormalProjectionMatrix();
    }

}
