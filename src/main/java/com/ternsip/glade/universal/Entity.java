package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Maths;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Getter
public class Entity {

    private final Animator animator;
    private final Vector3f position;
    private final Vector3f scale;
    private final Vector3f rotation;

    public Entity(Model model) {
        this.animator = new Animator(model);
        this.position = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f(0, 0, 0);
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

}
