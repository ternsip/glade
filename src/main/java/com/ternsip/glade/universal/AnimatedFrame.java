package com.ternsip.glade.universal;

import org.joml.Matrix4f;

import java.util.Arrays;

public class AnimatedFrame {

    public static final int MAX_JOINTS = 180;
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    private final Matrix4f[] jointMatrices;

    public AnimatedFrame() {
        jointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(jointMatrices, IDENTITY_MATRIX);
    }

    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

    public void setMatrix(int pos, Matrix4f jointMatrix) {
        jointMatrices[pos] = jointMatrix;
    }
}

