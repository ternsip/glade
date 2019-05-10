package com.ternsip.glade.model.loader.animation.model;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Joint {

    public final int index;// ID
    public final String name;
    public final List<Joint> children = new ArrayList<Joint>();
    private final Matrix4f localBindTransform;
    private Matrix4f animatedTransform = new Matrix4f();
    private Matrix4f inverseBindTransform = new Matrix4f();

    public Joint(int index, String name, Matrix4f bindLocalTransform) {
        this.index = index;
        this.name = name;
        this.localBindTransform = bindLocalTransform;
    }

    public void addChild(Joint child) {
        this.children.add(child);
    }

    public Matrix4f getAnimatedTransform() {
        return animatedTransform;
    }

    public void setAnimationTransform(Matrix4f animationTransform) {
        this.animatedTransform = animationTransform;
    }

    public Matrix4f getInverseBindTransform() {
        return inverseBindTransform;
    }

    protected void calcInverseBindTransform(Matrix4f parentBindTransform) {
        // TODO CHECK THIS IS OK (THIS/NEW)
        Matrix4f bindTransform = parentBindTransform.mul(localBindTransform, new Matrix4f());
        bindTransform.invert(inverseBindTransform);
        for (Joint child : children) {
            child.calcInverseBindTransform(bindTransform);
        }
    }

}
