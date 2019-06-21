package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class Indexer {

    private final Vector3ic size;

    public int getIndex(int x, int y, int z) {
        return x + y * getSize().x() * getSize().z() + z * getSize().x();
    }

    public int getX(int index) {
        return index % getSize().x();
    }

    public int getY(int index) {
        return index / (getSize().x() * getSize().z());
    }

    public int getZ(int index) {
        return (index / getSize().x()) % getSize().z();
    }

    public int getVolume() {
        return getSize().x() * getSize().y() * getSize().z();
    }

    public boolean isInside(int x, int y, int z) {
        return x >= 0 && x < getSize().x() && y >= 0 && y < getSize().y() && z >= 0 && z < getSize().z();
    }

}