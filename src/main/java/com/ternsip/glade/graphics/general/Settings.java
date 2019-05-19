package com.ternsip.glade.graphics.general;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

import java.io.File;

import static org.lwjgl.assimp.Assimp.*;

@Builder
@Getter
public class Settings {

    private File meshFile;
    private File animationFile;
    private File texturesDir;
    private Material[] manualMeshMaterials;

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private Vector3f baseRotation = new Vector3f(0);

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private Vector3f baseScale = new Vector3f(1);

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private Vector3f baseOffset = new Vector3f(0);

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private boolean preserveInvalidBoneLocalTransform = false;

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private int assimpFlags =
            aiProcess_GenSmoothNormals |
                    aiProcess_JoinIdenticalVertices |
                    aiProcess_Triangulate |
                    aiProcess_FixInfacingNormals |
                    aiProcess_LimitBoneWeights;

    boolean isAnimationAndMeshInOneFile() {
        return getAnimationFile() == null || getAnimationFile().equals(getMeshFile());
    }

    public File getMeshFile() {
        return meshFile == null ? animationFile : meshFile;
    }

    public File getAnimationFile() {
        return animationFile == null ? meshFile : animationFile;
    }

    File getTexturesDir() {
        if (texturesDir == null) {
            return new File(meshFile.getParent());
        }
        return texturesDir;
    }

    public boolean isManualMeshMaterialsExists(int meshIndex) {
        return getManualMeshMaterials() != null && getManualMeshMaterials().length > meshIndex;
    }

}