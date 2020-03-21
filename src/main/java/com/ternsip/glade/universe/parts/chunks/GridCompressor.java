package com.ternsip.glade.universe.parts.chunks;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.ternsip.glade.universe.parts.chunks.BlocksRepository.*;

public class GridCompressor {

    private static final int BUFFER_SIZE = 64;
    private static final int MAX_Y_BUFFERS = 128; // TODO this might be dynamically different for client and server

    private final HashMap<Hash, Node> hashToNode = new HashMap<>();
    private final int[][][] buffer = new int[BUFFER_SIZE][BUFFER_SIZE][BUFFER_SIZE];
    private final int[] yBuffer = new int[SIZE_Y];
    private final LinkedHashMap<Vector2i, Integer[]> yBuffers = new LinkedHashMap<>();
    private final Hash[][] roots = new Hash[SIZE_X][SIZE_Z];

    public GridCompressor() {

    }

    public int read(int x, int y, int z) {
        Vector2i stripPos = new Vector2i(x, z);
        Integer[] yBuffer = yBuffers.remove(stripPos);
        if (yBuffer == null) {
            yBuffer = Arrays.stream(loadStrip(x, z)).boxed().toArray(Integer[]::new);
        }
        yBuffers.put(stripPos, yBuffer);
        return yBuffer[y];
    }

    public int[] loadStrip(int x, int z) {
        int[] strip = new int[SIZE_Y];
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
            nodes[i].left.fill(strip, i * 2 * Hash.LENGTH);
            nodes[i].right.fill(strip, (i * 2 + 1) * Hash.LENGTH);
        }
        return strip;
    }

    public void saveStrip(int[] strip, int x, int z) {
        int leafs = SIZE_Y / (Hash.LENGTH * 2);
        Node[] nodes = new Node[leafs];
        for (int i = 0; i < leafs; ++i) {
            Hash left = new Hash(strip, i * 2 * Hash.LENGTH);
            Hash right = new Hash(strip, (i * 2 + 1) * Hash.LENGTH);
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
        // todo cache yBuffers
    }

    @SneakyThrows
    private static String nodeToHash(String node) {
        return new String(DigestUtils.md5(node), StandardCharsets.ISO_8859_1);
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

