package com.ternsip.glade.graphics.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ternsip.glade.common.logic.Utils.assertThat;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.MAX_BONES;
import static com.ternsip.glade.graphics.shader.impl.AnimationShader.MAX_WEIGHTS;

@Getter
class Skeleton {

    private final Bone[][] meshBones;
    private final ArrayList<Bone> allBones;
    private final Map<String, Integer> skeletonBoneNameToIndex;

    Skeleton(Bone[][] meshBones) {
        this.meshBones = meshBones;
        Map<String, Bone> boneNameToBone = new HashMap<>();
        for (Bone[] bones : meshBones) {
            for (Bone bone : bones) {
                boneNameToBone.put(bone.getBoneName(), bone);
            }
        }
        this.allBones = new ArrayList<>(boneNameToBone.values());
        this.skeletonBoneNameToIndex = IntStream.range(0, allBones.size()).boxed()
                .collect(Collectors.toMap(i -> allBones.get(i).getBoneName(), i -> i, (o, n) -> o));
        assertThat(MAX_BONES > skeletonBoneNameToIndex.size());
    }

    float[] getBonesWeights(int meshIndex, int numVertices) {
        int weightLimit = MAX_WEIGHTS;
        float[] weights = new float[numVertices * weightLimit];
        Map<Integer, List<BoneWeight>> combinedBoneWeights = combineBoneWeights(meshIndex);
        for (int i = 0; i < numVertices; i++) {
            List<BoneWeight> boneWeights = combinedBoneWeights.getOrDefault(i, Collections.emptyList());
            for (int j = 0; j < weightLimit; ++j) {
                weights[i * weightLimit + j] = boneWeights.size() > j ? boneWeights.get(j).getBoneWeight() : 0;
            }
        }
        return weights;
    }

    int[] getBoneIndices(int meshIndex, int numVertices) {
        int weightLimit = MAX_WEIGHTS;
        int[] indices = new int[numVertices * weightLimit];
        Map<Integer, List<BoneWeight>> combinedBoneWeights = combineBoneWeights(meshIndex);
        for (int i = 0; i < numVertices; i++) {
            List<BoneWeight> boneWeights = combinedBoneWeights.getOrDefault(i, Collections.emptyList());
            for (int j = 0; j < weightLimit; ++j) {
                indices[i * weightLimit + j] = boneWeights.size() > j ? boneWeights.get(j).getBoneIndex() : 0;
            }
        }
        return indices;
    }

    private Map<Integer, List<BoneWeight>> combineBoneWeights(int meshIndex) {
        Map<Integer, List<BoneWeight>> combination = new HashMap<>();
        for (int i = 0; i < meshBones[meshIndex].length; ++i) {
            final int boneIndex = getSkeletonBoneNameToIndex().get(meshBones[meshIndex][i].getBoneName());
            for (Map.Entry<Integer, List<Float>> entry : meshBones[meshIndex][i].getWeights().entrySet()) {
                int vertexIndex = entry.getKey();
                List<Float> boneVertexWeights = entry.getValue();
                List<BoneWeight> weights = boneVertexWeights.stream()
                        .map(e -> new BoneWeight(boneIndex, e))
                        .collect(Collectors.toList());
                combination.computeIfAbsent(vertexIndex, e -> new ArrayList<>()).addAll(weights);
            }
        }
        return combination;
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

        private final String boneName;
        private final Matrix4f offsetMatrix;
        private final Map<Integer, List<Float>> weights;

    }


}
