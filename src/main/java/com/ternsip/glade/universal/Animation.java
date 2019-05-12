package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class Animation {

    private final float length; // In seconds
    private final KeyFrame[] keyFrames;

    public Set<String> findAllDistinctJointNames() {
        Set<String> jointNames = new HashSet<>();
        for (int i = 0; i < keyFrames.length; ++i) {
            jointNames.addAll(keyFrames[i].getJointKeyFrames().keySet());
        }
        return jointNames;
    }

}
