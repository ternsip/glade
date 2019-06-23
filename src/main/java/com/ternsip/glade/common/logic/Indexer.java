package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class Indexer {

    private final Vector3ic size;

    public long getIndex(Vector3ic pos) {
        return getIndex(pos.x(), pos.y(), pos.z());
    }

    public long getIndex(int x, int y, int z) {
        return x + y * getSize().x() * getSize().z() + z * getSize().x();
    }

    public int getX(long index) {
        return (int) (index % getSize().x());
    }

    public int getY(long index) {
        return (int) (index / (getSize().x() * getSize().z()));
    }

    public int getZ(long index) {
        return (int) ((index / getSize().x()) % getSize().z());
    }

    public long getVolume() {
        return (long)getSize().x() * getSize().y() * getSize().z();
    }

    public boolean isInside(Vector3ic pos) {
        return isInside(pos.x(), pos.y(), pos.z());
    }

    public boolean isInside(int x, int y, int z) {
        return x >= 0 && x < getSize().x() && y >= 0 && y < getSize().y() && z >= 0 && z < getSize().z();
    }

}