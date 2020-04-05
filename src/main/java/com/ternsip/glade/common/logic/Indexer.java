package com.ternsip.glade.common.logic;

import lombok.RequiredArgsConstructor;
import org.joml.Vector3ic;

@RequiredArgsConstructor
public class Indexer {

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    public Indexer(Vector3ic size) {
        this.sizeX = size.x();
        this.sizeY = size.y();
        this.sizeZ = size.z();
    }

    public long getIndex(int x, int y, int z) {
        return x + y * sizeX * sizeZ + z * sizeX;
    }

    public long getIndexLooping(int x, int y, int z) {
        int nx = Maths.positiveLoop(x, sizeX);
        int ny = Maths.positiveLoop(y, sizeY);
        int nz = Maths.positiveLoop(z, sizeZ);
        return getIndex(nx, ny, nz);
    }

    public int getX(long index) {
        return (int) (index % sizeX);
    }

    public int getY(long index) {
        return (int) (index / (sizeX * sizeZ));
    }

    public int getZ(long index) {
        return (int) ((index / sizeX) % sizeZ);
    }

    public long getVolume() {
        return (long) sizeX * sizeY * sizeZ;
    }

    public boolean isInside(Vector3ic pos) {
        return isInside(pos.x(), pos.y(), pos.z());
    }

    public boolean isInside(int x, int y, int z) {
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY && z >= 0 && z < sizeZ;
    }

    public boolean isOnBorder(int x, int y, int z) {
        return x == 0 || x == sizeX - 1 || y == 0 || y == sizeY - 1 || z == 0 || z == sizeZ - 1;
    }

}