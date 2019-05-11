package com.ternsip.glade.universal;

import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.loader.animation.animation.AnimationI;
import com.ternsip.glade.model.loader.animation.animation.Animator;
import com.ternsip.glade.model.loader.animation.model.Joint;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Getter
@Setter
public class AnimGameItem extends GameItem {

    private Animator animator;

    public AnimGameItem(GLModel[] meshes, List<String> jointNames, Joint rootJoint, Map<String, AnimationI> animations) {
        super(meshes);
        this.animator = new Animator(rootJoint, jointNames.size());
        Optional<Map.Entry<String, AnimationI>> entry = animations.entrySet().stream().findFirst();
        animator.doAnimation(entry.isPresent() ? entry.get().getValue() : null);
    }

}