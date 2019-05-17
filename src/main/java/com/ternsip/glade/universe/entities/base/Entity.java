package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Animator;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.utils.Maths;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.ternsip.glade.Glade.UNIVERSE;

@Getter
public abstract class Entity {

    @Getter(lazy = true)
    private final Animator animator = new Animator(UNIVERSE.getEntityRepository().getEntityModel(this));
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

    public Entity() {
        UNIVERSE.getEntityRepository().addEntity(this);
    }

    public Matrix4f getTransformationMatrix() {
        return Maths.createTransformationMatrix(getPosition(), Maths.getRotationQuaternion(getRotation()), getScale());
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

    protected abstract Model loadModel();

    protected boolean isModelUnique() {
        return false;
    }

}
