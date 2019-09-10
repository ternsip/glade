package com.ternsip.glade.common.logic;

import org.joml.LineSegmentf;
import org.joml.Vector3fc;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

// TODO Ask JOML to make LineSegmentf externalizable like vector3f https://github.com/JOML-CI/JOML/issues/192
public class Segment extends LineSegmentf implements Externalizable {

    public Segment() {
    }

    public Segment(Vector3fc a, Vector3fc b) {
        super(a, b);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(aX);
        out.writeFloat(aY);
        out.writeFloat(aZ);
        out.writeFloat(bX);
        out.writeFloat(bY);
        out.writeFloat(bZ);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        aX = in.readFloat();
        aY = in.readFloat();
        aZ = in.readFloat();
        bX = in.readFloat();
        bY= in.readFloat();
        bZ = in.readFloat();
    }
}
