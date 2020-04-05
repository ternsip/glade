package com.ternsip.glade.common.logic;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Indexer2D {

    private final int sizeA;
    private final int sizeB;

    public long getIndex(int a, int b) {
        return a + b * sizeA;
    }

    public long getIndexLooping(int a, int b) {
        int na = Maths.positiveLoop(a, sizeA);
        int nb = Maths.positiveLoop(b, sizeB);
        return getIndex(na, nb);
    }

    public int getA(long index) {
        return (int) (index % sizeA);
    }

    public int getB(long index) {
        return (int) (index / sizeA);
    }

    public long getVolume() {
        return (long) sizeA * sizeB;
    }

    public boolean isInside(int a, int b) {
        return a >= 0 && a < sizeA && b >= 0 && b < sizeB;
    }

    public boolean isOnBorder(int a, int b) {
        return a == 0 || a == sizeA - 1 || b == 0 || b == sizeB - 1;
    }

}