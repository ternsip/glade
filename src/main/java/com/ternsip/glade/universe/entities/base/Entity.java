package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Animation;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.utils.Maths;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import org.joml.*;

import java.lang.Math;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.Glade.UNIVERSE;

@Getter
public abstract class Entity<SHADER extends ShaderProgram> {

    @Getter(lazy = true)
    private final Animation animation = new Animation(DISPLAY_MANAGER.getModelRepository().getEntityModel(this));
    private final SHADER shader = DISPLAY_MANAGER.getShaderRepository().getEntityShader(this);
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    public Entity() {
        /// XXX Automatically saving entity upon it's creation
        DISPLAY_MANAGER.getEntityRepository().addEntity(this);
    }

    public Matrix4f getTransformationMatrix() {
        Vector3fc totalScale = getAdjustedScale().mul(getAnimation().getModel().getNormalizingScale());
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
        return new Vector3f(getScale()).mul(getAnimation().getModel().getBaseScale());
    }

    public Vector3f getAdjustedPosition() {
        return new Vector3f(getPosition()).add(getAnimation().getModel().getBaseOffset());
    }

    public Vector3f getAdjustedRotation() {
        return new Vector3f(getRotation()).add(getAnimation().getModel().getBaseRotation());
    }

    public void update() {
    }

    protected abstract void render();

    protected abstract Class<SHADER> getShaderClass();

    protected abstract Model loadModel();

    public int getPriority() {
        return 0;
    }

    public boolean isSprite() {
        return false;
    }

    protected boolean isEntityInsideFrustum() {
        Matrix4fc projection = UNIVERSE.getCamera().getEntityProjectionMatrix();
        Matrix4fc view = UNIVERSE.getCamera().getFullViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        FrustumIntersection frustumIntersection = new FrustumIntersection(projectionViewMatrix);
        Vector3fc scale = getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * 1.5f;
        return frustumIntersection.testSphere(getAdjustedPosition(), delta);
    }

    protected Matrix4fc getViewMatrix() {
        return UNIVERSE.getCamera().getFullViewMatrix();
    }

    protected Matrix4fc getProjectionMatrix() {
        return UNIVERSE.getCamera().getEntityProjectionMatrix();
    }

    protected float getSquaredDistanceToCamera() {
        return getAdjustedPosition().distanceSquared(UNIVERSE.getCamera().getPosition());
    }

    public void finish() {
        DISPLAY_MANAGER.getEntityRepository().removeEntity(this);
    }

    public Object getModelKey() {
        return Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
    }

}
