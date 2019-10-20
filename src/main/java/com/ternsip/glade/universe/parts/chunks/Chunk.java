package com.ternsip.glade.universe.parts.chunks;

import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.universe.parts.blocks.Block;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Chunk implements Serializable {

    public static final int RELAX_PERIOD_MILLISECONDS = 30000;
    public static final int SIZE_X = 32;
    public static final int SIZE_Z = 32;

    public volatile transient Timer timer = new Timer(RELAX_PERIOD_MILLISECONDS);
    public volatile transient boolean modified = false;

    public volatile Block[][][] blocks = new Block[SIZE_X][BlocksRepository.SIZE_Y][SIZE_Z];
    public volatile byte[][][] skyLights = new byte[SIZE_X][BlocksRepository.SIZE_Y][SIZE_Z];
    public volatile byte[][][] emitLights = new byte[SIZE_X][BlocksRepository.SIZE_Y][SIZE_Z];
    public volatile int[][] heights = new int[SIZE_X][SIZE_Z];
    public volatile Sides sides = new Sides();
    public volatile int xPos;
    public volatile int zPos;

    public Chunk(int xPos, int zPos) {
        this.xPos = xPos;
        this.zPos = zPos;
    }

    public Vector3ic getStart() {
        return new Vector3i(xPos * Chunk.SIZE_X, 0, zPos * Chunk.SIZE_Z);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.timer = new Timer(RELAX_PERIOD_MILLISECONDS);
        this.modified = false;
    }

}