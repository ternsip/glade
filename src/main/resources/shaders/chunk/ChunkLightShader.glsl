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

int floorDiv(int x, int y) {
    int r = x / y;
    if ((x ^ y) < 0 && r * y != x) {
        --r;
    }
    return r;
}

int floorMod(int x, int y) {
    return x - floorDiv(x, y) * y;
}

//uniform int startX;
//uniform int startY;
//uniform int startZ;
//uniform int sizeX;
//uniform int sizeY;
//uniform int sizeZ;

void main(void) {
    //int offset = startX + startY * SIZE_X * SIZE_Z + startZ * SIZE_X;
    //int calcSize = sizeX * sizeY * sizeZ;
    int gid = int(dot(vec3(1, gl_NumWorkGroups.x, gl_NumWorkGroups.y * gl_NumWorkGroups.x), gl_GlobalInvocationID)) % VOLUME;

    int x = gid % SIZE_X;
    int y = gid / (SIZE_X * SIZE_Z); // better start from top for height maps
    int z = (gid / SIZE_X) % SIZE_Z;
    int currentOpacity = opacity[gid];
    int boundOpacity = max(1, opacity[gid]);
    int heightIndex = x + z * SIZE_X;
    height[heightIndex] = max(height[heightIndex], currentOpacity > 0 ? y + 1 : 0);
    int bestSkyLight = y >= height[heightIndex] ? MAX_LIGHT_LEVEL : 0;
    int bestEmitLight = selfEmit[gid];

    for (int k = 0; k < 6; ++k) {
        int nx = floorMod(x + dx[k], SIZE_X);
        int ny = floorMod(y + dy[k], SIZE_Y);
        int nz = floorMod(z + dz[k], SIZE_Z);
        int index = nx + ny * SIZE_X * SIZE_Z + nz * SIZE_X;
        bestSkyLight = max(bestSkyLight, sky[index] - boundOpacity);
        bestEmitLight = max(bestEmitLight, emit[index] - boundOpacity);
    }

    sky[gid] = bestSkyLight;
    emit[gid] = bestEmitLight;

}
