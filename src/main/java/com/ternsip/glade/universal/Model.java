package com.ternsip.glade.universal;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Model {

    private final Mesh[] meshes;
    private final Animation animation;

    public Model(Mesh[] meshes) {
        this.meshes = meshes;
        this.animation = new Animation();
    }

    public Model(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public Model(Mesh[] meshes, Bone rootBone, Map<String, AnimationFrames> animations) {
        this.meshes = meshes;
        this.animation = new Animation(rootBone, animations);
    }

    public void finish() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].finish();
        }
    }

}
