#version 430 core

const int SIZE_X = 256;
const int SIZE_Y = 256;
const int SIZE_Z = 256;
const int VOLUME = SIZE_X * SIZE_Y * SIZE_Z;

int[] dx = {-1, 1, 0, 0, 0, 0};
int[] dy = {0, 0, 1, -1, 0, 0};
int[] dz = {0, 0, 0, 0, 1, -1};

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


int positiveLoop(int a, int b) {
    return (b + a % b) % b;
}

uniform int startX;
uniform int startY;
uniform int startZ;
uniform int sizeX;
uniform int sizeY;
uniform int sizeZ;

void main(void) {
    //int offset = startX + startY * SIZE_X * SIZE_Z + startZ * SIZE_X;
    int calcSize = sizeX * sizeY * sizeZ;
    int gid = int(dot(vec3(1, gl_NumWorkGroups.x, gl_NumWorkGroups.y * gl_NumWorkGroups.x), gl_GlobalInvocationID)) % calcSize;

    int x = positiveLoop(startX + gid % sizeX, SIZE_X);
    int y = clamp(startY + gid / (sizeX * sizeZ), 0, SIZE_Y);
    int z = positiveLoop(startZ + (gid / sizeX) % sizeZ, SIZE_Z);
    int realIndex = x + y * SIZE_X * SIZE_Z + z * SIZE_X;
    int currentOpacity = opacity[realIndex];
    int boundOpacity = max(1, opacity[realIndex]);
    int bestSkyLight = y >= height[x + z * SIZE_X] ? MAX_LIGHT_LEVEL : 0;
    int bestEmitLight = selfEmit[realIndex];

    for (int k = 0; k < 6; ++k) {
        int nx = positiveLoop(x + dx[k], SIZE_X);
        int ny = clamp(y + dy[k], 0, SIZE_Y);
        int nz = positiveLoop(z + dz[k], SIZE_Z);
        int index = nx + ny * SIZE_X * SIZE_Z + nz * SIZE_X;
        bestSkyLight = max(bestSkyLight, sky[index] - boundOpacity);
        bestEmitLight = max(bestEmitLight, emit[index] - boundOpacity);
    }

    sky[realIndex] = bestSkyLight;
    emit[realIndex] = bestEmitLight;

}
