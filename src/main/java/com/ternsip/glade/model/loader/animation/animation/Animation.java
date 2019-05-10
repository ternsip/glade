package com.ternsip.glade.model.loader.animation.animation;

import com.ternsip.glade.model.loader.animation.model.AnimatedModel;


public class Animation {

	private final float length;//in seconds
	private final KeyFrame[] keyFrames;

	public Animation(float lengthInSeconds, KeyFrame[] frames) {
		this.keyFrames = frames;
		this.length = lengthInSeconds;
	}

	public float getLength() {
		return length;
	}

	public KeyFrame[] getKeyFrames() {
		return keyFrames;
	}

}
