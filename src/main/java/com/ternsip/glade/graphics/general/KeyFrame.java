package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
class KeyFrame {

    private final Map<String, BoneTransform> boneKeyFrames;

}
