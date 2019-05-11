package com.ternsip.glade.model.loader.animation.model;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Joint {

    public final int index;// ID
    public final String name;
    public final List<Joint> children;
    private final Matrix4f localBindTransform;
    private Matrix4f animatedTransform = new Matrix4f();
    private final Matrix4f inverseBindTransform;

    public Joint(int index, String name, List<Joint> children, Matrix4f bindLocalTransform, Matrix4f inverseBindTransform) {
        this.index = index;
        this.name = name;
        this.children = children;
        this.localBindTransform = bindLocalTransform;
        this.inverseBindTransform = inverseBindTransform;
    }
}
