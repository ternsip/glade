package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Maths;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.joml.Vector3i;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

import static com.ternsip.glade.universe.parts.chunks.BlocksServerRepository.*;

/**
 * Compressor of of 3d space of integer cells (voxels/blocks/cubes)
 * Uses octree to infuse similar nodes and to not duplicate memory when similar pattern occurs
 * The more diversity 3d space have - the less compression level is
 */
public class GridCompressor implements Serializable {

    private static final int SIZE = Maths.max(SIZE_X, SIZE_Y, SIZE_Z);
    private static final int NODES = SIZE / 2;
    private static final int DEPTH = Maths.log2(NODES);
    private static final int CHUNK_DEPTH = Maths.log2(NODES / Chunk.NODES);

    // Special buffers to decrease memory bandwidth
    private Hash[] hashesBuffer = new Hash[Chunk.NODES * Chunk.NODES * Chunk.NODES];
    private int[] indexPathBuffer = new int[CHUNK_DEPTH];
    private Node[] nodePathBuffer = new Node[CHUNK_DEPTH];

    // This map is used to cache chunks by chunk-coordinate
    private HashMap<Vector3i, Chunk> posToChunk = new HashMap<>();

    // Octree general structure, holding mapping: hash of some node -> to that node
    // Remember that leaf nodes are just hashes that keeps real data, not some kind of other classes
    private HashMap<Hash, Node> hashToNode = new HashMap<>();

    // The root of octree
    private Hash root;

    static {
        if (!Maths.isPowerOfTwo(SIZE) || SIZE < 2) {
            throw new IllegalArgumentException("Invalid size");
        }
        if (!Maths.isPowerOfTwo(Chunk.NODES) || Chunk.NODES > NODES) {
            throw new IllegalArgumentException("Invalid chunk size");
        }
    }

    public GridCompressor() {
        // Initially fill new tree with zero values, it takes really small number of nodes due to repetitions
        Hash hash = new Hash();
        for (int i = 0; i < DEPTH; ++i) {
            Node node = new Node(hash);
            hash = node.combine();
            hashToNode.put(hash, node);
        }
        root = hash;
    }

    public synchronized int read(int x, int y, int z) {
        Chunk chunk = loadChunk(x / Chunk.SIZE, y / Chunk.SIZE, z / Chunk.SIZE);
        return chunk.values[x % Chunk.SIZE][y % Chunk.SIZE][z % Chunk.SIZE];
    }

    public synchronized void write(int x, int y, int z, int value) {
        Chunk chunk = loadChunk(x / Chunk.SIZE, y / Chunk.SIZE, z / Chunk.SIZE);
        chunk.values[x % Chunk.SIZE][y % Chunk.SIZE][z % Chunk.SIZE] = value;
    }

    public synchronized void read(int[][][] values, int startX, int startY, int startZ) {
        int sizeX = values.length;
        int sizeY = values[0].length;
        int sizeZ = values[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    values[x][y][z] = read(startX + x, startY + y, startZ + z); // TODO slow due to chunk map, load each chunk and fill with it
                }
            }
        }
    }

    public synchronized void write(int[][][] values, int startX, int startY, int startZ) {
        int sizeX = values.length;
        int sizeY = values[0].length;
        int sizeZ = values[0][0].length;
        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    write(startX + x, startY + y, startZ + z, values[x][y][z]); // TODO slow due to chunk map, load each chunk and fill with it
                }
            }
        }
    }

    public synchronized void saveChunks() {
        posToChunk.keySet().forEach(this::saveChunk);
        posToChunk.clear();
        // TODO add unloading obsoleted chunks
        // TODO clean after saving periodically, otherwise you can get too many dead nodes
    }

    // Cleaning obsoleted nodes from map, using simple dfs to mark all used nodes starting from root
    public synchronized void cleanTree() {
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
            hashesBuffer[0] = traceChunk(cx, cy, cz);
            // Filling hashes buffer plain array with chunk's leafs
            // Iterating from top to bottom levels of tree, the reverse process is fully described in save-stage
            for (int step = hashesBuffer.length; step >= 8; step /= 8) {
                for (int ptr = 0; ptr < hashesBuffer.length; ptr += step) {
                    Node node = hashToNode.get(hashesBuffer[ptr]);
                    int smallStep = step / 8;
                    for (int j = 0, smallPtr = ptr; j < 8; ++j, smallPtr += smallStep) {
                        hashesBuffer[smallPtr] = node.children[j];
                    }
                }
            }
            // Filling chunk with values taken from hashes buffer in natural order described in save-stage
            for (int nx = 0, hashIndex = 0; nx < Chunk.NODES; ++nx) {
                for (int ny = 0; ny < Chunk.NODES; ++ny) {
                    for (int nz = 0; nz < Chunk.NODES; ++nz, ++hashIndex) {
                        for (int dx = 0, deltaIndex = 0; dx < 2; ++dx) {
                            for (int dy = 0; dy < 2; ++dy) {
                                for (int dz = 0; dz < 2; ++dz, ++deltaIndex) {
                                    chunk.values[nx * 2 + dx][ny * 2 + dy][nz * 2 + dz] = hashesBuffer[hashIndex].values[deltaIndex];
                                }
                            }
                        }
                    }
                }
            }
            // Buffering chunk using map
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
        // Keeps data in next order:  Sum_i [i in 0..n] of 8^i * (4xi + 2yi + 1zi) = index
        // Where xi or yi or zi is binary digit (1 | 0) representation of final coordinate
        // In other words index holds n triplets of coordinates in order: xi (highest), yi (middle), zi (lowest)
        // The final coordinate (x or y or z) should be composed by binary concatenation of all n components
        // All that is just natural order to pack all 3d values into flat array
        for (int nx = 0, hashIndex = 0; nx < Chunk.NODES; ++nx) {
            for (int ny = 0; ny < Chunk.NODES; ++ny) {
                for (int nz = 0; nz < Chunk.NODES; ++nz, ++hashIndex) {
                    hashesBuffer[hashIndex] = new Hash();
                    for (int dx = 0, deltaIndex = 0; dx < 2; ++dx) {
                        for (int dy = 0; dy < 2; ++dy) {
                            for (int dz = 0; dz < 2; ++dz, ++deltaIndex) {
                                hashesBuffer[hashIndex].values[deltaIndex] = chunk.values[nx * 2 + dx][ny * 2 + dy][nz * 2 + dz];
                            }
                        }
                    }
                }
            }
        }
        // Filling hashes buffer by next algorithm: compressing every oct-children into one parent
        // Starting from smallest step because initially it is just flattened tree leafs into plain array
        // The hole process can be described as iterating through levels of tree starting from bottom to top
        // Remember that this is just flattened chunk's nodes, not whole tree
        for (int step = 8; step <= hashesBuffer.length; step *= 8) {
            for (int ptr = 0; ptr < hashesBuffer.length; ptr += step) {
                Node node = new Node();
                int smallStep = step / 8;
                for (int j = 0, smallPtr = ptr; j < 8; ++j, smallPtr += smallStep) {
                    node.children[j] = hashesBuffer[smallPtr];
                }
                hashesBuffer[ptr] = node.combine();
                hashToNode.put(hashesBuffer[ptr], node);
            }
        }
        traceChunk(cx, cy, cz);
        // Now Buffer holds only one element (index - zero) - the top of chunk's node
        // So we need to move further uphill on the tree until we reach root level
        // For each node we pass through - we modifying one child and recording new hash sum for the node
        Hash updatedHash = hashesBuffer[0];
        for (int depth = CHUNK_DEPTH - 1; depth >= 0; --depth) {
            Node updatedNode = nodePathBuffer[depth].cloneWithChangedChild(indexPathBuffer[depth], updatedHash);
            updatedHash = updatedNode.combine();
            hashToNode.put(updatedHash, updatedNode);
        }
        // After all the process we just appointing new root
        // But old root and some data still remain in the node-map, hence we need to clean it in separate routine
        root = updatedHash;
    }

    // Recording path from (top) root node to (somewhere in middle) chunk's node
    // Keeping nodes and indices of the children we falling in
    // As the result we return chunk's top node
    private Hash traceChunk(int cx, int cy, int cz) {
        Hash pointer = root;
        for (int depth = 0, halfChunks = NODES / (2 * Chunk.NODES); depth < CHUNK_DEPTH; ++depth, halfChunks /= 2) {
            Node node = hashToNode.get(pointer);
            int index = 0;
            if (cz >= halfChunks) {
                cz -= halfChunks;
                index += 1;
            }
            if (cy >= halfChunks) {
                cy -= halfChunks;
                index += 2;
            }
            if (cx >= halfChunks) {
                cx -= halfChunks;
                index += 4;
            }
            nodePathBuffer[depth] = node;
            indexPathBuffer[depth] = index;
            pointer = node.children[index];
        }
        return pointer;
    }

    private synchronized void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        // TODO infuse with class default definition, get rid of boilerplate
        hashesBuffer = new Hash[Chunk.NODES * Chunk.NODES * Chunk.NODES];
        indexPathBuffer = new int[CHUNK_DEPTH];
        nodePathBuffer = new Node[CHUNK_DEPTH];
        posToChunk = new HashMap<>();
        hashToNode = new HashMap<>();
        int size = ois.readInt();
        for (int i = 0; i < size; ++i) {
            Hash hash = new Hash();
            for (int j = 0; j < Hash.LENGTH; ++j) {
                hash.values[j] = ois.readInt();
            }
            Node node = new Node();
            for (int c = 0; c < 8; ++c) {
                node.children[c] = new Hash();
                for (int j = 0; j < Hash.LENGTH; ++j) {
                    node.children[c].values[j] = ois.readInt();
                }
            }
            hashToNode.put(hash, node);
        }
        root = new Hash();
        for (int j = 0; j < Hash.LENGTH; ++j) {
            root.values[j] = ois.readInt();
        }
    }

    private synchronized void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(hashToNode.size());
        for (Map.Entry<Hash, Node> entry : hashToNode.entrySet()) {
            Hash hash = entry.getKey();
            Node node = entry.getValue();
            for (int j = 0; j < Hash.LENGTH; ++j) {
                oos.writeInt(hash.values[j]);
            }
            for (int c = 0; c < 8; ++c) {
                for (int j = 0; j < Hash.LENGTH; ++j) {
                    oos.writeInt(node.children[c].values[j]);
                }
            }
        }
        for (int j = 0; j < Hash.LENGTH; ++j) {
            oos.writeInt(root.values[j]);
        }
    }

    /**
     * This hash used for both - store real values for leaf-nodes of tree and store tree node's hash
     * Since leaf nodes are being used to keep real values, we can pack one octree node in it - 8 values
     * That way every leaf in octree is stored like hash
     */
    @NoArgsConstructor
    @EqualsAndHashCode
    private static class Hash {

        private static final int LENGTH = 8;
        private final int[] values = new int[LENGTH];

        // Constructing class from byte array (byte array -> int array)
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

        // Generate hash regarding children values
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
            System.arraycopy(children, 0, updatedNode.children, 0, children.length);
            updatedNode.children[index] = hash;
            return updatedNode;
        }

    }

    private static class Chunk {

        private static final int NODES = 1 << 3;
        private static final int SIZE = NODES * 2;
        private final int[][][] values = new int[SIZE][SIZE][SIZE];

    }

}
