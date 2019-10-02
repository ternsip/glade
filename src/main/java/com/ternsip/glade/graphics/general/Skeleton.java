package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;

import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.common.logic.Utils.assertThat;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.MAX_BONES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.MAX_WEIGHTS;

@Getter
class Skeleton {

    private final Bone[] bones;
    private final Map<String, Bone> boneNameToBone;
    private final Map<String, Integer> boneNameToIndex;

    Skeleton(Bone[] bones) {
        this.bones = bones;
        this.boneNameToBone = new HashMap<>();
        this.boneNameToIndex = new HashMap<>();
        for (int i = 0; i < bones.length; ++i) {
            boneNameToBone.put(bones[i].getName(), bones[i]);
            boneNameToIndex.put(bones[i].getName(), i);
        }
        assertThat(MAX_BONES > bones.length);
    }

    // TODO check out numVertices can we remove this?
    float[] getBonesWeights(int numVertices) {
        int weightLimit = MAX_WEIGHTS;
        float[] weights = new float[numVertices * weightLimit];
        Map<Integer, List<BoneWeight>> combinedBoneWeights = getBoneIndexToWeights();
        for (int i = 0; i < numVertices; i++) {
            List<BoneWeight> boneWeights = combinedBoneWeights.getOrDefault(i, Collections.emptyList());
            for (int j = 0; j < weightLimit; ++j) {
                weights[i * weightLimit + j] = boneWeights.size() > j ? boneWeights.get(j).getBoneWeight() : 0;
            }
        }
        return weights;
    }

    int[] getBoneIndices(int numVertices) {
        int weightLimit = MAX_WEIGHTS;
        int[] indices = new int[numVertices * weightLimit];
        Map<Integer, List<BoneWeight>> combinedBoneWeights = getBoneIndexToWeights();
        for (int i = 0; i < numVertices; i++) {
            List<BoneWeight> boneWeights = combinedBoneWeights.getOrDefault(i, Collections.emptyList());
            for (int j = 0; j < weightLimit; ++j) {
                indices[i * weightLimit + j] = boneWeights.size() > j ? boneWeights.get(j).getBoneIndex() : 0;
            }
        }
        return indices;
    }

    private Map<Integer, List<BoneWeight>> getBoneIndexToWeights() {
        Map<Integer, List<BoneWeight>> boneIndexToWeights = new HashMap<>();
        for (Bone bone : getBones()) {
            final int boneIndex = getBoneNameToIndex().get(bone.getName());
            bone.getWeights().forEach((vertexIndex, boneVertexWeights) -> {
                List<BoneWeight> weights = boneVertexWeights.stream()
                        .map(e -> new BoneWeight(boneIndex, e))
                        .collect(Collectors.toList());
                boneIndexToWeights.computeIfAbsent(vertexIndex, e -> new ArrayList<>()).addAll(weights);
            });
        }
        return boneIndexToWeights;
    }

    @RequiredArgsConstructor
    @Getter
    private static class BoneWeight {

        private final int boneIndex;
        private final float boneWeight;

    }

    @RequiredArgsConstructor
    @Getter
    public static class Bone {

        private final String name;
        private final Matrix4f offsetMatrix;
        private final Map<Integer, List<Float>> weights;

    }


}
