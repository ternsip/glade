package com.ternsip.glade.universe.parts.chunks;

import com.aparapi.Kernel;

import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.MAX_LIGHT_LEVEL;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.UPDATE_LIMIT;

public class LightMassKernel extends Kernel {

    private static final int BUFFER_DIM_LIMIT = UPDATE_LIMIT + 2 * MAX_LIGHT_LEVEL;

    private final int[] offsetX = {-1, 1, 0, 0, 0, 0};
    private final int[] offsetY = {0, 0, 1, -1, 0, 0};
    private final int[] offsetZ = {0, 0, 0, 0, 1, -1};

    final int[] lightBuffer = new int[BUFFER_DIM_LIMIT * BUFFER_DIM_LIMIT * BUFFER_DIM_LIMIT];
    final int[] heightBuffer = new int[BUFFER_DIM_LIMIT * BUFFER_DIM_LIMIT];

    int calcSize;
    int startX;
    int startY;
    int startZ;
    int sizeX;
    int sizeY;
    int sizeZ;
    int maxX;
    int maxY;
    int maxZ;
    int bannedStartX;
    int bannedStartY;
    int bannedStartZ;
    int bannedEndX;
    int bannedEndY;
    int bannedEndZ;

    int clamp(int v, int minValue, int maxValue) {
        return Math.min(maxValue, Math.max(v, minValue));
    }

    @Override
    public void run() {
        int realIndex = getGlobalId() % calcSize;
        int x = realIndex / (sizeY * sizeZ);
        int y = realIndex % sizeY;
        int z = (realIndex / sizeY) % sizeZ;
        if (bannedStartX == x || bannedStartY == y || bannedStartZ == z || bannedEndX == x || bannedEndY == y || bannedEndZ == z) {
            return;
        }
        int lightValue = lightBuffer[realIndex];
        int opacity = (lightValue >> 8) & 0xFF;
        int selfEmit = lightValue & 0xFF;
        boolean isSky = true;
        for (int deltaX = -1; deltaX <= 1; ++deltaX) {
            for (int deltaZ = -1; deltaZ <= 1; ++deltaZ) {
                isSky = isSky && ((startY + y) >= heightBuffer[clamp(x + deltaX, 0, maxX) + clamp(z + deltaZ, 0, maxZ) * sizeX]);
            }
        }
        int bestSkyLight = isSky ? MAX_LIGHT_LEVEL : 0;
        int bestEmitLight = selfEmit;
        for (int k = 0; k < 6; ++k) {
            int nx = clamp(x + offsetX[k], 0, maxX);
            int ny = clamp(y + offsetY[k], 0, maxY);
            int nz = clamp(z + offsetZ[k], 0, maxZ);
            int index = ny + nx * sizeY * sizeZ + nz * sizeY;
            int nLightValue = lightBuffer[index];
            bestSkyLight = max(bestSkyLight, ((nLightValue >> 24) & 0xFF) - opacity);
            bestEmitLight = max(bestEmitLight, ((nLightValue >> 16) & 0xFF) - opacity);
        }
        lightBuffer[realIndex] = (bestSkyLight << 24) + (bestEmitLight << 16) + (opacity << 8) + selfEmit;
    }

}