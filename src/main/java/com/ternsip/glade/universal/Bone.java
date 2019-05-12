package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.List;

@Getter
@Setter
public class Bone {

    private final int index;// ID TODO think about excessive
    private final String name;
    private final List<Bone> children;
    private final Matrix4f localBindTransform;
    private final Matrix4f inverseBindTransform;
    private Matrix4f animatedTransform = new Matrix4f();

    public Bone(
            int index,
            String name,
            List<Bone> children,
            Matrix4f bindLocalTransform,
            Matrix4f inverseBindTransform
    ) {
        this.index = index;
        this.name = name;
        this.children = children;
        this.localBindTransform = bindLocalTransform;
        this.inverseBindTransform = inverseBindTransform;
    }
}
