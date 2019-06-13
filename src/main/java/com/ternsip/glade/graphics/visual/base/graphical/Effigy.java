package com.ternsip.glade.graphics.visual.base.graphical;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.universe.common.Light;
import lombok.Getter;
import org.joml.*;

import java.lang.Math;
import java.util.Set;

@Getter
public abstract class Effigy<SHADER extends ShaderProgram> implements Visual, Transformable {

    @Getter(lazy = true)
    private final Model model = getGraphics().getGraphicalRepository().getModelRepository().getGraphicalModel(this);

    @Getter(lazy = true)
    private final SHADER shader = getGraphics().getGraphicalRepository().getShaderRepository().getShader(this);

    private final Vector3f position = new Vector3f(0);
    private final Vector3f scale = new Vector3f(1);
    private final Vector3f rotation = new Vector3f(0);

    public Effigy() {
        getGraphics().getGraphicalRepository().addGraphical(this);
    }

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

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public void increasePosition(Vector3f delta) {
        position.add(delta);
    }

    public void increaseRotation(Vector3f delta) {
        rotation.add(delta);
    }

    public abstract void render(Set<Light> lights);

    public abstract Model loadModel();

    public int getPriority() {
        return 0;
    }

    public boolean isGraphicalInsideFrustum() {
        Vector3fc scale = getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * 1.5f;
        return getFrustumIntersection().testSphere(getAdjustedPosition(), delta);
    }

    public FrustumIntersection getFrustumIntersection() {
        Matrix4fc projection = getGraphics().getGraphicalRepository().getCamera().getNormalProjectionMatrix();
        Matrix4fc view = getGraphics().getGraphicalRepository().getCamera().getViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        return new FrustumIntersection(projectionViewMatrix);
    }

    public void finish() {
        getGraphics().getGraphicalRepository().removeGraphical(this);
    }

    public float getSquaredDistanceToCamera() {
        return getAdjustedPosition().distanceSquared(getGraphics().getGraphicalRepository().getCamera().getPosition());
    }

    public Object getShaderKey() {
        return getShaderClass();
    }

    public abstract Class<SHADER> getShaderClass();

    public Object getModelKey() {
        return Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
    }

    protected Matrix4fc getViewMatrix() {
        return getGraphics().getGraphicalRepository().getCamera().getViewMatrix();
    }

    protected Matrix4fc getProjectionMatrix() {
        return getGraphics().getGraphicalRepository().getCamera().getNormalProjectionMatrix();
    }

}
