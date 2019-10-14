#version 430 core

const int SIZE_X = 256;
const int SIZE_Y = 256;
const int SIZE_Z = 256;
const int PADDING = 1;
const int SIZE_PX = SIZE_X - PADDING * 2;
const int SIZE_PY = SIZE_Y - PADDING * 2;
const int SIZE_PZ = SIZE_Z - PADDING * 2;
const int CALC_SIZE = SIZE_PX * SIZE_PY * SIZE_PZ;
const int PADDING_INDEX = PADDING + PADDING * SIZE_X * SIZE_Z  + PADDING * SIZE_X;

const int MAX_LIGHT_LEVEL = 15;

layout (std430, binding = 0) buffer skyBuffer {
    int sky[];
};

layout (std430, binding = 1) buffer emitBuffer {
    int emit[];
};

layout (std430, binding = 2) buffer selfEmitBuffer {
    int selfEmit[];
};

layout (std430, binding = 3) buffer opacityBuffer {
    int opacity[];
};

layout (std430, binding = 4) buffer heightBuffer {
    int height[];
};

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

//uniform int startX;
//uniform int startY;
//uniform int startZ;
//uniform int sizeX;
//uniform int sizeY;
//uniform int sizeZ;

void main(void) {
    //int offset = startX + startY * SIZE_X * SIZE_Z + startZ * SIZE_X;
    //int calcSize = sizeX * sizeY * sizeZ;
    int gid = PADDING_INDEX + int(gl_GlobalInvocationID) % CALC_SIZE;

    int x = gid % SIZE_X;
    int y = gid / (SIZE_X * SIZE_Z); // better start from top for height maps
    int z = (gid / SIZE_X) % SIZE_Z;
    int currentOpacity = opacity[gid];
    int boundOpacity = max(1, opacity[gid]);
    int heightIndex = x + z * SIZE_X;
    height[heightIndex] = max(height[heightIndex], currentOpacity > 0 ? y + 1 : 0);
    int bestSkyLight = y >= height[heightIndex] ? MAX_LIGHT_LEVEL : 0;
    int bestEmitLight = selfEmit[gid];

    bestSkyLight = max(bestSkyLight, sky[gid + 1] - boundOpacity);
    bestSkyLight = max(bestSkyLight, sky[gid + SIZE_X] - boundOpacity);
    bestSkyLight = max(bestSkyLight, sky[gid + SIZE_X * SIZE_Z] - boundOpacity);
    bestSkyLight = max(bestSkyLight, sky[gid - 1] - boundOpacity);
    bestSkyLight = max(bestSkyLight, sky[gid - SIZE_X] - boundOpacity);
    bestSkyLight = max(bestSkyLight, sky[gid - SIZE_X * SIZE_Z] - boundOpacity);

    bestEmitLight = max(bestEmitLight, emit[gid + 1] - boundOpacity);
    bestEmitLight = max(bestEmitLight, emit[gid + SIZE_X] - boundOpacity);
    bestEmitLight = max(bestEmitLight, emit[gid + SIZE_X * SIZE_Z] - boundOpacity);
    bestEmitLight = max(bestEmitLight, emit[gid - 1] - boundOpacity);
    bestEmitLight = max(bestEmitLight, emit[gid - SIZE_X] - boundOpacity);
    bestEmitLight = max(bestEmitLight, emit[gid - SIZE_X * SIZE_Z] - boundOpacity);

    sky[gid] = bestSkyLight;
    emit[gid] = bestEmitLight;

}
