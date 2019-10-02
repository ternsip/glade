package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
class Bone {

    private final int index;
    private final String name;
    private final List<Bone> children;
    private final Matrix4fc inverseLocalBindTransform;

    Bone() {
        this.index = -1;
        this.name = "";
        this.children = Collections.emptyList();
        this.inverseLocalBindTransform = new Matrix4f();
    }

}
