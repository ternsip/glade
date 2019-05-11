package com.ternsip.glade.universal;

import com.ternsip.glade.model.Mesh;
import com.ternsip.glade.model.loader.animation.animation.Animation;
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

    public AnimGameItem(Mesh[] meshes, List<String> jointNames, Joint rootJoint, Map<String, Animation> animations) {
        super(meshes);
        this.animator = new Animator(rootJoint, jointNames.size());
        Optional<Map.Entry<String, Animation>> entry = animations.entrySet().stream().findFirst();
        animator.doAnimation(entry.isPresent() ? entry.get().getValue() : null);
    }

}
