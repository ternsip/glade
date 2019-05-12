package com.ternsip.glade.universal;

import com.ternsip.glade.model.Mesh;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class Skeleton {

    private final Bone[] bones;

    public float[] getBonesWeights(int numVertices, int weightLimit) {
        float[] weights = new float[numVertices * weightLimit];
        Map<Integer, List<BoneWeight>> combinedBoneWeights = combineBoneWeights();
        for (int i = 0; i < numVertices; i++) {
            List<BoneWeight> boneWeights = combinedBoneWeights.getOrDefault(i, Collections.emptyList());
            for (int j = 0; j < weightLimit; ++j) {
                weights[i * weightLimit + j] = boneWeights.size() > j ? boneWeights.get(j).getBoneWeight() : 0;
            }
        }
        return weights;
    }

    public int[] getBonesIndices(int numVertices, int weightLimit) {
        int[] indices = new int[numVertices * weightLimit];
        Map<Integer, List<BoneWeight>> combinedBoneWeights = combineBoneWeights();
        for (int i = 0; i < numVertices; i++) {
            List<BoneWeight> boneWeights = combinedBoneWeights.getOrDefault(i, Collections.emptyList());
            for (int j = 0; j < weightLimit; ++j) {
                indices[i * weightLimit + j] = boneWeights.size() > j ? boneWeights.get(j).getBoneIndex() : 0;
            }
        }
        return indices;
    }

    private Map<Integer, List<BoneWeight>> combineBoneWeights() {
        Map<Integer, List<BoneWeight>> combination = new HashMap<>();
        for (int i = 0; i < bones.length; ++i) {
            final int boneIndex = i;
            for (Map.Entry<Integer, List<Float>> entry : bones[i].getWeights().entrySet()) {
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


}
