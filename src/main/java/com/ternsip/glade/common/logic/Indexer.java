package com.ternsip.glade.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class Indexer {

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    public Indexer(Vector3ic size) {
        this.sizeX = size.x();
        this.sizeY = size.y();
        this.sizeZ = size.z();
    }

    public long getIndex(Vector3ic pos) {
        return getIndex(pos.x(), pos.y(), pos.z());
    }

    public long getIndex(int x, int y, int z) {
        return y + x * sizeY * sizeZ + z * sizeY;
    }

    public long getIndexLooping(int x, int y, int z) {
        int nx = Maths.positiveLoop(x, sizeX);
        int ny = Maths.positiveLoop(y, sizeY);
        int nz = Maths.positiveLoop(z, sizeZ);
        return getIndex(nx, ny, nz);
    }

    public int getX(long index) {
        return (int) (index / (sizeY * sizeZ));
    }

    public int getY(long index) {
        return (int) (index % sizeY);
    }

    public int getZ(long index) {
        return (int) ((index / sizeY) % sizeZ);
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

    public Vector3ic getSize() {
        return new Vector3i(sizeX, sizeY, sizeZ);
    }
}