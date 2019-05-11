package com.ternsip.glade.model.loader.animation.model;

import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.loader.animation.animation.AnimationI;
import com.ternsip.glade.model.loader.animation.animation.Animator;
import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class AnimatedModel {

    // skin
    private final GLModel model;

    // skeleton
    private final Joint rootJoint;
    private final int jointCount;

    private final Animator animator;

    public AnimatedModel(GLModel model, Joint rootJoint, int jointCount, AnimationI animation) {
        this.model = model;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        animator.doAnimation(animation);
    }

    public void delete() {
        model.cleanUp();
    }

    public void update() {
        animator.update();
    }

    public Matrix4f[] getJointTransforms() {
        Matrix4f[] jointMatrices = new Matrix4f[jointCount];
        addJointsToArray(rootJoint, jointMatrices);

        // TODO this is just dummy to prevent crashing
        for (int i = 0; i < jointMatrices.length; ++i) {
            if (jointMatrices[i] == null) {
                jointMatrices[i] = new Matrix4f();
            }
        }

        return jointMatrices;
    }

    private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
        // TODO this if is just dummy to prevent crashing
        if (headJoint.index >= 0 && headJoint.index < jointMatrices.length) {
            jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
        }
        for (Joint childJoint : headJoint.children) {
            addJointsToArray(childJoint, jointMatrices);
        }
    }

}
