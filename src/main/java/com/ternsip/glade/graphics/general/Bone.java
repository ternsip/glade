package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
class Bone {

    private final int index;
    private final String name;
    private final List<Bone> children;
    private final Matrix4f inverseBindTransform;

    Bone() {
        this.index = -1;
        this.name = "";
        this.children = Collections.emptyList();
        this.inverseBindTransform = new Matrix4f();
    }

}