package com.ternsip.glade.model.loader.animation.model;

import com.ternsip.glade.model.loader.animation.animation.Animation;
import com.ternsip.glade.model.loader.animation.animation.Animator;
import com.ternsip.glade.model.loader.engine.globjects.Vao;
import com.ternsip.glade.model.loader.engine.textures.Texture;
import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class AnimatedModel {

	// skin
	private final Vao model;
	private final Texture texture;

	// skeleton
	private final Joint rootJoint;
	private final int jointCount;

	private final Animator animator;

	public AnimatedModel(Vao model, Texture texture, Joint rootJoint, int jointCount) {
		this.model = model;
		this.texture = texture;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
	}

	public void delete() {
		model.delete();
		texture.delete();
	}

	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}

	public void update() {
		animator.update();
	}

	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

}
