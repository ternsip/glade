package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Bone {

    private final String boneName;
    private final Matrix4f offsetMatrix;
    private final Map<Integer, List<Float>> weights;


}
