package com.ternsip.glade.model.loader.animation.model;

import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.loader.animation.animation.AnimationI;
import com.ternsip.glade.model.loader.animation.animation.Animator;
import lombok.Getter;

@Getter
public class AnimatedModel {

    // skin
    private final GLModel model;

    private final Animator animator;

    public AnimatedModel(GLModel model, Joint rootJoint, int jointCount, AnimationI animation) {
        this.model = model;
        this.animator = new Animator(rootJoint, jointCount);
        animator.doAnimation(animation);
    }

    public void delete() {
        model.cleanUp();
    }



}
