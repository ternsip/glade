package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Animation;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.utils.Maths;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static com.ternsip.glade.Glade.UNIVERSE;

@Getter
public abstract class Entity {

    @Getter(lazy = true)
    private final Animation animation = new Animation(UNIVERSE.getEntityRepository().getEntityModel(this));
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    public Entity() {
        /// XXX Automatically saving entity upon it's creation
        UNIVERSE.getEntityRepository().addEntity(this);
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

    protected abstract Model loadModel();

    public boolean isFrontal() {
        return false;
    }

    public boolean isSprite() {
        return false;
    }

    public void finish() {
        UNIVERSE.getEntityRepository().removeEntity(this);
    }

    public Object getModelKey() {
        return Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
    }

}
