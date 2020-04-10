#version 430 core

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

uniform int startX; // TODO THIS IS NOT NEEDED, REMOVE
uniform int startY;
uniform int startZ;
uniform int sizeX;
uniform int sizeY;
uniform int sizeZ;

void main(void) {
    int calcSize = sizeX * sizeY * sizeZ;
    int realIndex = int(dot(vec3(1, gl_NumWorkGroups.x, gl_NumWorkGroups.y * gl_NumWorkGroups.x), gl_GlobalInvocationID)) % calcSize;

    int maxX = sizeX - 1;
    int maxY = sizeY - 1;
    int maxZ = sizeZ - 1;
    int x = realIndex / (sizeY * sizeZ);
    int y = realIndex % sizeY;
    int z = (realIndex / sizeY) % sizeZ;
    int currentOpacity = opacity[realIndex];
    int boundOpacity = max(1, opacity[realIndex]);
    bool isSky = true;
    for (int deltaX = -1; deltaX <= 1; ++deltaX) {
        for (int deltaZ = -1; deltaZ <= 1; ++deltaZ) {
            isSky = isSky && ((startY + y) >= height[clamp(x + deltaX, 0, maxX) + clamp(z + deltaZ, 0, maxZ) * sizeX]);
        }
    }
    int bestSkyLight = isSky ? MAX_LIGHT_LEVEL : 0;
    int bestEmitLight = selfEmit[realIndex];

    for (int k = 0; k < 6; ++k) {
        int nx = clamp(x + dx[k], 0, maxX);
        int ny = clamp(y + dy[k], 0, maxY);
        int nz = clamp(z + dz[k], 0, maxZ);
        int index = ny + nx * sizeY * sizeZ + nz * sizeY;
        bestSkyLight = max(bestSkyLight, sky[index] - boundOpacity);
        bestEmitLight = max(bestEmitLight, emit[index] - boundOpacity);
    }

    sky[realIndex] = bestSkyLight;
    emit[realIndex] = bestEmitLight;

}
