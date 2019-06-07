package com.ternsip.glade.universe.graphicals.base;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.utils.Maths;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import org.joml.*;

import java.lang.Math;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
public abstract class Graphical<SHADER extends ShaderProgram> implements Visual {

    @Getter(lazy = true)
    private final Model model = DISPLAY_MANAGER.getModelRepository().getGraphicalModel(this);
    private final SHADER shader = DISPLAY_MANAGER.getShaderRepository().getGraphicalShader(this);
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    public Graphical() {
        /// XXX Automatically saving graphical upon it's creation
        DISPLAY_MANAGER.getGraphicalRepository().addGraphical(this);
    }

    public Matrix4f getTransformationMatrix() {
        Vector3fc totalScale = getAdjustedScale().mul(getModel().getNormalizingScale());
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(totalScale);
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

    public Vector3f getAdjustedScale() {
        return new Vector3f(getScale()).mul(getModel().getBaseScale());
    }

    public Vector3f getAdjustedPosition() {
        return new Vector3f(getPosition()).add(getModel().getBaseOffset());
    }

    public Vector3f getAdjustedRotation() {
        return new Vector3f(getRotation()).add(getModel().getBaseRotation());
    }

    public abstract void render();

    public abstract Class<SHADER> getShaderClass();

    public abstract Model loadModel();

    public int getPriority() {
        return 0;
    }

    public boolean isGraphicalInsideFrustum() {
        Matrix4fc projection = DISPLAY_MANAGER.getCamera().getGraphicalProjectionMatrix();
        Matrix4fc view = DISPLAY_MANAGER.getCamera().getFullViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        FrustumIntersection frustumIntersection = new FrustumIntersection(projectionViewMatrix);
        Vector3fc scale = getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * 1.5f;
        return frustumIntersection.testSphere(getAdjustedPosition(), delta);
    }

    protected Matrix4fc getViewMatrix() {
        return DISPLAY_MANAGER.getCamera().getFullViewMatrix();
    }

    protected Matrix4fc getProjectionMatrix() {
        return DISPLAY_MANAGER.getCamera().getGraphicalProjectionMatrix();
    }

    public float getSquaredDistanceToCamera() {
        return getAdjustedPosition().distanceSquared(DISPLAY_MANAGER.getCamera().getPosition());
    }

    public void finish() {
        DISPLAY_MANAGER.getGraphicalRepository().removeGraphical(this);
    }

    public Object getShaderKey() {
        return getShaderClass();
    }

    public Object getModelKey() {
        return Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
    }

}
