package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Skeleton {

    private final Bone[][] meshBones;
    private final List<Bone> allBones;

    public Skeleton(Bone[][] meshBones) {
        this.meshBones = meshBones;
        Map<String, Bone> boneNameToBone = new HashMap<>();
        for (Bone[] bones : meshBones) {
            for (Bone bone : bones) {
                boneNameToBone.put(bone.getBoneName(), bone);
            }
        }
        this.allBones = new ArrayList<>(boneNameToBone.values());
    }

    public float[] getBonesWeights(int meshIndex, int numVertices, int weightLimit) {
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

    public int[] getBoneNameToBone(int meshIndex, int numVertices, int weightLimit) {
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
            final int boneIndex = getBoneIndexByName(meshBones[meshIndex][i].getBoneName());
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

    private int getBoneIndexByName(String boneName) {
        Bone boneFound = getAllBones().stream().filter(bone -> bone.getBoneName().equals(boneName)).findFirst().orElseThrow(() -> new IllegalArgumentException(""));
        return getAllBones().indexOf(boneFound);
    }

    @RequiredArgsConstructor
    @Getter
    private static class BoneWeight {

        private final int boneIndex;
        private final float boneWeight;

    }


}
