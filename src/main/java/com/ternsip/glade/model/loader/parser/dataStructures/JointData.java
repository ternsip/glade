package com.ternsip.glade.model.loader.parser.dataStructures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class JointData {

    private final int index;
    private final String nameId;
    private final Matrix4f bindLocalTransform;
    public final List<JointData> children = new ArrayList<JointData>();

    public void addChild(JointData child) {
        children.add(child);
    }

}
