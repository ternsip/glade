package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.general.Animator;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.utils.Maths;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.reflect.Method;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;

@Getter
public abstract class Entity {

    private final Animator animator = new Animator(getModelInternally());
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Vector3f rotation = new Vector3f(0, 0, 0);

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

    @SneakyThrows
    private Model getModelInternally() {
        Method method = Utils.findDeclaredMethodInHierarchy(getClass(), "loadModel");
        return DISPLAY_MANAGER.getModelRepository().getMethodToModel().computeIfAbsent(method, e -> loadModel());
    }

}
