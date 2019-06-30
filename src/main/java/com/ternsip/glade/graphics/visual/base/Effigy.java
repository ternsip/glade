package com.ternsip.glade.graphics.visual.base;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Transformable;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.display.Graphical;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import org.joml.*;

import java.lang.Math;

@Getter
public abstract class Effigy<SHADER extends ShaderProgram> implements Transformable, Graphical, Universal {

    @Getter(lazy = true)
    private final Model model = getGraphics().getModelRepository().getEffigyModel(this);

    @Getter(lazy = true)
    private final SHADER shader = getGraphics().getShaderRepository().getShader(this);

    private final Vector3f position = new Vector3f(0);
    private final Vector3f scale = new Vector3f(1);
    private final Vector3f rotation = new Vector3f(0);

    public Matrix4f getTransformationMatrix() {
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

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public void setScale(Vector3fc scale) {
        this.scale.set(scale);
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.set(rotation);
    }

    public void increasePosition(Vector3fc delta) {
        position.add(delta);
    }

    public void increaseRotation(Vector3fc delta) {
        rotation.add(delta);
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

    public int getPriority() {
        return 0;
    }

    public boolean isGraphicalInsideFrustum() {
        Vector3fc scale = getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * 1.5f;
        return getFrustumIntersection().testSphere(getAdjustedPosition(), delta);
    }

    public FrustumIntersection getFrustumIntersection() {
        Matrix4fc projection = getGraphics().getCamera().getNormalProjectionMatrix();
        Matrix4fc view = getGraphics().getCamera().getViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        return new FrustumIntersection(projectionViewMatrix);
    }

    public float getSquaredDistanceToCamera() {
        return getAdjustedPosition().distanceSquared(getGraphics().getCamera().getPosition());
    }

    public Object getShaderKey() {
        return getShaderClass();
    }

    public abstract Class<SHADER> getShaderClass();

    public Object getModelKey() {
        return Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
    }

    protected Matrix4fc getViewMatrix() {
        return getGraphics().getCamera().getViewMatrix();
    }

    protected Matrix4fc getProjectionMatrix() {
        return getGraphics().getCamera().getNormalProjectionMatrix();
    }

}
