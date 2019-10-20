package com.ternsip.glade.graphics.general;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

import java.io.File;

import static org.lwjgl.assimp.Assimp.*;

@SuppressWarnings("UnusedAssignment")
@Builder
@Getter
public class Settings {

    private File meshFile;
    private File[] animationFiles;
    private File texturesDir;
    private Material[] manualMeshMaterials;

    @Builder.Default
    private Vector3f baseRotation = new Vector3f(0);

    @Builder.Default
    private Vector3f baseScale = new Vector3f(1);

    @Builder.Default
    private Vector3f baseOffset = new Vector3f(0);

    @Builder.Default
    private int assimpFlags =
            aiProcess_GenSmoothNormals |
                    aiProcess_JoinIdenticalVertices |
                    aiProcess_Triangulate |
                    aiProcess_FixInfacingNormals |
                    aiProcess_LimitBoneWeights;

    public boolean isManualMeshMaterialsExists(int meshIndex) {
        return getManualMeshMaterials() != null && getManualMeshMaterials().length > meshIndex;
    }

    public File[] produceAnimationFiles() {
        return getAnimationFiles() == null || getAnimationFiles().length == 0 ? new File[]{getMeshFile()} : getAnimationFiles();
    }

    public File produceMeshFile() {
        return getMeshFile() == null ? getAnimationFiles()[0] : getMeshFile();
    }

    public File produceTexturesDir() {
        if (getTexturesDir() == null) {
            return new File(produceMeshFile().getParent());
        }
        return getTexturesDir();
    }

}