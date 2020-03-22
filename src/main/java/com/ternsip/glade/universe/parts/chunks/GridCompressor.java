package com.ternsip.glade.universe.parts.chunks;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static com.ternsip.glade.universe.parts.chunks.BlocksRepository.*;

public class GridCompressor {

    private final HashMap<Hash, Node> hashToNode = new HashMap<>();
    private final LinkedHashMap<Vector2i, Strip> stripBuffers = new LinkedHashMap<>();
    private final Hash[][] roots = new Hash[SIZE_X][SIZE_Z];
    private final int stripBuffersLimit = 128; // TODO this might be dynamically different for client and server

    public GridCompressor() {
        Strip strip = new Strip();
        for (int x = 0; x < SIZE_X; ++x) {
            for (int z = 0; z < SIZE_Z; ++z) {
                saveStrip(strip, x, z);
            }
        }
    }

    public int read(int x, int y, int z) {
        return getStrip(x, z).values[y];
    }

    public void save(int x, int y, int z, int value) {
        getStrip(x, z).values[y] = value;
    }

    public void read(int[][][] data, int offsetX, int offsetY, int offsetZ) {
        int sizeX = data.length;
        int sizeY = data[0].length;
        int sizeZ = data[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int z = 0; z < sizeZ; ++z) {
                int[] stripValuesXZ = getStrip(x + offsetX, z + offsetZ).values;
                for (int y = 0; y < sizeY; ++y) {
                    data[x][y][z] = stripValuesXZ[y + offsetY];
                }
            }
        }
    }

    public void save(int[][][] data, int offsetX, int offsetY, int offsetZ) {
        int sizeX = data.length;
        int sizeY = data[0].length;
        int sizeZ = data[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int z = 0; z < sizeZ; ++z) {
                int[] stripValuesXZ = getStrip(x + offsetX, z + offsetZ).values;
                for (int y = 0; y < sizeY; ++y) {
                    stripValuesXZ[y + offsetY] = data[x][y][z];
                }
            }
        }
    }

    public void saveBufferedStrips() {
        stripBuffers.forEach((pos, strip) -> saveStrip(strip, pos.x(), pos.y()));
    }

    public void unloadExcessiveBufferedStrips() {
        int toRemove = Math.max(0, stripBuffers.size() - stripBuffersLimit);
        stripBuffers.keySet().removeAll(stripBuffers.keySet().stream().limit(toRemove).collect(Collectors.toSet()));
    }

    private Strip getStrip(int x, int z) {
        Vector2i stripPos = new Vector2i(x, z);
        Strip strip = stripBuffers.remove(stripPos);
        if (strip == null) {
            strip = loadStrip(x, z);
        }
        stripBuffers.put(stripPos, strip);
        return strip;
    }

    private Strip loadStrip(int x, int z) {
        Strip strip = new Strip();
        int leafs = SIZE_Y / (Hash.LENGTH * 2);
        Node[] nodes = new Node[leafs];
        nodes[0] = hashToNode.get(roots[x][z]);
        for (int leaf = 1; leaf < leafs; leaf *= 2) {
            int step = leafs / leaf;
            int smallStep = step / 2;
            for (int i = 0, d = 0; i < leaf; ++i, d += step) {
                Node node = nodes[d];
                nodes[d] = hashToNode.get(node.getLeft());
                nodes[d + smallStep] = hashToNode.get(node.getRight());
            }
        }
        for (int i = 0; i < leafs; ++i) {
            nodes[i].left.fill(strip.values, i * 2 * Hash.LENGTH);
            nodes[i].right.fill(strip.values, (i * 2 + 1) * Hash.LENGTH);
        }
        return strip;
    }

    private void saveStrip(Strip strip, int x, int z) {
        int leafs = SIZE_Y / (Hash.LENGTH * 2);
        Node[] nodes = new Node[leafs];
        for (int i = 0; i < leafs; ++i) {
            Hash left = new Hash(strip.values, i * 2 * Hash.LENGTH);
            Hash right = new Hash(strip.values, (i * 2 + 1) * Hash.LENGTH);
            nodes[i] = new Node(left, right);
        }
        for (int leaf = leafs; leaf > 1; leaf /= 2) {
            for (int i = 0, d = 0; i + 1 < leaf; ++d, i += 2) {
                Node leftNode = nodes[i];
                Node rightNode = nodes[i + 1];
                Hash leftCombo = leftNode.combine();
                Hash rightCombo = rightNode.combine();
                nodes[d] = new Node(leftCombo, rightCombo);
                hashToNode.put(leftCombo, leftNode);
                hashToNode.put(rightCombo, rightNode);
            }
        }
        roots[x][z] = nodes[0].combine();
        hashToNode.put(roots[x][z], nodes[0]);
    }

    @RequiredArgsConstructor
    @Getter
    private static class Strip {

        public final int[] values = new int[SIZE_Y];

    }

    @Data
    public static class Hash {

        public static final int LENGTH = 8;

        private final int[] values = new int[LENGTH];

        public Hash(byte[] bytes) {
            for (int i = 0, j = 0; i < LENGTH; ++i, j += 4) {
                values[i] |= ((0xFF & bytes[j]) << 24) | ((0xFF & bytes[j + 1]) << 16) | ((0xFF & bytes[j + 2]) << 8) | (0xFF & bytes[j + 3]);
            }
        }

        public Hash(int[] src, int offset) {
            for (int i = 0; i < LENGTH; ++i) {
                values[i] = src[offset + i];
            }
        }

        public void fill(int[] dst, int offset) {
            for (int i = 0; i < LENGTH; ++i) {
                dst[offset + i] = values[i];
            }
        }

    }

    @Data
    public static class Node {

        private final Hash left;
        private final Hash right;

        public Hash combine() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Hash.LENGTH * 4 * 2);
            for (int i = 0; i < Hash.LENGTH; ++i) {
                byteBuffer.putInt(left.getValues()[i]);
                byteBuffer.putInt(right.getValues()[i]);
            }
            return new Hash(DigestUtils.sha256(byteBuffer.array()));
        }

    }

}

