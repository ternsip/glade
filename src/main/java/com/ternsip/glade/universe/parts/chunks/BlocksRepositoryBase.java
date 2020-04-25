package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;
import java.util.function.BiFunction;

import static com.ternsip.glade.common.logic.Maths.frac;

@Getter
@Setter
public class BlocksRepositoryBase {

    public static final int SIZE_X = 128;
    public static final int SIZE_Y = 64;
    public static final int SIZE_Z = 128;
    public static final Vector3ic SIZE = new Vector3i(SIZE_X, SIZE_Y, SIZE_Z);
    public static final Indexer INDEXER = new Indexer(SIZE);
    public static final int[][][] arr = new int[SIZE_X][SIZE_Z][SIZE_Z];

    private final GridCompressor gridBlocks = new GridCompressor();

    public synchronized void setBlock(Vector3ic pos, Block block) {
        gridBlocks.write(pos.x(), pos.y(), pos.z(), block.getIndex());
    }

    public synchronized void setBlock(int x, int y, int z, Block block) {
        gridBlocks.write(x, y, z, block.getIndex());
    }

    public synchronized void setBlocks(Vector3ic start, Block[][][] region) {
        int sizeX = region.length;
        int sizeY = region[0].length;
        int sizeZ = region[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    gridBlocks.write(x + start.x(), y + start.y(), z + start.z(), region[x][y][z].getIndex());
                }
            }
        }
    }

    public synchronized Block[][][] getBlocks(Vector3ic start, Vector3ic end) {
        if (!INDEXER.isInside(start) || !INDEXER.isInside(end)) {
            throw new IllegalArgumentException("You tried to get blocks out of limits.");
        }
        Vector3ic fixStart = new Vector3i(start).min(end);
        Vector3ic fixEnd = new Vector3i(start).max(end);
        Vector3ic size = new Vector3i(fixEnd).sub(fixStart).add(1, 1, 1);
        Block[][][] blocks = new Block[size.x()][size.y()][size.z()];
        for (int x = 0; x < size.x(); ++x) {
            for (int y = 0; y < size.y(); ++y) {
                for (int z = 0; z < size.z(); ++z) {
                    blocks[x][y][z] = Block.getBlockByIndex(gridBlocks.read(x + start.x(), y + start.y(), z + start.z()));
                }
            }
        }
        return blocks;
    }

    public synchronized Block getBlock(Vector3ic pos) {
        return Block.getBlockByIndex(gridBlocks.read(pos.x(), pos.y(), pos.z()));
    }

    public synchronized Block getBlock(int x, int y, int z) {
        return Block.getBlockByIndex(gridBlocks.read(x, y, z));
    }

    public synchronized Block getBlockUniversal(int x, int y, int z) {
        return INDEXER.isInside(x, y, z) ? getBlock(x, y, z) : Block.AIR;
    }

    public synchronized boolean isBlockExists(Vector3ic pos) {
        return INDEXER.isInside(pos);
    }

    public synchronized boolean isBlockExists(int x, int y, int z) {
        return INDEXER.isInside(x, y, z);
    }

    // Using A Fast Voxel Traversal Algorithm for Ray Tracing by John Amanatides and Andrew Woo
    @Nullable
    public synchronized Vector3ic traverse(LineSegmentf segment, BiFunction<Block, Vector3i, Boolean> condition) {
        int cx = (int) Math.floor(segment.aX);
        int cy = (int) Math.floor(segment.aY);
        int cz = (int) Math.floor(segment.aZ);
        Vector3fc ray = new Vector3f(segment.bX - segment.aX, segment.bY - segment.aY, segment.bZ - segment.aZ);
        int dx = (int) Math.signum(ray.x());
        int dy = (int) Math.signum(ray.y());
        int dz = (int) Math.signum(ray.z());
        float tDeltaX = (dx == 0) ? Float.MAX_VALUE : dx / ray.x();
        float tMaxX = (dx == 0) ? Float.MAX_VALUE : ((dx > 0) ? tDeltaX * (1 - frac(segment.aX)) : tDeltaX * frac(segment.aX));
        float tDeltaY = (dy == 0) ? Float.MAX_VALUE : dy / ray.y();
        float tMaxY = (dy == 0) ? Float.MAX_VALUE : ((dy > 0) ? tDeltaY * (1 - frac(segment.aY)) : tDeltaY * frac(segment.aY));
        float tDeltaZ = (dz == 0) ? Float.MAX_VALUE : dz / ray.z();
        float tMaxZ = (dz == 0) ? Float.MAX_VALUE : ((dz > 0) ? tDeltaZ * (1 - frac(segment.aZ)) : tDeltaZ * frac(segment.aZ));
        if (checkVoxel(cx, cy, cz, condition)) {
            return new Vector3i(cx, cy, cz);
        }
        while (tMaxX <= 1 || tMaxY <= 1 || tMaxZ <= 1) {
            if (Maths.isFloatsEqual(tMaxX, tMaxZ) && Maths.isFloatsEqual(tMaxX, tMaxY)) {
                for (int ax = 0, nx = cx; ax <= 1; ++ax, nx += dx) {
                    for (int ay = 0, ny = cy; ay <= 1; ++ay, ny += dy) {
                        for (int az = 0, nz = cz; az <= 1; ++az, nz += dz) {
                            if ((ax > 0 || ay > 0 || az > 0) && checkVoxel(nx, ny, nz, condition)) {
                                return new Vector3i(nx, ny, nz);
                            }
                        }
                    }
                }
                cx += dx;
                cy += dy;
                cz += dz;
                tMaxX += tDeltaX;
                tMaxY += tDeltaY;
                tMaxZ += tDeltaZ;
                continue;
            }
            if (Maths.isFloatsEqual(tMaxX, tMaxZ) && tMaxX < tMaxY) {
                for (int ax = 0, nx = cx; ax <= 1; ++ax, nx += dx) {
                    for (int az = 0, nz = cz; az <= 1; ++az, nz += dz) {
                        if ((ax > 0 || az > 0) && checkVoxel(nx, cy, nz, condition)) {
                            return new Vector3i(nx, cy, nz);
                        }
                    }
                }
                cx += dx;
                cz += dz;
                tMaxX += tDeltaX;
                tMaxZ += tDeltaZ;
                continue;
            }
            if (Maths.isFloatsEqual(tMaxX, tMaxY) && tMaxX < tMaxZ) {
                for (int ax = 0, nx = cx; ax <= 1; ++ax, nx += dx) {
                    for (int ay = 0, ny = cy; ay <= 1; ++ay, ny += dy) {
                        if ((ax > 0 || ay > 0) && checkVoxel(nx, ny, cz, condition)) {
                            return new Vector3i(nx, ny, cz);
                        }
                    }
                }
                cx += dx;
                cy += dy;
                tMaxX += tDeltaX;
                tMaxY += tDeltaY;
                continue;
            }
            if (Maths.isFloatsEqual(tMaxY, tMaxZ) && tMaxY < tMaxX) {
                for (int ay = 0, ny = cy; ay <= 1; ++ay, ny += dy) {
                    for (int az = 0, nz = cz; az <= 1; ++az, nz += dz) {
                        if ((ay > 0 || az > 0) && checkVoxel(cx, ny, nz, condition)) {
                            return new Vector3i(cx, ny, nz);
                        }
                    }
                }
                cy += dy;
                cz += dz;
                tMaxY += tDeltaY;
                tMaxZ += tDeltaZ;
                continue;
            }
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    cx += dx;
                    tMaxX += tDeltaX;
                } else {
                    cz += dz;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    cy += dy;
                    tMaxY += tDeltaY;
                } else {
                    cz += dz;
                    tMaxZ += tDeltaZ;
                }
            }
            if (checkVoxel(cx, cy, cz, condition)) {
                return new Vector3i(cx, cy, cz);
            }
        }
        return null;
    }

    private boolean checkVoxel(int x, int y, int z, BiFunction<Block, Vector3i, Boolean> condition) {
        return isBlockExists(x, y, z) && condition.apply(getBlock(x, y, z), new Vector3i(x, y, z));
    }

}
