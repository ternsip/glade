package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Maths;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.util.*;

public class GridOctreeCompressor {

    private static final int SIZE = 256;
    private static final int NODES = SIZE / 2;
    private static final int CHUNK_DEPTH = Maths.log2(NODES / Chunk.NODES);

    private static final Hash[] HASHES_BUFFER = new Hash[Chunk.NODES * Chunk.NODES * Chunk.NODES];
    private static final int[] INDEX_PATH_BUFFER = new int[CHUNK_DEPTH];
    private static final Node[] NODE_PATH_BUFFER = new Node[CHUNK_DEPTH];

    private final HashMap<Hash, Node> hashToNode = new HashMap<>();
    private final HashMap<Vector3i, Chunk> posToChunk = new HashMap<>();
    private Hash root;

    public GridOctreeCompressor() {
        Hash hash = new Hash();
        for (int i = 0; i < NODES; ++i) {
            Node node = new Node(hash);
            hash = node.combine();
            hashToNode.put(hash, node);
        }
        root = hash;
    }

    public int read(int x, int y, int z) {
        Chunk chunk = loadChunk(x / Chunk.SIZE, y / Chunk.SIZE, z / Chunk.SIZE);
        return chunk.values[x % Chunk.SIZE][y % Chunk.SIZE][z % Chunk.SIZE];
    }

    public void write(int x, int y, int z, int value) {
        Chunk chunk = loadChunk(x / Chunk.SIZE, y / Chunk.SIZE, z / Chunk.SIZE);
        chunk.values[x % Chunk.SIZE][y % Chunk.SIZE][z % Chunk.SIZE] = value;
    }

    public void read(int[][][] values, int startX, int startY, int startZ) {
        int sizeX = values.length;
        int sizeY = values[0].length;
        int sizeZ = values[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int y =  0; y < sizeY; ++y) {
                for (int z =  0; z < sizeZ; ++z) {
                    values[x][y][z] = read(startX + x, startY + y, startZ + z); // TODO slow due to chunk map, load each chunk and fill with it
                }
            }
        }
    }

    public void write(int[][][] values, int startX, int startY, int startZ) {
        int sizeX = values.length;
        int sizeY = values[0].length;
        int sizeZ = values[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int y =  0; y < sizeY; ++y) {
                for (int z =  0; z < sizeZ; ++z) {
                   write(startX + x, startY + y, startZ + z,  values[x][y][z]); // TODO slow due to chunk map, load each chunk and fill with it
                }
            }
        }
    }

    public void saveChunks() {
        posToChunk.keySet().forEach(this::saveChunk);
        // TODO add unloading obsoleted chunks
    }

    public void cleanTree() {
        Set<Hash> used = new HashSet<>();
        Stack<Hash> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Hash top = stack.pop();
            if (used.contains(top)) continue;
            Node node = hashToNode.get(top);
            if (node == null) continue;
            used.add(top);
            for (int i = 0; i < 8; ++i) {
                stack.push(node.children[i]);
            }
        }
        hashToNode.keySet().retainAll(used);
    }

    private Chunk loadChunk(int cx, int cy, int cz) {
        Vector3i pos = new Vector3i(cx, cy, cz);
        Chunk chunk = posToChunk.get(pos);
        if (chunk == null) {
            chunk = new Chunk();
            Hash pointer = root;
            for (int depth = 0, halfNodes = NODES / 2; depth < CHUNK_DEPTH; ++depth, halfNodes /= 2) {
                Node node = hashToNode.get(pointer);
                int index = 0;
                if (cz > halfNodes) {
                    cz -= halfNodes;
                    index += 1;
                }
                if (cy > halfNodes) {
                    cy -= halfNodes;
                    index += 2;
                }
                if (cx > halfNodes) {
                    cx -= halfNodes;
                    index += 4;
                }
                pointer = node.children[index];
            }
            HASHES_BUFFER[0] = pointer;
            for (int step = HASHES_BUFFER.length; step >= 8; step /= 8) {
                for (int ptr = 0; ptr < HASHES_BUFFER.length; ptr += step) {
                    Node node = hashToNode.get(HASHES_BUFFER[ptr]);
                    int smallStep = step / 8;
                    for (int j = 0, smallPtr = ptr; j < 8; ++j, smallPtr += smallStep) {
                        HASHES_BUFFER[smallPtr] = node.children[j];
                    }
                }
            }
            for (int nx = 0, hashIndex = 0; nx < Chunk.NODES; ++nx) {
                for (int ny = 0; ny < Chunk.NODES; ++ny) {
                    for (int nz = 0; nz < Chunk.NODES; ++nz, ++hashIndex) {
                        for (int dx = 0, deltaIndex = 0; dx < 2; ++dx) {
                            for (int dy = 0; dy < 2; ++dy) {
                                for (int dz = 0; dz < 2; ++dz, ++deltaIndex) {
                                    chunk.values[nx * 2 + dx][ny * 2 + dy][nz * 2 + dz] = HASHES_BUFFER[hashIndex].values[deltaIndex];
                                }
                            }
                        }
                    }
                }
            }
            posToChunk.put(pos, chunk);
        }
        return chunk;
    }

    private void saveChunk(Vector3i pos) {
        Chunk chunk = posToChunk.get(pos);
        int cx = pos.x;
        int cy = pos.y;
        int cz = pos.z;
        if (chunk == null) {
            throw new IllegalArgumentException("Chunk not found");
        }
        for (int nx = 0, hashIndex = 0; nx < Chunk.NODES; ++nx) {
            for (int ny = 0; ny < Chunk.NODES; ++ny) {
                for (int nz = 0; nz < Chunk.NODES; ++nz, ++hashIndex) {
                    HASHES_BUFFER[hashIndex] = new Hash();
                    for (int dx = 0, deltaIndex = 0; dx < 2; ++dx) {
                        for (int dy = 0; dy < 2; ++dy) {
                            for (int dz = 0; dz < 2; ++dz, ++deltaIndex) {
                                HASHES_BUFFER[hashIndex].values[deltaIndex] = chunk.values[nx * 2 + dx][ny * 2 + dy][nz * 2 + dz];
                            }
                        }
                    }
                }
            }
        }
        for (int step = 8; step <= HASHES_BUFFER.length; step *= 8) {
            for (int ptr = 0; ptr < HASHES_BUFFER.length; ptr += step) {
                Node node = new Node();
                int smallStep = step / 8;
                for (int j = 0, smallPtr = ptr; j < 8; ++j, smallPtr += smallStep) {
                    node.children[j] = HASHES_BUFFER[smallPtr];
                }
                HASHES_BUFFER[ptr] = node.combine();
                hashToNode.put(HASHES_BUFFER[ptr], node);
            }
        }
        Hash pointer = root;
        for (int depth = 0, halfNodes = NODES / 2; depth < CHUNK_DEPTH; ++depth, halfNodes /= 2) {
            Node node = hashToNode.get(pointer);
            int index = 0;
            if (cz > halfNodes) {
                cz -= halfNodes;
                index += 1;
            }
            if (cy > halfNodes) {
                cy -= halfNodes;
                index += 2;
            }
            if (cx > halfNodes) {
                cx -= halfNodes;
                index += 4;
            }
            NODE_PATH_BUFFER[depth] = node;
            INDEX_PATH_BUFFER[depth] = index;
            pointer = node.children[index];
        }
        Hash updatedHash = HASHES_BUFFER[0];
        for (int depth = CHUNK_DEPTH - 2; depth >= 0; --depth) {
            Node updatedNode = NODE_PATH_BUFFER[depth].cloneWithChangedChild(INDEX_PATH_BUFFER[depth], updatedHash);
            updatedHash = updatedNode.combine();
            hashToNode.put(updatedHash, updatedNode);
        }
        root = updatedHash;
    }

    @NoArgsConstructor
    private static class Hash {

        private static final int LENGTH = 8;
        private final int[] values = new int[LENGTH];

        private Hash(byte[] bytes) {
            for (int i = 0, j = 0; i < LENGTH; ++i, j += 4) {
                values[i] |= ((0xFF & bytes[j]) << 24) | ((0xFF & bytes[j + 1]) << 16) | ((0xFF & bytes[j + 2]) << 8) | (0xFF & bytes[j + 3]);
            }
        }

    }

    @NoArgsConstructor
    private static class Node {

        private final Hash[] children = new Hash[8];

        private Node(Hash hash) {
            Arrays.fill(children, hash);
        }

        private Hash combine() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Hash.LENGTH * children.length * Integer.BYTES);
            for (int i = 0; i < Hash.LENGTH; ++i) {
                for (Hash hash : children) {
                    byteBuffer.putInt(hash.values[i]);
                }
            }
            return new Hash(DigestUtils.sha256(byteBuffer.array()));
        }

        private Node cloneWithChangedChild(int index, Hash hash) {
            Node updatedNode = new Node();
            for (int i = 0; i < children.length; ++i) {
                updatedNode.children[i] = children[i];
            }
            updatedNode.children[index] = hash;
            return updatedNode;
        }

    }

    private static class Chunk {

        private static final int NODES = 1 << 3; // todo NODES should be 2^n
        private static final int SIZE = NODES * 2;
        private final int[][][] values = new int[SIZE][SIZE][SIZE];

    }

}
